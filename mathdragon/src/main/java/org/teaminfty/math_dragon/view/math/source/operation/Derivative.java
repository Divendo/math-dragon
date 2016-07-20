package org.teaminfty.math_dragon.view.math.source.operation;

import static org.teaminfty.math_dragon.view.math.Expression.lineWidth;

import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Derivative extends Expression
{
	/** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    @Override
    public org.teaminfty.math_dragon.view.math.Expression createMathObject()
    { return new org.teaminfty.math_dragon.view.math.operation.binary.Derivative(null, Symbol.createVarSymbol('x')); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
    	// Set the size of the "d"
    	paintOperator.setTextSize( (h - 3 * lineWidth) / 2 );
    	
        // Get a boxes that fit twice in the given height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w, (int) (h - 3 * lineWidth) / 2, Empty.RATIO);
        Rect textBox = new Rect();
        paintOperator.getTextBounds("d", 0, "d".length(), textBox);
        
        // Draw the boxes
        emptyBox.offsetTo((w - textBox.width() / 5) / 2, (int) (h - 2 * emptyBox.height() - 3 * lineWidth) / 2);
        drawEmptyBox(canvas, emptyBox);
        
        emptyBox = getRectBoundingBox(w, (int) (h - 3 * lineWidth) / 2, Empty.RATIO);
        emptyBox.offset((w - textBox.width() / 5) / 2, (int) (emptyBox.height() + 3 * lineWidth));
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the text
        canvas.drawText("d", (w - emptyBox.width()) / 2 - textBox.width(), emptyBox.height() * 2 + org.teaminfty.math_dragon.view.math.Expression.lineWidth, paintOperator);
        canvas.drawText("d", (w - emptyBox.width()) / 2 - textBox.width(), emptyBox.height(), paintOperator);
        
        // Draw the operator
        paintOperator.setStrokeWidth(lineWidth);
        canvas.drawLine(emptyBox.left - textBox.width() * 1.5f, h / 2, emptyBox.right, h / 2, paintOperator);
    }
}
