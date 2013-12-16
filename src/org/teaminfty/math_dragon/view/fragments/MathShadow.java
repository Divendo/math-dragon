package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.view.HoverState;
import org.teaminfty.math_dragon.view.math.MathObject;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View.DragShadowBuilder;

public class MathShadow extends DragShadowBuilder
{
    /** The {@link MathObject} that is being dragged */
    private MathObject mathObject = null;

    /** Constructor
     * @param mo The {@link MathObject} that is to be dragged
     * @param s The size of the shadow
     */
    public MathShadow(MathObject mo, Point s)
    {
        setDragState(mathObject = mo);
        mathObject.setLevel(MathObject.MAX_LEVEL - 1);
    }
    
    /** Recursively sets the DRAG state for the given {@link MathObject} and all of its children
     * @param mo The {@link MathObject} to set the DRAG state for */
    private void setDragState(MathObject mo)
    {
        // Set the state
        mo.setState(HoverState.DRAG);
        
        // Loop through the children and set their states
        for(int i = 0; i < mo.getChildCount(); ++i)
            setDragState(mo.getChild(i));
    }
    
    /** Returns the {@link MathObject} in this shadow
     * @return The {@link MathObject} in this shadow */
    public MathObject getMathObject()
    { return mathObject; }
    
    /** Returns the bounding box of the current {@link MathObject} relative to the touch point
     * @return The requested bounding box
     */
    public Rect getMathObjectBounding()
    {
        // Calculate the bounding box
        Rect boundingBox = mathObject.getBoundingBox();
        
        // Translate the bounding, so that our touch point becomes the origin
        boundingBox.offset(-boundingBox.width() / 2, -boundingBox.height() - 64);
        
        // Return the result
        return boundingBox;
    }
    
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint)
    {
        // Calculate the bounding box
        Rect boundingBox = mathObject.getBoundingBox();
        
        // Set the size and the touch point
        shadowSize.set(boundingBox.width(), boundingBox.height());
        shadowTouchPoint.set(boundingBox.width() / 2, boundingBox.height() + 64);
    }
    
    @Override
    public void onDrawShadow(Canvas canvas)
    {
        // Simply draw the math object
        mathObject.draw(canvas);
    }

}
