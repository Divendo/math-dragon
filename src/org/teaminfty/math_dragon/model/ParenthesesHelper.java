package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationDerivative;
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
                if(child instanceof MathSymbol && multipleSymbolsVisible((MathSymbol) child))
                    child = new MathParentheses(child);
                else if(child instanceof MathParentheses && !(child.getChild(0) instanceof MathSymbol && multipleSymbolsVisible((MathSymbol) child.getChild(0))))
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
            else if(index == 1)
            {
                if(child instanceof MathOperationPower)
                    child = new MathParentheses(child);
                else if(child instanceof MathSymbol && powerVisible((MathSymbol) child))
                    child = new MathParentheses(child);
                else if( child instanceof MathParentheses && !(child.getChild(0) instanceof MathOperationPower || (child.getChild(0) instanceof MathSymbol && powerVisible((MathSymbol) child.getChild(0)))) )
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
        }
        // Special cases to never place parentheses:
        //      all children of the root operator
        //      the first child of the derivative operator
        else if(!(parent instanceof MathOperationRoot) && !(parent instanceof MathOperationDerivative && index == 0))
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
    
    /** Returns whether multiple symbols are visible in the given {@link MathSymbol}
     * @param symbol The {@link MathSymbol} to check
     * @return <tt>true</tt> if multiple symbols are visible, <tt>false</tt> otherwise */
    private static boolean multipleSymbolsVisible(MathSymbol symbol)
    {
        // Keeps track of whether or not we've already found a visible symbol
        boolean symbolVisible = symbol.getFactor() != 1;
        
        // Check for the constant's symbols (pi, e and the imaginary unit)
        if(symbol.getPiPow() != 0)
        {
            if(symbolVisible)
                return true;
            symbolVisible = true;
        }
        if(symbol.getEPow() != 0)
        {
            if(symbolVisible)
                return true;
            symbolVisible = true;
        }
        if(symbol.getIPow() != 0)
        {
            if(symbolVisible)
                return true;
            symbolVisible = true;
        }
        
        // Check all variables
        for(int i = 0; i < symbol.varPowCount(); ++i)
        {
            if(symbol.getVarPow(i) != 0)
            {
                if(symbolVisible)
                    return true;
                symbolVisible = true;
            }
        }
        
        // If we've come here we haven't found more than one visible symbol
        return false;
    }
    
    /** Returns whether or not powers are visible in the given {@link MathSymbol}
     * @param symbol The {@link MathSymbol} to check
     * @return <tt>true</tt> if powers are visible, <tt>false</tt> otherwise */
    private static boolean powerVisible(MathSymbol symbol)
    {
        // Check for the constant's symbols (pi, e and the imaginary unit)
        if((symbol.getPiPow() | symbol.getEPow() | symbol.getIPow()) > 1)
                return true;
        
        // Check all variables
        for(int i = 0; i < symbol.varPowCount(); ++i)
        {
            if(symbol.getVarPow(i) > 1)
                return true;
        }
        
        // If we've come here we haven't found any powers
        return false;
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
