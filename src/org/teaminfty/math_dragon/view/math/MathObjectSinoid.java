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
    
    /** The ratio (width : height) of a bracket (i.e. the golden ratio) */
    final float HALF_RATIO = 0.5f / 1.61803398874989f;
    public final float FULL_RATIO = 1 / 1.61803398874989f;
    protected int sineWidth = 110*3;
    
    public MathObjectSinoid()
    {
        children.add(new MathObjectEmpty());
    }

	//Returns the bounding boxes of the Operator
	@Override
	public Rect[] getOperatorBoundingBoxes() 
	{
		//Gets the child's bounding box and determines it's own size
		final Rect childRect = getChild(0).getBoundingBox();
		final int operatorWidth = (int)(sineWidth* Math.pow(2.0/3.0, level));
		final int operatorHeight = (int)(defaultHeight);
		
		//Returns the sin operator itself and the brackets
		return new Rect[] { 
			new Rect(0,0, operatorWidth, operatorHeight),
		};
	}

	
	@Override
	public Rect getChildBoundingBox( int index) throws IndexOutOfBoundsException 
	{
		//Gets the needed sizes and centers
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect childSize = getChild(index).getBoundingBox();
		int centerY = this.getCenter().y;
		int centerY_child = this.getChild(0).getCenter().y;
		
		//offsets the child.
		childSize.offsetTo(operatorSizes[0].width(), centerY - centerY_child );
		return childSize;
	}
	
	@Override
	public Rect getBoundingBox()
	{
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		
		return new Rect(0,0, operatorSizes[0].width() + getChild(0).getBoundingBox().width(), getChild(0).getBoundingBox().height());
	}
	
	//Returns the center of the sinoid
    protected float findTextSize(int lvl)
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, lvl);
    }
	
	@Override
	public Point getCenter()
	{
		Rect child = getChild(0).getBoundingBox();
		Rect[] operators = getOperatorBoundingBoxes();
		
		return new Point(getBoundingBox().centerX(), getBoundingBox().centerY());
	}
}
