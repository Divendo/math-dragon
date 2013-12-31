package org.teaminfty.math_dragon.model;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathVariable;

/**
 * Hack helper class that communicates as a wrapper between our model and the
 * symja library.
 * <p>
 * <h1>Reporting issues</h1> Use our repo to report issues and show how to
 * reproduce incorrect output.
 * 
 * @author Folkert van Verseveld
 */
public final class ModelHelper {
	/**
	 * Convert a mathematical expression from symja to a graphical viewer that
	 * contains the mathematical expression. Unknown mathematical expressions
	 * result in a {@link ParseException}.
	 * 
	 * @param expr
	 *            The mathematical expression from symja. Usually obtained from
	 *            <tt>MathObject.eval()</tt>.
	 * @param w
	 *            The specified width.
	 * @param h
	 *            The specified height.
	 * @return A viewer that contains <tt>expr</tt>.
	 * @throws ParseException
	 *             Thrown when conversion is impossible.
	 */
	public static MathObject toMathObject(IExpr expr) throws ParseException {
		if (expr.isAST()) {
			AST ast = (AST) expr;
			if (!(ast.get(0) instanceof Symbol))
				throw new ParseException(ast.toString() + ": invalid operation");
			if (expr.isPlus())
				return toOpAdd(ast);
			if (expr.isTimes())
				return toOpMul(ast);
			if (expr.isPower())
				return toOpPow(ast);
		} else if (expr.isInteger()) {
			MathConstant c = new MathConstant();
			c.setFactor(((IInteger) expr).longValue());
			return c;
		} else if (expr.isFraction()) {
			IRational rational = (IRational) expr;
			return new MathOperationDivide(new MathConstant(
					Long.toString(rational.getNumerator().longValue())),
					new MathConstant(Long.toString(rational.getDenominator()
							.longValue())));
		} else if (expr instanceof Symbol) {
			Symbol s = (Symbol) expr;
			String str = s.toString();
			if (str.matches("[a-df-hj-z]")) {
				MathVariable v = new MathVariable(str);
				return v;
			}
			MathConstant c = new MathConstant();
			c.setFactor(1);
			if (s.equals(F.Pi)) {
				c.setPiPow(1);
				return c;
			} else if (s.equals(F.E)) {
				c.setEPow(1);
				return c;
			} else if (s.equals(F.I)) {
				c.setIPow(1);
				return c;
			}
		}
		throw new ParseException();
	}

	static MathObject toOpAdd(AST ast) throws ParseException {
		if (ast.size() > 3) {
			int n = ast.size() - 1;
			MathOperationAdd child = new MathOperationAdd(
					toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
			for (n -= 2; n > 0; --n) {
				MathOperationAdd parent = new MathOperationAdd(
						toMathObject(ast.get(n)), child);
				child = parent;
			}
			return child;
		}
		return new MathOperationAdd(toMathObject(ast.get(1)),
				toMathObject(ast.get(2)));
	}

	/**
	 * Parse multiplication and convert mathematical constants to MathConstant
	 * if possible. Unknown mathematical expressions result in a
	 * {@link ParseException}.
	 * 
	 * @param ast
	 *            Symja's abstract syntax tree. May not be <tt>null</tt>.
	 * @return The parsed mathematical object.
	 * @throws ParseException
	 *             Thrown when conversion is impossible.
	 */
	static MathObject toOpMul(AST ast) throws ParseException {
		if (ast.size() > 3) {
			int n = ast.size() - 1;
			MathOperationMultiply child = new MathOperationMultiply(
					toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
			for (n -= 2; n > 0; --n) {
				MathOperationMultiply parent = new MathOperationMultiply(
						toMathObject(ast.get(n)), child);
				child = parent;
			}
			return child;
		}
		IExpr r = ast.get(2);
		if (r.isPower()) {
			AST a = (AST) r;
			IExpr p = a.get(2), b;
			if (p.isInteger()) {
				if (p.isNegative())
					return toOpDiv(ast.get(1), a);
				if ((b = a.get(1)) instanceof Symbol) {
					Symbol s = (Symbol) b;
					MathConstant c = new MathConstant();
					c.setFactor(1);
					if (s.equals(F.Pi))
						c.setPiPow(((IInteger) p).longValue());
					else if (s.equals(F.E))
						c.setEPow(((IInteger) p).longValue());
					else if (s.equals(F.I))
						c.setIPow(((IInteger) p).longValue());
					b = ast.get(1);
					if (b.isInteger()) {
						c.setFactor(((IInteger) b).longValue());
						return c;
					} else {
						return new MathOperationMultiply(toMathObject(b), c);
					}
				}
			}
		}
		return new MathOperationMultiply(toMathObject(ast.get(1)),
				toMathObject(r));
	}

	// XXX implement more than 2 children for operation divide?
	static MathObject toOpDiv(IExpr l, IExpr r) throws ParseException {
		return new MathOperationDivide(toMathObject(l), toMathObject(r));
	}

	static MathObject toOpDiv(IExpr l, AST r) throws ParseException {
		if (r.size() > 3) {
			throw new ParseException(
					"no more than 2 children supported for division");
		}
		// ugly hack
		r.set(2, r.get(2).negate());
		if (r.get(2).isInteger() && ((IInteger) r.get(2)).longValue() == 1) {
			return toOpDiv(l, r.get(1));
		}
		return new MathOperationDivide(toMathObject(l), toMathObject(r));
	}

	static MathObject toOpPow(AST ast) throws ParseException {
		if (ast.size() > 3) {
			int n = ast.size() - 1;
			MathOperationPower child = new MathOperationPower(
					toMathObject(ast.get(n - 1)), toMathObject(ast.get(n)));
			for (n -= 2; n > 0; --n) {
				MathOperationPower parent = new MathOperationPower(
						toMathObject(ast.get(n)), child);
				child = parent;
			}
			return child;
		}
		return new MathOperationPower(toMathObject(ast.get(1)),
				toMathObject(ast.get(2)));
	}
}
