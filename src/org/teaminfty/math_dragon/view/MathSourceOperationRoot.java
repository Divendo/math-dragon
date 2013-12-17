package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/** A class that represents a source for new {@link MathOperationRoot}s in the drag-and-drop interface */
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
    public MathObject createMathObject()
    { return new MathOperationRoot(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // The width of the gap between the big box and the small box
        final int gapWidth = (int) (3 * MathObject.lineWidth);
        
        // Get a boxes that fit the given width and height (we'll use it to draw the empty boxes)
        // We'll want one big and one small (2/3 times the big one) box
        Rect bigBox = getRectBoundingBox(3 * (w - gapWidth) / 5, 3 * h / 4, MathObjectEmpty.RATIO);
        Rect smallBox = getRectBoundingBox(2 * (w - gapWidth) / 5, 2 * h / 4, MathObjectEmpty.RATIO);
        
        // Position the boxes
        smallBox.offsetTo((w - bigBox.width() - smallBox.width() - gapWidth) / 2, (h - bigBox.height() - smallBox.height() / 2) / 2);
        bigBox.offsetTo(smallBox.right + gapWidth, smallBox.centerY());
        
        // Draw the boxes
        drawEmptyBox(canvas, bigBox);
        drawEmptyBox(canvas, smallBox);
        
        // Create a path for the operator
        Path path = new Path();
        path.moveTo(smallBox.left, smallBox.bottom + 2 * MathObject.lineWidth);
        path.lineTo(smallBox.right - 2 * MathObject.lineWidth, smallBox.bottom + 2 * MathObject.lineWidth);
        path.lineTo(smallBox.right + 1.5f * MathObject.lineWidth, bigBox.bottom - MathObject.lineWidth / 2);
        path.lineTo(smallBox.right + 1.5f * MathObject.lineWidth, bigBox.top - 2 * MathObject.lineWidth);
        path.lineTo(bigBox.right, bigBox.top - 2 * MathObject.lineWidth);
        
        // Draw the operator
        paintOperator.setStrokeWidth(MathObject.lineWidth);
        canvas.drawPath(path, paintOperator);
    }
}
