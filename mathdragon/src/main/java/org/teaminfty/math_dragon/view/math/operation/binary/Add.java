package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Precedence;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Add extends Linear
{
	public static final String TYPE = "add";
	
    public Add()
    {}

    public Add(Expression left, Expression right)
    { 
    	super(left, right);
	}
    
    public String toString(){
        return "(" + getLeft().toString() + "+" + getRight().toString() + ")";
    }
    
    @Override
    public int getPrecedence()
    { return Precedence.ADD; }


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
        canvas.drawLine(0, operator.height() / 2, operator.width(), operator.height() / 2, operatorPaint);
        canvas.drawLine(operator.width() / 2, 0, operator.width() / 2, operator.height(), operatorPaint);
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
