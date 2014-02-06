package org.teaminfty.math_dragon.exceptions;

import org.teaminfty.math_dragon.view.math.Expression;

/**
 * An exception that is thrown when a {@link Expression} is expected to be
 * constant but it is not
 */
public class NotConstantException extends MathException
{
    private static final long serialVersionUID = 9095526946506225182L;

    /** Default constructor, constructs a default message */
    public NotConstantException()
    {
        super("value isn't a constant value.");
    }

    /**
     * Constructor, constructs an exception with the given message
     * 
     * @param msg
     *        The message that the exception should be constructed with
     */
    public NotConstantException(String msg)
    {
        super(msg);
    }
}
