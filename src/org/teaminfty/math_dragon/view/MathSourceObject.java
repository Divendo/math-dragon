package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Expression;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;

/** A class that represents a source for new {@link Expression}s in the drag-and-drop interface */
public abstract class MathSourceObject
{
    /** A paint that's used to draw an empty box */
    private Paint paintEmptyBox = new Paint();
    
    /** Default constructor */
    public MathSourceObject()
    {
        // Initialise the paint
        paintEmptyBox.setStyle(Paint.Style.STROKE);
        paintEmptyBox.setPathEffect(new DashPathEffect(new float[] {16.0f, 16.0f}, 0));
    }
    
    /** Creates a new {@link Expression} that can be dragged
     * @return The new {@link Expression} */
    public abstract Expression createMathObject();
    
    /** Draws the {@link MathSourceObject}
     * @param canvas The canvas to draw on
     * @param w The maximum width
     * @param h The maximum height */
    public abstract void draw(Canvas canvas, int w, int h);
    
    /** Helper function to draw a dashed empty box
     * @param canvas The canvas to draw the box on
     * @param rect The rectangle that describes the box that is to be drawn */
    protected void drawEmptyBox(Canvas canvas, Rect rect)
    {
        // Copy the rectangle and then make it just a bit smaller
        Rect copy = new Rect(rect);
        copy.inset((int) Math.ceil(Expression.lineWidth / 2), (int) Math.ceil(Expression.lineWidth / 2));
        
        // Draw the dashed rectangle
        paintEmptyBox.setStrokeWidth(Expression.lineWidth);
        canvas.drawRect(copy, paintEmptyBox);
    }

    /** Helper function, returns a rectangle of the given ratio (width : height) that fits exactly in the given maximum rectangle.
     * @param maxWidth The maximum width of the rectangle
     * @param maxHeight The maximum height of the rectangle
     * @param ratio The ratio (width : height)
     * @return The rectangle fitting in the given maximum size
     */
    protected Rect getRectBoundingBox(int maxWidth, int maxHeight, float ratio)
    {
        if(maxWidth / ratio <= maxHeight)
            return new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
        else
            return new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
    }
}
