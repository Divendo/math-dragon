package org.teaminfty.math_dragon.view.math;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationSubtract extends MathBinaryOperationLinear
{
	public static final String TYPE = "subtract";
	
    public MathOperationSubtract()
    {}

    public MathOperationSubtract(MathObject left, MathObject right)
    { 
        super(left, right);
    }
    
    @Override
    public int getPrecedence()
    { return MathObjectPrecedence.ADD; }
    
    @Override
    public String toString()
    { return "(" + getLeft().toString() + "-" + getRight().toString() + ")"; }
    
    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes()[0];
        
        // Draw the operator
        operator.inset(operator.width() / 10, operator.height() / 10);      // Padding
        canvas.save();
        operatorPaint.setStrokeWidth(lineWidth);
        operatorPaint.setColor(getColor());
        canvas.drawLine(operator.left, operator.centerY(), operator.right, operator.centerY(), operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }
    
    @Override
    protected String getType()
    {
        return TYPE;
    }
}
