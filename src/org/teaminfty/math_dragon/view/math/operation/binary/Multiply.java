package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Precedence;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Multiply extends Linear
{
	public static final String TYPE = "multiply";
	
    public Multiply()
    {}
    
    public Multiply(Expression left, Expression right)
    { 
        super(left, right);
    }
    
    public String toString()
    {
        return "(" + getLeft().toString() + "*" + getRight().toString() + ")";
    }

    @Override
    public int getPrecedence()
    { return Precedence.MULTIPLY; }
    
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
        canvas.translate(operator.left, operator.top);
        operatorPaint.setStrokeWidth(lineWidth);
        operatorPaint.setColor(this.getColor());
        operatorPaint.setAntiAlias(true);
        canvas.drawCircle(operator.width()/2, operator.height()/2, operator.width()/10, operatorPaint);
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
