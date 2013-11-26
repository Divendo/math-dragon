package org.teaminfty.math_dragon.engine;
/**
 * Represents a simple multiply operation
 * @author Folkert van Verseveld
 */
public class MathOperationMultiply extends MathOperationBinary
{
	public MathOperationMultiply() {}
	/**
	 * Construct mathematical multiply operation with predefined lvalue and
	 * rvalue as floating point numbers.
	 * @param lvalue Left floating point value.
	 * @param rvalue Right floating point value.
	 */
	public MathOperationMultiply(double lvalue, double rvalue)
	{
		setChild(0, new MathConstantNumber(lvalue));
		setChild(1, new MathConstantNumber(rvalue));
	}
	/**
	 * Construct mathematical multiply operation with predefined lvalue and
	 * rvalue as mathematical expressions.
	 * @param lvalue Left mathematical expression.
	 * @param rvalue Right mathematical expression.
	 */
	public MathOperationMultiply(MathObject lvalue, MathObject rvalue)
	{
		setChild(0, lvalue);
		setChild(1, rvalue);
	}
	/**{@inheritDoc}*/
    @Override
    public String getName()
    { return "operation-multiply"; }
    /**{@inheritDoc}*/
    @Override
    public MathObject solveSymb()
    {
    	if (leftChild instanceof MathConstantNumber && rightChild instanceof MathConstantNumber)
    		/*
    		 * Note: we could have reduced this to
    		 *   return new MathConstantNumber(approximate());
    		 * but this is not an approximation, so we explicitly retrieve the
    		 * values and calculate the result.
    		 */
    			return new MathConstantNumber(
    					((MathConstantNumber)leftChild).getValue()
    					* ((MathConstantNumber)rightChild).getValue()
    			);
    	return this;
    }
    /**{@inheritDoc}*/
    @Override
    public double approximate() throws MathException
    {
        super.approximate();
        return leftChild.approximate() * rightChild.approximate();
    }
	@Override
	public boolean isInverseOf(MathObject o)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
