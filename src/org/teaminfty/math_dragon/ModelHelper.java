package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p><b>Note:</b> your eyes may start to bleed when you try to read this code.
 * So, keep your hands of this code and let me do da hacks for ya.
 * @author Folkert van Verseveld
 *
 */
public final class ModelHelper
{
	/**
	 * Convert a mathematical expression from symja to a graphical viewer that
	 * contains the mathematical expression.
	 * @param expr The mathematical expression from symja. Usually obtained
	 * from <tt>MathObject.eval()</tt>.
	 * @param w The specified width.
	 * @param h The specified height.
	 * @return A viewer that contains <tt>expr</tt>.
	 * @throws MathException Thrown when conversion is impossible.
	 */
	public static MathObject toMathObject(IExpr expr, int w, int h) throws MathException
	{
		if(expr.isAST())
		{
			AST ast = (AST) expr;
			if(expr.isPlus())
			{
				return toOpAdd(ast, w, h);
			}
			else if (expr.isTimes())
			{
				return toOpMul(ast, w, h);
			}
			else if (expr.isPower())
			{
				return toOpPow(ast, w, h);
			}
		}
		else if (expr.isInteger())
		{
			MathConstant c = new MathConstant(w, h);
			c.factor = ((IInteger) expr).longValue();
			return c;
		}
		else if(expr.isFraction())
		{
		    IRational rational = (IRational) expr;
		    MathOperationDivide div = new MathOperationDivide(w, h);
		    div.setChild(0, new MathConstant(Long.toString(rational.getNumerator().longValue()), w, h));
            div.setChild(1, new MathConstant(Long.toString(rational.getDenominator().longValue()), w, h));
            return div;
		}
        else if(expr instanceof Symbol)
        {
            Symbol s = (Symbol) expr;
            MathConstant c = new MathConstant(w, h);
            c.factor = 1;
            if(s.equals(F.Pi))
            {
                c.piPow = 1;
                return c;
            }
            else if(s.equals(F.E))
            {
                c.ePow = 1;
                return c;
            }
            else if(s.equals(F.I))
            {
                c.iPow = 1;
                return c;
            }
        }
		throw new ParseException();
	}
	
	static MathObject toOpAdd(AST ast, int w, int h) throws MathException
	{
		return new MathOperationAdd(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h), w, h);
	}
	
	static MathObject toOpMul(AST ast, int w, int h) throws MathException
	{
		IExpr r = ast.get(2);
		if(r.isPower())
		{
			AST a = (AST) r;
			IExpr p = a.get(2), b;
			if(p.isInteger())
			{
				if(p.isNegative())
				{
					return toOpDiv(ast.get(1), p, w, h);
				}
				else if((b = a.get(1)) instanceof Symbol)
				{
					Symbol s = (Symbol) b;
					MathConstant c = new MathConstant(w, h);
					c.factor = 1;
					if(s.equals(F.Pi))
					{
						c.piPow = ((IInteger) p).longValue();
						return c;
					}
					else if(s.equals(F.E))
					{
						c.ePow = ((IInteger) p).longValue();
						return c;
					}
					else if(s.equals(F.I))
					{
						c.iPow = ((IInteger) p).longValue();
						return c;
					}
				}
			}
		}
		return new MathOperationMultiply(toMathObject(ast.get(1), w, h), toMathObject(r, w, h), w, h);
	}
	
	static MathObject toOpDiv(IExpr l, IExpr r, int w, int h) throws MathException
	{
		MathOperationDivide d = new MathOperationDivide(w, h);
		d.set(toMathObject(l, w, h), toMathObject(r, w, h));
		return d;
	}
	
	static MathObject toOpPow(AST ast, int w, int h) throws MathException
	{
		MathOperationPower p = new MathOperationPower(w, h);
		p.set(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h));
		return p;
	}
}
