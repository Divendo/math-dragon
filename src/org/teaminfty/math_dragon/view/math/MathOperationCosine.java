package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathOperationCosine extends MathObjectSinoid
{
    public MathOperationCosine()
    {
    }

    //returns a cosine (or does it?)
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
        
		    final float textSize = findTextSize(level);
        // Draw the main operator
        canvas.save();
        operatorPaint.setColor(getColor());
        operatorPaint.setTextSize(textSize);
        Rect boundingBox = getBoundingBox();
        canvas.drawText("Cos", boundingBox.left,boundingBox.height()/2 + (int)(85* Math.pow(2.0/3.0, level)), operatorPaint);
        canvas.restore();
        
        this.drawChildren(canvas);
	}

}
