package org.teaminfty.math_dragon;

import android.graphics.Paint;
import android.graphics.Rect;

/** This class draws binary operations that are written linear.
 * That is, operations which are written like '&lt;left operand&gt; &lt;operator&gt; &lt;right operand&gt;'.
 * For example: the add or subtract operation.
 */
public abstract class MathBinaryOperationLinear extends MathObject
{

    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** Constructor
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathBinaryOperationLinear(int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
        
        // We have two children
        children.add(null);
        children.add(null);
    }
    
    /** Returns the size of the operator bounding box
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the operator bounding box
     */
    protected Rect getOperatorSize(int maxWidth, int maxHeight)
    {
        // Get the bounding box both operands want to take
        Rect leftSize = getChild(0) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(NO_MAXIMUM, maxHeight);
        Rect rightSize = getChild(1) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(NO_MAXIMUM, maxHeight);

        // Determine a new maxHeight (if the new maxHeight would be smaller)
        if(maxHeight == NO_MAXIMUM || maxHeight > Math.min(leftSize.height(), rightSize.height()) * 2 / 3)
            maxHeight = Math.min(leftSize.height(), rightSize.height()) * 2 / 3;
        
        // Return a square that fits in the given maxWidth and maxHeight
        return getRectBoundingBox(maxWidth, maxHeight, 1);
    }

    /** Returns the size of the child bounding boxes
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    protected Rect[] getChildrenSize(int maxWidth, int maxHeight)
    {
        // Get the width of the operator
        final int operatorWidth = getOperatorSize(maxWidth, maxHeight).width();
        
        // Get the bounding box both operands want to take
        Rect leftSize = getChild(0) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(NO_MAXIMUM, maxHeight);
        Rect rightSize = getChild(1) == null ? getRectBoundingBox(NO_MAXIMUM, maxHeight, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(NO_MAXIMUM, maxHeight);
        
        // If width restrictions are given and we would get wider than the maximum width, shrink so we fit in
        if(maxWidth != NO_MAXIMUM && leftSize.width() + operatorWidth + rightSize.width() > maxWidth)
        {
            // Determine the maximum width for each operator
            final int leftMax = (maxWidth - operatorWidth) * leftSize.width() / (leftSize.width() + rightSize.width());
            final int rightMax = (maxWidth - operatorWidth) * rightSize.width() / (leftSize.width() + rightSize.width());
            
            // Determine the new bounding box for each operand
            leftSize = getChild(0) == null ? getRectBoundingBox(leftMax, maxHeight, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(leftMax, maxHeight);
            rightSize = getChild(1) == null ? getRectBoundingBox(rightMax, maxHeight, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(rightMax, maxHeight);
        }
        
        // Return the sizes
        return new Rect[]{ leftSize, rightSize };
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Get a square that fits in the given maxWidth and maxHeight
        Rect out = getOperatorSize(maxWidth, maxHeight);
        
        // Get the sizes of the children
        Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
        
        // Position the bounding box and return it
        final int totalHeight = Math.max(out.height(), Math.max(childrenSize[0].height(), childrenSize[1].height()));
        out.offsetTo(getChildBoundingBox(0, maxWidth, maxHeight).width(), (totalHeight - out.height()) / 2);
        return new Rect[]{ out };
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the size of the operator
        final Rect operatorSize = getOperatorSize(maxWidth, maxHeight);
        
        // Get the sizes of the children
        Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);

        // Position the bounding boxes of both children
        final int totalHeight = Math.max(operatorSize.height(), Math.max(childrenSize[0].height(), childrenSize[1].height()));
        childrenSize[0].offsetTo(0, (totalHeight - childrenSize[0].height()) / 2);
        childrenSize[1].offsetTo(childrenSize[0].width() + operatorSize.width(), (totalHeight - childrenSize[1].height()) / 2);
        
        // Return the requested bounding box
        return childrenSize[index];
    }

}
