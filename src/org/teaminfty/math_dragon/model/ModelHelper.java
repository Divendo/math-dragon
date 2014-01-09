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
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationFunction;
import org.teaminfty.math_dragon.view.math.MathOperationFunction.FunctionType;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathSymbol;

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
    public static MathObject toMathObject(IExpr expr) throws ParseException
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
            MathSymbol c = new MathSymbol();
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
                MathSymbol c = new MathSymbol();
                c.setFactor(numerator.longValue());
                return c;
            }
            return new MathOperationDivide(new MathSymbol(numerator.longValue()), new MathSymbol(denominator));
        }
        else if(expr instanceof Symbol)
        {
            // We'll return a symbol
            Symbol s = (Symbol) expr;
            MathSymbol symbol = new MathSymbol(1, 0, 0, 0, null);

            // Figure out which symbol it is
            String str = s.toString().toLowerCase();
            if(str.matches("[a-df-hj-z]"))
                symbol.setVarPow(str.charAt(0) - 'a', 1);
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
            MathSymbol imag = new MathSymbol();
            imag.setFactor(1);
            MathSymbol zero = new MathSymbol();
            MathObject real = toMathObject(c.getRe());
            IExpr pow = c.getIm();
            // remove real part if zero
            if (real instanceof MathSymbol && ((MathSymbol) real).equals(zero)) {
                if (pow.isInteger()) {
                    imag.setIPow(((IInteger) pow).longValue());
                    return imag;
                } else if (pow.isFraction()) {
                    IFraction frac = (IFraction) pow;
                    imag.setIPow(1);
                    imag.setFactor(((MathSymbol) toOpDiv(frac.getNumerator(), frac.getDenominator())).getFactor());
                    return imag;
                } else {
                    imag.setIPow(1);
                    return new MathOperationPower(imag, toMathObject(pow));
                }
            } else if (pow.isInteger()) {
                imag.setIPow(((IInteger) pow).longValue());
                return new MathOperationAdd(real, imag);
            } else {
                imag.setIPow(1);
                return new MathOperationAdd(real, new MathOperationPower(imag, toMathObject(pow)));
            }
        }
        else if (expr.isFraction()) {
            IFraction frac = (IFraction) expr;
            return toOpDiv(frac.getNumerator(), frac.getDenominator());
        }
        throw new ParseException(expr);
    }

    /**
     * Convert a mathematical unary addition from Symja to a graphical
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
    static MathObject toOpAdd(AST ast) throws ParseException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            MathOperationAdd child = new MathOperationAdd(toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
            for(n -= 2; n > 0; --n)
            {
                MathOperationAdd parent = new MathOperationAdd(toMathObject(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
        return new MathOperationAdd(toMathObject(ast.get(1)), toMathObject(ast.get(2)));
    }

    /**
     * Convert a mathematical unary multiplication from Symja to a graphical
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
    static MathObject toOpMul(AST ast) throws ParseException
    {
        if (ast.size() > 3) {
            int n = ast.size() - 1;
            MathOperationMultiply child = new MathOperationMultiply(toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
            for (n -= 2; n > 0; --n) {
                MathOperationMultiply parent = new MathOperationMultiply(toMathObject(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
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
                    MathSymbol c = new MathSymbol();
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
                            return new MathOperationMultiply(toMathObject(b), c);
                        }
                    }
                }
            }
        }
        return new MathOperationMultiply(toMathObject(ast.get(1)), toMathObject(r));
    }

    // XXX implement more than 2 children for operation divide?
    static MathObject toOpDiv(IExpr l, IExpr r) throws ParseException
    {
        if (r.isInteger() && ((IInteger) r).longValue() == 1)
            return toMathObject(l);
        return new MathOperationDivide(toMathObject(l), toMathObject(r));
    }

    static MathObject toOpDiv(IExpr l, AST r) throws ParseException
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
        return new MathOperationDivide(toMathObject(l), toMathObject(r));
    }

    /**
     * Convert a mathematical unary power from Symja to a graphical viewer that
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
    static MathObject toOpPow(AST ast) throws ParseException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            MathOperationPower child = new MathOperationPower(toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
            for(n -= 2; n > 0; --n)
            {
                MathOperationPower parent = new MathOperationPower(toMathObject(ast.get(n)), child);
                child = parent;
            }
            return child;
        }
        return new MathOperationPower(toMathObject(ast.get(1)), toMathObject(ast.get(2)));
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
    static MathObject toOpFunction(IAST ast) throws ParseException
    {
        if (ast.isSin())
            return new MathOperationFunction(FunctionType.SIN, toMathObject(ast.get(1)));
        if (ast.isCos())
            return new MathOperationFunction(FunctionType.COS, toMathObject(ast.get(1)));
        if (ast.isTan())
            return new MathOperationFunction(FunctionType.TAN, toMathObject(ast.get(1)));
        if (ast.isSinh())
            return new MathOperationFunction(FunctionType.SINH, toMathObject(ast.get(1)));
        if (ast.isCosh())
            return new MathOperationFunction(FunctionType.COSH, toMathObject(ast.get(1)));
        if (ast.isArcSin())
            return new MathOperationFunction(FunctionType.ARCSIN, toMathObject(ast.get(1)));
        if (ast.isArcCos())
            return new MathOperationFunction(FunctionType.ARCCOS, toMathObject(ast.get(1)));
        if (ast.isLog())
            return new MathOperationFunction(FunctionType.LN, toMathObject(ast.get(1)));
        throw new ParseException(ast);
    }
}
