package org.teaminfty.math_dragon.view.math.source.operation;

import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.source.Expression;

import android.graphics.Canvas;
import android.graphics.Rect;

/** A class that represents a source for new {@link Power}s in the drag-and-drop interface */
public class Power extends Expression
{
    @Override
    public org.teaminfty.math_dragon.view.math.Expression createMathObject()
    { return new org.teaminfty.math_dragon.view.math.operation.binary.Power(); }

    @Override
    public void draw(Canvas canvas, int w, int h)
    {
        // Get a boxes that fit the given width and height (we'll use it to draw the empty boxes)
        // We'll want one big and one small (2/3 times the big one) box
        Rect bigBox = getRectBoundingBox(3 * w / 5, 3 * h / 5, Empty.RATIO);
        Rect smallBox = getRectBoundingBox(2 * w / 5, 2 * h / 5, Empty.RATIO);
        
        // Position the boxes
        bigBox.offsetTo((w - bigBox.width() - smallBox.width()) / 2, (h - bigBox.height() - smallBox.height()) / 2 + smallBox.height());
        smallBox.offsetTo(bigBox.right, bigBox.top - smallBox.height());
        
        // Draw the boxes
        drawEmptyBox(canvas, bigBox);
        drawEmptyBox(canvas, smallBox);
    }
}
