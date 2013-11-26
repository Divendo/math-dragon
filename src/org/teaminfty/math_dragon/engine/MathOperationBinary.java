package org.teaminfty.math_dragon.engine;

/** 
 * Represents a mathematical binary operation like adding or multiplying
 */
public abstract class MathOperationBinary extends MathObject
{
    @Override
    public final int getChildrenCount()
    { return 2; }
    
    @Override
    public final MathObject getChild(int index) throws IndexOutOfBoundsException
    {
        checkChildIndex(index);
        
        return index == 0 ? leftChild : rightChild;
    }

    @Override
    public final void setChild(int index, MathObject child) throws IndexOutOfBoundsException
    {
        checkChildIndex(index);
        
        if(index == 0)
            leftChild = child;
        else
            rightChild = child;
    }

    @Override
    public boolean isConstant()
    { return leftChild != null && leftChild.isConstant() && rightChild != null && rightChild.isConstant(); }

    @Override
    public double approximate() throws NotConstantException
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
     * The left operand of the binary operation
     */
    protected MathObject leftChild = null;
    /**
     * The right operand of the binary operation
     */
    protected MathObject rightChild = null;

}
