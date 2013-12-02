package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.engine.MathOperationAdd;
import org.teaminfty.math_dragon.engine.MathObject;
import org.teaminfty.math_dragon.engine.MathOperationSubtract;

import android.graphics.Canvas;
import android.graphics.Rect;

/** This class draws binary operations that are written linear.
 * For example: the add or subtract operation.
 */
public class DrawableMathOperationBinaryLinear extends DrawableMathObject
{

    /** Constructor
     * @param mObject The {@link MathObject} that is to be drawn by this instance
     * @param defWidth The default width
     * @param defHeight The default height
     */
    public DrawableMathOperationBinaryLinear(MathObject mObject, int defWidth, int defHeight)
    {
        super(mObject, defWidth, defHeight);
        
        // Check if mObject is really a linear binary operation
        if(! (mObject instanceof MathOperationAdd || mObject instanceof MathOperationSubtract))
            throw InvalidMathObjectException.createUnsupportedTypeException("the constructor of " + getClass().getCanonicalName(), mObject);
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Return a square that fits in the given maxWidth and maxHeight
        return new Rect[]{ getRectBoundingBox(maxWidth, maxHeight, 1) };
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);
        
        // Get the bounding box both operands want to take
        final Rect leftBounding = drawableMathObjects[0] == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : drawableMathObjects[0].getBoundingBox(NO_MAXIMUM, maxHeight);
        final Rect rightBounding = drawableMathObjects[1] == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : drawableMathObjects[1].getBoundingBox(NO_MAXIMUM, maxHeight);
        
        // If no width restrictions are given, just return the width the requested operand wants
        // If width restrictions are given, determine the maximum width for the requested operand
        if(maxWidth == NO_MAXIMUM)
            return index == 0 ? leftBounding : rightBounding;
        else
        {
            // Get the width of the operator
            final int operatorWidth = getOperatorBoundingBoxes(maxWidth, maxHeight)[0].width();
            
            // Determine the new maximum width
            final int maxOperandWidth = (maxWidth - operatorWidth) * (index == 0 ? leftBounding.width() : rightBounding.width()) / (leftBounding.width() + rightBounding.width());
            
            // Return the bounding box for the requested operand with the new maximum width
            return drawableMathObjects[index] == null ? getRectBoundingBox(maxOperandWidth, maxHeight, EMPTY_CHILD_RATIO) : drawableMathObjects[index].getBoundingBox(maxOperandWidth, maxHeight);
        }
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // TODO Auto-generated method stub
    }

}
