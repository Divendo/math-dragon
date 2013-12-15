package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationMultiply extends MathBinaryOperationLinear
{
    public MathOperationMultiply(int defWidth, int defHeight)
    { super(defWidth, defHeight); }
    
    public MathOperationMultiply(MathObject left, MathObject right, int defWidth, int defHeight)
    { 
        super(left, right, defWidth, defHeight);
    }
    
    public String toString()
    {
        return "(" + getLeft().toString() + "*" + getRight().toString() + ")";
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
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas, maxWidth, maxHeight);
        
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        
        // Draw the operator
        canvas.save();
        canvas.translate(operator.left, operator.top);
        operatorPaint.setColor(this.getColor());
        operatorPaint.setAntiAlias(true);
        canvas.drawCircle(operator.width() / 2, operator.height() / 2, operator.width() / 7, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas, maxWidth, maxHeight);
    }
}
