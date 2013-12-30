package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Rect;


public class MathOperationCosine extends MathObjectSinoid
{
	//String of which to get the TextBounds
	public MathOperationCosine()
	{
		tmpStr = "cos";
	}
	
    //returns a cosine
	@Override
	public IExpr eval() throws EmptyChildException 
	{
		return F.Cos(getChild(0).eval());
	}

	@Override
	public void draw(Canvas canvas) 
	{
		// Draw the bounding boxes
        drawBoundingBoxes(canvas);
		
        // Get the text size and the bounding box
        final float textSize = findTextSize(level);
        Rect textBounding = getSize(textSize);

        // Set the text size
        operatorPaint.setTextSize(textSize);
        
        // Set the paint color
        operatorPaint.setColor(getColor());

        // Draw the main operator
        operatorPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
        canvas.drawText(tmpStr, bounds.left, this.getCenter().y + textBounding.height()/2 , operatorPaint);
        
        this.drawChildren(canvas);
	}

}

