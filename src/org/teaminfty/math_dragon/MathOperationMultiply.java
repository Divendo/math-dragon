package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationMultiply extends MathBinaryOperationLinear
{
	public MathOperationMultiply(int defWidth, int defHeight)
    { super(defWidth, defHeight); }
	
	 @Override
	    public IExpr eval() throws EmptyChildException
	    {
	        // Check if the children are not empty
	        this.checkChildren();
	        
	        // Return the result
	        return F.Times(getChild(0).eval(), getChild(1).eval());
	    }
	 @Override
	    public double approximate() throws NotConstantException, EmptyChildException
	    {
	        // Check if the children are not empty
	        this.checkChildren();
	        
	        // Return the result
	        return getChild(0).approximate() * getChild(1).approximate();
	    }

	    @Override
	    public void draw(Canvas canvas, int maxWidth, int maxHeight)
	    {
	        // Get the bounding box
	        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
	        
	        // Draw the operator
	        canvas.save();
	        canvas.translate(operator.left, operator.top);
	        operatorPaint.setStrokeWidth(operator.width() / 5);
	        canvas.drawLine(0, 0, operator.width(), operator.height(), operatorPaint);
	        canvas.drawLine(0, operator.height(), operator.width(), 0, operatorPaint);
	        canvas.restore();
	        
	        drawLeft(canvas, getChildBoundingBox(0, maxWidth, maxHeight));
	        drawRight(canvas, getChildBoundingBox(1, maxWidth, maxHeight));
	    }
}
