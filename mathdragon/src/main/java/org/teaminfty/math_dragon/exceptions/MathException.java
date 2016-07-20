package org.teaminfty.math_dragon.exceptions;


/**
 * Thrown to indicate a mathematical evaluation/operation/assertion has failed.
 * 
 * @author Folkert van Verseveld
 * @see NotConstantException
 */

public class MathException extends Exception
{
	private static final long serialVersionUID = 3030104203573345473L;

	// note: javadoc copy-pasted from exception, because {@inheritDoc} doesn't
	// work for some reason
	/**
	 * Constructs a new mathematical exception with <code>null</code> as its
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause}.
	 */
	public MathException()
	{
	}

	/**
	 * Constructs a new mathematical exception with the specified detail
	 * message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public MathException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a new mathematical exception with the specified cause and a
	 * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for exceptions that are little more than wrappers
	 * for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since 1.4
	 */
	public MathException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct a new mathematical exception with the specified cause and a
	 * detail message that describes the error.
	 * 
	 * @param message
	 *            The detailed human-readable message.
	 * @param cause
	 *            Some evil demon that wants to crash our program.
	 */
	public MathException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
