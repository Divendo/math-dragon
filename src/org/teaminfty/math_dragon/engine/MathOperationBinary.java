package org.teaminfty.math_dragon.engine;

/**
 * Represents a mathematical binary operation like adding or multiplying
 * 
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 */
public abstract class MathOperationBinary extends MathContainer
{
	/**
	 * Construct binary mathematical operation with empty expressions.
	 */
	public MathOperationBinary()
	{
		this(null, null);
	}

	/**
	 * Construct binary mathematical operation with specified left and right
	 * expressions.
	 * 
	 * @param left
	 *            Mathematical expression to the left.
	 * @param right
	 *            Mathematical expression to the right.
	 */
	public MathOperationBinary(MathObject left, MathObject right)
	{
		leftChild = left;
		rightChild = right;
	}

	/** {@inheritDoc} */
	@Override
	public final int getChildCount()
	{
		return 2;
	}

	/** {@inheritDoc} */
	@Override
	public final MathObject getChild(int index)
			throws IndexOutOfBoundsException
	{
		checkChildIndex(index);

		return index == 0 ? leftChild : rightChild;
	}

	/**
	 * Retrieve the mathematical expression to the left. <b>Note:</b>
	 * <tt>null</tt> may be returned.
	 * 
	 * @return The mathematical expression to the left.
	 */
	public final MathObject getLeft()
	{
		return leftChild;
	}

	/**
	 * Retrieve the mathematical expression to the right. <b>Note:</b>
	 * <tt>null</tt> may be returned.
	 * 
	 * @return The mathematical expression to the right.
	 */
	public final MathObject getRight()
	{
		return rightChild;
	}

	/** {@inheritDoc} */
	@Override
	public final void setChild(int index, MathObject child)
			throws IndexOutOfBoundsException
	{
		checkChildIndex(index);

		if (index == 0)
			leftChild = child;
		else
			rightChild = child;
	}

	/**
	 * Assign mathematical expression to the left to the specified expression.
	 * <b>Note:</b> <tt>null</tt> may or may not be accepted by subclasses.
	 * 
	 * @param left
	 *            A new mathematical expression.
	 * @return The specified value.
	 */
	public final MathObject setLeft(MathObject left)
	{
		return leftChild = left;
	}

	/**
	 * Assign mathematical expression to the right to the specified expression.
	 * <b>Note:</b> <tt>null</tt> may or may not be accepted by subclasses.
	 * 
	 * @param right
	 *            A new mathematical expression.
	 * @return The specified value.
	 */
	public final MathObject setRight(MathObject right)
	{
		return rightChild = right;
	}

	/**
	 * Reset mathematical expressions to the left and right to the specified
	 * expressions. <b>Note:</b> <tt>null</tt> may or may not be accepted by
	 * subclasses.
	 * 
	 * @param left
	 *            The new mathematical expression to the left.
	 * @param right
	 *            The new mathematical expression to the right.
	 */
	public final void set(MathObject left, MathObject right)
	{
		setLeft(left);
		setRight(right);
	}

	/**
	 * Reset mathematical expressions to the left and right to the specified
	 * integers. <b>Note:</b> <tt>null</tt> may or may not be accepted by
	 * subclasses.
	 * 
	 * @param left
	 *            The new mathematical expression to the left.
	 * @param right
	 *            The new mathematical expression to the right.
	 */
	public final void set(int left, int right)
	{
		setLeft(new MathConstantNumber<Integer>(left));
		setRight(new MathConstantNumber<Integer>(right));
	}

	/**
	 * Reset mathematical expressions to the left and right to the specified
	 * long precision integers. <b>Note:</b> <tt>null</tt> may or may not be
	 * accepted by subclasses.
	 * 
	 * @param left
	 *            The new mathematical expression to the left.
	 * @param right
	 *            The new mathematical expression to the right.
	 */
	public final void set(long left, long right)
	{
		setLeft(new MathConstantNumber<Long>(left));
		setRight(new MathConstantNumber<Long>(right));
	}

	/**
	 * Reset mathematical expressions to the left and right to the specified
	 * floating point numbers. <b>Note:</b> <tt>null</tt> may or may not be
	 * accepted by subclasses.
	 * 
	 * @param left
	 *            The new mathematical expression to the left.
	 * @param right
	 *            The new mathematical expression to the right.
	 */
	public final void set(double left, double right)
	{
		setLeft(new MathConstantNumber<Double>(left));
		setRight(new MathConstantNumber<Double>(right));
	}

	/**
	 * Swap the mathematical expression to the left with the mathematical
	 * expression to the right.
	 */
	public final void swap()
	{
		MathObject temp = getLeft();
		setLeft(getRight());
		setRight(temp);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConstant()
	{
		return leftChild != null && leftChild instanceof MathConstant
				&& rightChild != null && rightChild instanceof MathConstant;
	}

	/**
	 * Ensures that both operands (i.e. the mathematical expression to the left
	 * and to the right) are not <tt>null</tt>.
	 * 
	 * @throws MathException
	 *             Thrown when at least one mathematical expression equals
	 *             <tt>null</tt>.
	 */
	public void checkOperandsNotNull() throws MathException
	{
		// Check if the left child isn't empty
		if (leftChild == null)
			throw new NotConstantException(getClass().getCanonicalName()
					+ " doesn't have a left operand.");

		// Check if the right child isn't empty
		if (rightChild == null)
			throw new NotConstantException(getClass().getCanonicalName()
					+ " doesn't have a right operand.");
	}

	/**
	 * The left operand of the binary operation.
	 */
	protected MathObject leftChild = null;
	/**
	 * The right operand of the binary operation.
	 */
	protected MathObject rightChild = null;

}
