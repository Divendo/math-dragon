package org.teaminfty.math_dragon.engine;

/**
 * Represents a simple subtract operation
 */
public class MathOperationSubtract extends MathOperationBinary
{

    @Override
    public String getName()
    { return "operation-subtract"; }

    @Override
    public MathObject solveSymb()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isConstant()
    {
        // If both operands are constant, the value of this operator will be constant
        if(super.isConstant()) return true;
        
        // TODO If the left and right operand are equal, the value is constant as well (i.e. 0)
        return false;
    }

    @Override
    public double approximate() throws NotConstantException
    {
        // TODO If the left and right operand are equal, 0 should be returned
        
        // Checks whether the operands are valid constant values
        super.approximate();
        
        // Return the actual value
        return leftChild.approximate() - rightChild.approximate();
    }

}
