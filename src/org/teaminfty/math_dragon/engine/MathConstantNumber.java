package org.teaminfty.math_dragon.engine;

/**
 * Mathematical java primitive number constant that will be used in <q>simple</q>
 * numeric operations. (e.g. integer + double, float * float).
 * 
 * @author Folkert van Verseveld
 */
public final class MathConstantNumber<T extends Number> extends MathConstant<T>
{
	/**
	 * Construct mathematical constant with a predefined numerical value.
	 * 
	 * @param v
	 *            The numerical value.
	 */
	public MathConstantNumber(T value)
	{
		super(value);
	}

	/** {@inheritDoc} */
	@Override
	public String getName()
	{
		return "constant-number";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #value
	 */
	@Override
	public double approximate()
	{
		return value.doubleValue();
	}

	/** {@inheritDoc} */
	@Override
	public MathObject solveSymb()
	{
		return this;
	}

	@Override
	public boolean isConstant()
	{
		return true;
	}
}
