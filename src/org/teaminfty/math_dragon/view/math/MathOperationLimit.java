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

    public MathOperationLimit(MathObject start, MathObject end,
            MathObject expression)
    {
        super(Arrays.asList(start, end, expression));
    }
    
    public String toString()
    {
        return "lim(" + getStart() + "->" + getEnd() + "," + getExpression() + ")";
    }

    public MathObject getStart()
    {
        return getChild(0);
    }

    public MathObject getEnd()
    {
        return getChild(1);
    }

    public MathObject getExpression()
    {
        return getChild(2);
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
