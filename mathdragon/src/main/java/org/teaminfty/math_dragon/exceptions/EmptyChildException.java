package org.teaminfty.math_dragon.exceptions;

import org.teaminfty.math_dragon.view.math.Expression;

/** An exception that is thrown when a {@link Expression} is being evaluated but
 * an empty child where no empty child is allowed
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 */
public class EmptyChildException extends MathException
{
    private static final long serialVersionUID = 5101446079156444420L;

    /** Default constructor, constructs a default message */
    public EmptyChildException()
    { super("An empty child was found in a class where no empty child is allowed."); }

    /** Constructor, constructs a default message
     * @param index The index of the child that is empty */
    public EmptyChildException(int index)
    { super("An empty child was found in a class at index " + index + ", no empty child is allowed there."); }

    /** Constructor, constructs an exception with the given message
     * @param msg The message that the exception should be constructed with */
    public EmptyChildException(String msg)
    { super(msg); }
}
