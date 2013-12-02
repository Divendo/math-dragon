package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.engine.MathConstant;
import org.teaminfty.math_dragon.engine.MathObject;

import android.graphics.Canvas;
import android.graphics.Rect;

/** This class draws math constants */
public class DrawableMathConstant extends DrawableMathObject
{

    /** Constructor
     * @param mObject The {@link MathObject} that is to be drawn by this instance
     * @param defWidth The default width
     * @param defHeight The default height
     */
    public DrawableMathConstant(MathObject mObject, int defWidth, int defHeight)
    {
        super(mObject, defWidth, defHeight);

        // Check if mObject is really a constant
        if(! (mObject instanceof MathConstant))
            throw InvalidMathObjectException.createUnsupportedTypeException("the constructor of " + getClass().getCanonicalName(), mObject);
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Will always throw an error since constants do not have children
        checkChildIndex(index);
        return null;
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // TODO Auto-generated method stub

    }

}
