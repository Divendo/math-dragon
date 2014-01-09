package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.MathObject;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MathSourceView extends View
{
    /** The {@link MathSourceObject} that will be used as a source for {@link MathObject}s */
    private MathSourceObject mathSourceObject = null;

    public MathSourceView(Context context)
    {
        super(context);
    }

    public MathSourceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MathSourceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    /** Sets the {@link MathSourceObject} that should be used as a source for {@link MathObject}s
     * @param mso The source for {@link MathObject}s
     */
    public void setSource(MathSourceObject mso)
    {
        mathSourceObject = mso;
        invalidate();
    }

    /** Interface definition for a callback to be invoked when the dragging has started */
    public interface DragStartedListener
    { public void dragStarted(); }

    /** The drag start event listener */
    private DragStartedListener onDragStarted = null;
    
    /** Set the drag start event listener */
    public void setOnDragStarted(DragStartedListener listener)
    { onDragStarted = listener; }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Simply draw the math source object
        if(mathSourceObject != null)
        {
            final int maxWidth = getResources().getDimensionPixelSize(R.dimen.math_source_max_width);
            if(getWidth() > maxWidth)
            {
                canvas.translate((getWidth() - maxWidth) / 2, 0);
                canvas.clipRect(new Rect(0, 0, maxWidth, getHeight()));
            }
            mathSourceObject.draw(canvas, Math.min(maxWidth, getWidth()), getHeight());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        // We only want ACTION_DOWN events
        if(mathSourceObject == null || me.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        
        // Prepare a MathObject for dragging
        MathObject mathObject = mathSourceObject.createMathObject();
        mathObject.setDefaultHeight(getResources().getDimensionPixelSize(R.dimen.math_object_drag_default_size));
        
        // Start the dragging
        MathShadow mathShadow = new MathShadow(mathObject);
        startDrag(ClipData.newPlainText("", ""), mathShadow, mathShadow, 0);
        if(onDragStarted != null)
            onDragStarted.dragStarted();
        
        // We handled the event
        return true;
    }

}
