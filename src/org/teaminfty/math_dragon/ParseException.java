package org.teaminfty.math_dragon;

/**
 * Thrown when <tt>ModelHelper</tt> couldn't parse a <tt>MathObject</tt> from
 * an <tt>IExpr</tt> or vice versa.
 * @author Folkert van Verseveld
 *
 */

public class ParseException extends MathException {
	private static final long serialVersionUID = -5061683875213406313L;

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
