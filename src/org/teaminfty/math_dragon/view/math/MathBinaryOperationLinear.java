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
    protected Rect[] getSizes()
    {
        // Get the size both operands want to take
        Rect leftSize = getChild(0).getBoundingBox();
        Rect rightSize = getChild(1).getBoundingBox();
        
        // Calculate the width and height the operator wants to take
        int operatorSize = Math.min(leftSize.height(), rightSize.height()) * 2 / 3;
        return new Rect[] {new Rect(0, 0, operatorSize, operatorSize), leftSize, rightSize};
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        // Get the sizes
        Rect[] sizes = getSizes();
        Point center_one = getChild(0).getCenter();
        Point center_two = getChild(1).getCenter();
        final int centerY = Math.max(center_one.y, center_two.y);

        // Position the bounding box and return it
        sizes[0].offsetTo(sizes[1].width(), centerY - sizes[0].height() / 2);
        return new Rect[] {sizes[0]};
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes and the centers
        Rect[] sizes = getSizes();
        Point center_one = getChild(0).getCenter();
        Point center_two = getChild(1).getCenter();
        final int centerY = Math.max(center_one.y, center_two.y);
        
        // Translate the operand's bounding box
        if(index == 0)
            sizes[1].offsetTo(center_one.x-sizes[1].width()/2, centerY-center_one.y);
       else
            sizes[2].offsetTo(sizes[1].width() + sizes[0].width() + center_two.x - sizes[2].width()/2, centerY - center_two.y);
        
        this.getCenter().y = this.getCenter().y + centerY-center_two.y;

        // Return the requested bounding box
        return sizes[index + 1];
    }
    
    @Override
    public Point getCenter()
    {
    	Rect bounding_vertical = this.getOperatorBoundingBoxes()[0];
    	Rect bounding_horizontal = this.getBoundingBox();
    	return new Point(bounding_horizontal.centerX(), bounding_vertical.centerY());
    }
    
    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getSizes();
        int width = sizes[0].width() + sizes[1].width() + sizes[2].width();
        int height = Math.max(sizes[1].height(), sizes[2].height());
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        return new Rect(0, 0, width, height);
    }
}
