package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Log;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathSourceOperationLimit extends MathSourceObject
{
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
    public MathSourceOperationLimit() 
    {
        paintOperator.setTypeface(TypefaceHolder.dejavuSans);
        paintOperator.setAntiAlias(true);
    }

    @Override
    public MathObject createMathObject()
    {
        return new Log();
    }
    
    public void draw(Canvas canvas, int w, int h)
    {
        // Set the text size
        paintOperator.setTextSize(h / 2.0f);
        
        // Determine the padding size
        final int padding = w / 15;
        
        // Determine the size of an empty box
        Rect emptyBox = getRectBoundingBox(3 * w / 5, 3 * h / 4, MathObjectEmpty.RATIO);
        
        // Determine the size of the string we're going to draw
        Rect textBox = new Rect();
        paintOperator.getTextBounds("log", 0, 3, textBox);
        
        // Place the empty box at the right position and draw it
        emptyBox.offsetTo((w - textBox.width() - padding - emptyBox.width()) / 2 + textBox.width() + padding, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the text
        canvas.drawText("log" , (w - textBox.width() - padding - emptyBox.width()) / 2 - textBox.left, (h - textBox.height()) / 2 - textBox.top, paintOperator);
    }
}
