package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p><b>Note:</b> your eyes may start to bleed when you try to read this code.
 * So, keep your hands of this code and let me do da hacks for ya.
 * @author Folkert van Verseveld
 *
 */

public final class ModelHelper {
	static MathObject toOpAdd(AST ast, int w, int h) throws MathException {
		return new MathOperationAdd(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h), w, h);
	}
	
	static MathObject toOpMul(AST ast, int w, int h) throws MathException {
		return new MathOperationMultiply(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h), w, h);
	}
	
	static MathObject toOpDiv(AST ast, int w, int h) throws MathException {
		MathOperationDivide d = new MathOperationDivide(w, h);
		d.set(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h));
		return d;
	}
	
	static MathObject toOpPow(AST ast, int w, int h) throws MathException {
		MathOperationPower p = new MathOperationPower(w, h);
		p.set(toMathObject(ast.get(1), w, h), toMathObject(ast.get(2), w, h));
		return p;
	}
	
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
	public static MathObject toMathObject(IExpr expr, int w, int h) throws MathException {
		if (expr.isAST()) {
			AST ast = (AST) expr;
			if (expr.isPlus()) {
				return toOpAdd(ast, w, h);
			} else if (expr.isTimes()) {
				return toOpMul(ast, w, h);
			} else if (expr.isPower()) {
				return toOpPow(ast, w, h);
			}
		} else if (expr.isInteger()) {
			MathConstant c = new MathConstant(w, h);
			c.factor = ((IInteger) expr).longValue();
			return c;
		}
		throw new ParseException();
	}
}
