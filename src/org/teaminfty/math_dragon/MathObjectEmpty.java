package org.teaminfty.math_dragon;

import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;

/** Represents an empty child */
public class MathObjectEmpty extends MathObject
{
    /** The ratio (width : height) of the empty child box (i.e. the golden ratio) */
    final float RATIO = 1 / 1.61803398874989f;
    
    /** The paint that's used to draw the child */
    private Paint paint = new Paint();
    
    /** Constructor
     * 
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathObjectEmpty(int defWidth, int defHeight)
    {
        super(defWidth, defHeight);

        // Initialise the paint
        paint.setColor(Color.rgb(0x88, 0x88, 0x88));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {16.0f, 16.0f}, 0));
    }

    @Override
    public IExpr eval() throws EmptyChildException
    {
        // Just throw an error
        throw new EmptyChildException();
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        // Just throw an error
        throw new EmptyChildException();
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // A MathObjectEmpty will never get bigger than the default size
        maxWidth = maxWidth == NO_MAXIMUM ? defaultMaxWidth : Math.min(maxWidth, defaultMaxWidth);
        maxHeight = maxHeight == NO_MAXIMUM ? defaultMaxHeight : Math.min(maxHeight, defaultMaxHeight);
        
        // Return the bounding box
        return new Rect[] {getRectBoundingBox(maxWidth, maxHeight, RATIO)};
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Will always throw an error since empty boxes do not have children
        checkChildIndex(index);
        return null;
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        Paint tmp = new Paint();
        tmp.setColor(Color.GREEN);
        tmp.setStyle(Paint.Style.STROKE);
        canvas.drawRect(getBoundingBox(maxWidth, maxHeight), tmp);
        tmp.setColor(Color.RED);
        for(int i = 0; i < getChildCount(); ++i)
            canvas.drawRect(getChildBoundingBox(i, maxWidth, maxHeight), tmp);
        
        // Get the bounding box
        Rect rect = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        
        // Set the right color of the paint
        paint.setColor(getColor());
        
        // Draw the dashed rectangle
        paint.setStrokeWidth(rect.width() / 20);
        rect.inset((int) Math.ceil(paint.getStrokeWidth() / 2), (int) Math.ceil(paint.getStrokeWidth() / 2));
        canvas.drawRect(rect, paint);
        
        //If you're trying to drag the item, make an aiming cross
        if(this.state == HoverState.DRAG)
        {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.height(), rect.width()) / 10, paint);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

}
