package org.teaminfty.math_dragon;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MathSourceView extends View implements View.OnLongClickListener
{
    
    /** The {@link MathObject} that should be dragged */
    private MathObject mathObject = null;

    public MathSourceView(Context context)
    {
        super(context);
        setOnLongClickListener(this);
    }

    public MathSourceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnLongClickListener(this);
    }

    public MathSourceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setOnLongClickListener(this);
    }
    
    /** Sets the {@link MathObject} that should be dragged
     * @param mo The {@link MathObject} that should be dragged
     */
    public void setMathObject(MathObject mo)
    {
        mathObject = mo;
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
    }

    @Override
    public boolean onLongClick(View v)
    {
        // TODO Convert the MathObject to a string (xml?) and send it as clip data with the drag
        startDrag(ClipData.newPlainText("", ""),
                new MathShadow(mathObject, new Point(getResources().getDimensionPixelSize(R.dimen.math_shadow_dimensions), getResources().getDimensionPixelSize(R.dimen.math_shadow_dimensions))),
                null, 0);
        if(onDragStarted != null)
            onDragStarted.dragStarted();
        return true;
    }

}
