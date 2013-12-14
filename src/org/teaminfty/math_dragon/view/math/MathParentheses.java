package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathParentheses extends MathObject
{
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    final float RATIO = 0.5f / 1.61803398874989f;
    
    /** The paint that's used to draw the parentheses */
    private Paint paint = new Paint();

    /** Constructor
     * 
     * @param child The child to wrap the parentheses around
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathParentheses(MathObject child, int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
        
        // We have one child
        children.add(child == null ? new MathObjectEmpty(defWidth, defHeight) : child);
        
        // Initialise the paint
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }
    
    /** Constructor
     * 
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathParentheses(int defWidth, int defHeight)
    { this(null, defWidth, defHeight); }

    @Override
    public IExpr eval() throws EmptyChildException
    {
        return getChild(0).eval();
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        return getChild(0).approximate();
    }

    /**
     * Returns the sizes of the bounding boxes.
     * The first two rectangle are the sizes of the brackets, the third rectangle is the size of the child.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    protected Rect[] getSizes(int maxWidth, int maxHeight)
    {
        // Get the size the child wants to take
        Rect childSize = getChild(0).getBoundingBox(NO_MAXIMUM, maxHeight);
        
        // Calculate the width and height the operator wants to take
        Rect operatorSize = getRectBoundingBox(NO_MAXIMUM, childSize.height(), RATIO);

        // If we have no maximum width, we're done
        if(maxWidth == NO_MAXIMUM)
            return new Rect[] {operatorSize, new Rect(operatorSize), childSize};

        // If we would get wider than the maximum width, shrink so we fit in
        if(childSize.width() + operatorSize.width() * 2 > maxWidth)
        {
            // Determine the maximum width for the child
            final int childMax = maxWidth * childSize.width() / (childSize.width() + operatorSize.width() * 2);
            
            // Set the new size of the child
            childSize.set(0, 0, childMax, childMax * childSize.height() / childSize.width());
            
            // Calculate the new operator size
            operatorSize = getRectBoundingBox(NO_MAXIMUM, childSize.height(), RATIO);
        }

        // Return the sizes
        return new Rect[] {operatorSize, new Rect(operatorSize), childSize};
    }
    
    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Get the sizes
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        
        // Position the bounding box of the right bracket
        sizes[1].offset(sizes[0].width() + sizes[2].width(), 0);
        
        // Return the bounding boxes
        return new Rect[] {sizes[0], sizes[1]};
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Get the sizes
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        
        // Position the bounding box of the child
        sizes[2].offset(sizes[0].width(), 0);
        
        // Return the bounding box
        return sizes[2];
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas, maxWidth, maxHeight);
        
        // Get the operator bounding boxes
        Rect[] boxes = getOperatorBoundingBoxes(maxWidth, maxHeight);
        
        // Prepare the paint and canvas for drawing the brackets
        paint.setColor(getColor());
        paint.setStrokeWidth(boxes[0].width() / 5);
        canvas.save();
        
        // Draw the left bracket
        canvas.clipRect(boxes[0], Region.Op.REPLACE);
        RectF bracket = new RectF(boxes[0]);
        bracket.inset(0, -paint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, paint);
        
        // Draw the right bracket
        canvas.clipRect(boxes[1], Region.Op.REPLACE);
        bracket = new RectF(boxes[1]);
        bracket.inset(0, -paint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, paint);
        
        // Restore the canvas
        canvas.restore();
        
        // Draw the child
        drawChildren(canvas, maxWidth, maxHeight);
    }

}
