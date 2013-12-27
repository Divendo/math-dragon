package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathOperationCosh extends MathObjectSinoid
{
	//String of which to get the TextBounds
	public MathOperationCosh()
	{
		tmpStr = "cosh";
	}
	
    //returns a cosine (or does it?)
	@Override
	public IExpr eval() throws EmptyChildException 
	{
		return F.Cosh(getChild(0).eval());
	}

	@Override
	public void draw(Canvas canvas) 
	{
		// Draw the bounding boxes
        drawBoundingBoxes(canvas);
		
        // Get the text size and the bounding box
        final float textSize = findTextSize(level);
        Rect textBounding = getSize(textSize);
        Rect totalBounding = sizeAddPadding(textBounding);

        // Set the text size
        operatorPaint.setTextSize(textSize);
        
        // Set the paint color
        operatorPaint.setColor(getColor());

        // Draw the main operator
        canvas.save();
        canvas.translate((totalBounding.width() - textBounding.width()) / 2, (totalBounding.height() - textBounding.height()) / 2);
        operatorPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
        canvas.drawText(tmpStr, bounds.left, textBounding.height() - bounds.height() - bounds.top, operatorPaint);
        canvas.restore();
        
        this.drawChildren(canvas);
	}

}

