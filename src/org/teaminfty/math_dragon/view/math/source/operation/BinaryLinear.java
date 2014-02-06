package org.teaminfty.math_dragon.view.math.source.operation;

import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** A class that represents a source for new {@link Linear}s (or one of its subclasses) in the drag-and-drop interface */
public class BinaryLinear extends Expression
{
    /** An enumeration that describes the types this source object can hold */
    public enum OperatorType
    {
        ADD, SUBTRACT, MULTIPLY
    }
    
    /** The type this source object holds */
    private OperatorType type;
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
    public BinaryLinear(OperatorType t)
    { type = t; }
    
    @Override
    public org.teaminfty.math_dragon.view.math.Expression createMathObject()
    {
        // Return the right MathObject
        switch(type)
        {
            case ADD:       return new Add();
            case SUBTRACT:  return new Subtract();
            case MULTIPLY:  return new Multiply();
        }
        
        // We'll never get here
        return null;
    }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a box that fits the given width and height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w / 3, h, Empty.RATIO);
        
        // Draw the the empty boxes
        emptyBox.offsetTo(0, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the operator in the centre
        final int centerX = w / 2;
        final int centerY = h / 2;
        paintOperator.setStrokeWidth(org.teaminfty.math_dragon.view.math.Expression.lineWidth);
        switch(type)
        {
            case ADD:
            case SUBTRACT:
            {
                final int segmentSize = w / 9;
                paintOperator.setAntiAlias(false);
                canvas.drawLine(centerX - segmentSize, centerY, centerX + segmentSize, centerY, paintOperator);
                if(type == OperatorType.ADD)
                    canvas.drawLine(centerX, centerY - segmentSize, centerX, centerY + segmentSize, paintOperator);
            }
            break;
                
            case MULTIPLY:
                paintOperator.setAntiAlias(true);
                canvas.drawCircle(centerX, centerY, org.teaminfty.math_dragon.view.math.Expression.lineWidth * 2, paintOperator);
            break;
        }
    }
}
