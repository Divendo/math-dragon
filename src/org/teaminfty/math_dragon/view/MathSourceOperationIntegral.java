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
    
    /** Constructor */
    public MathSourceOperationIntegral()
    {
        paintOperator.setTypeface(TypefaceHolder.dejavuSans);
    }
    
    @Override
    public MathObject createMathObject()
    { return new MathOperationIntegral(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Determine the size of the empty boxes
    	Rect emptyBox = getRectBoundingBox(w / 4, 2 * h / 3, MathObjectEmpty.RATIO);
    	
    	// Determine the padding size
    	final int padding = w / 30;
    	
    	// Draw the integral sign
    	Rect integralSignBounds = new Rect();
    	paintOperator.setStyle(Paint.Style.FILL);
        paintOperator.setTextSize(0.7f * h);
        paintOperator.getTextBounds( "\u222B", 0, "\u222B".length(), integralSignBounds);
        canvas.drawText("\u222B", -integralSignBounds.left, (h - integralSignBounds.height()) / 2 - integralSignBounds.top, paintOperator);
        
        // Draw the d
        Rect dBounds = new Rect();
        paintOperator.setTextSize(0.5f * h);
        paintOperator.getTextBounds("d", 0, "d".length(), dBounds);
        canvas.drawText("d", integralSignBounds.width() + emptyBox.width() + 2 * padding - dBounds.left, (h - dBounds.height()) / 2 - dBounds.top, paintOperator);
        
        // Draw the empty boxes
        emptyBox.offsetTo(integralSignBounds.width() + padding, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        emptyBox.offsetTo(integralSignBounds.width() + emptyBox.width() + dBounds.width() + 3 * padding, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
    }
}
