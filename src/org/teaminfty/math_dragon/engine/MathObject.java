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
	{
		return 0;
	}

	/**
	 * Check whether at least one children has been set (i.e. non-null by
	 * default). Note that this is implementation defined, because some might
	 * have multiple empty values or do not permit null values.
	 * 
	 * @return <tt>true</tt> on empty. <tt>false</tt> otherwise.
	 */
	public boolean isEmpty()
	{
		for (int i = getChildrenCount(); i > 0; --i)
			if (getChild(i) != null)
				return false;
		return true;
	}

	/**
	 * Check whether <tt>o</tt> is an empty element (i.e. has no further
	 * mathematical meaning). By default, <tt>null</tt> is an empty element.
	 * <p>
	 * <b>Note:</b> this method is implementation defined. The only guarantee it
	 * makes is that if <tt>o == null</tt> than <tt>isNull(o)</tt> yields the
	 * same result.
	 * 
	 * @param o
	 *            The mathematical object
	 * @return <tt>true</tt> when empty. <tt>false</tt> otherwise.
	 */
	public boolean isNull(MathObject o)
	{
		return o == null;
	}

	/**
	 * Check whether it has only one children.
	 * 
	 * @return <tt>true</tt> when singleton. <tt>false</tt> otherwise.
	 */
	public final boolean isSingleton()
	{
		return getChildrenCount() == 1;
	}

	/**
	 * Returns the child at the given index
	 * 
	 * @param index
	 *            The index of the child that is to be retrieved
	 * @return The child at the given index
	 * @throws IndexOutOfBoundsException
	 *             If an invalid index is given
	 */
	public abstract MathObject getChild(int index)
			throws IndexOutOfBoundsException;

	/**
	 * Sets the child at the given index to the given MathObject
	 * 
	 * @param index
	 *            The index of the child that should be set
	 * @param child
	 *            The child that is to be assigned to the given index (null is
	 *            allowed)
	 * @throws IndexOutOfBoundsException
	 *             If an invalid index is given
	 */
	public abstract void setChild(int index, MathObject child)
			throws IndexOutOfBoundsException;

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
	 * Check whether <tt>o</tt> is the mathematical inverse of this instance.
	 * <p>
	 * <b>Note:</b> some implementations always return <tt>false</tt> because
	 * there does not exists a mathematical inverse-equivalent object.
	 * 
	 * @param o
	 *            The inverse-equivalent mathematical object.
	 * @return <tt>true</tt> when o is inverse of this instance. <tt>false</tt>
	 *         otherwise.
	 */
	public abstract boolean isInverseOf(MathObject o);

	/**
	 * Approximates the value of this math object, this is only possible if this
	 * math object evaluates into a constant value
	 * 
	 * @return The approximation of this math object
	 * @throws MathException
	 * 
	 * @throws NotConstantException
	 *             If this math object doesn't evaluate to a constant value
	 */
	public abstract double approximate() throws MathException;

	/**
	 * Checks if the given child index is valid, and throws an exception is it
	 * isn't
	 * 
	 * @param index
	 *            The child index that is to be checked
	 * @throws IndexOutOfBoundsException
	 *             If the child index is invalid
	 */
	protected final void checkChildIndex(int index)
			throws IndexOutOfBoundsException
	{
		final int childCount = getChildrenCount();
		if (childCount == 0)
			throw new IndexOutOfBoundsException(getClass().getCanonicalName()
					+ " doesn't have any children.");
		else if (childCount == 1 && index != 0)
			throw new IndexOutOfBoundsException("Invalid child index "
					+ Integer.toString(index) + ", "
					+ getClass().getCanonicalName() + " has only 1 child.");
		else if (index < 0 || index >= childCount)
			throw new IndexOutOfBoundsException("Invalid child index "
					+ Integer.toString(index) + ", "
					+ getClass().getCanonicalName() + " has only "
					+ Integer.toString(getChildrenCount()) + " children.");
	}

	// TODO Add a method that writes the object to an XML element
	// TODO Add a method that reads the object from an XML element (could be a
	// static method that creates an object from the XML element)
}
