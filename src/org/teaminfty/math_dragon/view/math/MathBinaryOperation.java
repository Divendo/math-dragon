package org.teaminfty.math_dragon.view.math;

import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class draws binary operations and provides simply functions to modify
 * both mathematical expressions.
 * @author Folkert van Verseveld
 * @see MathBinaryOperationLinear
 */

public abstract class MathBinaryOperation extends MathOperation
{	
    public MathBinaryOperation()
    {
        this(null, null);
    }

    public MathBinaryOperation(MathObject left, MathObject right)
    {
        // We have two children
        children.add(new MathObjectEmpty());
        children.add(new MathObjectEmpty());
        
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
    
    protected final void writeChildrenToXML(Document doc, Element el)
    {
        getLeft().writeToXML(doc, el);
        getRight().writeToXML(doc, el);
    }
}
