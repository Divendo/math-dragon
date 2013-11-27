package org.teaminfty.math_dragon.engine;

/**
 * Represents a simple add operation
 */
public class MathOperationAdd extends MathOperationBinary
{

	@Override
	public String getName()
	{
		return "operation-add";
	}

	@Override
	public MathObject solveSymb()
	{
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean isConstant()
	{
		// If both operands are constant, the value of this operator will be
		// constant
		if (super.isConstant())
			return true;

		// TODO If the left and right operand are each others opposites, the
		// value is constant as well (i.e. 0)
		return false;
	}

	@Override
	public double approximate() throws MathException
	{
		// TODO If the left and right operand are each others opposites, 0
		// should be returned

		// Checks whether the operands are valid constant values
		checkOperandsNotNull();

		// Return the actual value
		return leftChild.approximate() + rightChild.approximate();
	}
}
