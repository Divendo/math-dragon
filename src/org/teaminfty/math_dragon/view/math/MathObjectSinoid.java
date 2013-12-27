package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public abstract class MathObjectSinoid extends MathObject 
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    protected String tmpStr = "";
    protected Rect bounds = new Rect();
    protected int exponent = 0;
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;
    
    /** The ratio (width : height) of a bracket (i.e. the golden ratio) */
    final float HALF_RATIO = 0.5f / 1.61803398874989f;
    public final float FULL_RATIO = 1 / 1.61803398874989f;
    
    public MathObjectSinoid()
    {
        children.add(new MathObjectEmpty());
    }

    /** Calculates the right text size for the given level
     * @param lvl The level
     * @return The right text size for the given level */
    protected float findTextSize(int lvl)
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, lvl);
    }   
    
    /** Adds padding to the given size rectangle
     * @param size The size where the padding should be added to
     * @return The size with the padding
     */
    protected Rect sizeAddPadding(Rect size)
    {
        // Copy the rectangle
        Rect out = new Rect(size);
        
        // Add the padding
        out.inset(-out.width() / 10, -out.height() / 10);
        out.offsetTo(0, 0);
        
        // Return the result
        return out;
    }
    
    /** Calculates the size of this Sinoid when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathConstant}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        operatorPaint.setTextSize(fontSize);
        operatorPaint.setTextSize(fontSize * EXPONENT_FACTOR);

        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        operatorPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
        out.right += bounds.width();
        out.bottom = Math.max(out.bottom, bounds.height());
                
        return out;
    }
    
	//Returns the bounding boxes of the Operator
	@Override
	public Rect[] getOperatorBoundingBoxes() 
	{
		 return new Rect[]{ sizeAddPadding(getSize(findTextSize(level))) };
	}
	
	
	@Override
	public Rect getChildBoundingBox( int index) throws IndexOutOfBoundsException 
	{
		
		// Make sure the child index is valid
        checkChildIndex(index);
        
		//Gets the needed sizes and centers
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect childSize = getChild(index).getBoundingBox();
		int centerY = this.getCenter().y;
		int centerY_child = this.getChild(0).getCenter().y;
		
		//offsets the child.
		childSize.offsetTo(operatorSizes[0].width(), centerY - centerY_child );
		return childSize;
	}
	
	//Complete bounding box
	@Override
	public Rect getBoundingBox()
	{
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		
		return new Rect(0,0, operatorSizes[0].width() + getChild(0).getBoundingBox().width(), getChild(0).getBoundingBox().height());
	}

	
	@Override
	public Point getCenter()
	{		
		return new Point(getBoundingBox().centerX(), getBoundingBox().centerY());
	}
}
