package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Function;
import org.teaminfty.math_dragon.view.math.Parentheses;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Negate;
import org.teaminfty.math_dragon.view.math.operation.binary.Derivative;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Log;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;

/** Class that helps setting parentheses at the right places in a {@link Expression} */
public class ParenthesesHelper
{
    /** Sets parentheses where necessary and removes unnecessary parentheses for the given {@link Expression}
     * @param expr The {@link Expression} for which the parentheses should be set
     * @return The {@link Expression} with the parentheses rightly set
     */
    public static Expression setParentheses(Expression expr)
    {
        // The root never is enclosed in parentheses
        if(expr instanceof Parentheses)
            return setParentheses(expr.getChild(0));
        
        // Recursively set the parentheses correctly for all children of the expression
        for(int i = expr.getChildCount(); i > 0; --i)
            makeChild(expr, setParentheses(expr.getChild(i - 1)), i - 1);
        
        // Return the result
        return expr;
    }
    
    /** Makes a {@link Expression} the child of another {@link Expression}, while correctly placing / removing parentheses
     * @param parent The {@link Expression} that is to become the parent
     * @param child The {@link Expression} that is to become the child
     * @param index The index where the child should be placed
     */
    private static void makeChild(Expression parent, Expression child, int index)
    {
        // The children, one with parentheses, the other one without them
        Expression withParentheses = null;
        Expression withoutParentheses = null;
        
        // Set the children (one with and one without parentheses)
        if(child instanceof Parentheses)
        {
            withParentheses = child;
            withoutParentheses = child.getChild(0);
            while(withoutParentheses instanceof Parentheses)
            {
                withParentheses = withoutParentheses;
                withoutParentheses = withoutParentheses.getChild(0);
            }
        }
        else
        {
            withParentheses = new Parentheses(child);
            withoutParentheses = child;
        }

        // Whether or not parentheses should be placed
        boolean placeParentheses = false;
        
        // Whether or not the value in placeParentheses is definitive
        boolean definitivePlaceParentheses = false;

        // Special case: the divide operator
        // We only place parentheses around other divide operator (and around nothing else)
        if(parent instanceof Divide)
        {
            if(withoutParentheses instanceof Divide)
                placeParentheses = true;
            definitivePlaceParentheses = true;
        }

        // Special case: the second operand of the subtract operator
        if(parent instanceof Subtract && index == 1 && parent.getPrecedence() == withoutParentheses.getPrecedence())
        {
            placeParentheses = true;
            definitivePlaceParentheses = true;
        }

        // Special case: the power operator
        if(parent instanceof Power)
        {
            // Place parentheses around the base of the power operator
            // if the it's a Symbol and the Symbol has multiple visible symbols
            // or if it's an instance of Negate
            // or if its precedence is lower than that of the power operator
            if(index == 0)
            {
                if(withoutParentheses instanceof Symbol && multipleSymbolsVisible((Symbol) withoutParentheses))
                    placeParentheses = true;
                else if(withoutParentheses instanceof Negate)
                    placeParentheses = true;
                else if(parent.getPrecedence() < withoutParentheses.getPrecedence())
                    placeParentheses = true;
            }
            
            // Place parentheses around the exponent if the exponent itself is a power
            // either as a Power operation or a Symbol 
            if(index == 1)
            {
                if(withoutParentheses instanceof Power)
                    placeParentheses = true;
                else if(withoutParentheses instanceof Symbol && powerVisible((Symbol) withoutParentheses))
                    placeParentheses = true;
            }
            
            // Make the value of placeParentheses definitive
            definitivePlaceParentheses = true;
        }

        // Special cases to never place parentheses around:
        //      The children of a Root operation
        //      The child of a Parentheses object
        //      The first child of the Derivative
        //      The child of a function
        //      The children of a logarithm
        //      The child of a negate operation
        if(parent instanceof Root || (parent instanceof Derivative && index == 0) || parent instanceof Parentheses ||
           parent instanceof Function ||parent instanceof Log || parent instanceof Negate)
        {
            placeParentheses = false;
            definitivePlaceParentheses = true;
        }
        
        // Place parentheses if the child has a lower precedence
        if(!definitivePlaceParentheses)
        {
            if(parent.getPrecedence() < withoutParentheses.getPrecedence())
                placeParentheses = true;
        }

        // Set the child
        if(placeParentheses)
            parent.setChild(index, withParentheses);
        else
            parent.setChild(index, withoutParentheses);
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
}
