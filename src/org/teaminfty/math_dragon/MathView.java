package org.teaminfty.math_dragon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/** A view that can hold and draw a mathematical formula */
public class MathView extends View
{

    /** The top-level {@link MathObject} */
    private MathObject mathObject = null;
    
    public MathView(Context context)
    {
        super(context);
    }

    public MathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    /** Set the top-level {@link MathObject}
     * @param newMathObject The new value for the top-level {@link MathObject}
     */
    public void setMathObject(MathObject newMathObject)
    {
        mathObject = newMathObject;
        invalidate();
    }
    
    /** Get the top-level {@link MathObject}
     * @return The top-level {@link MathObject}
     */
    public MathObject getMathObject()
    { return mathObject; }
    
    @Override
    protected void onDraw(Canvas canvas)
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
        
        // TODO Draw an empty box when mathObject == null, this should look the same as an empty child box
    }

}
