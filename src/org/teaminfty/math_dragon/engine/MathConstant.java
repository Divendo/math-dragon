package org.teaminfty.math_dragon.engine;

/**
 * Represents a mathematical constant like '42' or '2*pi'
 * 
 * @author Mark Kamsma
 * @author FolkertVanVerseveld
 */
public abstract class MathConstant extends MathObject
{
	@Override
	public String getName()
	{
		return "constant";
	}

	@Override
	public MathObject getChild(int index) throws IndexOutOfBoundsException
	{
		checkChildIndex(index);
		return null;
	}

	@Override
	public void setChild(int index, MathObject child)
			throws IndexOutOfBoundsException
	{
		checkChildIndex(index);
	}
}
