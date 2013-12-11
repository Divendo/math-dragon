package org.teaminfty.math_dragon;

import java.lang.reflect.InvocationTargetException;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MathSourceView extends View
{
    /** The {@link MathObject} that should be dragged */
    private MathObject mathObject = null;

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
    
    /** Creates a copy of the given {@link MathObject} and returns the copy
     * @param mo The {@link MathObject} that is to be copied
     * @return The created copy
     */
    private MathObject copyMathObject(MathObject mo)
    {
        // TODO Handle the exceptions in this method (or maybe support copying from the MathObject classes?)
        
        try
        {
            final int defSize = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
            MathObject copy = mo.getClass().getConstructor(int.class, int.class).newInstance(defSize, defSize);
            for(int i = 0; i < mo.getChildCount(); ++i)
                copy.setChild(i, copyMathObject(mo.getChild(i)));
            return copy;
        }
        catch(InstantiationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

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
    public boolean onTouchEvent(MotionEvent me)
    {
        // We only want ACTION_DOWN events
        if(me.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        
        // Start the dragging
        MathShadow mathShadow = new MathShadow(copyMathObject(mathObject), new Point(getResources().getDimensionPixelSize(R.dimen.math_shadow_dimensions), getResources().getDimensionPixelSize(R.dimen.math_shadow_dimensions)));
        startDrag(ClipData.newPlainText("", ""), mathShadow, mathShadow, 0);
        if(onDragStarted != null)
            onDragStarted.dragStarted();
        
        // We handled the event
        return true;
    }

}
