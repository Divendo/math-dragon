package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

public class MathOperationRoot extends MathBinaryOperation
{
     protected Paint operatorPaint = new Paint();
    
    public MathOperationRoot()
    { this(null, null); }

    public MathOperationRoot(MathObject base, MathObject exponent)
    {
        super(exponent, base);

        // Initialise the paint
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public IExpr eval() throws EmptyChildException 
    {
        // Check the children
        this.checkChildren();
        
        // Return the result
        return F.Power(getChild(1).eval(), F.Divide(F.ZZ(1), getChild(0).eval()));
    }

    @Override
    public Rect[] getOperatorBoundingBoxes() 
    {
        // Get the children sizes
        Rect[] sizes = getChildrenSize();
        
        // We'll need 3 bounding boxes to contain the operator
        return new Rect[] {
                new Rect(0, sizes[0].height(), 7 * sizes[0].width() / 5, sizes[0].height() + sizes[1].height()),
                new Rect(sizes[0].width(), 0, 7 * sizes[0].width() / 5, sizes[0].height()),
                new Rect(7 * sizes[0].width() / 5, 0, sizes[0].width() + sizes[1].width(), sizes[0].height() / 2)
            };
    }

    /**
     * Returns the sizes of the bounding of the children.
     * At index 0 the exponent's size is stored, at index 1 the base's size is stored
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
        
        int width =  7 * sizes[0].width() / 5 + sizes[1].width();
        int height = sizes[0].height()/2 + sizes[1].height();
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, width, height);
    }
    
    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException 
    {
        // Check if the child exists
        this.checkChildIndex(index);
        
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize();
        
        // Move the bounding boxes to the correct position
        childrenSize[1].offsetTo(7 * childrenSize[0].width() / 5, childrenSize[0].height() / 2);
        
        // Return the right bounding box
        return childrenSize[index];
    }
    
    @Override
	public void setLevel(int l)
	{
		level = l;
		getChild(0).setLevel(level+1);
		getChild(1).setLevel(level);
	}
    
    @Override
    public void setChild(int index, MathObject child) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Create an MathObjectEmpty if null is given
        if(child == null)
            child = new MathObjectEmpty();
        //if it is the exponent of the root, make it smaller
        if(index == 1)
        	child.setLevel(level);
        else 
        	child.setLevel(level +1);
        
        // Set the child
        children.set(index, child);
    }
    
    //We regard the center of the base operand as the center of the mathObject
    @Override
    public Point getCenter()
    {
    	Rect bounding_vertical = this.getChildBoundingBox(1);
    	Rect bounding_horizontal = this.getBoundingBox();
    	return new Point(bounding_horizontal.centerX(), bounding_vertical.centerY());
    }

    @Override
    public void draw(Canvas canvas) 
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the bounding boxes
        final Rect[] childSize = getChildrenSize();
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(lineWidth);
        Path path = new Path();
        path.moveTo(0, (childSize[0].height() + childSize[1].height()) / 2);
        path.lineTo(6 * childSize[0].width() / 5, childSize[0].height() / 2 + childSize[1].height());
        path.lineTo(6 * childSize[0].width() / 5, childSize[0].height() / 4);
        path.lineTo(7 * childSize[0].width() / 5 + childSize[1].width(), childSize[0].height() / 4);
        canvas.drawPath(path, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }
}
