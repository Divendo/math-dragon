package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.MathBinaryOperation;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDerivative;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;
import org.teaminfty.math_dragon.view.math.MathOperationSubtract;
import org.teaminfty.math_dragon.view.math.MathParentheses;
import org.teaminfty.math_dragon.view.math.MathSymbol;

/**
 * Mathematical evaluator for {@link MathObject}s into expressions returned by
 * the symja library.
 * 
 * @author Folkert van Verseveld
 */
public class EvalHelper
{
    private EvalHelper()
    {}

    /**
     * Convert the mathematical object to an expression returned by the symja
     * library. Never returns <tt>null</tt>. On failure, a {@link MathException}
     * is thrown.
     * 
     * @param o
     *        The mathematical object. May not be <tt>null</tt>.
     * @return The mathematical expression returned by symja.
     * @throws MathException
     *         Thrown when it could not be converted.
     */
    public static IExpr eval(MathObject o) throws MathException
    {
        if(o == null)
            throw new NullPointerException("o");
        if(o instanceof MathBinaryOperation)
        {
            MathBinaryOperation op = (MathBinaryOperation) o;
            if(op instanceof MathOperationAdd)
                return add((MathOperationAdd) op);
            if(op instanceof MathOperationSubtract)
                return sub((MathOperationSubtract) op);
            if(op instanceof MathOperationMultiply)
                return mul((MathOperationMultiply) op);
            if(op instanceof MathOperationDivide)
                return div((MathOperationDivide) op);
            if(op instanceof MathOperationPower)
                return pow((MathOperationPower) op);
            if(op instanceof MathOperationRoot)
                return root((MathOperationRoot) op);
            if(op instanceof MathOperationDerivative)
                return derivative((MathOperationDerivative) op);
        }
        else if(o instanceof MathSymbol)
            return symbol((MathSymbol) o);
        else if(o instanceof MathParentheses)
            return eval(o.getChild(0));
        else if(o instanceof MathObjectEmpty)
            throw new EmptyChildException();

        throw new ParseException(o.toString());
    }

    /**
     * Ensure both children are valid. An {@link EmptyChildException} is thrown
     * when at least one child equals <tt>null</tt>.
     * 
     * @param op
     *        The binary operation.
     * @throws EmptyChildException
     *         Thrown when one or more children are invalid.
     */
    static void checkChildren(MathBinaryOperation op) throws EmptyChildException
    {
        if(op.getLeft() == null)
            throw new EmptyChildException(0);
        if(op.getRight() == null)
            throw new EmptyChildException(1);
    }

    /** Variable lookup table */
    private final static ISymbol[] SYMBOLS = new ISymbol[] {F.a, F.b, F.c, F.d, F.e,
            F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q, F.r,
            F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};
    
    public static IExpr symbol(MathSymbol symbol)
    {
        if(symbol == null)
            throw new NullPointerException("symbol");
        IExpr result = F.ZZ(symbol.getFactor());
        if(symbol.getFactor() == 0)
            return result;

        // Add the constants and their powers
        if(symbol.getPiPow() != 0)
            result = F.Times(result, F.Power(F.Pi, symbol.getPiPow()));
        if(symbol.getEPow() != 0)
            result = F.Times(result, F.Power(F.E, symbol.getEPow()));
        if(symbol.getIPow() != 0)
            result = F.Times(result, F.Power(F.I, symbol.getIPow()));
        
        // Add the variables
        for(int i = 0; i < symbol.varPowCount(); i++)
        {
            if(symbol.getVarPow(i) != 0)
                result = F.Times(result, F.Power(SYMBOLS[i], symbol.getVarPow(i)));
        }

        // Return the result
        return result;
    }

    public static IExpr add(MathOperationAdd add) throws MathException
    {
        checkChildren(add);
        return F.Plus(eval(add.getLeft()), eval(add.getRight()));
    }

    public static IExpr div(MathOperationDivide div) throws MathException
    {
        checkChildren(div);
        return F.Divide(eval(div.getLeft()), eval(div.getRight()));
    }

    public static IExpr mul(MathOperationMultiply mul) throws MathException
    {
        checkChildren(mul);
        return F.Times(eval(mul.getLeft()), eval(mul.getRight()));
    }

    public static IExpr pow(MathOperationPower pow) throws MathException
    {
        checkChildren(pow);
        return F.Power(eval(pow.getBase()), eval(pow.getExponent()));
    }

    public static IExpr root(MathOperationRoot root) throws MathException
    {
        checkChildren(root);
        return F.Power(eval(root.getExponent()), F.Divide(F.ZZ(1), eval(root.getBase())));
    }

    public static IExpr sub(MathOperationSubtract sub) throws MathException
    {
        checkChildren(sub);
        return F.Subtract(eval(sub.getLeft()), eval(sub.getRight()));
    }

    public static IExpr derivative(MathOperationDerivative ddx) throws MathException
    {
        checkChildren(ddx);
        return F.D(eval(ddx.getLeft()), eval(ddx.getRight()));
    }
}
