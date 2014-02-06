package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.operation.Binary;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * This class draws binary operations that are written linear. That is,
 * operations which are written like '&lt;left operand&gt; &lt;operator&gt;
 * &lt;right operand&gt;'. For example: the add or subtract operation.
 */
public abstract class Linear extends Binary
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** The operatorSize */
    protected Rect operatorSize;
    
    /** Default constructor */
    public Linear()
    { }
    
    public Linear(Expression left, Expression right)
    {
    	super(left, right);
    }

    /** Returns the size of the operator
     * @return The size of the operator */
    protected Rect getOperatorSize()
    {
        final int size = (int) (defaultHeight * Math.pow(2.0 / 3.0, level + 1));
        return new Rect(0, 0, size, size);
    }

    @Override
    public Rect[] calculateOperatorBoundingBoxes()
    {
        // Get the operator size and the size of the left child
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChild(0).getBoundingBox();
        
        // Determine the centre y-coordinate of the operator
        final int centerY = Math.max(operatorSize.height() / 2, Math.max(getChild(0).getCenter().y, getChild(1).getCenter().y));

        // Position the bounding box and return it
        operatorSize.offsetTo(leftChild.width(), centerY - operatorSize.height() / 2);
        return new Rect[] { operatorSize };
    }
    
    @Override
    public Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Check if the child exists
        this.checkChildIndex(index);
        
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize();
        Rect operatorSize = getOperatorSize();
        
        // Move the bounding boxes to the correct position
        final int centerY = getCenter().y;
        childrenSize[0].offsetTo(0, centerY - getChild(0).getCenter().y);
        childrenSize[1].offsetTo(childrenSize[0].width() + operatorSize.width(), centerY - getChild(1).getCenter().y);
        
        // Return the right bounding box
        return childrenSize[index];
    }
    
    @Override
    public void calculateAllChildBoundingBox()
    {        
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize();
        Rect operatorSize = getOperatorSize();
        
        // Move the bounding boxes to the correct position
        final int centerY = getCenter().y;
        childrenSize[0].offsetTo(0, centerY - getChild(0).getCenter().y);
        childrenSize[1].offsetTo(childrenSize[0].width() + operatorSize.width(), centerY - getChild(1).getCenter().y);
        
        // Set the children
        childrenBoundingBoxes.add( childrenSize[0]);
        childrenBoundingBoxes.add( childrenSize[1]);
    }
    
    
    @Override
    public Point calculateCenter()
    {
    	  // Get the sizes
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        Rect betaRect = new Rect(0, 0, leftChild.width() + operatorSize.width() + rightChild.width(), Math.max(Math.max(leftChild.height(), rightChild.height()), operatorSize.height()));
    	return new Point(betaRect.centerX(), Math.max(operatorSize.height() / 2, Math.max(getChild(0).getCenter().y, getChild(1).getCenter().y)));
    }
    
    @Override
    public Rect calculateBoundingBox()
    {
        // Get the sizes
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChildBoundingBox(0);
        Rect rightChild = getChildBoundingBox(1);
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        return new Rect(0, 0, leftChild.width() + operatorSize.width() + rightChild.width(), Math.max(leftChild.bottom, rightChild.bottom));
    }
}
