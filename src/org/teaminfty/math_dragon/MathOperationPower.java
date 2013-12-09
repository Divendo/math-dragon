package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationPower extends MathBinaryOperation
{
	public MathOperationPower(int defWidth, int defHeight)
	{
		super(defWidth, defHeight);
	}

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
		// Powers don't have an operator sign
		// You still have to set a bounding box, otherwise a nullpointexception will be thrown.
		return new Rect[] {new Rect(0,0,0,0)};
	}

	public Rect[] getChildrenSize(int maxWidth, int maxHeight)
	{
		// Get the bounding box both operands want to take
        Rect leftSize = getChild(0) == null ? getRectBoundingBox(NO_MAXIMUM,
                NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(
                NO_MAXIMUM, NO_MAXIMUM);
        Rect rightSize = getChild(1) == null ? getRectBoundingBox(NO_MAXIMUM,
        		NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(
                NO_MAXIMUM, NO_MAXIMUM);
        
        // Multiply the exponent with a scaling factor so the exponent will be smaller
        final double scaling = 0.5;
        rightSize.bottom = (int) (rightSize.top + rightSize.height() * scaling);
        rightSize.right = (int) (rightSize.left + rightSize.width() * scaling);
        
        // Check if the bounding boxes fit in the specified maxim, otherwise change them to fit       
        if(	maxWidth != NO_MAXIMUM &&
        	leftSize.width() + rightSize.width() > maxWidth)
        {
        	// Calculate the new width's of the bounding boxes
        	final int leftWidthMax = leftSize.width() * maxWidth / (leftSize.width() + rightSize.width());
        	final int rightWidthMax = rightSize.width() * maxWidth / (leftSize.width() + rightSize.width());
        	
        	// Make the new Bounding Boxes with the correct Size
        	leftSize = getChild(0) == null ? getRectBoundingBox(leftWidthMax,
                    leftSize.height(), EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(
                    leftWidthMax, leftSize.height());
            rightSize = getChild(1) == null ? getRectBoundingBox(rightWidthMax,
            		rightSize.height(), EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(
                    rightWidthMax, rightSize.height());
        }
        
        // Check if the Height of the power is in the bounding box
        if(	maxHeight != NO_MAXIMUM &&
            (maxHeight - leftSize.height()) / 2 + leftSize.height() + rightSize.height() > maxHeight)
        {        	
        	/* 	Calculate the new height's of the bounding boxes
				The left side shouldn't scale, otherwise it would get out of size in a linear equation, it
        		should, however, still fit in the bounding box. */
        	final int leftHeightMax = (int) Math.min(maxHeight, leftSize.height());
        	// The right side has to adjust to fit in the remaining space in the bounding box.
            final int rightHeightMax = (int) Math.max(0, maxHeight - (maxHeight - leftHeightMax) / 2 - leftHeightMax);
            
            // Make the new Bounding Boxes with the correct Size
            leftSize = getChild(0) == null ? getRectBoundingBox(leftSize.width(),
                    leftHeightMax, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(
                    leftSize.width(), leftHeightMax);
            rightSize = getChild(1) == null ? getRectBoundingBox(rightSize.width(),
            		rightHeightMax, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(
            		rightSize.width(), rightHeightMax);
        }
        
        // Return the Sizes
		return new Rect[] {leftSize, rightSize};
	}
	
	@Override
	public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight)
			throws IndexOutOfBoundsException 
	{
		// Check if the child exists
		this.checkChildIndex(index);
		
		// Get the Size of the children
		Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
		
		// Move the bounding boxes to the correct position
		childrenSize[0].offsetTo( 0, (maxHeight - childrenSize[0].height()) / 2);
		childrenSize[1].offsetTo( childrenSize[0].width(), (int) (0.5 * (maxHeight - childrenSize[0].height()) - childrenSize[1].height()));
		
		return childrenSize[index];
	}

	@Override
	public void draw(Canvas canvas, int maxWidth, int maxHeight) 
	{		
		// Only draw the children
		drawLeft(canvas, getChildBoundingBox(0, maxWidth, maxHeight));
        drawRight(canvas, getChildBoundingBox(1, maxWidth, maxHeight));
	}
	
	
}
