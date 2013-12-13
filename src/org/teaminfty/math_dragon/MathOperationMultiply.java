package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathOperationMultiply extends MathBinaryOperationLinear
{
    public MathOperationMultiply(int defWidth, int defHeight)
    { super(defWidth, defHeight); }
    
    public MathOperationMultiply(MathObject A, MathObject B, int defWidth, int defHeight)
    { 
        super(defWidth, defHeight);
        this.setChild(0, A);
        this.setChild(1, B);
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
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];

        Paint tmp = new Paint();
        tmp.setColor(Color.GREEN);
        canvas.drawRect(getBoundingBox(maxWidth, maxHeight), tmp);
        tmp.setColor(Color.RED);
        for(int i = 0; i < getChildCount(); ++i)
            canvas.drawRect(getChildBoundingBox(i, maxWidth, maxHeight), tmp);
        
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
