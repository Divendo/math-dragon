package org.teaminfty.math_dragon.view.math;

import android.graphics.Canvas;
import android.graphics.Rect;


public class MathOperationLog extends MathObjectLog
{
	//String of which to get the TextBounds
	public MathOperationLog()
	{
		tmpStr = "log";
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
        canvas.drawText(tmpStr, this.getChildrenSize()[0].width(), this.getCenter().y + textBounding.height()/2 , operatorPaint);
        
        super.draw(canvas);
        this.drawChildren(canvas);
	}

    @Override
    protected String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

}

