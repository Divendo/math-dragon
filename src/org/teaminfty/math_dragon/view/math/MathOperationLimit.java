package org.teaminfty.math_dragon.view.math;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Ternary mathematical operation that implements a mathematical limit using a
 * starting expression into an ending expression upon a mathematical expression.
 * 
 * @author Folkert van Verseveld
 */
public class MathOperationLimit extends MathOperation
{
    public static final String TYPE = "limit";
    
    /** Constructor
     * @param start What variable should be approach the value of <tt>end</tt>
     * @param end The value that <tt>start</tt> should approach
     * @param expression The expression to calculate the limit of
     */
    public MathOperationLimit(MathObject start, MathObject end, MathObject expression)
    {
        super(Arrays.asList(start, end, expression));
    }
    
    /** Default constructor */
    public MathOperationLimit()
    { this(null, null, null); }
    
    public String toString()
    {
        return "lim(" + getStart() + "->" + getEnd() + "," + getExpression() + ")";
    }

    /**
     * Retrieve the mathematical start state of this limit. E.g. <tt>x</tt>.
     * @return A mathematical expression indicating the start state.
     * @see #getEnd()
     */
    public MathObject getStart()
    {
        return getChild(0);
    }

    /**
     * Retrieve the mathematical end state of this limit. E.g. <tt>34</tt>.
     * @return A mathematical expression indicating the end state.
     * @see #getStart()
     */
    public MathObject getEnd()
    {
        return getChild(1);
    }

    /**
     * Retrieve the mathematical expression the limit manipulates upon.
     * @return A mathematical expression.
     */
    public MathObject getExpression()
    {
        return getChild(2);
    }
    
    /**
     * Assign a mathematical expression indicating the start state of this limit.
     * @param start The start state.
     */
    public void setStart(MathObject start)
    {
        setChild(0, start);
    }
    
    /**
     * Assign a mathematical expression indicating the end state of this limit.
     * @param end The end state.
     */
    public void setEnd(MathObject end)
    {
        setChild(1, end);
    }
    
    /**
     * Assign a mathematical expression that this limit manipulates.
     * @param expr A mathematical expression.
     */
    public void setExpression(MathObject expr)
    {
        setChild(2, expr);
    }

    @Override
    protected String getType()
    {
        return TYPE;
    }

    @Override
    protected void writeChildrenToXML(Document doc, Element el)
    {
        getStart().writeToXML(doc, el);
        getEnd().writeToXML(doc, el);
        getExpression().writeToXML(doc, el);
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("stub");
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("stub");
    }

    @Override
    public void draw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("stub");
    }
}
