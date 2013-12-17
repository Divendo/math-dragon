package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationMultiply extends MathBinaryOperationLinear
{
    public MathOperationMultiply()
    {}
    
    public MathOperationMultiply(MathObject left, MathObject right)
    { 
        super(left, right);
    }
    
    public String toString()
    {
        return "(" + getLeft().toString() + "*" + getRight().toString() + ")";
    }

    @Override
    protected Rect getOperatorSize()
    {
        final int size = (int) (18 * lineWidth);
        return new Rect(0, 0, size, size);
    }
    
    @Override
    public int getPrecedence()
    { return MathObjectPrecedence.MULTIPLY; }
    
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
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes()[0];
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(this.getColor());
        operatorPaint.setAntiAlias(true);
        canvas.drawCircle(operator.centerX(), operator.centerY(), 2 * lineWidth, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }
}
