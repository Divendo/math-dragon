package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class MathOperationPower extends MathBinaryOperation
{
    public MathOperationPower()
    {}
    
	public MathOperationPower(MathObject base, MathObject power)
	{
		super(base, power);
	}
	
	public String toString()
	{
	    return "(" + getLeft().toString() + "^" + getRight().toString() + ")";
	}
    
    @Override
    public int getPrecedence()
    { return MathObjectPrecedence.POWER; }

	@Override
	public IExpr eval() throws EmptyChildException 
	{
		// Check the children
		this.checkChildren();
		
		// Return the result
		return F.Power( getChild(0).eval(), getChild(1).eval() );
	}

	@Override
	public Rect[] getOperatorBoundingBoxes() 
	{
	    // Get the children sizes
	    Rect[] sizes = getChildrenSize();
	    
		// Powers don't have a visible operator
	    // However, they need an operator bounding box (for dropping other operations on them)
		return new Rect[] {
		        new Rect(0, 0, sizes[0].width(), sizes[1].height()),
                new Rect(sizes[0].width(), sizes[1].height(), sizes[0].width() + sizes[1].width(), sizes[0].height() + sizes[1].height())
	        };
	}

    /**
     * Returns the sizes of the bounding of the children.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
	public Rect[] getChildrenSize()
	{
		// Get the sizes both operands want to take
        Rect leftSize = getChild(0).getBoundingBox();
        Rect rightSize = getChild(1).getBoundingBox();
        
        // Return the Sizes
		return new Rect[] {leftSize, rightSize};
	}

    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getChildrenSize();
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, sizes[0].width() + sizes[1].width(), sizes[0].height() + sizes[1].height());
    }
	
	@Override
	public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException 
	{
		// Check if the child exists
		this.checkChildIndex(index);
		
		// Get the Size of the children
		Rect[] childrenSize = getChildrenSize();
		
		// Move the bounding boxes to the correct position
		childrenSize[0].offsetTo(0, childrenSize[1].height());
		childrenSize[1].offsetTo(childrenSize[0].width(), 0);
		
		// Return the right bounding box
		return childrenSize[index];
	}
	
	@Override
	public void setLevel(int l)
	{
		level = l;
		getChild(0).setLevel(level);
		getChild(1).setLevel(level + 1);
	}
	
	//We regard the base operand as the vertical center of the mathObject
	@Override
    public Point getCenter()
    {
    	Rect bounding_vertical = this.getChildBoundingBox(0);
    	Rect bounding_horizontal = this.getBoundingBox();
    	return new Point(bounding_horizontal.centerX(), bounding_vertical.centerY());
    }

	@Override
	public void draw(Canvas canvas) 
	{
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Only draw the children
        drawChildren(canvas);
	}
	
	
}
