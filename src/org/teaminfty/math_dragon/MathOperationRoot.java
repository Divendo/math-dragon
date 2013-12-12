package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathOperationRoot extends MathBinaryOperation
{
	 protected Paint operatorPaint = new Paint();
	
	public MathOperationRoot(int defWidth, int defHeight)
	{
		super(defWidth, defHeight);
	}

	@Override
	public IExpr eval() throws EmptyChildException 
	{
		// Check the children
		this.checkChildren();
		
		// Return the result
		return F.Power( getChild(1).eval(), F.Divide(F.ZZ(1), getChild(0).eval()));
	}

	@Override
	public double approximate() throws NotConstantException, EmptyChildException 
	{
		// Check the children
		this.checkChildren();
		
		// Return the result
		return Math.pow( getChild(1).approximate(), 1/getChild(0).approximate() );
	}

	@Override
	public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight) 
	{
	    // Get the children sizes
	    Rect[] sizes = getChildrenSize(maxWidth, maxHeight);
	    
		//Square roots have a fairly large operator, which we split in 2.
		return new Rect[] {
		        new Rect(0, sizes[0].height(), sizes[0].width(), sizes[0].height() + sizes[1].height()),
                new Rect(sizes[0].width(), 0, sizes[0].width() + sizes[1].width(), sizes[0].height())
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
        Rect leftSize = getChild(0) == null ? getRectBoundingBox(NO_MAXIMUM, NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
        Rect rightSize = getChild(1) == null ? getRectBoundingBox(NO_MAXIMUM, NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
        
        // Make sure the exponent is smaller than the base
        leftSize.bottom = 2 * leftSize.bottom / 3;
        leftSize.right = 2 * leftSize.right / 3;
        
        //Since the exponent's size has changed, 
        leftSize = getChild(0).getBoundingBox(leftSize.bottom, leftSize.right);
        
        
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
        return new Rect(0, 0, sizes[0].width() + sizes[1].width(), sizes[0].height()*2 + sizes[1].height());
    }
	
	@Override
	public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight)
			throws IndexOutOfBoundsException 
	{
		// Check if the child exists
		this.checkChildIndex(index);
		
		// Get the Size of the children
		Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
		Rect boundingBox = getBoundingBox(maxWidth, maxHeight);
		Rect[] operatorBox = getOperatorBoundingBoxes(maxWidth, maxHeight);
		
		// Move the bounding boxes to the correct position
		childrenSize[0].offsetTo( (boundingBox.width()-childrenSize[1].width())/2- childrenSize[0].width()/2, (boundingBox.height()-childrenSize[1].height())/2- childrenSize[0].height()/2);
		childrenSize[1].offsetTo(operatorBox[0].width(), operatorBox[1].height());
		
		// Return the right bounding box
		return childrenSize[index];
	}

	@Override
	public void draw(Canvas canvas, int maxWidth, int maxHeight) 
	{
		// Get the bounding boxes
        final Rect[] operator = getOperatorBoundingBoxes(maxWidth, maxHeight);
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(operator[1].height()/10);
        canvas.drawLine(operator[0].left-operator[0].width()/3,		 operator[0].top + operator[0].height()/3,	operator[0].left + operator[0].width()/3, 		operator[0].top + operator[0].height()/3, 	operatorPaint);
        canvas.drawLine(operator[0].left + operator[0].width()/3 - operatorPaint.getStrokeWidth()/2,	 operator[0].top + operator[0].height()/3, 	operator[0].left + 2*operator[0].width()/3, 	operator[0].bottom, 						operatorPaint);
        canvas.drawLine(operator[0].left + 2*operator[0].width()/3 - operatorPaint.getStrokeWidth()/2,  operator[0].bottom,	operator[0].right, 		operator[0].top,							operatorPaint);
        canvas.drawLine(operator[1].left - operatorPaint.getStrokeWidth()/2, operator[1].bottom, operator[1].right, operator[1].bottom, operatorPaint);
        canvas.restore();
		// Draw the children
		drawLeft(canvas, getChildBoundingBox(0, maxWidth, maxHeight));
        drawRight(canvas, getChildBoundingBox(1, maxWidth, maxHeight));
	}
	
	
}
