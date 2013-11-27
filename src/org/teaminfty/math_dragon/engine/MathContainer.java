package org.teaminfty.math_dragon.engine;

/**
 * Container that ensures this object contains one or more mathematical objects.
 * 
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 * @see MathOperationBinary
 */
public abstract class MathContainer extends MathObject
{
	/**
	 * Returns the amount of children this math object has. Subclasses
	 * <b>must</b> override this method.
	 * 
	 * @return The amount of children this math object has. Returning <tt>0</tt>
	 *         is not possible (if so, the implementation is broken).
	 */
	public abstract int getChildCount();

	/**
	 * Check whether it has only one children.
	 * 
	 * @return <tt>true</tt> when singleton. <tt>false</tt> otherwise.
	 */
	public final boolean isSingleton()
	{
		return getChildCount() == 1;
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
		final int childCount = getChildCount();
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
					+ Integer.toString(getChildCount()) + " children.");
	}
}
