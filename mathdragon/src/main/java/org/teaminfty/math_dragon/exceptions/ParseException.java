package org.teaminfty.math_dragon.exceptions;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Thrown when <tt>ModelHelper</tt> couldn't parse a <tt>MathObject</tt> from an
 * <tt>IExpr</tt> or when <tt>EvalHelper</tt> couldn't parse a <tt>IExpr</tt>
 * from a <tt>MathObject</tt>.
 * 
 * @author Folkert van Verseveld
 * 
 */

public class ParseException extends MathException
{
    private static final long serialVersionUID = -5061683875213406313L;

    // note: javadoc copy-pasted from exception, because {@inheritDoc} doesn't
    // work for some reason
    /**
     * Constructs a new parse exception with <code>null</code> as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ParseException()
    {}

    /**
     * Constructs a new parse exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     * 
     * @param message
     *        the detail message. The detail message is saved for later
     *        retrieval by the {@link #getMessage()} method.
     */
    public ParseException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new parse exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for exceptions that are little more than wrappers
     * for other throwables (for example,
     * {@link java.security.PrivilegedActionException}).
     * 
     * @param cause
     *        the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method). (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public ParseException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Construct a new parse exception with the specified cause and a detail
     * message that describes the error.
     * 
     * @param message
     *        The detailed human-readable message.
     * @param cause
     *        Some evil demon that wants to crash our program.
     */
    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Construct a new parse exception with the specified abstract syntax tree
     * that caused this exception to be thrown.
     * 
     * @param ast
     *        The abstract syntax tree from Symja.
     */
    public ParseException(AST ast)
    {
        super(ast.toString() + ": invalid operation");
    }

    /**
     * Construct a new parse exception with the specified mathematical
     * expression that caused this exception to be thrown.
     * 
     * @param expr
     *        The mathematical expression from Symja.
     */
    public ParseException(IExpr expr)
    {
        super(expr.toString() + ": invalid operation");
    }
}
