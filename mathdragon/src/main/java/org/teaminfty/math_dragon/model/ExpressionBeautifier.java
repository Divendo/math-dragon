package org.teaminfty.math_dragon.model;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Function;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Binary;
import org.teaminfty.math_dragon.view.math.operation.Negate;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;

/**
 * Ensures that complicated and hard-to-read mathematical expressions are
 * simplified and beautified such that the expression remains as simple as
 * possible to be read by users.
 * 
 * @author FolkertVanVerseveld
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
     * @return Usually a simplified and beautified expression. <tt>expr</tt>
     *         otherwise.
     */
    public static Expression parse(Expression expr)
    {
        if (expr instanceof Symbol)
            return symbol((Symbol) expr);
        if (expr instanceof Operation)
            return operation((Operation) expr);
        if (expr instanceof Function)
            return function((Function) expr);
        return expr;
    }
    
    /**
     * Simplify and beautify the specified mathematical symbolic constant as far
     * as possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param s
     *        The mathematical symbolic constant. If it could not be simplified
     *        or beautified, {@code bin} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression symbol(Symbol s)
    {
        double factor = s.getFactor();
        // if it is interchangeable with 'real' integers
        long tempFactor = (long) factor;
        if(tempFactor == factor)
        {
            long pow = 0;
            if(tempFactor < 0)
                tempFactor = -tempFactor;
            while(tempFactor >= 10 && tempFactor % 10 == 0)
            {
                tempFactor /= 10;
                factor /= 10;
                ++pow;
            }
            // only transform if it could be simplified
            if(pow >= 6)
            {
                final long powDelta = Math.min((long) Math.floor(Math.log10(factor < 0 ? -factor : factor)), 6);
                pow += powDelta;
                s.setFactor(factor / Math.pow(10, powDelta));
                Expression power = new Power(new Symbol(10), new Symbol(pow));
                if(s.equals(Symbol.ONE))
                    return power;
                return new Multiply(s, power);
            }
        }
        return s;
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
        if (bin instanceof Subtract)
            return subtract((Subtract) bin);
        if (bin instanceof Multiply)
            return mul((Multiply) bin);
        if (bin instanceof Divide)
            return div((Divide) bin);
        if (bin instanceof Power)
            return pow((Power) bin);
        return bin;
    }
    
    /**
     * Simplify and beautify the specified mathematical unary function as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param func
     *        The mathematical unary function. If it could not be simplified or
     *        beautified, {@code bin} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression function(Function func)
    {
        func.setChild(0, parse(func.getChild(0)));
        return func;
    }
    
    /**
     * Simplify and beautify the specified mathematical addition as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param add
     *        The mathematical addition. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression add(Add add)
    {
        // Make sure we handle additions from left to right
        if(add.getRight() instanceof Add)
        {
            Add radd = (Add) add.getRight();
            Expression lexpr = add(new Add(add.getLeft(), radd.getLeft()));
            return add(new Add(lexpr, radd.getRight()));
        }
        
        Expression left = parse(add.getLeft());
        Expression right = parse(add.getRight());
        if(isSymbol(right) && !isSymbol(left) && !(left instanceof  Add))
        {
            add.set(right, left); // swap
            left = add.getLeft();
            right = add.getRight();
        }
        else
            add.set(left, right);
        if (left.equals(Symbol.ZERO))
            return right;
        if (right.equals(Symbol.ZERO))
            return left;
        if (right instanceof Symbol)
        {
            Symbol symr = (Symbol) right;
            double factor = symr.getFactor();
            if (factor < 0)
            {
                symr.setFactor(-factor);
                return new Subtract(left, symr);
            }
        }
        if (right instanceof Negate)
            return subtract(new Subtract(left, right.getChild(0)));
        return add;
    }
    
    /**
     * Simplify and beautify the specified mathematical subtraction as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param sub
     *        The mathematical subtraction. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression subtract(Subtract sub)
    {
        Expression left = parse(sub.getLeft());
        Expression right = parse(sub.getRight());
        if (left.equals(Symbol.ZERO))
            return new Negate(right);
        if (right.equals(Symbol.ZERO))
            return left;
        if (right instanceof Symbol)
        {
            Symbol symr = (Symbol) right;
            double factor = symr.getFactor();
            if (factor < 0)
            {
                symr.setFactor(-factor);
                return new Add(left, symr);
            }
        }
        if (right instanceof Negate)
            return add(new Add(left, right.getChild(0)));
        sub.set(left, right);
        return sub;
    }
    
    /**
     * Simplify and beautify the specified mathematical multiplication as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param mul
     *        The mathematical multiplication. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression mul(Multiply mul)
    {
        // Make sure we handle multiplications from left to right
        if(mul.getRight() instanceof Multiply)
        {
            Multiply rmul = (Multiply) mul.getRight();
            Expression lexpr = mul(new Multiply(mul.getLeft(), rmul.getLeft()));
            return mul(new Multiply(lexpr, rmul.getRight()));
        }
        
        Expression left = parse(mul.getLeft());
        Expression right = parse(mul.getRight());
        
        // move symbolic constants to left if not already done
        if(isSymbol(right) && !isSymbol(left))
        {
            mul.set(right, left); // swap
            left = mul.getLeft();
            right = mul.getRight();
        }
        else
            mul.set(left, right);
        // combine if both expressions are symbolic constants
        if (left instanceof Symbol && right instanceof Symbol)
        {
            Symbol symleft = (Symbol) left;
            Symbol symright = (Symbol) right;
            if (symleft.varPowCount() == symright.varPowCount())
            {
                symleft.setFactor(symleft.getFactor() * symright.getFactor());
                symleft.setPiPow(symleft.getPiPow() + symright.getPiPow());
                symleft.setEPow(symleft.getEPow() + symright.getEPow());
                symleft.setIPow(symleft.getIPow() + symright.getIPow());
                for (int i = 0; i < Symbol.VAR_POWS_LENGTH; ++i)
                    symleft.setVarPow(i, symleft.getVarPow(i) + symright.getVarPow(i));
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
            return div(ldiv);
        }
        // A * (B/C) -> (A*B)/C 
        if (right instanceof Divide)
        {
            Divide rdiv = (Divide) right;
            rdiv.setNumerator(mul(new Multiply(rdiv.getNumerator(), left)));
            return div(rdiv);
        }
        // (A/B) * C -> (A*C)/B
        if (left instanceof Divide)
        {
            Divide ldiv = (Divide) left;
            ldiv.setNumerator(mul(new Multiply(ldiv.getNumerator(), right)));
            return div(ldiv);
        }
        // combine if left operand equals -1 or 1
        if(left.equals(Symbol.M_ONE))
        {
            if(right instanceof Negate)
                return right.getChild(0);
            else
                return new Negate(right);
        }
        else if(left.equals(Symbol.ONE))
            return right;

        // -a*b -> -(a*b), a*-b -> -(a*b), -a*-b -> a*b
        final boolean leftNegative = (left instanceof Symbol && ((Symbol) left).getFactor() < 0) || left instanceof Negate;
        final boolean rightNegative = (right instanceof Symbol && ((Symbol) right).getFactor() < 0) || right instanceof Negate;
        if(leftNegative)
        {
            if(left instanceof Symbol)
            {
                ((Symbol) left).invertFactor();
            }
            else
                left = left.getChild(0);
        }
        if(rightNegative)
        {
            if(right instanceof Symbol)
            {
                ((Symbol) right).invertFactor();
            }
            else
                right = right.getChild(0);
        }
        
        if(leftNegative || rightNegative)
        {
            mul.set(left, right);
            return leftNegative ^ rightNegative ? new Negate(mul(mul)) : mul(mul);
        }
        else
            return mul;
    }
    
    /**
     * Simplify and beautify the specified mathematical division as far as
     * possible such that the expression remains as simple as possible to be
     * read by users.
     * 
     * @param div
     *        The mathematical division. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression div(Divide div)
    {
        // Simplify fractions
        if(div.getNumerator() instanceof Symbol && div.getDenominator() instanceof Symbol && ((Symbol) div.getNumerator()).isFactorOnly() && ((Symbol) div.getDenominator()).isFactorOnly())
        {
            // Get the numerator, denominator and integer part
            double num = ((Symbol) div.getNumerator()).getFactor();
            double denom = ((Symbol) div.getDenominator()).getFactor();
            final boolean negative = num < 0 ^ denom < 0;
            num = Math.abs(num);
            denom = Math.abs(denom);
            double intPart =  Math.floor(num / denom);
            num -= intPart * denom;
            
            // If the numerator is zero, we're done
            if(num == 0)
                return new Symbol(negative ? -intPart : intPart);
            
            // Check if the denominator only contains the prime factors 2 and 5
            int exp2 = 0;
            int exp5 = 0;
            long denomInt = (long) denom;
            boolean validDenom = true;
            while(validDenom && denomInt != 1)
            {
                if(denomInt % 2 == 0)
                {
                    denomInt /= 2;
                    ++exp2;
                }
                else if(denomInt % 5 == 0)
                {
                    denomInt /= 5;
                    ++exp5;
                }
                else
                    validDenom = false;
            }

            // We leave fractions that have a smaller denominator than 10
            // Also if our denominator contains other factors besides 2 and 5, we stop here
            // Also if our numerator or denominator isn't an integer, we stop here
            if(denom < 10 || !validDenom || num != Math.round(num) || denom != Math.round(denom))
            {
                Expression lexpr, rexpr;
                lexpr = new Symbol(num);
                rexpr = new Symbol(denom);
                Expression divide = new Divide(lexpr, rexpr);
                if(negative)
                {
                    if(intPart != 0)
                    {
                        return new Subtract(new Negate(new Symbol(intPart)), divide);
                    }
                    else
                        return new Negate(divide);
                }
                else
                {
                    if(intPart != 0)
                    {
                        return new Add(new Symbol(intPart), divide);
                    }
                    else
                    {
                        return divide;
                    }
                }
            }
            
            // If the denominator is smaller than or equal to 1 000 000, we're done
            if(denom <= 1000000)
                return symbol(new Symbol((negative ? -1 : 1) * (intPart + num / denom)));
            
            // Create a denominator that's a power of 10
            int exp = Math.max(exp2, exp5);
            
            // Create a numerator for the new denominator
            double newNum = num + intPart * denom;
            if(exp2 < exp5)
                newNum *= Math.pow(2, exp5 - exp2);
            else if(exp5 < exp2)
                newNum *= Math.pow(5, exp2 - exp5);
            
            // Determine by which 10-power we'll divide the numerator
            int usedExp = Math.min((int) Math.floor(Math.log10(newNum)), 6);
            
            // Return the result
            if(negative)
                return new Multiply(new Negate(new Symbol(newNum / Math.pow(10, usedExp))), new Power(new Symbol(10), new Symbol(usedExp - exp)));
            else
                return new Multiply(new Symbol(newNum / Math.pow(10, usedExp)), new Power(new Symbol(10), new Symbol(usedExp - exp)));
        }
        
        Expression num = parse(div.getNumerator());
        Expression denom = parse(div.getDenominator());
        Expression result = div;
        // x/1 -> x
        if (denom.equals(Symbol.ONE))
            return num;
        // 0/x -> 0, x != 0
        if (num.equals(Symbol.ZERO) && !denom.equals(Symbol.ZERO))
            return num;
        if (num.equals(Symbol.ONE) && denom instanceof Power)
        {
            Power power = (Power) denom;
            if (power.getBase().equals(Symbol.TEN))
            {
                Expression powexp = power.getExponent();
                if (powexp instanceof Symbol)
                {
                    Symbol symexp = (Symbol) powexp;
                    if (symexp.isFactorOnly())
                    {
                        // 1/(10^n) -> 10^-n
                        symexp.setFactor(-symexp.getFactor());
                        return power;
                    }
                }
            }
            if (power.getBase() instanceof Symbol)
            {
                Symbol powbase = (Symbol) power.getBase();
                if (powbase.isFactorOnly())
                {
                    Expression powexp = power.getExponent();
                    if (powexp instanceof Symbol)
                    {
                        Symbol symexp = (Symbol) powexp;
                        if (symexp.isFactorOnly())
                        {
                            // 1/(10^n) -> 10^-n
                            symexp.setFactor(-symexp.getFactor());
                            return power;
                        }
                    }
                }
            }
        }
        // -a/b -> -(a/b), a/-b -> -(a/b), -a/-b -> a/b
        boolean numNegative = (num instanceof Symbol && ((Symbol) num).getFactor() < 0) || num instanceof Negate;
        boolean denomNegative = (denom instanceof Symbol && ((Symbol) denom).getFactor() < 0) || denom instanceof Negate;
        if(numNegative)
        {
            if(num instanceof Symbol)
                ((Symbol) num).setFactor(-((Symbol) num).getFactor());
            else
                num = num.getChild(0);
        }
        if(denomNegative)
        {
            if(denom instanceof Symbol)
                ((Symbol) denom).setFactor(-((Symbol) denom).getFactor());
            else
                denom = denom.getChild(0);
        }
        div.set(num, denom);
        return numNegative ^ denomNegative ? new Negate(result) : result;
    }
    
    /**
     * Simplify and beautify the specified mathematical power as far as possible
     * such that the expression remains as simple as possible to be read by
     * users.
     * 
     * @param pow
     *        The mathematical power. If it could not be simplified or
     *        beautified, {@code expr} is returned.
     * @return Usually a simplified and beautified expression. <tt>this</tt>
     *         otherwise.
     */
    static Expression pow(Power pow)
    {
        Expression rawExponent = pow.getExponent();
        Expression exponent = parse(rawExponent);
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
                    if (base instanceof Symbol && symexp.isFactorOnly())
                    {
                        if(((Symbol) base).tryRaisePower(symexp.getFactor()))
                            return base;
                    }
                    pow.setExponent(symexp);
                    return pow;
                }
            }
        }
        else if(exponent instanceof Divide || rawExponent instanceof Divide)
        {
            Expression beautifiedExp = exponent;
            if(!(exponent instanceof Divide))
                exponent = rawExponent;
            
            // x^(a/b) -> root(b,x^a): b > 0
            Divide div = (Divide) exponent;
            Expression dividend = parse(div.getNumerator());
            Expression divisor = parse(div.getDenominator());
            if(divisor instanceof Symbol)
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
                    {
                        return new Root(pow(new Power(pow.getBase(), symnum)), symdenom);
                    }
                    // if a < 0 && b > 0
                    if (fnum < 0 && fdenom > 0)
                    {
                        symnum.setFactor(-fnum);
                        Root root = new Root(pow(new Power(pow.getBase(), symnum)), symdenom);
                        return new Divide(new Symbol(1), root);
                    }
                }
            }
            exponent = beautifiedExp;
        }
        if(pow.getBase() instanceof Symbol && pow.getBase().equals(Symbol.ONE))
            return new Symbol(1);
        pow.setExponent(exponent);
        return pow;
    }
    
    /** Whether or not the given {@link Expression} is a Symbol
     * or a fraction (i.e. a Divide with two symbols as operators)
     * or base 10 power (i.e. a Power with 10 as base and a Symbol as exponent) */
    private static boolean isSymbol(Expression expr)
    {
        if(expr instanceof Symbol)
            return true;
        else if(expr instanceof Divide)
        {
            Divide div = (Divide) expr;
            return (div.getLeft() instanceof Symbol || isBase10(div.getLeft())) &&
                   (div.getRight() instanceof Symbol || isBase10(div.getRight()));
        }
        return isBase10(expr);
    }
    
    /**
     * Returns true if the given {@link Expression} is a base 10 power (i.e. a
     * Power with 10 as base and a Symbol as exponent)
     */
    private static boolean isBase10(Expression expr)
    {
        if(!(expr instanceof Power))
            return false;
        Power pow = (Power) expr;
        return pow.getBase() instanceof Symbol && ((Symbol) pow.getBase()).isFactorOnly() && ((Symbol) pow.getBase()).getFactor() == 10 && pow.getExponent() instanceof Symbol && ((Symbol) pow.getExponent()).isFactorOnly();
    }
}
