package org.teaminfty.math_dragon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

/** A view that can hold and draw a mathematical formula */
public class MathView extends View
{

    /** The top-level {@link MathObject} */
    private MathObject mathObject = null;
    
    /** The paint that is used for an empty box */
    private Paint emptyChildPaint = new Paint();
    
    public MathView(Context context)
    {
        super(context);
        initEmptyChildPaint();
    }

    public MathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initEmptyChildPaint();
    }

    public MathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initEmptyChildPaint();
    }
    
    /** Initialises the {@link MathView#emptyChildPaint emptyChildPaint} */
    private void initEmptyChildPaint()
    {
        emptyChildPaint.setColor(getResources().getColor(R.color.gray));
        emptyChildPaint.setStyle(Paint.Style.STROKE);
        emptyChildPaint.setPathEffect(new DashPathEffect(new float[] {16.0f, 8.0f}, 0));
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
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Determine whether we have to draw the math object or if we have to draw an empty box
        if(mathObject != null)
        {
            // Simply draw the math object
            canvas.save();
            Rect boundingBox = mathObject.getBoundingBox(canvas.getWidth(), canvas.getHeight());
            canvas.translate((canvas.getWidth() - boundingBox.width()) / 2, (canvas.getHeight() - boundingBox.height()) / 2);
            mathObject.draw(canvas, canvas.getWidth(), canvas.getHeight());
            canvas.restore();
        }
        else
        {
            // Determine the rectangle for the empty box
            final float ratio = 1 / 1.61803398874989f;
            final int maxWidth = canvas.getWidth() * 1 / 4;
            final int maxHeight = canvas.getHeight() * 1 / 4;
            Rect emptyChildRect = null;
            if(maxWidth / ratio <= maxHeight)
                emptyChildRect = new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
            else
                emptyChildRect = new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
            
            // Place the box in the centre of the screen
            emptyChildRect.offsetTo((canvas.getWidth() - emptyChildRect.width()) / 2, (canvas.getHeight() - emptyChildRect.height()) / 2);
            
            // Draw the empty child box
            emptyChildPaint.setStrokeWidth(emptyChildRect.width() / 20);
            canvas.drawRect(emptyChildRect, emptyChildPaint);
        }
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
                emptyChildPaint.setColor(getResources().getColor(R.color.blue));
                invalidate();
            return true;
            
            case DragEvent.ACTION_DRAG_EXITED:
                emptyChildPaint.setColor(getResources().getColor(R.color.gray));
                invalidate();
            return true;
            
            case DragEvent.ACTION_DROP:
                // If we don't have a math object yet, just make the dropped math object our math object
                if(mathObject == null)
                {
                    mathObject = mathShadow.getMathObject();
                    mathObject.setState(HoverState.NONE);
                }
                invalidate();
            return true;

            case DragEvent.ACTION_DRAG_ENDED:
                emptyChildPaint.setColor(getResources().getColor(R.color.gray));
                invalidate();
            return true;
        }
        return false;
    }

}
