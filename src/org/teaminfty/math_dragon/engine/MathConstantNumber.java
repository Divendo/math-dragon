package org.teaminfty.math_dragon.engine;

/**
 * Mathematical java primitive number constant that will be used in <q>simple</q>
 * numeric operations. (e.g. integer + double, float * float).
 * 
 * @author Folkert van Verseveld
 */
public final class MathConstantNumber extends MathConstant
{
	/**
	 * The numerical value. Only package classes may manipulate this member.
	 */
	protected double value;

	/**
	 * Construct mathematical constant with a predefined numerical value.
	 * 
	 * @param v
	 *            The numerical value.
	 */
	public MathConstantNumber(double v)
	{
		value = v;
	}

	@Override
	public String getName()
	{
		return "constant-number";
	}

	@Override
	public MathObject getChild(int index) throws IndexOutOfBoundsException
	{
		checkChildIndex(index);
		return this;
	}

	@Override
	public void setChild(int index, MathObject child)
			throws IndexOutOfBoundsException, IllegalArgumentException
	{
		checkChildIndex(index);

		if (!(child instanceof MathConstantNumber))
			throw new IllegalArgumentException(
					"MathConstantNumber expected, but got "
							+ child.getClass().getCanonicalName());
		value = ((MathConstantNumber) child).value;
	}

	/**
	 * Return numerical value as a floating point number.
	 * 
	 * @return The numerical value.
	 * @see #approximate()
	 */
	public double getValue()
	{
		return approximate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #value
	 */
	public double approximate()
	{
		return value;
	}

	/**
	 * Return text representation of numerical value.
	 * 
	 * @return Numerical value as text.
	 */
	public String toString()
	{
		return String.valueOf(value);
	}

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

	@Override
	public boolean isInverseOf(MathObject o)
	{
		return o instanceof MathConstantNumber ? ((MathConstantNumber) o)
				.getValue() == -value : false;
	}
}
