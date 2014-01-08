package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;
import org.teaminfty.math_dragon.view.math.MathOperationSubtract;
import org.teaminfty.math_dragon.view.math.MathParentheses;
import org.teaminfty.math_dragon.view.math.MathSymbol;

/** Class that helps setting parentheses at the right places in a {@link MathObject} */
public class ParenthesesHelper
{
    /** Makes a {@link MathObject} the child of another {@link MathObject}, while correctly placing / removing parentheses
     * @param parent The {@link MathObject} that is to become the parent
     * @param child The {@link MathObject} that is to become the child
     * @param index The index where the child should be placed
     */
    public static void makeChild(MathObject parent, MathObject child, int index)
    {
        // Special case: the divide operator
        if(parent instanceof MathOperationDivide)
        {
            if(child instanceof MathOperationDivide)
                child = new MathParentheses(child);
            else if(child instanceof MathParentheses && !(child.getChild(0) instanceof MathOperationDivide))
            {
                makeChild(parent, child.getChild(0), index);
                return;
            }
        }
        // Special case: the second operand of the subtract operator
        else if(parent instanceof MathOperationSubtract && index == 1 && parent.getPrecedence() == child.getPrecedence())
            child = new MathParentheses(child);
        // Special case: the power operator
        else if(parent instanceof MathOperationPower)
        {
            if(index == 0)
            {
                if(child instanceof MathSymbol && ((MathSymbol) child).symbolVisible())
                    child = new MathParentheses(child);
                else if(child instanceof MathParentheses && !(child.getChild(0) instanceof MathSymbol && ((MathSymbol) child).symbolVisible()))
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
            else if(index == 1)
            {
                if(child instanceof MathOperationPower)
                    child = new MathParentheses(child);
                else if(child instanceof MathParentheses && !(child.getChild(0) instanceof MathOperationPower))
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
        }
        // Special case: the root operator (never place parentheses)
        else if(!(parent instanceof MathOperationRoot))
        {
            // Wrap in parentheses if necessary
            if(!(parent instanceof MathParentheses) && parent.getPrecedence() < child.getPrecedence())
                child = new MathParentheses(child);
            else if(child instanceof MathParentheses && (parent instanceof MathParentheses || child.getChild(0).getPrecedence() <= parent.getPrecedence()))
            {
                // Maybe the child is already wrapped in parentheses, in that case we unwrap it and make that MathObject a child of parent
                makeChild(parent, child.getChild(0), index);
                return;
            }
        }
        
        // Set the child
        parent.setChild(index, child);
    }
    
    /** Sets parentheses where necessary and removes unnecessary parentheses for the given {@link MathObject}
     * @param mathObject The {@link MathObject} for which the parentheses should be set
     * @return The {@link MathObject} with the parentheses rightly set
     */
    public static MathObject setParentheses(MathObject mathObject)
    {
        // The root never is enclosed in parentheses
        if(mathObject instanceof MathParentheses)
            return setParentheses(mathObject.getChild(0));
        
        // For every child of mathObject, use makeChild() to set it correctly as a child
        for(int i = mathObject.getChildCount(); i > 0; --i)
            makeChild(mathObject, mathObject.getChild(i - 1), i - 1);
        
        // Return the result
        return mathObject;
    }
}
