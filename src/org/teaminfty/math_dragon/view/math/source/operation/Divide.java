package org.teaminfty.math_dragon.view.math.source.operation;

import static org.teaminfty.math_dragon.view.math.Expression.lineWidth;

import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** A class that represents a source for new {@link Divide}s in the drag-and-drop interface */
public class Divide extends Expression
{
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    @Override
    public org.teaminfty.math_dragon.view.math.Expression createMathObject()
    { return new org.teaminfty.math_dragon.view.math.operation.binary.Divide(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a boxes that fit twice in the given height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w, (int) (h - 3 * lineWidth) / 2, Empty.RATIO);
        
        // Draw the boxes
        emptyBox.offsetTo((w - emptyBox.width()) / 2, (int) (h - 2 * emptyBox.height() - 3 * lineWidth) / 2);
        drawEmptyBox(canvas, emptyBox);
        emptyBox.offset(0, (int) (emptyBox.height() + 3 * lineWidth));
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the operator
        paintOperator.setStrokeWidth(lineWidth);
        canvas.drawLine(emptyBox.left, h / 2, emptyBox.right, h / 2, paintOperator);
    }
}
