package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationSubtract extends MathBinaryOperationLinear
{
    public MathOperationSubtract(int defWidth, int defHeight)
    { super(defWidth, defHeight); }

    @Override
    public int getPrecedence()
    { return MathObjectPrecedence.ADD; }
    
    @Override
    public IExpr eval() throws EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return F.Subtract(getChild(0).eval(), getChild(1).eval());
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return getChild(0).approximate() - getChild(1).approximate();
    }
    
    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        
        // Draw the operator
        operator.inset(operator.width() / 10, operator.height() / 10);      // Padding
        canvas.save();
        canvas.translate(operator.left, operator.top);
        operatorPaint.setStrokeWidth(operator.width() / 5);
        operatorPaint.setColor(this.getColor());
        canvas.drawLine(0, operator.height() / 2, operator.width(), operator.height() / 2, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas, maxWidth, maxHeight);
    }
}
