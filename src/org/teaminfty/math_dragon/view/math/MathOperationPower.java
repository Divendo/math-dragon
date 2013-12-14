package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class MathOperationPower extends MathBinaryOperation
{
	public MathOperationPower(int defWidth, int defHeight)
	{
		super(defWidth, defHeight);
	}
	
	public MathOperationPower(MathObject base, MathObject power, int defWidth, int defHeight)
	{
		super(base, power, defWidth, defHeight);
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
	public double approximate() throws NotConstantException, EmptyChildException 
	{
		// Check the children
		this.checkChildren();
		
		// Return the result
		return Math.pow( getChild(0).approximate(), getChild(1).approximate() );
	}

	@Override
	public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight) 
	{
	    // Get the children sizes
	    Rect[] sizes = getChildrenSize(maxWidth, maxHeight);
	    
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
	public Rect[] getChildrenSize(int maxWidth, int maxHeight)
	{
		// Get the sizes both operands want to take
        Rect leftSize = getChild(0).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
        Rect rightSize = getChild(1).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
        
        // Make sure the exponent is smaller than the base
        rightSize.bottom = 2 * rightSize.bottom / 3;
        rightSize.right = 2 * rightSize.right / 3;
        
        // If the boxes fit within the specified maximum, we're done
        if((maxWidth == NO_MAXIMUM || leftSize.width() + rightSize.width() < maxWidth) && (maxHeight == NO_MAXIMUM || leftSize.height() + rightSize.height() < maxHeight))
            return new Rect[] {leftSize, rightSize};
        
        // Calculate the shrinking factor
        final float widthFactor = maxWidth == NO_MAXIMUM ? 1.0f : ((float) maxWidth) / (leftSize.width() + rightSize.width());
        final float heightFactor = maxHeight == NO_MAXIMUM ? 1.0f : ((float) maxHeight) / (leftSize.height() + rightSize.height());
        final float factor = Math.min(widthFactor, heightFactor);
        
    	// Calculate the sizes
    	leftSize.set(0, 0, (int) (leftSize.width() * factor), (int) (leftSize.height() * factor));
    	rightSize.set(0, 0, (int) (rightSize.width() * factor), (int) (rightSize.height() * factor));
        
        // Return the Sizes
		return new Rect[] {leftSize, rightSize};
	}

    @Override
    public Rect getBoundingBox(int maxWidth, int maxHeight)
    {
        // Get the sizes
        Rect[] sizes = getChildrenSize(maxWidth, maxHeight);
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, sizes[0].width() + sizes[1].width(), sizes[0].height() + sizes[1].height());
    }
	
	@Override
	public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException 
	{
		// Check if the child exists
		this.checkChildIndex(index);
		
		// Get the Size of the children
		Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
		
		// Move the bounding boxes to the correct position
		childrenSize[0].offsetTo(0, childrenSize[1].height());
		childrenSize[1].offsetTo(childrenSize[0].width(), 0);
		
		// Return the right bounding box
		return childrenSize[index];
	}
	
	//We regard the base operand as the vertical center of the mathObject
	@Override
    public Point getCenter(int maxWidth, int maxHeight)
    {
    	Rect bounding_vertical = this.getChildBoundingBox(0, maxWidth, maxHeight);
    	Rect bounding_horizontal = this.getBoundingBox(maxWidth, maxHeight);
    	return new Point(bounding_horizontal.centerX(), bounding_vertical.centerY());
    }

	@Override
	public void draw(Canvas canvas, int maxWidth, int maxHeight) 
	{
        // Draw the bounding boxes
        drawBoundingBoxes(canvas, maxWidth, maxHeight);
        
        // Only draw the children
        drawChildren(canvas, maxWidth, maxHeight);
	}
	
	
}
