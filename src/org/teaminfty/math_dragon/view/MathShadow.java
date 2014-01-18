package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Expression;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View.DragShadowBuilder;

public class MathShadow extends DragShadowBuilder
{
    /** The {@link Expression} that is being dragged */
    private Expression mathObject = null;

    /** Constructor
     * @param mo The {@link Expression} that is to be dragged
     */
    public MathShadow(Expression mo)
    {
        setDragState(mathObject = mo);
        mathObject.setLevel(0);
    }
    
    /** Recursively sets the DRAG state for the given {@link Expression} and all of its children
     * @param mo The {@link Expression} to set the DRAG state for */
    private void setDragState(Expression mo)
    {
        // Set the state
        mo.setState(HoverState.DRAG);
        
        // Loop through the children and set their states
        for(int i = 0; i < mo.getChildCount(); ++i)
            setDragState(mo.getChild(i));
    }
    
    /** Returns the {@link Expression} in this shadow
     * @return The {@link Expression} in this shadow */
    public Expression getExpression()
    { return mathObject; }
    
    /** Returns the bounding box of the current {@link Expression} relative to the touch point
     * @return The requested bounding box
     */
    public Rect getExpressionBounding()
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
