package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** A class that represents a source for new {@link Divide}s in the drag-and-drop interface */
public class MathSourceOperationDivide extends MathSourceObject
{
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    @Override
    public Expression createMathObject()
    { return new Divide(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a boxes that fit twice in the given height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w, (int) (h - 3 * Expression.lineWidth) / 2, Empty.RATIO);
        
        // Draw the boxes
        emptyBox.offsetTo((w - emptyBox.width()) / 2, (int) (h - 2 * emptyBox.height() - 3 * Expression.lineWidth) / 2);
        drawEmptyBox(canvas, emptyBox);
        emptyBox.offset(0, (int) (emptyBox.height() + 3 * Expression.lineWidth));
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the operator
        paintOperator.setStrokeWidth(Expression.lineWidth);
        canvas.drawLine(emptyBox.left, h / 2, emptyBox.right, h / 2, paintOperator);
    }
}
