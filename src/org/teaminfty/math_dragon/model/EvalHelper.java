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
import org.teaminfty.math_dragon.view.math.MathOperationFunction;
import org.teaminfty.math_dragon.view.math.MathOperationIntegral;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;
import org.teaminfty.math_dragon.view.math.MathOperationSubtract;
import org.teaminfty.math_dragon.view.math.MathParentheses;
import org.teaminfty.math_dragon.view.math.MathSymbol;

/**
 * Mathematical evaluator for {@link MathObject}s into expressions returned by the Symja library.
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
        else if(o instanceof MathOperationFunction)
            return function((MathOperationFunction) o);
        else if(o instanceof MathOperationIntegral)
            return integral((MathOperationIntegral) o);
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
    static void checkChildren(MathBinaryOperation op)
            throws EmptyChildException
    {
        if(op.getLeft() instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        if(op.getRight() instanceof MathObjectEmpty)
            throw new EmptyChildException(1);
    }

    /** Variable lookup table */
    private final static ISymbol[] SYMBOLS = new ISymbol[] {F.a, F.b, F.c, F.d,
            F.e, F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q,
            F.r, F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};

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
                result = F.Times(result,
                        F.Power(SYMBOLS[i], symbol.getVarPow(i)));
        }

        // Return the result
        return result;
    }

    /**
     * Evaluate mathematical unary addition using specified argument.
     * 
     * @param add
     *        The mathematical unary addition.
     * @return Converted mathematical unary addition for Symja.
     * @throws MathException
     *         Thrown when <tt>add</tt> contains invalid children.
     */
    public static IExpr add(MathOperationAdd add) throws MathException
    {
        checkChildren(add);
        return F.Plus(eval(add.getLeft()), eval(add.getRight()));
    }

    /**
     * Evaluate mathematical unary division using specified argument.
     * 
     * @param div
     *        The mathematical unary division.
     * @return Converted mathematical unary division for Symja.
     * @throws MathException
     *         Thrown when <tt>div</tt> contains invalid children.
     */
    public static IExpr div(MathOperationDivide div) throws MathException
    {
        checkChildren(div);
        return F.Divide(eval(div.getLeft()), eval(div.getRight()));
    }

    /**
     * Evaluate mathematical unary multiplication using specified argument.
     * 
     * @param mul
     *        The mathematical unary multiplication.
     * @return Converted mathematical unary multiplication for Symja.
     * @throws MathException
     *         Thrown when <tt>mul</tt> contains invalid children.
     */
    public static IExpr mul(MathOperationMultiply mul) throws MathException
    {
        checkChildren(mul);
        return F.Times(eval(mul.getLeft()), eval(mul.getRight()));
    }

    /**
     * Evaluate mathematical unary power using specified argument.
     * 
     * @param pow
     *        The mathematical unary power.
     * @return Converted mathematical unary power for Symja.
     * @throws MathException
     *         Thrown when <tt>pow</tt> contains invalid children.
     */
    public static IExpr pow(MathOperationPower pow) throws MathException
    {
        checkChildren(pow);
        return F.Power(eval(pow.getBase()), eval(pow.getExponent()));
    }

    /**
     * Evaluate mathematical unary root using specified argument.
     * 
     * @param root
     *        The mathematical unary root.
     * @return Converted mathematical unary root for Symja.
     * @throws MathException
     *         Thrown when <tt>root</tt> contains invalid children.
     */
    public static IExpr root(MathOperationRoot root) throws MathException
    {
        checkChildren(root);
        return F.Power(eval(root.getBase()), F.Divide(F.ZZ(1), eval(root.getExponent())));
    }

    /**
     * Evaluate mathematical unary subtraction using specified argument.
     * 
     * @param sub
     *        The mathematical unary subtraction.
     * @return Converted mathematical unary subtraction for Symja.
     * @throws MathException
     *         Thrown when <tt>sub</tt> contains invalid children.
     */
    public static IExpr sub(MathOperationSubtract sub) throws MathException
    {
        checkChildren(sub);
        return F.Subtract(eval(sub.getLeft()), eval(sub.getRight()));
    }

    /**
     * Evaluate mathematical derivation using specified argument.
     * 
     * @param ddx
     *        The mathematical derivation.
     * @return Converted mathematical derivation for Symja.
     * @throws MathException
     *         Thrown when <tt>ddx</tt> contains invalid children.
     */
    public static IExpr derivative(MathOperationDerivative ddx) throws MathException
    {
        checkChildren(ddx);
        return F.D(eval(ddx.getLeft()), eval(ddx.getRight()));
    }

    /**
     * Evaluate mathematical function using specified argument.
     * 
     * @param f The mathematical function.
     * @return Converted mathematical function for Symja
     * @throws MathException
     *         Thrown when <tt>f</tt> contains invalid children
     */
    public static IExpr function(MathOperationFunction f) throws MathException
    {
        switch(f.getType())
        {
            case ARCCOS:    return F.ArcCos(eval(f.getChild(0)));
            case ARCSIN:    return F.ArcSin(eval(f.getChild(0)));
            case ARCTAN:    return F.ArcTan(eval(f.getChild(0)));
            case COS:       return F.Cos(eval(f.getChild(0)));
            case COSH:      return F.Cosh(eval(f.getChild(0)));
            case LN:        return F.Log(eval(f.getChild(0)));
            case SIN:       return F.Sin(eval(f.getChild(0)));
            case SINH:      return F.Sinh(eval(f.getChild(0)));
            case TAN:       return F.Tan(eval(f.getChild(0)));
        }
        
        throw new ParseException(f.toString());
    }

    /**
     * Evaluate mathematical integral using specified argument.
     * 
     * @param i The mathematical integral.
     * @return Converted mathematical integral for Symja
     * @throws MathException
     *         Thrown when <tt>i</tt> contains invalid children
     */
    public static IExpr integral(MathOperationIntegral i) throws MathException
    {
        // Check for empty children that are never allowed to be empty
        if(i.getIntegratePart() instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        else if(i.getIntegrateOver() instanceof MathObjectEmpty)
            throw new EmptyChildException(1);
        
        // Evaluate depending on whether or not a 'from' and 'to' value are given
        if(i.getIntegrateFrom() instanceof MathObjectEmpty && i.getIntegrateTo() instanceof MathObjectEmpty)
            return F.Integrate(eval(i.getIntegratePart()), eval(i.getIntegrateOver()));
        else
        {
            if(i.getIntegrateFrom() instanceof MathObjectEmpty)
                throw new EmptyChildException(2);
            else if(i.getIntegrateTo() instanceof MathObjectEmpty)
                throw new EmptyChildException(3);
            
            return F.Integrate( eval(i.getIntegratePart()), F.List(eval(i.getIntegrateOver()), eval(i.getIntegrateFrom()), eval(i.getIntegrateTo())) );
        }
    }
}
