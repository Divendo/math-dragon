package org.teaminfty.math_dragon.model;

import static org.teaminfty.math_dragon.view.math.Function.FunctionType.ARCCOS;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.ARCSIN;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.ARCTAN;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.COS;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.COSH;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.LN;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.SIN;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.SINH;
import static org.teaminfty.math_dragon.view.math.Function.FunctionType.TAN;

import java.math.BigInteger;
import java.util.Locale;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.exceptions.TooBigValueException;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Function;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p>
 * <h1>Reporting issues</h1> Use our repo to report issues and show how to
 * reproduce incorrect output.
 * 
 * @author Folkert van Verseveld
 */
public final class ModelHelper
{
    /**
     * Convert a mathematical expression from symja to a graphical viewer that
     * contains the mathematical expression. Unknown mathematical expressions
     * result in a {@link ParseException}.
     * 
     * @param expr
     *        The mathematical expression from symja. Usually obtained from
     *        <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains expr.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    public static Expression toExpression(IExpr expr) throws ParseException
    {
        if(expr == null)
            throw new NullPointerException("expr");
        if(expr.isSymbol())
            return symbol((Symbol) expr);
        if(expr.isInteger())
            return integer((IInteger) expr);
        if(expr.isRational())
            return rational((IRational) expr);
        if(expr.isComplex())
            return complex((IComplex) expr);
        else if(expr.isNumeric())
        {
            if(expr instanceof INum)
                return num((INum) expr);
            else if(expr instanceof IComplexNum)
                return complexNum((IComplexNum) expr);
        }
        if(expr.isAST())
            return toExpression((IAST) expr);
        throw new ParseException(expr);
    }

    /**
     * Convert a mathematical symbolic constant from symja to a graphical viewer
     * that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param s
     *        The mathematical symbolic constant from symja. Usually obtained
     *        from <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains s.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static org.teaminfty.math_dragon.view.math.Symbol symbol(Symbol s) throws ParseException
    {
        org.teaminfty.math_dragon.view.math.Symbol symbol = new org.teaminfty.math_dragon.view.math.Symbol(1);
        // Figure out which symbol it is
        if(s.equals(F.Pi))
            symbol.setPiPow(1);
        else if(s.equals(F.E))
            symbol.setEPow(1);
        else if(s.equals(F.I))
            symbol.setIPow(1);
        else
        {
            String str = s.toString().toLowerCase(Locale.US);
            if(str.length() == 1)
            {
                char var = str.charAt(0);
                if((var >= 'A' && var <= 'Z' && var != 'E' && var != 'I') || (var >= 'a' && var <= 'z' && var != 'e' && var != 'i'))
                    symbol.setVarPow(var, 1);
            }
            else
            {
                // Invalid variable symbol, could also indicate an invalid calculation (e.g. the result "indeterminate")
                throw new ParseException("Invalid symbol: " + s.toString());
            }
        }
        return symbol;
    }

    /**
     * Convert a mathematical numerical constant from symja to a graphical
     * viewer that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param i
     *        The mathematical numerical constant from symja. Usually obtained
     *        from <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains i.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static org.teaminfty.math_dragon.view.math.Symbol integer(IInteger i) throws ParseException
    {
        BigInteger big = i.getBigNumerator();
        if(big.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1)
            throw new TooBigValueException(true);
        else if(big.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) == -1)
            throw new TooBigValueException(false);
            
        return new org.teaminfty.math_dragon.view.math.Symbol(i.longValue());
    }

    /**
     * Convert a mathematical rational constant from symja to a graphical viewer
     * that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param rat
     *        The mathematical rational constant from symja. Usually obtained
     *        from <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains rat.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Expression rational(IRational rat) throws ParseException
    {
        Expression numerator = toExpression(rat.getNumerator());
        Expression denominator = toExpression(rat.getDenominator());
        return new Divide(numerator, denominator);
    }

    /**
     * Convert a mathematical complex constant from symja to a graphical viewer
     * that contains the mathematical expression. Unknown mathematical
     * expressions result in a {@link ParseException}.
     * 
     * @param c
     *        The mathematical complex constant from symja. Usually obtained
     *        from <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains s.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    static Add complex(IComplex c) throws ParseException
    {
        Expression real = toExpression(c.getRe());
        org.teaminfty.math_dragon.view.math.Symbol imag = new org.teaminfty.math_dragon.view.math.Symbol(1);
        imag.setIPow(1);
        Expression multiplicand = toExpression(c.getIm());
        return new Add(real, new Multiply(multiplicand, imag));
    }

    /** Convert a numeric constant from Symja to {@link org.teaminfty.math_dragon.view.math.Symbol Symbol}
     * 
     * @param expr The numeric constant from Symja
     * @return A {@link org.teaminfty.math_dragon.view.math.Symbol Symbol} with the value of <tt>expr</tt> */
    private static Expression num(INum expr)
    {
        return new org.teaminfty.math_dragon.view.math.Symbol(expr.getRealPart());
    }

    /** Convert a numeric complex constant from Symja to {@link org.teaminfty.math_dragon.view.math.Expression Expression}
     * 
     * @param expr The numeric complex constant from Symja
     * @return A {@link org.teaminfty.math_dragon.view.math.Expression Expression} with the value of <tt>expr</tt> */
    private static Expression complexNum(IComplexNum expr)
    {
        org.teaminfty.math_dragon.view.math.Symbol re = new org.teaminfty.math_dragon.view.math.Symbol(expr.getRealPart());
        org.teaminfty.math_dragon.view.math.Symbol im = new org.teaminfty.math_dragon.view.math.Symbol(expr.getImaginaryPart());
        im.setIPow(1);
        return new Add(re, im);
    }

