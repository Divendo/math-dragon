package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.MathBinaryOperation;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathObjectSinoid;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationArcCos;
import org.teaminfty.math_dragon.view.math.MathOperationArcSine;
import org.teaminfty.math_dragon.view.math.MathOperationArcTangent;
import org.teaminfty.math_dragon.view.math.MathOperationCosh;
import org.teaminfty.math_dragon.view.math.MathOperationCosine;
import org.teaminfty.math_dragon.view.math.MathOperationDerivative;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;
import org.teaminfty.math_dragon.view.math.MathOperationSine;
import org.teaminfty.math_dragon.view.math.MathOperationSinh;
import org.teaminfty.math_dragon.view.math.MathOperationSubtract;
import org.teaminfty.math_dragon.view.math.MathOperationTangent;
import org.teaminfty.math_dragon.view.math.MathParentheses;

/**
 * Mathematical evaluator for {@link MathObject}s into expressions returned by
 * the symja library.
 * @author Folkert van Verseveld
 */
public class EvalHelper {
    private EvalHelper() {}
    
    /**
     * Convert the mathematical object to an expression returned by the symja
     * library. Never returns <tt>null</tt>. On failure, a
     * {@link MathException} is thrown.
     * @param o The mathematical object. May not be <tt>null</tt>.
     * @return The mathematical expression returned by symja.
     * @throws MathException Thrown when it could not be converted.
     */
    public static IExpr eval(MathObject o) throws MathException {
        if (o == null)
            throw new NullPointerException("o");
        if (o instanceof MathBinaryOperation) {
            MathBinaryOperation op = (MathBinaryOperation) o;
            if (op instanceof MathOperationAdd)
                return add((MathOperationAdd) op);
            if (op instanceof MathOperationSubtract)
                return sub((MathOperationSubtract) op);
            if (op instanceof MathOperationMultiply)
                return mul((MathOperationMultiply) op);
            if (op instanceof MathOperationDivide)
                return div((MathOperationDivide) op);
            if (op instanceof MathOperationPower)
                return pow((MathOperationPower) op);
            if (op instanceof MathOperationRoot)
                return root((MathOperationRoot) op);
            if (op instanceof MathOperationDerivative)
                return derivative((MathOperationDerivative) op);
        }
        else if(o instanceof MathObjectSinoid)
        {
            MathObjectSinoid sinusoid = (MathObjectSinoid) o;
            
            if (sinusoid instanceof MathOperationArcCos)
                return arccos((MathOperationArcCos) sinusoid);
            if (sinusoid instanceof MathOperationArcSine)
                return arcsin((MathOperationArcSine) sinusoid);
            if (sinusoid instanceof MathOperationArcTangent)
                return arctan((MathOperationArcTangent) sinusoid);
            if (sinusoid instanceof MathOperationCosine)
                return cos((MathOperationCosine) sinusoid);
            if (sinusoid instanceof MathOperationSine)
                return sin((MathOperationSine) sinusoid);
            if (sinusoid instanceof MathOperationTangent)
                return tan((MathOperationTangent) sinusoid);
            if (sinusoid instanceof MathOperationCosh)
                return cosh((MathOperationCosh) sinusoid);
            if (sinusoid instanceof MathOperationSinh)
                return sinh((MathOperationSinh) sinusoid);
        }
        else if(o instanceof MathConstant)
            return symbol((MathConstant) o);
        else if(o instanceof MathParentheses)
            return eval(o.getChild(0));
        else if(o instanceof MathObjectEmpty)
            throw new EmptyChildException();
        
        throw new ParseException(o.toString());
    }
    
    /**
     * Ensure both children are valid. An {@link EmptyChildException} is thrown
     * when at least one child equals <tt>null</tt>.
     * @param op The binary operation.
     * @throws EmptyChildException Thrown when one or more children are
     * invalid.
     */
    static void checkChildren(MathBinaryOperation op) throws EmptyChildException {
        if(op.getLeft() instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        if(op.getRight() instanceof MathObjectEmpty)
            throw new EmptyChildException(1);
    }
    
    public static IExpr symbol(MathConstant c) {
        if (c == null)
            throw new NullPointerException("c");
        IExpr result = F.ZZ(c.getFactor());
        if(c.getFactor() == 0) return result;

        // Add the other constants and their powers
        if(c.getPiPow() != 0)
            result = F.Times(result, F.Power(F.Pi, c.getPiPow()));
        if(c.getEPow() != 0)
            result = F.Times(result, F.Power(F.E, c.getEPow()));
        if(c.getIPow() != 0)
            result = F.Times(result, F.Power(F.I, c.getIPow()));
        
        // Return the result
        return result;
    }
    
    public static IExpr add(MathOperationAdd add) throws MathException {
        checkChildren(add);
        return F.Plus(eval(add.getLeft()), eval(add.getRight()));
    }
    
    public static IExpr div(MathOperationDivide div) throws MathException {
        checkChildren(div);
        return F.Divide(eval(div.getLeft()), eval(div.getRight()));
    }
    
    public static IExpr mul(MathOperationMultiply mul) throws MathException {
        checkChildren(mul);
        return F.Times(eval(mul.getLeft()), eval(mul.getRight()));
    }
    
    public static IExpr pow(MathOperationPower pow) throws MathException {
        checkChildren(pow);
        return F.Power(eval(pow.getBase()), eval(pow.getExponent()));
    }
    
    public static IExpr root(MathOperationRoot root) throws MathException {
        checkChildren(root);
        return F.Power(eval(root.getExponent()), F.Divide(F.ZZ(1), eval(root.getBase())));
    }
    
    public static IExpr sub(MathOperationSubtract sub) throws MathException {
        checkChildren(sub);
        return F.Subtract(eval(sub.getLeft()), eval(sub.getRight()));
    }
    
    public static IExpr derivative(MathOperationDerivative ddx) throws MathException {
        checkChildren(ddx);
        return F.D(eval(ddx.getLeft()), eval(ddx.getRight()));
    }
    
    // Sinusoids
    public static IExpr arccos(MathOperationArcCos f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.ArcCos(eval(f.getChild(0)));
    }
    public static IExpr arcsin(MathOperationArcSine f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.ArcSin(eval(f.getChild(0)));
    }
    public static IExpr arctan(MathOperationArcTangent f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.ArcTan(eval(f.getChild(0)));
    }
    public static IExpr cos(MathOperationCosine f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.Cos(eval(f.getChild(0)));
    }
    public static IExpr sin(MathOperationSine f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.Sin(eval(f.getChild(0)));
    }
    public static IExpr tan(MathOperationTangent f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.Tan(eval(f.getChild(0)));
    }
    public static IExpr cosh(MathOperationCosh f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.Cosh(eval(f.getChild(0)));
    }
    public static IExpr sinh(MathOperationSinh f) throws MathException {
        if(f.getChild(0) instanceof MathObjectEmpty)
            throw new EmptyChildException(0);
        return F.Sinh(eval(f.getChild(0)));
    }
}
