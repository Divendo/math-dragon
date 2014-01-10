package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.MathObject;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View.DragShadowBuilder;

public class MathDeleteShadow extends DragShadowBuilder
{
    /** The {@link MathObject} that is being dragged */
    private MathObject expression = null;
    
    /** The paint that's used to draw the question */
    private Paint paint = new Paint();
    
    /** The question text */
    private String question = "";
    
    /** The padding */
    private int padding = 0;

    /** Constructor
     * @param mo The {@link MathObject} that is to be dragged
     * @param res A {@link Resources} object to load resources */
    public MathDeleteShadow(MathObject mo, Resources res)
    {
        expression = mo;
        expression.setLevel(0);
        
        paint.setTextSize(res.getDimensionPixelSize(R.dimen.delete_shadow_text_size));
        question = res.getString(R.string.remove);
        padding = res.getDimensionPixelSize(R.dimen.delete_shadow_padding);
    }
    
    /** Returns the {@link MathObject} in this shadow
     * @return The {@link MathObject} in this shadow */
    public MathObject getMathObject()
    { return expression; }
    
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint)
    {
        // Calculate the bounding box
        Rect boundingBox = expression.getBoundingBox();
        
        // Calculate the text size
        Rect textBounding = new Rect();
        paint.getTextBounds(question, 0, question.length(), textBounding);
        
        // Set the size and the touch point
        shadowSize.set(Math.max(textBounding.width(), boundingBox.width()) + 2 * padding, boundingBox.height() + textBounding.height() + 3 * padding);
        shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y + 64);
    }
    
    @Override
    public void onDrawShadow(Canvas canvas)
    {
        // Draw the text
        paint.setColor(Color.BLACK);
        Rect textBounding = new Rect();
        paint.getTextBounds(question, 0, question.length(), textBounding);
        canvas.drawText(question, (canvas.getClipBounds().width() - textBounding.width()) / 2 - textBounding.left, padding - textBounding.top, paint);
        
        // Draw the expression
        Rect boundingBox = expression.getBoundingBox();
        canvas.translate((canvas.getClipBounds().width() - boundingBox.width()) / 2, textBounding.height() + 2 * padding);
        expression.draw(canvas);
    }

    
    /** A listener that's called when the deletion is confirmed */
    public interface OnDeleteConfirmListener
    {
        /** Called when the user confirms that the expression should be deleted */
        public void confirmed();
    }
    
    /** The current {@link OnDeleteConfirmListener} */
    private OnDeleteConfirmListener onDeleteConfirmListener = null;
    
    /** Set the {@link OnDeleteConfirmListener}
     * @param listener The new {@link OnDeleteConfirmListener} */
    public void setOnDeleteConfirmListener(OnDeleteConfirmListener listener)
    { onDeleteConfirmListener = listener; }

    /** Returns the {@link OnDeleteConfirmListener}
     * @return The {@link OnDeleteConfirmListener} */
    public OnDeleteConfirmListener getOnDeleteConfirmListener()
    { return onDeleteConfirmListener; }
    
    /** Confirms the deletion */
    public void confirm()
    {
        if(onDeleteConfirmListener != null)
            onDeleteConfirmListener.confirmed();
    }
}
