package org.teaminfty.math_dragon;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * This class draws binary operations and provides simply functions to modify
 * both mathematical expressions.
 * @author Folkert van Verseveld
 * @see MathBinaryOperationLinear
 */

public abstract class MathBinaryOperation extends MathObject
{
    public MathBinaryOperation(int defWidth, int defHeight)
    {
        this(null, null, defWidth, defHeight);
    }

    public MathBinaryOperation(MathObject left, MathObject right, int defWidth,
            int defHeight)
    {
        super(defWidth, defHeight);

        // We have two children
        children.add(null);
        children.add(null);
        
        set(left, right);
    }
    
    /**
     * Ensure both children are not empty. If at least one of them is empty,
     * <tt>EmptyChildException</tt> will be thrown with the empty index number.
     * @throws EmptyChildException Thrown when at least one of both children is
     * empty.
     */
    protected void checkChildren() throws EmptyChildException
    {
        if(getChild(0) == null)
            throw new EmptyChildException(0);
        if(getChild(1) == null)
            throw new EmptyChildException(1);
    }
    /**
     * Set mathematical expression to the left and to the right to new specified
     * mathematical objects.
     * 
     * @param left
     *        The mathematical expression to the left.
     * @param right
     *        The mathematical expression to the right.
     */
    public void set(MathObject left, MathObject right)
    {
        setChild(0, left);
        setChild(1, right);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the left.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setLeft(MathObject o)
    {
        setChild(0, o);
    }

    /**
     * Retrieve the mathematical expression to the left. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the left.
     */
    public MathObject getLeft()
    {
        return getChild(0);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the right.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setRight(MathObject o)
    {
        setChild(1, o);
    }

    /**
     * Retrieve the mathematical expression to the right. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the right.
     */
    public MathObject getRight()
    {
        return getChild(1);
    }
    
    /**
     * Draw left child on <tt>canvas</tt> within the specified bounding box.
     * @param canvas The graphical instance to draw on.
     * @param box The bounding box.
     */
    protected void drawLeft(Canvas canvas, final Rect box)
    {
        // Draw the left child
        canvas.save();
        canvas.translate(box.left, box.top);
        if(getChild(0) != null)
            getChild(0).draw(canvas, box.width(), box.height());
        else
        {
            box.offsetTo(0, 0);
            drawEmptyChild(canvas, box);
        }
        canvas.restore();
    }
    /**
     * Draw right child on <tt>canvas</tt> within the specified bounding box.
     * @param canvas The graphical instance to draw on.
     * @param box The bounding box.
     */
    protected void drawRight(Canvas canvas, final Rect box)
    {
        // Draw the right child
        canvas.save();
        canvas.translate(box.left, box.top);
        if(getChild(1) != null)
            getChild(1).draw(canvas, box.width(), box.height());
        else
        {
            box.offsetTo(0, 0);
            drawEmptyChild(canvas, box);
        }
        canvas.restore();
    }
}
