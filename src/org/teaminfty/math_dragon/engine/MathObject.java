package org.teaminfty.math_dragon.engine;

/**
 * Represents a mathematical object
 */
public abstract class MathObject
{

    /**
     * Returns the name of this math object
     */
    public abstract String getName();

    /**
     * Returns the amount of children this math object has
     * 
     * @return The amount of children this math object has
     */
    public int getChildrenCount()
    { return 0; }

    /**
     * Returns the child at the given index
     * 
     * @param index
     *        The index of the child that is to be retrieved
     * @return The child at the given index
     * @throws IndexOutOfBoundsException
     *         If an invalid index is given
     */
    public abstract MathObject getChild(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the child at the given index to the given MathObject
     * 
     * @param index
     *        The index of the child that should be set
     * @param child
     *        The child that is to be assigned to the given index (null is
     *        allowed)
     * @throws IndexOutOfBoundsException
     *         If an invalid index is given
     */
    public abstract void setChild(int index, MathObject child) throws IndexOutOfBoundsException;

    /**
     * Solves this math object symbolically
     * 
     * @return The symbolical solution of this math object
     */
    public abstract MathObject solveSymb();

    /**
     * Returns whether or not this math object will evaluate into a constant
     * value
     * 
     * @return Whether or not this math object will evaluate into a constant
     *         value
     */
    public abstract boolean isConstant();

    /**
     * Approximates the value of this math object, this is only possible if this
     * math object evaluates into a constant value
     * 
     * @return The approximation of this math object
     * @throws NotConstantException
     *         If this math object doesn't evaluate to a constant value
     */
    public abstract double approximate() throws NotConstantException;

    /**
     * Checks if the given child index is valid, and throws an exception is it
     * isn't
     * 
     * @param index
     *        The child index that is to be checked
     * @throws IndexOutOfBoundsException
     *         If the child index is invalid
     */
    protected final void checkChildIndex(int index) throws IndexOutOfBoundsException
    {
        final int childCount = getChildrenCount();
        if(childCount == 0)
            throw new IndexOutOfBoundsException(getClass().getCanonicalName() + " doesn't have any children.");
        else if(childCount == 1 && index != 0)
            throw new IndexOutOfBoundsException("Invalid child index " + Integer.toString(index) + ", "
                    + getClass().getCanonicalName() + " has only 1 child.");
        else if(index < 0 || index >= childCount)
            throw new IndexOutOfBoundsException("Invalid child index " + Integer.toString(index) + ", "
                    + getClass().getCanonicalName() + " has only "
                    + Integer.toString(getChildrenCount()) + " children.");
    }

    // TODO Add a method that writes the object to an XML element
    // TODO Add a method that reads the object from an XML element (could be a static method that creates an object from the XML element)
}
