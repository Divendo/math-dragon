package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.operation.Function;
import org.teaminfty.math_dragon.view.math.operation.Function.FunctionType;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;

import android.annotation.SuppressLint;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p>
 * <h1>Reporting issues</h1>
 * Use our repo to report issues and show how to reproduce incorrect output.
 * 
 * @author Folkert van Verseveld
 */
public final class ModelHelper
{
    /**
     * Convert a mathematical expression from Symja to a graphical viewer that
     * contains the mathematical expression. Unknown mathematical expressions
     * result in a {@link ParseException}.
     * 
     * @param expr
     *        The mathematical expression from Symja. Usually obtained from
     *        <tt>EvalHelper.eval(MathObject)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    @SuppressLint("DefaultLocale")
    public static Expression toExpression(IExpr expr) throws ParseException
    {
        if(expr.isAST())
        {
            AST ast = (AST) expr;
            if(!(ast.get(0) instanceof Symbol))
                throw new ParseException(ast);
            if(expr.isPlus())
                return toOpAdd(ast);
            if(expr.isTimes())
                return toOpMul(ast);
            if(expr.isPower())
                return toOpPow(ast);
            return toOpFunction(ast);
        }
        else if(expr.isInteger())
        {
        	org.teaminfty.math_dragon.view.math.Symbol c = new org.teaminfty.math_dragon.view.math.Symbol();
            c.setFactor(((IInteger) expr).longValue());
            return c;
        }
        else if(expr.isFraction())
        {
            IRational rational = (IRational) expr;
            IInteger numerator = rational.getNumerator();
            long denominator = rational.getDenominator().longValue();
            // avoid equations like (x)/(1)
            if (denominator == 1) {
            	org.teaminfty.math_dragon.view.math.Symbol c = new org.teaminfty.math_dragon.view.math.Symbol();
                c.setFactor(numerator.longValue());
                return c;
            }
            return new Divide(new org.teaminfty.math_dragon.view.math.Symbol(numerator.longValue()), new org.teaminfty.math_dragon.view.math.Symbol(denominator));
        }
        else if(expr instanceof Symbol)
        {
            // We'll return a symbol
            Symbol s = (Symbol) expr;
            org.teaminfty.math_dragon.view.math.Symbol symbol = new org.teaminfty.math_dragon.view.math.Symbol(1, 0, 0, 0, null);

            // Figure out which symbol it is
            String str = s.toString().toLowerCase();
            if(str.matches("[a-df-hj-z]"))
                symbol.setVarPow(str.charAt(0), 1);
            else if(str.equals("nv"))       // The 'n' has a different name for some reason
                symbol.setVarPow('n', 1);
            else if(s.equals(F.Pi))
                symbol.setPiPow(1);
            else if(s.equals(F.E))
                symbol.setEPow(1);
            else if(s.equals(F.I))
                symbol.setIPow(1);

            // Return the symbol
            return symbol;
        }
        else if (expr instanceof IComplex) {
            IComplex c = (IComplex) expr;
            org.teaminfty.math_dragon.view.math.Symbol imag = new org.teaminfty.math_dragon.view.math.Symbol();
            imag.setFactor(1);
            org.teaminfty.math_dragon.view.math.Symbol zero = new org.teaminfty.math_dragon.view.math.Symbol();
            Expression real = toExpression(c.getRe());
            IExpr pow = c.getIm();
            // remove real part if zero
            if (real instanceof org.teaminfty.math_dragon.view.math.Symbol && ((org.teaminfty.math_dragon.view.math.Symbol) real).equals(zero)) {
                if (pow.isInteger()) {
                    imag.setIPow(((IInteger) pow).longValue());
                    return imag;
                } else if (pow.isFraction()) {
                    IFraction frac = (IFraction) pow;
                    imag.setIPow(1);
                    imag.setFactor(((org.teaminfty.math_dragon.view.math.Symbol) toOpDiv(frac.getNumerator(), frac.getDenominator())).getFactor());
                    return imag;
                } else {
                    imag.setIPow(1);
                    return new Power(imag, toExpression(pow));
                }
            } else if (pow.isInteger()) {
                imag.setIPow(((IInteger) pow).longValue());
                return new Add(real, imag);
            } else {
                imag.setIPow(1);
                return new Add(real, new Power(imag, toExpression(pow)));
            }
        }
        else if (expr.isFraction()) {
            IFraction frac = (IFraction) expr;
            return toOpDiv(frac.getNumerator(), frac.getDenominator());
        }
        throw new ParseException(expr);
    }

    /**
     * Convert a mathematical binary addition from Symja to a graphical viewer
     * that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param ast
     *        The abstract syntax tree from Symja. Usually obtained from
     *        <tt>EvalHelper.eval(MathObject)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Expression toOpAdd(AST ast) throws ParseException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            Add child = new Add(toExpression(ast.get(n - 1)), toExpression(ast.get(n)));
            for(n -= 2; n > 0; --n)
            {
                Add parent = new Add(toExpression(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
        return new Add(toExpression(ast.get(1)), toExpression(ast.get(2)));
    }

    /**
     * Convert a mathematical binary multiplication from Symja to a graphical
     * viewer that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param ast
     *        The abstract syntax tree from Symja. Usually obtained from
     *        <tt>EvalHelper.eval(MathObject)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Expression toOpMul(AST ast) throws ParseException
    {
        if (ast.size() > 3) {
            int n = ast.size() - 1;
            Multiply child = new Multiply(toExpression(ast.get(n - 1)), toExpression(ast.get(n)));
            for (n -= 2; n > 0; --n) {
                Multiply parent = new Multiply(toExpression(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
        /*IExpr l = ast.get(1);
        if (l.isFraction()) {
            IRational rational = (IRational) l;
            org.teaminfty.math_dragon.view.math.Symbol numerator = new org.teaminfty.math_dragon.view.math.Symbol(rational.getNumerator().longValue());
            Expression denominator = toExpression(ast.get(2));
            if (denominator instanceof org.teaminfty.math_dragon.view.math.Symbol) {
                org.teaminfty.math_dragon.view.math.Symbol denomSym = (org.teaminfty.math_dragon.view.math.Symbol) denominator;
                denomSym.setFactor(denomSym.getFactor() * rational.getDenominator().longValue());
                return new Divide(numerator, denomSym);
            }
            // FIXME #72 number 3 (inverted fraction)
            return new Divide(numerator, new Multiply(new org.teaminfty.math_dragon.view.math.Symbol(rational.getDenominator().longValue()), denominator));
        }*/
        IExpr r = ast.get(2);
        if(r.isPower())
        {
            AST a = (AST) r;
            IExpr p = a.get(2), b;
            if(p.isInteger())
            {
                if(p.isNegative())
                    return toOpDiv(ast.get(1), a);
                if ((b = a.get(1)) instanceof Symbol)
                {
                    Symbol s = (Symbol) b;
                    org.teaminfty.math_dragon.view.math.Symbol c = new org.teaminfty.math_dragon.view.math.Symbol();
                    c.setFactor(1);
                    if(s.equals(F.Pi))
                    {
                        c.setPiPow(((IInteger) p).longValue());
                    }
                    else if(s.equals(F.E))
                    {
                        c.setEPow(((IInteger) p).longValue());
                    }
                    else if(s.equals(F.I))
                    {
                        c.setIPow(((IInteger) p).longValue());
                        b = ast.get(1);
                        if (b.isInteger())
                        {
                            c.setFactor(((IInteger) b).longValue());
                            return c;
                        }
                        else
                        {
                            return new Multiply(toExpression(b), c);
                        }
                    }
                }
            }
        }
        Expression left = toExpression(ast.get(1));
        Expression right = toExpression(r);
        if(left instanceof org.teaminfty.math_dragon.view.math.Symbol)
        {
            org.teaminfty.math_dragon.view.math.Symbol sl = (org.teaminfty.math_dragon.view.math.Symbol) left;
            if(right instanceof org.teaminfty.math_dragon.view.math.Symbol)
            {
                org.teaminfty.math_dragon.view.math.Symbol sr = (org.teaminfty.math_dragon.view.math.Symbol) right;
                if(sl.isFactorOnly())
                {
                    sr.setFactor(sr.getFactor() * sl.getFactor());
                    return sr;
                }
                else if(sr.isFactorOnly())
                {
                    sl.setFactor(sl.getFactor() * sr.getFactor());
                    return sl;
                }
            }
        }
        return new Multiply(left, right);
    }

    static Expression toOpDiv(IExpr l, IExpr r) throws ParseException
    {
        if (r.isInteger() && ((IInteger) r).longValue() == 1)
            return toExpression(l);
        return new Divide(toExpression(l), toExpression(r));
    }

    static Expression toOpDiv(IExpr l, AST r) throws ParseException
    {
        if (r.size() > 3) {
            throw new ParseException("no more than 2 children supported for division");
        }
        // ugly hack
        r.set(2, r.get(2).negate());
        if (r.get(2).isInteger() && ((IInteger) r.get(2)).longValue() == 1)
        {
            return toOpDiv(l, r.get(1));
        }
        return new Divide(toExpression(l), toExpression(r));
    }

    /**
     * Convert a mathematical binary power from Symja to a graphical viewer that
     * contains the mathematical expression. Unknown mathematical expressions
     * result in a {@link ParseException}.
     * 
     * @param ast
     *        The abstract syntax tree from Symja. Usually obtained from
     *        <tt>EvalHelper.eval(MathObject)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Expression toOpPow(AST ast) throws ParseException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            Power child = new Power(toExpression(ast.get(n - 1)), toExpression(ast.get(n)));
            for(n -= 2; n > 0; --n)
            {
                Power parent = new Power(toExpression(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
        Expression power = toExpression(ast.get(2));
        if (power instanceof org.teaminfty.math_dragon.view.math.Symbol)
        {
        	org.teaminfty.math_dragon.view.math.Symbol s = (org.teaminfty.math_dragon.view.math.Symbol) power;
        	if (s.getFactor() < 0)
        	{
        		s.setFactor(-s.getFactor());
        		return new Divide(new org.teaminfty.math_dragon.view.math.Symbol(1),
        		                  new Power(toExpression(ast.get(1)), s));
        	}
        } else if (power instanceof Divide)
        {
            Divide div = (Divide) power;
            if (div.getLeft() instanceof org.teaminfty.math_dragon.view.math.Symbol)
            {
                org.teaminfty.math_dragon.view.math.Symbol numerator = (org.teaminfty.math_dragon.view.math.Symbol) div.getLeft();
                if (numerator.isFactorOnly())
                {
                    if (numerator.getFactor() == 1)
                        return new Root(toExpression(ast.get(1)), div.getRight());// x^(1/n) -> root(x,n)
                    return new Root(toExpression(ast.get(1)), new Divide(div.getRight(), numerator));
                }
            }
        }
        return new Power(toExpression(ast.get(1)), power);
    }
    
    /**
     * Convert a mathematical function (currently that only applies for
     * trigonometric functions) from symja to a graphical viewer contains the
     * mathematical expression. Unknown mathematical expressions result in a
     * {@link ParseException}.
     * 
     * @param ast
     *        The abstract syntax tree from Symja. Usually obtained from
     *        <tt>EvalHelper.eval(MathObject)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Expression toOpFunction(IAST ast) throws ParseException
    {
        if (ast.isSin())
            return new Function(FunctionType.SIN, toExpression(ast.get(1)));
        if (ast.isCos())
            return new Function(FunctionType.COS, toExpression(ast.get(1)));
        if (ast.isTan())
            return new Function(FunctionType.TAN, toExpression(ast.get(1)));
        if (ast.isSinh())
            return new Function(FunctionType.SINH, toExpression(ast.get(1)));
        if (ast.isCosh())
            return new Function(FunctionType.COSH, toExpression(ast.get(1)));
        if (ast.isArcSin())
            return new Function(FunctionType.ARCSIN, toExpression(ast.get(1)));
        if (ast.isArcCos())
            return new Function(FunctionType.ARCCOS, toExpression(ast.get(1)));
        if (ast.isLog())
            return new Function(FunctionType.LN, toExpression(ast.get(1)));
        throw new ParseException(ast);
    }
}
