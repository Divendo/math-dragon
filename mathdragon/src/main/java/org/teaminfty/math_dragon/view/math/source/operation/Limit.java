package org.teaminfty.math_dragon.view.math.source.operation;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.operation.binary.Log;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Limit extends Expression
{
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
    public Limit() 
    {
        paintOperator.setTypeface(TypefaceHolder.dejavuSans);
        paintOperator.setAntiAlias(true);
    }

    @Override
    public org.teaminfty.math_dragon.view.math.Expression createMathObject()
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
        Rect emptyBox = getRectBoundingBox(3 * w / 5, 3 * h / 4, Empty.RATIO);
        
        // Determine the size of the string we're going to draw
        Rect textBox = new Rect();
        paintOperator.getTextBounds("lim", 0, 3, textBox);
        
        // Place the empty box at the right position and draw it
        emptyBox.offsetTo((w - textBox.width() - padding - emptyBox.width()) / 2 + textBox.width() + padding, (h - emptyBox.height()) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the text
        canvas.drawText("lim" , (w - textBox.width() - padding - emptyBox.width()) / 2 - textBox.left, (h - textBox.height()) / 2 - textBox.top, paintOperator);
    }
}
