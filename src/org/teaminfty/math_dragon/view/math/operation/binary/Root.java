package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.operation.Binary;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

public class Root extends Binary
{
	public static final String TYPE = "root";
    protected Paint operatorPaint = new Paint();
    
    public Root()
    { this(null, null); }

    public Root(Expression base, Expression exponent)
    {
        super(exponent, base);

        // Initialise the paint
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStyle(Paint.Style.STROKE);
    }
    
    /**
     * Assign <tt>o</tt> to mathematical expression to base expression.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setBase(Expression o)
    {
        setChild(1, o);
    }

    /**
     * Retrieve the base mathematical expression. <b>Note:</b> <tt>null</tt>
     * may be returned.
     * 
     * @return The base mathematical expression.
     */
    public Expression getBase()
    { return getChild(1); }

    @Override
    public String toString()
    {
        return "(" + getBase().toString() + "^ (1/" + getExponent().toString() + "))";
    }
    
    /**
     * Assign <tt>o</tt> to mathematical expression to exponent expression.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setExponent(Expression o)
    {
        setChild(0, o);
    }

    /**
     * Retrieve the exponent mathematical expression. <b>Note:</b> <tt>null</tt>
     * may be returned.
     * 
     * @return The exponent mathematical expression.
     */
    public Expression getExponent()
    {
        return getChild(0);
    }

    @Override
    public Rect[] getOperatorBoundingBoxes() 
    {
        // Get the bounding boxes (not the sizes) of the children
        Rect exponentBounding = getChildBoundingBox(0);
        Rect baseBounding = getChildBoundingBox(1);
        
        // The size of the gap
        final int gapSize = (int) (3 * Expression.lineWidth);
        
        // We'll need 3 bounding boxes to contain the operator
        return new Rect[] {
                new Rect(exponentBounding.left, exponentBounding.bottom, baseBounding.left, baseBounding.bottom),
                new Rect(exponentBounding.right, baseBounding.top - gapSize, baseBounding.left, exponentBounding.bottom),
                new Rect(baseBounding.left, baseBounding.top - gapSize, baseBounding.right, baseBounding.top)
            };
    }

    @Override
    public Rect getBoundingBox()
    {
        // Get the bounding box (not the size) of the base
        Rect baseBounding = getChildBoundingBox(1);
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, baseBounding.right, baseBounding.bottom);
    }
    
    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException 
    {
        // Check if the child exists
        checkChildIndex(index);
        
        // The size of the gap
        final int gapSize = (int) (3 * Expression.lineWidth);
        
        // We'll always need the y-coordinate of the centre of the base
        final int centerY = getChild(1).getCenter().y;
        
        // We'll always need the bounding box of the exponent
        Rect exponentBounding = getChild(0).getBoundingBox();
        if(exponentBounding.bottom - gapSize / 2 < centerY)
            exponentBounding.offset(0, centerY - (exponentBounding.bottom - gapSize / 2));
        
        // If we're calculating the bounding box of the exponent, we're done
        if(index == 0)
            return exponentBounding;
        
        // We want the bounding box of the base, so we'll need its size
        Rect baseBounding = getChild(1).getBoundingBox();
        
        // Position the bounding box
        baseBounding.offsetTo(exponentBounding.right + 2 * gapSize, Math.max(0, exponentBounding.bottom + gapSize / 2 - centerY));
        
        // Return the bounding box
        return baseBounding;
    }
    
    @Override
	public void setLevel(int l)
	{
		level = l;
		getChild(0).setLevel(level + 1);
		getChild(1).setLevel(level);
	}
    
    @Override
    public Point getCenter()
    {
        // The size of the gap
        final int gapSize = (int) (3 * Expression.lineWidth);
        
        // The bounding box of the exponent
        Rect exponentBounding = getChildBoundingBox(0);
        
        // We regard the vertical centre of the base operand as the vertical centre of the root
        // The centre of the base operand is the same as the bottom of the exponent operand + gapSize / 2
    	return new Point((exponentBounding.width() + 2 * gapSize + getChild(1).getBoundingBox().width()) / 2, exponentBounding.bottom + gapSize / 2);
    }

    @Override
    public void draw(Canvas canvas) 
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);

        // Get the bounding boxes (not the sizes) of the children
        Rect exponentBounding = getChildBoundingBox(0);
        Rect baseBounding = getChildBoundingBox(1);

        // The size of the gap and the centre y-coordinate
        final int gapSize = (int) (3 * Expression.lineWidth);
        final int centerY = getCenter().y;
        
        // Create a path to draw the operator
        Path path = new Path();
        path.moveTo(exponentBounding.left, centerY);
        path.lineTo(baseBounding.left - 2 * gapSize, centerY);
        path.lineTo(baseBounding.left - gapSize / 2, baseBounding.bottom - Expression.lineWidth / 2);
        path.lineTo(baseBounding.left - gapSize / 2, baseBounding.top - gapSize / 2);
        path.lineTo(baseBounding.right, baseBounding.top - gapSize / 2);
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(lineWidth);
        canvas.drawPath(path, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }
    
    @Override
    protected String getType()
    {
        return TYPE;
    }
    
    @Override
    public boolean isCompleted()
    {
        return getBase().isCompleted();
    }
}
