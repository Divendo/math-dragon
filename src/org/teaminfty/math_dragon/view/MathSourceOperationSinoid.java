package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationArcCos;
import org.teaminfty.math_dragon.view.math.MathOperationArcSine;
import org.teaminfty.math_dragon.view.math.MathOperationArcTangent;
import org.teaminfty.math_dragon.view.math.MathOperationCosh;
import org.teaminfty.math_dragon.view.math.MathOperationCosine;
import org.teaminfty.math_dragon.view.math.MathOperationSine;
import org.teaminfty.math_dragon.view.math.MathOperationSinh;
import org.teaminfty.math_dragon.view.math.MathOperationTangent;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathSourceOperationSinoid extends MathSourceObject
{
	/** An enumeration that describes the types this source object can hold */
    public enum OperatorType
    {
        SIN, COS, TAN, SINH, COSH, ARCSIN, ARCCOS, ARCTAN
    }
	
    
    /** The type this source object holds */
    private OperatorType type;
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
	public MathSourceOperationSinoid(OperatorType t) 
	{ type = t;	}

    @Override
    public MathObject createMathObject()
    {
        // Return the right MathObject
        switch(type)
        {
            case SIN:       return new MathOperationSine();
            case COS:  		return new MathOperationCosine();
            case TAN:  		return new MathOperationTangent();
            case SINH:      return new MathOperationSinh();
            case COSH:  	return new MathOperationCosh();
            case ARCSIN:    return new MathOperationArcSine();
            case ARCCOS: 	return new MathOperationArcCos();
            case ARCTAN:  	return new MathOperationArcTangent();
        }
        
        // We'll never get here
        return null;
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
                if(type == OperatorType.COS)
                    canvas.drawLine(centerX, centerY - segmentSize, centerX, centerY + segmentSize, paintOperator);
                if(type == OperatorType.TAN)
                    canvas.drawLine(centerX, centerY - segmentSize, centerX, centerY + segmentSize, paintOperator);
            }
            break;
                
            case SINH:
            case COSH:
            {
                paintOperator.setAntiAlias(true);
                canvas.drawCircle(centerX, centerY, MathObject.lineWidth * 2, paintOperator);
                if(type == OperatorType.COSH)
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