    /**
     * Convert a mathematical expression from symja to a graphical viewer that
     * contains the mathematical expression. Unknown mathematical expressions
     * result in a {@link ParseException}.
     * 
     * @param ast
     *        The mathematical expression from symja. Usually obtained from
     *        <tt>EvalHelper.eval(Expression)</tt>.
     * @return A viewer that contains <tt>expr</tt>.
     * @throws ParseException
     *         Thrown when conversion is impossible.
     */
    public static Expression toExpression(IAST ast) throws ParseException
    {
        if(ast == null)
            throw new NullPointerException("ast");
        if(ast.isPlus())
            return add(ast);
        if(ast.isTimes())
            return mul(ast);
        if(ast.isPower())
            return pow(ast);
        return unary(ast);
    }

    /**
     * Convert a mathematical binary addition from symja's expression to a
     * graphical viewer that contains the mathematical expression. Unknown or
     * unimplemented mathematical expressions result in a {@link ParseException}
     * .
     * 
     * @param add
     *        Symja's abstract syntax tree holding the current binary addition.
     *        Usually obtained from EvalHelper.eval().
     * @return A graphical viewer that contains add
     * @throws ParseException
     *         Throw when conversion is impossible
     */
    static Add add(IAST add) throws ParseException
    {
        // If we have combined/multiple additions, we need to split them
        if(add.size() > 3)
        {
            int operandIndex = add.size() - 1;
            Add child = new Add(toExpression(add.get(operandIndex - 1)), toExpression(add.get(operandIndex)));
            for(operandIndex -= 2; operandIndex > 0; --operandIndex)
            {
                // Navigate to root element
                child = new Add(toExpression(add.get(operandIndex)), child);
            }
            return child;
        }
        // Just two operands, convert them directly.
        Expression left = toExpression(add.get(1));
        Expression right = toExpression(add.get(2));
        return new Add(left, right);
    }

    /**
     * Convert a mathematical binary multiplication from symja's expression to a
     * graphical viewer that contains the mathematical expression. Unknown or
     * unimplemented mathematical expressions result in a {@link ParseException}
     * .
     * 
     * @param mul
     *        Symja's abstract syntax tree holding the current binary
     *        multiplication. Usually obtained from EvalHelper.eval().
     * @return A graphical viewer that contains mul
     * @throws ParseException
     *         Throw when conversion is impossible
     */
    static Multiply mul(IAST mul) throws ParseException
    {
        // If we have combined/multiple additions, we need to split them
        if(mul.size() > 3)
        {
            int operandIndex = mul.size() - 1;
            Multiply child = new Multiply(toExpression(mul.get(operandIndex - 1)), toExpression(mul.get(operandIndex)));
            for(operandIndex -= 2; operandIndex > 0; --operandIndex)
            {
                // Navigate to root element
                child = new Multiply(toExpression(mul.get(operandIndex)), child);
            }
            return child;
        }
        // Just two operands, convert them directly.
        Expression multiplicand = toExpression(mul.get(1));
        Expression multiplier = toExpression(mul.get(2));
        return new Multiply(multiplicand, multiplier);
    }

    /**
     * Convert a mathematical binary power from symja's expression to a
     * graphical viewer that contains the mathematical expression. Unknown or
     * unimplemented mathematical expressions result in a {@link ParseException}
     * .
     * 
     * @param pow
     *        Symja's abstract syntax tree holding the current binary power.
     *        Usually obtained from EvalHelper.eval().
     * @return A graphical viewer that contains pow
     * @throws ParseException
     *         Throw when conversion is impossible
     */
    static Power pow(IAST pow) throws ParseException
    {
        Expression base = toExpression(pow.get(1));
        Expression exponent = toExpression(pow.get(2));
        return new Power(base, exponent);
    }

    /**
     * Convert a mathematical unary function from symja's expression to a
     * graphical viewer that contains the mathematical expression. Unknown or
     * unimplemented mathematical expressions result in a {@link ParseException}
     * .
     * 
     * @param func
     *        Symja's abstract syntax tree holding the current unary function.
     *        Usually obtained from EvalHelper.eval().
     * @return A graphical viewer that contains pow
     * @throws ParseException
     *         Throw when conversion is impossible
     */
    static Expression unary(IAST func) throws ParseException
    {
        Expression expr = toExpression(func.get(1));
        if(func.isSin())
            return new Function(SIN, expr);
        if(func.isCos())
            return new Function(COS, expr);
        if(func.isTan())
            return new Function(TAN, expr);
        if(func.isSinh())
            return new Function(SINH, expr);
        if(func.isCosh())
            return new Function(COSH, expr);
        if(func.isArcSin())
            return new Function(ARCSIN, expr);
        if(func.isArcCos())
            return new Function(ARCCOS, expr);
        if(func.isArcTan())
            return new Function(ARCTAN, expr);
        if(func.isLog())
            return new Function(LN, expr);
        // also check for incompatible results
        if(func.get(0) instanceof Symbol)
        {
           Symbol sym = (Symbol) func.get(0);
           if(sym.equals(F.Sec))
           {
               return new Divide(new org.teaminfty.math_dragon.view.math.Symbol(1), new Function(COS, expr));
           }
           if(sym.equals(F.Csc))
           {
               return new Divide(new org.teaminfty.math_dragon.view.math.Symbol(1), new Function(SIN, expr));
           }
           if(sym.equals(F.Cot))
           {
               return new Divide(new org.teaminfty.math_dragon.view.math.Symbol(1), new Function(TAN, expr));
           }
        }
        // Whoops, not supported
        throw new ParseException(func);
    }
}
