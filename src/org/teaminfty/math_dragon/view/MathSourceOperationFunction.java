package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationFunction;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathSourceOperationFunction extends MathSourceObject
{
    /** The type this source object holds */
    private MathOperationFunction.FunctionType type;
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
	public MathSourceOperationFunction(MathOperationFunction.FunctionType t) 
	{ type = t;	}

    @Override
    public MathObject createMathObject()
    {
        return new MathOperationFunction(type);
    }
	
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a box that fits the given width and height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w / 3, h, MathObjectEmpty.RATIO);
        
        // Draw the the empty box
        emptyBox.offsetTo(0, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the operator 
        final int centerX = w / 2;
        final int centerY = h / 2;
        paintOperator.setStrokeWidth(MathObject.lineWidth);
        switch(type)
        {
            case SIN:
            case COS:
            case TAN:
            {
                final int segmentSize = w / 9;
                paintOperator.setAntiAlias(false);
                canvas.drawLine(centerX - segmentSize, centerY, centerX + segmentSize, centerY, paintOperator);
                if(type == MathOperationFunction.FunctionType.COS)
                    canvas.drawLine(centerX, centerY - segmentSize, centerX, centerY + segmentSize, paintOperator);
                if(type == MathOperationFunction.FunctionType.TAN)
                    canvas.drawLine(centerX, centerY - segmentSize, centerX, centerY + segmentSize, paintOperator);
            }
            break;
                
            case SINH:
            case COSH:
            case LN:
            {
                paintOperator.setAntiAlias(true);
                canvas.drawCircle(centerX, centerY, MathObject.lineWidth * 2, paintOperator);
                if(type == MathOperationFunction.FunctionType.COSH)
                	canvas.drawCircle(centerX, centerY, MathObject.lineWidth * 2, paintOperator);
            }
            break;
            
            case ARCSIN:
            case ARCCOS:
            case ARCTAN:
            {
            	
            	
            }
            break;
        }
    }
	
}
