package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class MathConstant extends MathObject
{
    
    // TODO Support real values and constants like pi, e, etc
    /** The value this constant currently holds */
    protected long value = 0;
    
    /** The paint that is used to draw the constant */
    protected Paint paint = new Paint();

    /** Constructor
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathConstant(int defWidth, int defHeight)
    { super(defWidth, defHeight); }

    /** Constructor, constructs with the given value
     * @param v The value that this constant should be initialized with
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathConstant(long v, int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
        value = v;
    }
    
    /** Uses binary search to find the right text size so that the text fits the given bounding box
     * @return The right text size so that the text fits the given bounding box */
    protected float findTextSize(int maxWidth, int maxHeight)
    {
        // If both the width and height are unrestricted, restrict the height
        if(maxWidth == NO_MAXIMUM && maxHeight == NO_MAXIMUM)
            return findTextSize(NO_MAXIMUM, defaultMaxHeight);
        
        // We don't want a text size bigger than 128 or smaller than 8
        final float maxTextSize = 128.0f;
        final float minTextSize = 8.0f;
        
        // Our initial text size and delta
        float textSize = (maxTextSize - minTextSize) / 2;
        float delta = (maxTextSize - textSize) / 2;
        
        // Keep searching until the text fits or until delta becomes too small
        // Note that we will never reach the maximum or minimum text size this way
        final String str = Long.toString(value);
        Rect bounds = new Rect();
        while(delta >= 0.1f)
        {
            // Set the text size and calculate the bounds
            paint.setTextSize(textSize);
            paint.getTextBounds(str, 0, str.length(), bounds);
            
            // Determine if the text size should become smaller or bigger
            if((maxWidth != NO_MAXIMUM && bounds.width() > maxWidth) || (maxHeight != NO_MAXIMUM && bounds.height() > maxHeight))
                textSize -= delta;
            else if((maxWidth == NO_MAXIMUM || bounds.width() == maxWidth) && (maxHeight == NO_MAXIMUM || bounds.height() == maxHeight))
                break;
            else
                textSize += delta;
            
            // Calculate the new delta
            delta /= 2;
        }
        
        // Return the text size
        return textSize;
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Find and set the text size
        paint.setTextSize(findTextSize(maxWidth, maxHeight));
        
        // Get the text bounds
        final String str = Long.toString(value);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        bounds.offsetTo(0, 0);
        
        // Make sure that bounds is contained within the maximum bounds
        if(maxWidth != NO_MAXIMUM && bounds.right > maxWidth)
            bounds.right = maxWidth;
        if(maxHeight != NO_MAXIMUM && bounds.bottom > maxHeight)
            bounds.bottom = maxHeight;
        
        // Return the bounds
        return new Rect[]{ bounds };
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Will always throw an error since constants do not have children
        checkChildIndex(index);
        return null;
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Get the bounding box
        Rect boundingBox = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        
        // Find and set the text size
        // Deliberately take 80% of the text size to create a small padding
        paint.setTextSize(0.8f * findTextSize(maxWidth, maxHeight));
        
        // Get the text and the text bounds
        final String str = Long.toString(value);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        
        // Draw the text
        canvas.drawText(str, (boundingBox.width() - bounds.width()) / 2 - bounds.left, (boundingBox.height() - bounds.height()) / 2 - bounds.top, paint);
    }

    @Override
    public IExpr eval()
    { return F.ZZ(value); }

    @Override
    public double approximate() throws NotConstantException
    { return value; }

}
