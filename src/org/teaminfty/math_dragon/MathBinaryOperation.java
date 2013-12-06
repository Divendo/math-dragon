package org.teaminfty.math_dragon;

/**
 * This class draws binary operations and provides simply functions to modify
 * both mathematical expressions.
 * @author Folkert van Verseveld
 * @see MathBinaryOperationLinear
 */

public abstract class MathBinaryOperation extends MathObject
{
    public MathBinaryOperation(int defWidth, int defHeight)
    {
        this(null, null, defWidth, defHeight);
    }

    public MathBinaryOperation(MathObject left, MathObject right, int defWidth,
            int defHeight)
    {
        super(defWidth, defHeight);
        set(left, right);
    }

    /**
     * Set mathematical expression to the left and to the right to new specified
     * mathematical objects.
     * 
     * @param left
     *        The mathematical expression to the left.
     * @param right
     *        The mathematical expression to the right.
     */
    public void set(MathObject left, MathObject right)
    {
        setChild(0, left);
        setChild(1, right);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the left.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setLeft(MathObject o)
    {
        setChild(0, o);
    }

    /**
     * Retrieve the mathematical expression to the left. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the left.
     */
    public MathObject getLeft()
    {
        return getChild(0);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the right.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setRight(MathObject o)
    {
        setChild(1, o);
    }

    /**
     * Retrieve the mathematical expression to the right. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the right.
     */
    public MathObject getRight()
    {
        return getChild(1);
    }
}
