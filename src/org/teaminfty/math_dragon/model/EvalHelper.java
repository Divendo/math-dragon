package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Parentheses;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Derivative;
import org.teaminfty.math_dragon.view.math.operation.Function;
import org.teaminfty.math_dragon.view.math.operation.Integral;
import org.teaminfty.math_dragon.view.math.operation.Limit;
import org.teaminfty.math_dragon.view.math.operation.Binary;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;

/**
 * Mathematical evaluator for {@link Expression}s into expressions returned by the Symja library.
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
    public static IExpr eval(Expression o) throws MathException
    {
        if(o == null)
            throw new NullPointerException("o");
        if(o instanceof Binary)
        {
            Binary op = (Binary) o;
            if(op instanceof Add)
                return add((Add) op);
            if(op instanceof Subtract)
                return sub((Subtract) op);
            if(op instanceof Multiply)
                return mul((Multiply) op);
            if(op instanceof Divide)
                return div((Divide) op);
            if(op instanceof Power)
                return pow((Power) op);
            if(op instanceof Root)
                return root((Root) op);
            if(op instanceof Derivative)
                return derivative((Derivative) op);
        }
        else if(o instanceof Limit)
            return limit((Limit) o);
        else if(o instanceof Function)
            return function((Function) o);
        else if(o instanceof Integral)
            return integral((Integral) o);
        else if(o instanceof Symbol)
            return symbol((Symbol) o);
        else if(o instanceof Parentheses)
            return eval(o.getChild(0));
        else if(o instanceof Empty)
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
    static void checkChildren(Binary op) throws EmptyChildException
    {
        if(op.getLeft() == null || op.getLeft() instanceof Empty)
            throw new EmptyChildException(0);
        if(op.getRight() == null || op.getRight() instanceof Empty)
            throw new EmptyChildException(1);
    }
    
    /**
     * Ensure all children are valid. An {@link EmptyChildException} is thrown
     * when at least one child equals <tt>null</tt>.
     * 
     * @param op
     *        The mathematical operation.
     * @throws EmptyChildException
     *         Thrown when one or more children are invalid.
     */
    static void checkChildren(Operation op) throws EmptyChildException
    {
        for(int i = 0; i < op.getChildCount(); ++i)
        {
            if(op.getChild(i) == null || op.getChild(i) instanceof Empty)
                throw new EmptyChildException(i);
        }
    }

    /** Variable lookup table */
    private final static ISymbol[] SYMBOLS = new ISymbol[] {F.a, F.b, F.c, F.d,
            F.e, F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q,
            F.r, F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};

    public static IExpr symbol(Symbol symbol)
    {
        if(symbol == null)
            throw new NullPointerException("symbol");
        IExpr result = F.ZZ(symbol.getFactor());
        if(symbol.getFactor() == 0)
            return result;
        
        boolean justOne = symbol.getFactor() == 1;

        // Add the constants and their powers
        long pow;
        if((pow = symbol.getPiPow()) != 0) {
            result = justOne ? (pow != 1 ? F.Power(F.Pi, pow) : F.Pi) : F.Times(result, pow != 1 ? F.Power(F.Pi, pow) : F.Pi);
            justOne = false;
        }
        if((pow = symbol.getEPow()) != 0) {
            result = justOne ? (pow != 1 ? F.Power(F.E, pow) : F.E) : F.Times(result, pow != 1 ? F.Power(F.E, pow) : F.E);
            justOne = false;
        }
        if((pow = symbol.getIPow()) != 0) {
            result = justOne ? (pow != 1 ? F.Power(F.I, pow) : F.I) : F.Times(result, pow != 1 ? F.Power(F.I, pow) : F.I);
            justOne = false;
        }

        // Add the variables
        for(int i = 0; i < symbol.varPowCount(); i++)
        {
            if(symbol.getVarPow(i) != 0)
                result = F.Times(result, F.Power(SYMBOLS[i], symbol.getVarPow(i)));
        }

        // Return the result
        return result;
    }

    /**
     * Evaluate mathematical addition using specified argument.
     * 
     * @param add
     *        The mathematical addition.
     * @return Converted mathematical unary addition for Symja.
     * @throws MathException
     *         Thrown when <tt>add</tt> contains invalid children.
     */
    public static IExpr add(Add add) throws MathException
    {
        checkChildren(add);
        return F.Plus(eval(add.getLeft()), eval(add.getRight()));
    }

    /**
     * Evaluate mathematical division using specified argument.
     * 
     * @param div
     *        The mathematical division.
     * @return Converted mathematical unary division for Symja.
     * @throws MathException
     *         Thrown when <tt>div</tt> contains invalid children.
     */
    public static IExpr div(Divide div) throws MathException
    {
        checkChildren(div);
        return F.Divide(eval(div.getLeft()), eval(div.getRight()));
    }

    /**
     * Evaluate mathematical multiplication using specified argument.
     * 
     * @param mul
     *        The mathematical multiplication.
     * @return Converted mathematical unary multiplication for Symja.
     * @throws MathException
     *         Thrown when <tt>mul</tt> contains invalid children.
     */
    public static IExpr mul(Multiply mul) throws MathException
    {
        checkChildren(mul);
        return F.Times(eval(mul.getLeft()), eval(mul.getRight()));
    }

    /**
     * Evaluate mathematical power using specified argument.
     * 
     * @param pow
     *        The mathematical power.
     * @return Converted mathematical unary power for Symja.
     * @throws MathException
     *         Thrown when <tt>pow</tt> contains invalid children.
     */
    public static IExpr pow(Power pow) throws MathException
    {
        checkChildren(pow);
        return F.Power(eval(pow.getBase()), eval(pow.getExponent()));
    }

    /**
     * Evaluate mathematical root using specified argument.
     * 
     * @param root
     *        The mathematical root.
     * @return Converted mathematical unary root for Symja.
     * @throws MathException
     *         Thrown when <tt>root</tt> contains invalid children.
     */
    public static IExpr root(Root root) throws MathException
    {
        if (!root.isCompleted())
            throw new EmptyChildException(1);
        return F.Power(eval(root.getBase()), F.Divide(F.ZZ(1), root.getExponent() instanceof Empty ? F.ZZ(2) : eval(root.getExponent())));
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
    public static IExpr sub(Subtract sub) throws MathException
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
    public static IExpr derivative(Derivative ddx) throws MathException
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
    public static IExpr function(Function f) throws MathException
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
    
    public static IExpr limit(Limit lim) throws MathException
    {
        checkChildren(lim);
        // try {
        // EvalEngine eng = new EvalEngine();
        // IExpr result = eng.parse("Limit[x,x->34]");
        // System.out.println(result);
        // } catch (Throwable t) {
        // }
        // IExpr result = F.Limit(eval(lim.getExpression()),
        // F.Rule(eval(lim.getStart()), eval(lim.getEnd())));
        // System.out.println(result);
        return F.Limit(eval(lim.getExpression()), F.Rule(eval(lim.getStart()), eval(lim.getEnd())));
        //return temporary.parse("Limit[(" + eval(lim.getExpression()) + "),(" + eval(lim.getStart()) + ")->(" + eval(lim.getEnd()) + ")]");
        // [5], symbol=limit, symbol=x, rule=x->34, null, null
        // [5], symbol=limit, ast=1*x^1, ast=rule=1*a^1->34
        // return F.Limit(eval(lim.getExpression()), F.unary(F.$p(F.SymbolHead,
        // eval(lim.getStart())), eval(lim.getEnd())));
        // return F.Limit(F.Rule(eval(lim.getStart()), eval(lim.getEnd())),
        // eval(lim.getExpression()));
        /*
         * try { Field f = Limit.class.getDeclaredField("RULES");
         * f.setAccessible(true); Class<?> t = f.getType();
         * System.out.println("type: " + t.getCanonicalName());
         * System.out.println(((IAST) f.get(Limit.class)).toString()); }
         * catch(Exception e) { e.printStackTrace(); } evaluates to: 01-08
         * 09:32:32.097: I/System.out(2206): type:
         * org.matheclipse.core.interfaces.IAST 01-08 09:32:32.121:
         * I/System.out(2206): {null, null,
         * Limit[(x_^(-1)+1)^x_,x_symbol->Infinity]=E,
         * Limit[(-x_^(-1)+1)^x_,x_symbol->Infinity]=E^(-1)}
         * 
         * Limit[(x_^(-1)+1)^x_,x_symbol->Infinity]=E
         * 
         * F.Limit(F.Power(F.Plus(F.C1, F.Power(F.$p(F.x), F.CN1)), F.$p(F.x)),
         * F.Rule(F.$p(F.x, F.SymbolHead), F.CInfinity)) ((1) + ((x) ^ (-1))) ^
         * (x), ((x) -> (Infinity))
         */
    }
    
    /**
     * Evaluate mathematical integral using specified argument.
     * 
     * @param i The mathematical integral.
     * @return Converted mathematical integral for Symja
     * @throws MathException
     *         Thrown when <tt>i</tt> contains invalid children
     */
    public static IExpr integral(Integral i) throws MathException
    {
        // Check for empty children that are never allowed to be empty
        if(i.getIntegratePart() instanceof Empty)
            throw new EmptyChildException(0);
        else if(i.getIntegrateOver() instanceof Empty)
            throw new EmptyChildException(1);
        
        // Evaluate depending on whether or not a 'from' and 'to' value are given
        if(i.getIntegrateFrom() instanceof Empty && i.getIntegrateTo() instanceof Empty)
            return F.Integrate(eval(i.getIntegratePart()), eval(i.getIntegrateOver()));
        else
        {
            if(i.getIntegrateFrom() instanceof Empty)
                throw new EmptyChildException(2);
            else if(i.getIntegrateTo() instanceof Empty)
                throw new EmptyChildException(3);
            
            return F.Integrate( eval(i.getIntegratePart()), F.List(eval(i.getIntegrateOver()), eval(i.getIntegrateFrom()), eval(i.getIntegrateTo())) );
        }
    }
}
