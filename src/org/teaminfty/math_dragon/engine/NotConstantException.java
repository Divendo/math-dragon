package org.teaminfty.math_dragon.engine;

/**
 * An exception that is thrown when an operation requiring a constant value is
 * requested on a {@link MathObject} while that math object doesn't evaluate
 * into a constant value.
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 */
public class NotConstantException extends MathException
{

    private static final long serialVersionUID = -8541288407746261213L;

    /**
     * Default constructor
     */
    public NotConstantException()
    {}

    /**
     * Constructor
     * 
     * @param msg
     *        A message describing the error
     */
    public NotConstantException(String msg)
    {
        super(msg);
    }

}
