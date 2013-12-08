package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class draws binary operations that are written linear.
 * That is, operations which are written like '&lt;left operand&gt; &lt;operator&gt; &lt;right operand&gt;'.
 * For example: the add or subtract operation.
 */
public class MathOperationDivide extends MathObject
{

    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** Constructor
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathOperationDivide(int defWidth, int defHeight)
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
        // Get the size of the children
        final Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);

        // Return the operator size
        return new Rect(0, 0, Math.max(childrenSize[0].width(), childrenSize[1].width()),
                Math.max(5, maxHeight == NO_MAXIMUM ? maxHeight / 20 : (childrenSize[0].height() + childrenSize[1].height()) / 20));
    }

    /** Returns the size of the child bounding boxes
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    protected Rect[] getChildrenSize(int maxWidth, int maxHeight)
    {
        // Get the bounding box both operands want to take
        Rect topSize = getChild(0) == null ? getRectBoundingBox(maxWidth, NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(maxWidth, NO_MAXIMUM);
        Rect bottomSize = getChild(1) == null ? getRectBoundingBox(maxWidth, NO_MAXIMUM, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(maxWidth, NO_MAXIMUM);
        
        // Get the operator height
        final int operatorHeight = Math.max(5, maxHeight == NO_MAXIMUM ? maxHeight / 20 : (topSize.height() + bottomSize.height()) / 20);
        
        // If width restrictions are given and we would get wider than the maximum height, shrink so we fit in
        if(maxHeight != NO_MAXIMUM && topSize.height() + operatorHeight + bottomSize.height() > maxHeight)
        {
            // Determine the maximum height for each operator
            final int topMax = (maxHeight - operatorHeight) * topSize.height() / (topSize.height() + bottomSize.height());
            final int bottomMax = (maxHeight - operatorHeight) * bottomSize.height() / (topSize.height() + bottomSize.height());
            
            // Determine the new bounding box for each operand
            topSize = getChild(0) == null ? getRectBoundingBox(maxWidth, topMax, EMPTY_CHILD_RATIO) : getChild(0).getBoundingBox(maxWidth, topMax);
            bottomSize = getChild(1) == null ? getRectBoundingBox(maxWidth, bottomMax, EMPTY_CHILD_RATIO) : getChild(1).getBoundingBox(maxWidth, bottomMax);
        }
        
        // Return the sizes
        return new Rect[]{ topSize, bottomSize };
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Get the sizes of the children
        Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
        
        // Position the bounding box and return it
        final int totalWidth = Math.max(childrenSize[0].width(), childrenSize[1].width());
        
        // Get a square that fits in the given maxWidth and maxHeight
        Rect out = getOperatorSize(totalWidth, maxHeight);
        out.offsetTo(0, getChildBoundingBox(0,maxWidth,maxHeight).height());
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
        childrenSize[0].offsetTo((operatorSize.width() - childrenSize[0].width()) / 2, 0);
        childrenSize[1].offsetTo((operatorSize.width() - childrenSize[1].width()) / 2, childrenSize[0].height() + operatorSize.height());
        
        // Return the requested bounding box
        return childrenSize[index];
    }
    
    protected void checkChildren() throws EmptyChildException
    {
        if(getChild(0) == null)
            throw new EmptyChildException(0);
        if(getChild(1) == null)
            throw new EmptyChildException(1);
    }
    
    @Override
    public IExpr eval() throws EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return F.Divide(getChild(0).eval(), getChild(1).eval());
    }
 @Override
    public double approximate() throws NotConstantException, EmptyChildException
    {
        // Check if the children are not empty
        this.checkChildren();
        
        // Return the result
        return getChild(0).approximate() / getChild(1).approximate();
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Get the bounding boxes
        final Rect operator = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        final Rect leftChild = getChildBoundingBox(0, maxWidth, maxHeight);
        final Rect rightChild = getChildBoundingBox(1, maxWidth, maxHeight);
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(getColor());
        canvas.drawRect(operator.left, operator.top + operator.height() / 3, operator.right, operator.bottom - operator.height() / 3, operatorPaint);
        canvas.restore();
        
        // Draw the left child
        canvas.save();
        canvas.translate(leftChild.left, leftChild.top);
        if(getChild(0) != null)
            getChild(0).draw(canvas, leftChild.width(), leftChild.height());
        else
        {
            leftChild.offsetTo(0, 0);
            drawEmptyChild(canvas, leftChild);
        }
        canvas.restore();

        // Draw the right child
        canvas.save();
        canvas.translate(rightChild.left, rightChild.top);
        if(getChild(1) != null)
            getChild(1).draw(canvas, rightChild.width(), rightChild.height());
        else
        {
            rightChild.offsetTo(0, 0);
            drawEmptyChild(canvas, rightChild);
        }
        canvas.restore();
    }
    

}
