package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Binary;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;

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
    
    /**
     * Simplify and beautify the specified mathematical operation as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param op
     *        The mathematical operation. If it could not be simplified or
     *        beautified, {@code op} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression operation(Operation op)
    {
        if (op instanceof Binary)
            return binary((Binary) op);
        return op;
    }
    
    /**
     * Simplify and beautify the specified mathematical binary operation as far
     * as possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param bin
     *        The mathematical binary operation. If it could not be simplified
     *        or beautified, {@code bin} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
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
    
    /**
     * Simplify and beautify the specified mathematical addition as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param expr
     *        The mathematical addition. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression add(Add add)
    {
        Expression left = parse(add.getLeft());
        Expression right = parse(add.getRight());
        if (right instanceof Symbol && !(left instanceof Symbol))
            add.set(right, left); // swap
        else
            add.set(left, right);
        if (left.equals(Symbol.ZERO))
            return right;
        if (right.equals(Symbol.ZERO))
            return left;
        return add;
    }
    
    /**
     * Simplify and beautify the specified mathematical multiplication as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param expr
     *        The mathematical multiplication. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
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
        // combine if both expressions are fractions
        if (left instanceof Divide && right instanceof Divide)
        {
            Divide ldiv = (Divide) left;
            Divide rdiv = (Divide) right;
            ldiv.setNumerator(mul(new Multiply(ldiv.getNumerator(), rdiv.getNumerator())));
            ldiv.setDenominator(mul(new Multiply(ldiv.getDenominator(), rdiv.getDenominator())));
            return ldiv;
        }
        return mul;
    }
    
    /**
     * Simplify and beautify the specified mathematical division as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param expr
     *        The mathematical division. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression div(Divide div)
    {
        Expression num = parse(div.getNumerator());
        Expression denom = parse(div.getDenominator());
        // x/1 -> x
        if (denom.equals(Symbol.ONE))
            return num;
        // 0/x -> 0, x != 0
        if (num.equals(Symbol.ZERO) && !denom.equals(Symbol.ZERO))
            return num;
        div.set(num, denom);
        return div;
    }
    
    /**
     * Simplify and beautify the specified mathematical power as far as possible
     * such that the expression remains as simple as possible to be read by
     * users.
     * 
     * @param expr
     *        The mathematical power. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression pow(Power pow)
    {
        Expression exponent = parse(pow.getExponent());
        if(exponent instanceof Symbol)
        {
            Symbol symexp = (Symbol) exponent;
            // x^1 -> x
            if(symexp.equals(Symbol.ONE))
                return parse(pow.getBase());
            if(symexp.isFactorOnly())
            {
                double value = symexp.getFactor();
                // x^-n -> 1/(x^n)
                if(value < 0)
                {
                    // n -> -n
                    symexp.setFactor(-value);
                    pow.setExponent(symexp);
                    return div(new Divide(new Symbol(1), pow(pow)));
                }
                else
                {
                    // explicit (symbol) ^ (n) -> implicit symbol^n
                    // i.e.: Power -> Symbol
                    Expression base = parse(pow.getBase());
                    if (base instanceof Symbol)
                    {
                        if (((Symbol) base).setPow(symexp))
                            return base;
                    }
                    pow.setExponent(symexp);
                    return pow;
                }
            }
        }
        else if(exponent instanceof Divide)
        {
            // x^(a/b) -> root(b,x^a): b > 0
            Divide div = (Divide) exponent;
            Expression dividend = parse(div.getNumerator());
            Expression divisor = parse(div.getDenominator());
            if (divisor instanceof Symbol)
            {
                Symbol symdenom = (Symbol) divisor;
                if (dividend instanceof Symbol)
                {
                    Symbol symnum = (Symbol) dividend;
                    double fdenom = symdenom.getFactor();
                    double fnum = symnum.getFactor();
                    // if a/b >= 0
                    if ((fnum > 0 && fdenom > 0) ||
                            (fnum < 0 && fdenom < 0))
                        return new Root(pow(new Power(pow.getBase(), symnum)), symdenom);
                    // if a < 0 && b > 0
                    if (fnum < 0 && fdenom > 0)
                    {
                        symnum.setFactor(-fnum);
                        Root root = new Root(pow(new Power(pow.getBase(), symnum)), symdenom);
                        return new Divide(new Symbol(1), root);
                    }
                }
            }
        }
        pow.setExponent(exponent);
        return pow;
    }
    
    // FIXME 5e * 9pi2 + 2i/4 + i, expected: 45epi2 + i*3/2
}
