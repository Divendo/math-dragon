package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** A class that represents a source for new {@link MathOperationDivide}s in the drag-and-drop interface */
public class MathSourceOperationDivide extends MathSourceObject
{
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    @Override
    public MathObject createMathObject()
    { return new MathOperationDivide(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a boxes that fit twice in the given height (we'll use it to draw the empty boxes)
        Rect emptyBox = getRectBoundingBox(w, (int) (h - 3 * MathObject.lineWidth) / 2, MathObjectEmpty.RATIO);
        
        // Draw the boxes
        emptyBox.offsetTo((w - emptyBox.width()) / 2, (int) (h - 2 * emptyBox.height() - 3 * MathObject.lineWidth) / 2);
        drawEmptyBox(canvas, emptyBox);
        emptyBox.offset(0, (int) (emptyBox.height() + 3 * MathObject.lineWidth));
        drawEmptyBox(canvas, emptyBox);
        
        // Draw the operator
        paintOperator.setStrokeWidth(MathObject.lineWidth);
        canvas.drawLine(emptyBox.left, h / 2, emptyBox.right, h / 2, paintOperator);
    }
}
