package org.teaminfty.math_dragon.engine;


/** 
 * Represents a mathematical binary operation like adding or multiplying
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 */
public abstract class MathOperationBinary extends MathObject
{
	/**
	 * Construct binary mathematical operation with empty expressions.
	 */
	public MathOperationBinary()
	{ this(null, null); }
	/**
	 * Construct binary mathematical operation with specified left and right
	 * expressions.
	 * @param left Mathematical expression to the left.
	 * @param right Mathematical expression to the right.
	 */
	public MathOperationBinary(MathObject left, MathObject right)
	{
		leftChild = left;
		rightChild = right;
	}
    @Override
    public final int getChildrenCount()
    { return 2; }
    
    @Override
    public final MathObject getChild(int index) throws IndexOutOfBoundsException
    {
        checkChildIndex(index);
        
        return index == 0 ? leftChild : rightChild;
    }
    /**
     * Retrieve the mathematical expression to the left. <b>note:</b>
     * <tt>null</tt> may be returned.
     * @return lvalue
     */
    public final MathObject getLeft()
    { return leftChild; }
    /**
     * Retrieve the mathematical expression to the right. <b>note:</b>
     * <tt>null</tt> may be returned.
     * @return lvalue
     */
    public final MathObject getRight()
    { return rightChild; }

    @Override
    public final void setChild(int index, MathObject child) throws IndexOutOfBoundsException
    {
        checkChildIndex(index);
        
        if(index == 0)
            leftChild = child;
        else
            rightChild = child;
    }
    /**
     * Assign mathematical expression to the left to the specified expression.
     * <b>Note:</b> <tt>null</tt> may or may not be accepted by subclasses.
     * @param left A new mathematical expression.
     * @return The specified value.
     */
    public final MathObject setLeft(MathObject left)
    { return leftChild = left; }
    /**
     * Assign mathematical expression to the right to the specified expression.
     * <b>Note:</b> <tt>null</tt> may or may not be accepted by subclasses.
     * @param right A new mathematical expression.
     * @return The specified value.
     */
    public final MathObject setRight(MathObject right)
    { return rightChild = right; }
    /**
     * Reset mathematical expressions to the left and right to the specified
     * expressions. <b>Note:</b> <tt>null</tt> may or may not be accepted by
     * subclasses.
     * @param left The new lvalue
     * @param right The new rvalue
     */
    public final void set(MathObject left, MathObject right)
    {
    	setLeft(left);
    	setRight(right);
    }
    /**
     * Reset mathematical expressions to the left and right to the specified
     * floating point numbers. <b>Note:</b> <tt>null</tt> may or may not be
     * accepted by subclasses.
     * @param left The new lvalue
     * @param right The new rvalue
     */
    public final void set(double left, double right)
    {
    	setLeft(new MathConstantNumber(left));
    	setRight(new MathConstantNumber(right));
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
    /**
     * Duplicate right mathematical expression and assign it to the left. If
     * right is empty, a <tt>MathException</tt> is thrown.
     * @throws MathException Thrown when rvalue is empty.
     */
    public final void dupLeft() throws MathException
    {
    	if (isNull(getRight()))
    		throw new MathException("rvalue is empty");
    	setLeft(getRight());
    }
    /**
     * Duplicate left mathematical expression and assign it to the right. If
     * left is empty, a <tt>MathException</tt> is thrown.
     * @throws MathException Thrown when lvalue is empty.
     */
    public final void dupRight() throws MathException
    {
    	if (isNull(getLeft()))
    		throw new MathException("lvalue is empty");
    	setRight(getLeft());
    }
    /**
     * Ensure both left and right expressions are not empty. If left is empty,
     * this will invoke {@link #dupRight()}. If right is empty, this will invoke
     * {@link #dupLeft()}. If both are empty, a <tt>MathException</tt> is
     * thrown.
     * @throws MathException Thrown when both lvalue and rvalue are empty.
     */
    public final void dup() throws MathException
    {
    	if (isNull(getLeft()))
    		dupRight();
    	else if (isNull(getRight()))
    		dupLeft();
    	else
    		throw new MathException("");
    }

    @Override
    public boolean isConstant()
    { return !isNull(leftChild) && leftChild.isConstant() && !isNull(rightChild) && rightChild.isConstant(); }

    @Override
    public double approximate() throws MathException
    {
        // Check if the left child is a valid constant value
        if(leftChild == null)
            throw new NotConstantException(getClass().getCanonicalName() + " doesn't have a left operand.");
        else if(!leftChild.isConstant())
            throw new NotConstantException("The left operand of " + getClass().getCanonicalName() + " isn't a constant value.");

        // Check if the right child is a valid constant value
        if(rightChild == null)
            throw new NotConstantException(getClass().getCanonicalName() + " doesn't have a right operand.");
        else if(!rightChild.isConstant())
            throw new NotConstantException("The right operand of " + getClass().getCanonicalName() + " isn't a constant value.");
    
        // Dummy return value
        return 0;
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
