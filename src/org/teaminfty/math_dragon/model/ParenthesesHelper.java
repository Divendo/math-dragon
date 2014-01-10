package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Parentheses;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Derivative;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;

/** Class that helps setting parentheses at the right places in a {@link Expression} */
public class ParenthesesHelper
{
    /** Makes a {@link Expression} the child of another {@link Expression}, while correctly placing / removing parentheses
     * @param parent The {@link Expression} that is to become the parent
     * @param child The {@link Expression} that is to become the child
     * @param index The index where the child should be placed
     */
    public static void makeChild(Expression parent, Expression child, int index)
    {
        // Special case: the divide operator
        if(parent instanceof Divide)
        {
            if(child instanceof Divide)
                child = new Parentheses(child);
            else if(child instanceof Parentheses && !(child.getChild(0) instanceof Divide))
            {
                makeChild(parent, child.getChild(0), index);
                return;
            }
        }
        // Special case: the second operand of the subtract operator
        else if(parent instanceof Subtract && index == 1 && parent.getPrecedence() == child.getPrecedence())
            child = new Parentheses(child);
        // Special case: the power operator
        else if(parent instanceof Power)
        {
            if(index == 0)
            {
                if(child instanceof Symbol && multipleSymbolsVisible((Symbol) child))
                    child = new Parentheses(child);
                else if(child instanceof Parentheses && !(child.getChild(0) instanceof Symbol && multipleSymbolsVisible((Symbol) child.getChild(0))))
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
            else if(index == 1)
            {
                if(child instanceof Power)
                    child = new Parentheses(child);
                else if(child instanceof Symbol && powerVisible((Symbol) child))
                    child = new Parentheses(child);
                else if( child instanceof Parentheses && !(child.getChild(0) instanceof Power || (child.getChild(0) instanceof Symbol && powerVisible((Symbol) child.getChild(0)))) )
                {
                    makeChild(parent, child.getChild(0), index);
                    return;
                }
            }
        }
        // Special cases to never place parentheses:
        //      all children of the root operator
        //      the first child of the derivative operator
        else if(!(parent instanceof Root) && !(parent instanceof Derivative && index == 0))
        {
            // Wrap in parentheses if necessary
            if(!(parent instanceof Parentheses) && parent.getPrecedence() < child.getPrecedence())
                child = new Parentheses(child);
            else if(child instanceof Parentheses && (parent instanceof Parentheses || child.getChild(0).getPrecedence() <= parent.getPrecedence()))
            {
                // Maybe the child is already wrapped in parentheses, in that case we unwrap it and make that MathObject a child of parent
                makeChild(parent, child.getChild(0), index);
                return;
            }
        }
        
        // Set the child
        parent.setChild(index, child);
    }
    
    /** Returns whether multiple symbols are visible in the given {@link Symbol}
     * @param symbol The {@link Symbol} to check
     * @return <tt>true</tt> if multiple symbols are visible, <tt>false</tt> otherwise */
    private static boolean multipleSymbolsVisible(Symbol symbol)
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
    
    /** Returns whether or not powers are visible in the given {@link Symbol}
     * @param symbol The {@link Symbol} to check
     * @return <tt>true</tt> if powers are visible, <tt>false</tt> otherwise */
    private static boolean powerVisible(Symbol symbol)
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
    
    /** Sets parentheses where necessary and removes unnecessary parentheses for the given {@link Expression}
     * @param mathObject The {@link Expression} for which the parentheses should be set
     * @return The {@link Expression} with the parentheses rightly set
     */
    public static Expression setParentheses(Expression mathObject)
    {
        // The root never is enclosed in parentheses
        if(mathObject instanceof Parentheses)
            return setParentheses(mathObject.getChild(0));
        
        // For every child of mathObject, use makeChild() to set it correctly as a child
        for(int i = mathObject.getChildCount(); i > 0; --i)
            makeChild(mathObject, mathObject.getChild(i - 1), i - 1);
        
        // Return the result
        return mathObject;
    }
}
