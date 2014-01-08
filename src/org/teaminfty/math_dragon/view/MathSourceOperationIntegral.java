package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
//import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationIntegral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
//import android.graphics.Rect;

public class MathSourceOperationIntegral extends MathSourceObject 
{
	
	/** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    @Override
    public MathObject createMathObject()
    { return new MathOperationIntegral(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
    	Rect emptyBox = getRectBoundingBox((int) (0.6 * w / 2),(int) (0.6 * h), MathObjectEmpty.RATIO);
    	
    	// Draw the integral sign
    	Rect bounds = new Rect();
    	paintOperator.setStyle(Paint.Style.FILL);
        paintOperator.setTextSize( (int) (h * 0.8));
        paintOperator.getTextBounds( "\u222B", 0, "\u222B".length(), bounds);
        canvas.drawText( "\u222B", 0, (int) (h * 0.7), paintOperator);
        
        // Draw the d
        paintOperator.setTextSize( (int) (h * 0.65));
        paintOperator.getTextBounds( "d", 0, "d".length(), bounds);
        canvas.drawText( "d", (int) emptyBox.width() + bounds.width(), (int) ((h - bounds.height()) / 2) + bounds.height(), paintOperator);
        
        // Draw the empty boxes
        emptyBox.offsetTo((int) bounds.width(), (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        emptyBox.offsetTo((int) bounds.width() + 2 * emptyBox.width(), (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
    }
}
