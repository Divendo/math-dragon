package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationFunction;
import org.teaminfty.math_dragon.view.math.MathOperationFunction.FunctionType;

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
    {
        type = t;
        paintOperator.setTypeface(TypefaceHolder.dejavuSans);
        paintOperator.setAntiAlias(true);
    }

    @Override
    public MathObject createMathObject()
    {
        return new MathOperationFunction(type);
    }
    
    public void draw(Canvas canvas, int w, int h)
    {
        // Set the text size
        paintOperator.setTextSize(h / 2.0f);
        if(type == FunctionType.ARCCOS || type == FunctionType.ARCSIN  || type == FunctionType.ARCTAN)
            paintOperator.setTextSize(h / 3.0f);
        
        // Determine the padding size
        final int padding = w / 15;
        
        // Determine the size of an empty box
        Rect emptyBox = getRectBoundingBox(3 * w / 5, 3 * h / 4, MathObjectEmpty.RATIO);
        
        // Determine the size of the string we're going to draw
        Rect textBox = new Rect();
        paintOperator.getTextBounds(type.getName(), 0, type.getName().length(), textBox);
        
        // Place the empty box at the right position and draw it
        emptyBox.offsetTo((w - textBox.width() - padding - emptyBox.width()) / 2 + textBox.width() + padding, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the text
        canvas.drawText(type.getName(), (w - textBox.width() - padding - emptyBox.width()) / 2 - textBox.left, (h - textBox.height()) / 2 - textBox.top, paintOperator);
    }
}
