package org.teaminfty.math_dragon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

/** A view that can hold and draw a mathematical formula */
public class MathView extends View
{
    /** The top-level {@link MathObject} */
    private MathObject mathObject = null;
    
    public MathView(Context context)
    {
        super(context);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
    }

    public MathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
    }

    public MathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
    }
    
    /** Set the top-level {@link MathObject}
     * @param newMathObject The new value for the top-level {@link MathObject}
     */
    public void setMathObject(MathObject newMathObject)
    {
        // Remember the new MathObject, if it is null we create a MathObjectEmpty
        if((mathObject = newMathObject) == null)
            mathObject = new MathObjectEmpty(getResources().getDimensionPixelSize(R.dimen.math_object_default_size), getResources().getDimensionPixelSize(R.dimen.math_object_default_size));
        
        // Redraw
        invalidate();
    }
    
    /** Get the top-level {@link MathObject}
     * @return The top-level {@link MathObject}
     */
    public MathObject getMathObject()
    { return mathObject; }
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Simply draw the math object
        canvas.save();
        Rect boundingBox = mathObject.getBoundingBox(canvas.getWidth(), canvas.getHeight());
        canvas.translate((canvas.getWidth() - boundingBox.width()) / 2, (canvas.getHeight() - boundingBox.height()) / 2);
        mathObject.draw(canvas, canvas.getWidth(), canvas.getHeight());
        canvas.restore();
    }
    
    @Override
    public boolean onDragEvent(DragEvent event)
    {
        // Retrieve the shadow
        MathShadow mathShadow = (MathShadow) event.getLocalState();
        
        switch(event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:
            return true;
            
            case DragEvent.ACTION_DRAG_ENTERED:
                invalidate();
            return true;
            
            case DragEvent.ACTION_DRAG_EXITED:
                invalidate();
            return true;
            
            case DragEvent.ACTION_DROP:
                invalidate();
            return true;

            case DragEvent.ACTION_DRAG_ENDED:
                invalidate();
            return true;
        }
        return false;
    }

}
