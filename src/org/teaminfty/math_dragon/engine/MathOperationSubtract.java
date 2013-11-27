package org.teaminfty.math_dragon.engine;

/**
 * Represents a simple subtract operation
 */
public class MathOperationSubtract extends MathOperationBinary
{

	@Override
	public String getName()
	{
		return "operation-subtract";
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

		// If the left and right operand are equal, the value is constant as
		// well (i.e. 0)
		return leftChild.equals(rightChild);
	}

	@Override
	public double approximate() throws MathException
	{
		// If the left and right operand are equal, 0 should be returned
		if (leftChild.equals(rightChild))
			return 0;

		// Checks whether the operands are valid constant values
		checkOperandsNotNull();

		// Return the actual value
		return leftChild.approximate() - rightChild.approximate();
	}
}
