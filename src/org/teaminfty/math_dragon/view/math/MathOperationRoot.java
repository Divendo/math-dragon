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
        // Get the bounding boxes (not the sizes) of the children
        Rect exponentBounding = getChildBoundingBox(0);
        Rect baseBounding = getChildBoundingBox(1);
        
        // The size of the gap
        final int gapSize = (int) (3 * MathObject.lineWidth);
        
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
        final int gapSize = (int) (3 * MathObject.lineWidth);
        
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
        final int gapSize = (int) (3 * MathObject.lineWidth);
        
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
        final int gapSize = (int) (3 * MathObject.lineWidth);
        final int centerY = getCenter().y;
        
        // Create a path to draw the operator
        Path path = new Path();
        path.moveTo(exponentBounding.left, centerY);
        path.lineTo(baseBounding.left - 2 * gapSize, centerY);
        path.lineTo(baseBounding.left - gapSize / 2, baseBounding.bottom - MathObject.lineWidth / 2);
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
}
