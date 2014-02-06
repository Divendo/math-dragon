package org.teaminfty.math_dragon.view.math;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathShadow;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SourceView extends android.view.View
{
    /** The {@link Expression} that will be used as a source for {@link org.teaminfty.math_dragon.view.math.Expression}s */
    private Expression mathSourceObject = null;

    public SourceView(Context context)
    {
        super(context);
    }

    public SourceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SourceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    /** Sets the {@link SourceExpression} that should be used as a source for {@link Expression}s
     * @param mso The source for {@link Expression}s
     */
    public void setSource(Expression mso)
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
            return true;
        
        // Prepare a MathObject for dragging
        org.teaminfty.math_dragon.view.math.Expression mathObject = mathSourceObject.createMathObject();
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
