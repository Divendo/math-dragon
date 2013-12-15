package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p>
 * <h1>Reporting issues</h1>
 * Use our repo to report issues and show how to reproduce incorrect output.
 * <p>
 * <b>Note:</b> your eyes may start to bleed when you try to read this code. So,
 * keep your hands of this code and let me do da hacks for ya.
 * 
 * @author Folkert van Verseveld
 * 
 */
public final class ModelHelper
{
    /**
     * Convert a mathematical expression from symja to a graphical viewer that
     * contains the mathematical expression.
     * 
     * @param expr
     *        The mathematical expression from symja. Usually obtained from
     *        <tt>MathObject.eval()</tt>.
     * @param w
     *        The specified width.
     * @param h
     *        The specified height.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws MathException
     *         Thrown when conversion is impossible.
     */
    public static MathObject toMathObject(IExpr expr, int w, int h) throws MathException
    {
        if(expr.isAST())
        {
            AST ast = (AST) expr;
            if(!(ast.get(0) instanceof Symbol))
                throw new MathException(ast.toString() + ": invalid operation");
            if(expr.isPlus())
                return toOpAdd(ast, w, h);
            if(expr.isTimes())
                return toOpMul(ast, w, h);
            if(expr.isPower())
                return toOpPow(ast, w, h);
        }
        else if(expr.isInteger())
        {
            MathConstant c = new MathConstant(w, h);
            c.setFactor(((IInteger) expr).longValue());
            return c;
        }
        else if(expr.isFraction())
        {
            IRational rational = (IRational) expr;
            return new MathOperationDivide(new MathConstant(Long.toString(rational.getNumerator().longValue()), w, h), new MathConstant(Long.toString(rational.getDenominator().longValue()), w, h), w, h);
        }
        else if(expr instanceof Symbol)
        {
            Symbol s = (Symbol) expr;
            MathConstant c = new MathConstant(w, h);
            c.setFactor(1);
            if(s.equals(F.Pi))
            {
                c.setPiPow(1);
                return c;
            }
            else if(s.equals(F.E))
            {
                c.setEPow(1);
                return c;
            }
            else if(s.equals(F.I))
            {
                c.setIPow(1);
                return c;
            }
        }
        throw new ParseException();
    }

    static MathObject toOpAdd(AST ast, int w, int h) throws MathException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            MathOperationAdd child = new MathOperationAdd(toMathObject(ast.get(n - 1), w, h), toMathObject(ast.get(n), w, h), w, h);
            for(n -= 2; n > 0; --n)
            {
                MathOperationAdd parent = new MathOperationAdd(toMathObject(ast.get(n), w, h), child, w, h);
                child = parent;
            }
            return child;
        }
        return new MathOperationAdd(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h), w, h);
    }

    static MathObject toOpMul(AST ast, int w, int h) throws MathException
    {
        if (ast.size() > 3) {
            int n = ast.size() - 1;
            MathOperationMultiply child = new MathOperationMultiply(toMathObject(ast.get(n - 1), w, h), toMathObject(ast.get(n), w, h), w, h);
            for (n -= 2; n > 0; --n) {
                MathOperationMultiply parent = new MathOperationMultiply(toMathObject(ast.get(n), w, h), child, w, h);
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
                    return toOpDiv(ast.get(1), a, w, h);
                if((b = a.get(1)) instanceof Symbol)
                {
                    Symbol s = (Symbol) b;
                    MathConstant c = new MathConstant(w, h);
                    c.setFactor(1);
                    if(s.equals(F.Pi))
                    {
                        c.setPiPow(((IInteger) p).longValue());
                        return c;
                    }
                    else if(s.equals(F.E))
                    {
                        c.setEPow(((IInteger) p).longValue());
                        return c;
                    }
                    else if(s.equals(F.I))
                    {
                        c.setIPow(((IInteger) p).longValue());
                        return c;
                    }
                }
            }
        }
        return new MathOperationMultiply(toMathObject(ast.get(1), w, h), toMathObject(r, w, h), w, h);
    }

    // TODO implement more than 2 children for operation divide
    static MathObject toOpDiv(IExpr l, IExpr r, int w, int h) throws MathException
    {
        return new MathOperationDivide(toMathObject(l, w, h), toMathObject(r, w, h), w, h);
    }
    
    static MathObject toOpDiv(IExpr l, AST r, int w, int h) throws MathException
    {
        if (r.size() > 3) {
            throw new MathException("no more than 2 children supported for division");
        }
        // XXX ugly hack
        r.set(2, r.get(2).negate());
        if (r.get(2).isInteger() && ((IInteger) r.get(2)).longValue() == 1)
        {
            return toOpDiv(l, r.get(1), w, h);
        }
        return new MathOperationDivide(toMathObject(l, w, h), toMathObject(r, w, h), w, h);
    }

    static MathObject toOpPow(AST ast, int w, int h) throws MathException
    {
        if(ast.size() > 3)
        {
            int n = ast.size() - 1;
            MathOperationPower child = new MathOperationPower(toMathObject(ast.get(n - 1), w, h), toMathObject(ast.get(n), w, h), w, h);
            for(n -= 2; n > 0; --n)
            {
                MathOperationPower parent = new MathOperationPower(toMathObject(ast.get(n), w, h), child, w, h);
                child = parent;
            }
            return child;
        }
        return new MathOperationPower(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h), w, h);
    }
}
