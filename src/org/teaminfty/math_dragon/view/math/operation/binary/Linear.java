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
    public Rect[] getOperatorBoundingBoxes()
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
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        
        // Get the centers
        Point leftChildCenter = getChild(0).getCenter();
        Point rightChildCenter = getChild(1).getCenter();
        final int centerY = Math.max(operatorSize.height() / 2, Math.max(leftChildCenter.y, rightChildCenter.y));
        
        // Translate and return the operand's bounding box
        if(index == 0)
        {
            leftChild.offsetTo(0, centerY - leftChildCenter.y);
            return leftChild;
        }
        else
        {
            rightChild.offsetTo(leftChild.width() + operatorSize.width(), centerY - rightChildCenter.y);
            return rightChild;
        }
    }
    
    @Override
    public Point getCenter()
    {
    	  // Get the sizes
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        Rect betaRect = new Rect(0, 0, leftChild.width() + operatorSize.width() + rightChild.width(), Math.max(Math.max(leftChild.height(), rightChild.height()), operatorSize.height()));
    	return new Point(betaRect.centerX(), Math.max(getOperatorSize().height() / 2, Math.max(getChild(0).getCenter().y, getChild(1).getCenter().y)));
    }
    
    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect operatorSize = getOperatorSize();
        Rect leftChild = getChildBoundingBox(0);
        Rect rightChild = getChildBoundingBox(1);
        
        // Return a bounding box, containing the bounding boxes of the children and the operator
        return new Rect(0, 0, leftChild.width() + operatorSize.width() + rightChild.width(), Math.max(leftChild.bottom, rightChild.bottom));
    }
}
