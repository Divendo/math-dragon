package org.teaminfty.math_dragon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
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

}
