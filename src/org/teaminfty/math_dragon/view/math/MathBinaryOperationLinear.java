package org.teaminfty.math_dragon.view.math;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * This class draws binary operations that are written linear. That is,
 * operations which are written like '&lt;left operand&gt; &lt;operator&gt;
 * &lt;right operand&gt;'. For example: the add or subtract operation.
 */
public abstract class MathBinaryOperationLinear extends MathBinaryOperation
{

    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();

    /**
     * Constructor
     * 
     * @param defWidth
     *        The default maximum width
     * @param defHeight
     *        The default maximum height
     */
    public MathBinaryOperationLinear(int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
    }
    
    public MathBinaryOperationLinear(MathObject left, MathObject right, int defWidth, int defHeight)
    {
    	super(left, right, defWidth, defHeight);
    }

    /**
     * Returns the sizes of the bounding boxes.
     * The first rectangle is the size of the operator, the second and third rectangle are the sizes of the children.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    protected Rect[] getSizes(int maxWidth, int maxHeight)
    {
        // Get the size both operands want to take
        Rect leftSize = getChild(0).getBoundingBox(NO_MAXIMUM, maxHeight);
        Rect rightSize = getChild(1).getBoundingBox(NO_MAXIMUM, maxHeight);
        
        // Calculate the width and height the operator wants to take
        int operatorSize = Math.min(leftSize.height(), rightSize.height()) * 2 / 3;

        // If we have no maximum width, we're done
        if(maxWidth == NO_MAXIMUM)
            return new Rect[] {new Rect(0, 0, operatorSize, operatorSize), leftSize, rightSize};

        // If we would get wider than the maximum width, shrink so we fit in
        if(leftSize.width() + operatorSize + rightSize.width() > maxWidth)
        {
            // Determine the maximum width for each operand
            final int totalWidth = leftSize.width() + operatorSize + rightSize.width();
            final float factor = ((float)(maxWidth))/((float)(totalWidth));
            
            // Set the new sizes
            leftSize.set(0, 0, (int)(leftSize.width()*factor), (int)(leftSize.height()*factor));
            rightSize.set(0, 0, (int)(rightSize.width()*factor), (int)(rightSize.height()*factor));
            
            // Calculate the new operator size
            operatorSize = Math.min(leftSize.height(), rightSize.height()) * 2 / 3;
       }
      
        // Return the sizes
        return new Rect[] {new Rect(0, 0, operatorSize, operatorSize), leftSize, rightSize};
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Get the sizes
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        Point center_one = getChild(0).getCenter(sizes[1].width(), sizes[1].height());
        Point center_two = getChild(1).getCenter(sizes[2].width(), sizes[2].height());
        final int centerY = Math.max(center_one.y, center_two.y);

        // Position the bounding box and return it
        sizes[0].offsetTo(sizes[1].width(), centerY - sizes[0].height() / 2);
        return new Rect[] {sizes[0]};
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes and the centers
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        Point center_one = getChild(0).getCenter(sizes[1].width(), sizes[1].height());
        Point center_two = getChild(1).getCenter(sizes[2].width(), sizes[2].height());
        final int centerX = this.getCenter(maxWidth,maxHeight).x;
        final int centerY = Math.max(center_one.y, center_two.y);
        
        // Translate the operand's bounding box
        if(index == 0)
            sizes[1].offsetTo(0, centerY-center_one.y);
        else
            sizes[2].offsetTo(sizes[0].width() + sizes[1].width(), centerY - center_two.y);
        
        this.getCenter(maxWidth, maxHeight).y = this.getCenter(maxWidth, maxHeight).y + centerY-center_two.y;

        // Return the requested bounding box
        return sizes[index + 1];
    }
    
    @Override
    public Point getCenter(int maxWidth, int maxHeight)
    {
    	Rect bounding_vertical = this.getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
    	Rect bounding_horizontal = this.getBoundingBox(maxWidth, maxHeight);
    	return new Point(bounding_horizontal.centerX(), bounding_vertical.centerY());
    }
    
    @Override
    public Rect getBoundingBox(int maxWidth, int maxHeight)
    {
        // Get the sizes
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        return new Rect(0, 0, sizes[0].width() + sizes[1].width() + sizes[2].width(), Math.max(sizes[1].height(), sizes[2].height()));
    }
}
