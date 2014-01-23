package org.teaminfty.math_dragon.view.math.operation;

import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.operation.binary.Linear;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Rect;

/**
 * This class draws binary operations and provides simply functions to modify
 * both mathematical expressions.
 * @author Folkert van Verseveld
 * @see Linear
 */

public abstract class Binary extends Operation
{	
    public Binary()
    {
        this(null, null);
    }

    public Binary(Expression left, Expression right)
    {
        // We have two children
        children.add(new Empty());
        children.add(new Empty());
        
        // Set the left and right child
        set(left, right);
    }
    
    public String toString()
    {
        return "(" + getLeft().toString() + "," + getRight().toString() + ")";
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
    public void set(Expression left, Expression right)
    {
        setChildWithoutRefresh(0, left);
        setChildWithoutRefresh(1, right);
        setAll(level, defaultHeight, false);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the left.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setLeft(Expression o)
    {
        setChild(0, o);
    }

    /**
     * Retrieve the mathematical expression to the left. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the left.
     */
    public Expression getLeft()
    {
        return getChild(0);
    }

    /**
     * Assign <tt>o</tt> to mathematical expression to the right.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setRight(Expression o)
    {
        setChild(1, o);
    }

    /**
     * Retrieve the mathematical expression to the right. <b>Note:</b>
     * <tt>null</tt> may be returned.
     * 
     * @return The mathematical expression to the right.
     */
    public Expression getRight()
    {
        return getChild(1);
    }
    
    /**
     * Returns the sizes of the bounding of the children.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    public Rect[] getChildrenSize()
    {
        // Get the sizes both operands want to take
        Rect leftSize = getChild(0).getBoundingBox();
        Rect rightSize = getChild(1).getBoundingBox();
        
        // Return the Sizes
        return new Rect[] {leftSize, rightSize};
    }
    
    protected final void writeChildrenToXML(Document doc, Element el)
    {
        getLeft().writeToXML(doc, el);
        getRight().writeToXML(doc, el);
    }
}
