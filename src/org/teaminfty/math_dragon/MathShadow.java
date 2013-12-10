package org.teaminfty.math_dragon;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View.DragShadowBuilder;

public class MathShadow extends DragShadowBuilder
{
    /** The {@link MathObject} that is being dragged */
    private MathObject mathObject = null;
    
    /** The size of the shadow */
    private Point size = new Point(0, 0);

    /** Constructor
     * @param mo The {@link MathObject} that is to be dragged
     * @param s The size of the shadow
     */
    public MathShadow(MathObject mo, Point s)
    {
        mathObject = mo;
        mathObject.setState(HoverState.DRAG);
        
        size = s;
    }
    
    /** Returns the {@link MathObject} in this shadow
     * @return The {@link MathObject} in this shadow */
    public MathObject getMathObject()
    { return mathObject; }
    
    /** Returns the size of this shadow
     * @return The size of this shadow */
    public Point getSize()
    { return size; }
    
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint)
    {
        shadowSize.set(size.x, size.y);
        shadowTouchPoint.set(size.x / 2, size.y + 64);
    }
    
    @Override
    public void onDrawShadow(Canvas canvas)
    {
        // Simply draw the math object
        if(mathObject != null)
        {
            canvas.save();
            Rect boundingBox = mathObject.getBoundingBox(canvas.getWidth(), canvas.getHeight());
            canvas.translate((canvas.getWidth() - boundingBox.width()) / 2, (canvas.getHeight() - boundingBox.height()) / 2);
            mathObject.draw(canvas, canvas.getWidth(), canvas.getHeight());
            canvas.restore();
        }
    }

}
