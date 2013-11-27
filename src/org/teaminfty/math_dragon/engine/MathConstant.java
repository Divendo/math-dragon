package org.teaminfty.math_dragon.engine;

/**
 * Represents a mathematical constant like <tt>42</tt> or <tt>-1.13</tt>.
 * 
 * @author Mark Kamsma
 * @author FolkertVanVerseveld
 */
public abstract class MathConstant<T> extends MathObject
{
	/** The mathematical constant result value of this expression. */
	protected T value;

	/**
	 * Construct mathematical constant with specified value.
	 * 
	 * @param value
	 *            The mathematical value.
	 */
	public MathConstant(T value)
	{
		this.value = value;
	}

	/**
	 * Retrieve the mathematical value.
	 * 
	 * @return value
	 */
	public final T getValue()
	{
		return value;
	}

	/**
	 * Assign specified value to current mathematical value.
	 * 
	 * @param value
	 *            The new mathematical value.
	 */
	public final void setValue(T value)
	{
		this.value = value;
	}

	/** {@inheritDoc} */
	@Override
	public String getName()
	{
		return "constant";
	}

	/**
	 * Return text representation of numerical value.
	 * 
	 * @return Numerical value as text.
	 */
	public final String toString()
	{
		return value.toString();
	}
}
