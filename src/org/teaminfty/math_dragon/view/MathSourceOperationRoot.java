package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/** A class that represents a source for new {@link Root}s in the drag-and-drop interface */
public class MathSourceOperationRoot extends MathSourceObject
{
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Default constructor */
    public MathSourceOperationRoot()
    {
        // Initialise the paint
        paintOperator.setStyle(Paint.Style.STROKE);
        paintOperator.setAntiAlias(true);
    }
    
    @Override
    public Expression createMathObject()
    { return new Root(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // The width of the gap between the big box and the small box
        final int gapWidth = (int) (3 * Expression.lineWidth);
        
        // Get a boxes that fit the given width and height (we'll use it to draw the empty boxes)
        // We'll want one big and one small (2/3 times the big one) box
        Rect bigBox = getRectBoundingBox(3 * (w - gapWidth) / 5, 3 * h / 4, Empty.RATIO);
        Rect smallBox = getRectBoundingBox(2 * (w - gapWidth) / 5, 2 * h / 4, Empty.RATIO);
        
        // Position the boxes
        smallBox.offsetTo((w - bigBox.width() - smallBox.width() - gapWidth) / 2, (h - bigBox.height() - smallBox.height() / 2) / 2);
        bigBox.offsetTo(smallBox.right + gapWidth, smallBox.centerY());
        
        // Draw the boxes
        drawEmptyBox(canvas, bigBox);
        drawEmptyBox(canvas, smallBox);
        
        // Create a path for the operator
        Path path = new Path();
        path.moveTo(smallBox.left, smallBox.bottom + 2 * Expression.lineWidth);
        path.lineTo(smallBox.right - 2 * Expression.lineWidth, smallBox.bottom + 2 * Expression.lineWidth);
        path.lineTo(smallBox.right + 1.5f * Expression.lineWidth, bigBox.bottom - Expression.lineWidth / 2);
        path.lineTo(smallBox.right + 1.5f * Expression.lineWidth, bigBox.top - 2 * Expression.lineWidth);
        path.lineTo(bigBox.right, bigBox.top - 2 * Expression.lineWidth);
        
        // Draw the operator
        paintOperator.setStrokeWidth(Expression.lineWidth);
        canvas.drawPath(path, paintOperator);
    }
}
