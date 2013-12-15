package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

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

    public MathOperationAdd(MathObject left, MathObject right, int defWidth, int defHeight)
    { 
    	super(left, right, defWidth, defHeight);
	}
    
    public String toString(){
        return "(" + getLeft().toString() + "+" + getRight().toString() + ")";
    }
    
    @Override
    public int getPrecedence()
    { return MathObjectPrecedence.ADD; }

	@Override
    public IExpr eval() throws EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return F.Plus(getLeft().eval(), getRight().eval());
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return getLeft().approximate() + getRight().approximate();
    }


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
        operatorPaint.setStrokeWidth(operator.width() / 5);
        operatorPaint.setColor(this.getColor());
        canvas.drawLine(0, operator.height() / 2, operator.width(), operator.height() / 2, operatorPaint);
        canvas.drawLine(operator.width() / 2, 0, operator.width() / 2, operator.height(), operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }

}
