package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Rect;

public class MathOperationAdd extends MathBinaryOperationLinear
{

    /** Constructor
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathOperationAdd(int defWidth, int defHeight)
    { super(defWidth, defHeight); }

    @Override
    public IExpr eval() throws EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return F.Plus(getChild(0).eval(), getChild(1).eval());
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return getChild(0).approximate() + getChild(1).approximate();
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Get the bounding boxes
        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        final Rect leftChild = getChildBoundingBox(0, maxWidth, maxHeight);
        final Rect rightChild = getChildBoundingBox(1, maxWidth, maxHeight);
        
        // Draw the operator
        canvas.save();
        canvas.translate(operator.left, operator.top);
        operatorPaint.setStrokeWidth(operator.width() / 5);
        canvas.drawLine(0, operator.height() / 2, operator.width(), operator.height() / 2, operatorPaint);
        canvas.drawLine(operator.width() / 2, 0, operator.width() / 2, operator.height(), operatorPaint);
        canvas.restore();
        
        // Draw the left child
        canvas.save();
        canvas.translate(leftChild.left, leftChild.top);
        if(getChild(0) != null)
            getChild(0).draw(canvas, leftChild.width(), leftChild.height());
        else
            drawEmtyChild(canvas, leftChild);
        canvas.restore();

        // Draw the right child
        canvas.save();
        canvas.translate(rightChild.left, rightChild.top);
        if(getChild(1) != null)
            getChild(1).draw(canvas, rightChild.width(), rightChild.height());
        else
            drawEmtyChild(canvas, rightChild);
        canvas.restore();
    }

}
