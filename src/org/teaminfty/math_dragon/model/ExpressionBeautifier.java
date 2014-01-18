package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Binary;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;

/**
 * Ensures that complicated and hard-to-read mathematical expression are
 * simplified and beautified such that the expression remains as simple as
 * possible to be read by users.
 * 
 * @author Folkert van Verseveld
 * @see #parse(Expression)
 */
public class ExpressionBeautifier
{
    private ExpressionBeautifier()
    {}

    /**
     * Simplify and beautify the specified mathematical expression as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param expr
     *        The mathematical expression. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    public static Expression parse(Expression expr)
    {
        if (expr instanceof Operation)
            return operation((Operation) expr);
        return expr;
    }
    
    static Expression operation(Operation op)
    {
        if (op instanceof Binary)
            return binary((Binary) op);
        return op;
    }
    
    static Expression binary(Binary bin)
    {
        if (bin instanceof Add)
            return add((Add) bin);
        if (bin instanceof Multiply)
            return mul((Multiply) bin);
        if (bin instanceof Divide)
            return div((Divide) bin);
        if (bin instanceof Power)
            return pow((Power) bin);
        return bin;
    }
    
    static Expression add(Add add)
    {
        Expression left = parse(add.getLeft());
        Expression right = parse(add.getRight());
        if (right instanceof Symbol && !(left instanceof Symbol))
            add.set(right, left); // swap
        else
            add.set(left, right);
        if (left instanceof Symbol)
        {
            Symbol symleft = (Symbol) left;
            if (symleft.isFactorOnly() && symleft.getFactor() == 0)
                return right;
        }
        return add;
    }
    
    static Expression mul(Multiply mul)
    {
        Expression left = parse(mul.getLeft());
        Expression right = parse(mul.getRight());
        // move symbolic constants to left if not already done
        if (right instanceof Symbol && !(left instanceof Symbol))
            mul.set(right, left); // swap
        else
            mul.set(left, right);
        // combine if both expressions are symbolic constants
        if (left instanceof Symbol && right instanceof Symbol)
        {
            Symbol symleft = (Symbol) left;
            Symbol symright = (Symbol) right;
            if (symleft.isFactorOnly())
            {
                symright.setFactor(symright.getFactor() * symleft.getFactor());
                return symright;
            }
            else if (symright.isFactorOnly())
            {
                symleft.setFactor(symleft.getFactor() * symright.getFactor());
                return symleft;
            }
        }
        return mul;
    }
    
    static Expression div(Divide div)
    {
        Expression num = parse(div.getNumerator());
        Expression denom = parse(div.getDenominator());
        // x/1 -> x
        if (denom.equals(Symbol.ONE))
            return num;
        div.set(num, denom);
        return div;
    }
    
    static Expression pow(Power pow)
    {
        Expression exponent = parse(pow.getExponent());
        if (exponent instanceof Symbol) {
            Symbol symexp = (Symbol) exponent;
            // x^1 -> x
            if (symexp.equals(Symbol.ONE))
                return pow.getBase();
            if (symexp.isFactorOnly())
            {
                double value = symexp.getFactor();
                // x^-n -> 1/(x^n)
                if (value < 0)
                {
                    symexp.setFactor(-value); // -n -> n
                    pow.setExponent(symexp);
                    return div(new Divide(new Symbol(1), pow(pow)));
                }
            }
        }
        return pow;
    }
    
    // 5e * 9pi2 + 2i/4 + i
}
