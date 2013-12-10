package org.teaminfty.math_dragon;

import android.graphics.Paint;
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
        Rect leftSize = getChild(0) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(NO_MAXIMUM, maxHeight);
        Rect rightSize = getChild(1) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(NO_MAXIMUM, maxHeight);
        
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
            final int leftMax = maxWidth * leftSize.width() / totalWidth;
            final int rightMax = maxWidth * rightSize.width() / totalWidth;
            
            // Set the new sizes
            leftSize.set(0, 0, leftMax, leftMax * leftSize.height() / leftSize.width());
            rightSize.set(0, 0, rightMax, rightMax * rightSize.height() / rightSize.width());
            
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

        // Position the bounding box and return it
        final int totalHeight = Math.max(sizes[1].height(), sizes[2].height());
        sizes[0].offsetTo(sizes[1].width(), (totalHeight - sizes[0].height()) / 2);
        return new Rect[] {sizes[0]};
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes and the total height
        Rect[] sizes = getSizes(maxWidth, maxHeight);
        final int totalHeight = Math.max(sizes[1].height(), sizes[2].height());
        
        // Translate the operand's bounding box
        if(index == 0)
            sizes[1].offsetTo(0, (totalHeight - sizes[1].height()) / 2);
        else
            sizes[2].offsetTo(sizes[0].width() + sizes[1].width(), (totalHeight - sizes[2].height()) / 2);

        // Return the requested bounding box
        return sizes[index + 1];
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
