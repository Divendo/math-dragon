package org.teaminfty.math_dragon.engine;

/**
 * Represents a simple multiply operation
 * 
 * @author Folkert van Verseveld
 */
public class MathOperationMultiply extends MathOperationBinary
{
	public MathOperationMultiply()
	{
	}

	/**
	 * Construct mathematical multiply operation with predefined left value and
	 * right value as long integers.
	 * 
	 * @param lvalue
	 *            Left integer value.
	 * @param rvalue
	 *            Right integer value.
	 */
	public MathOperationMultiply(int lvalue, int rvalue)
	{
		set(new MathConstantNumber<Integer>(lvalue),
				new MathConstantNumber<Integer>(rvalue));
	}

	/**
	 * Construct mathematical multiply operation with predefined left value and
	 * right value as long integers.
	 * 
	 * @param lvalue
	 *            Left long integer value.
	 * @param rvalue
	 *            Right long integer value.
	 */
	public MathOperationMultiply(long lvalue, long rvalue)
	{
		set(new MathConstantNumber<Long>(lvalue), new MathConstantNumber<Long>(
				rvalue));
	}

	/**
	 * Construct mathematical multiply operation with predefined left value and
	 * right value as floating point numbers.
	 * 
	 * @param lvalue
	 *            Left floating point value.
	 * @param rvalue
	 *            Right floating point value.
	 */
	public MathOperationMultiply(double lvalue, double rvalue)
	{
		set(new MathConstantNumber<Double>(lvalue),
				new MathConstantNumber<Double>(rvalue));
	}

	/**
	 * Construct mathematical multiply operation with predefined lvalue and
	 * rvalue as mathematical expressions.
	 * 
	 * @param lvalue
	 *            Left mathematical expression.
	 * @param rvalue
	 *            Right mathematical expression.
	 */
	public MathOperationMultiply(MathObject lvalue, MathObject rvalue)
	{
		set(lvalue, rvalue);
	}

	/** {@inheritDoc} */
	@Override
	public String getName()
	{
		return "operation-multiply";
	}

	/** {@inheritDoc} */
	@Override
	public MathObject solveSymb()
	{
		if (leftChild.isConstant() && rightChild.isConstant())
		{
			MathObject lexpr = leftChild, rexpr = rightChild;
			if (!(lexpr instanceof MathConstantNumber))
				lexpr = lexpr.solveSymb();
			if (!(rexpr instanceof MathConstantNumber))
				rexpr = rexpr.solveSymb();
			if (lexpr instanceof MathConstantNumber
					&& rexpr instanceof MathConstantNumber)
			{
				Number lvalue = ((MathConstantNumber<?>) lexpr).getValue(), rvalue = ((MathConstantNumber<?>) rexpr)
						.getValue();
				/*
				 * Both operands are constant numbers, check if neither is a
				 * floating point number with double precision. If at least one
				 * is a floating point, it can't guarantee that simplifying both
				 * operands won't cause round errors.
				 */
				// TODO implement overflow/underflow checks
				if (lvalue.longValue() == lvalue.doubleValue()
						&& rvalue.longValue() == rvalue.doubleValue())
				{
					return new MathConstantNumber<Long>(lvalue.longValue()
							* rvalue.longValue());
				}
			}
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public double approximate() throws MathException
	{
		checkOperandsNotNull();
		return leftChild.approximate() * rightChild.approximate();
	}
}
