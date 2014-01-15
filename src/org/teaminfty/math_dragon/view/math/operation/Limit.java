package org.teaminfty.math_dragon.view.math.operation;

import java.util.Arrays;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Ternary mathematical operation that implements a mathematical limit using a
 * starting expression into an ending expression upon a mathematical expression.
 * 
 * @author Folkert van Verseveld
 */
public class Limit extends Operation
{
    public static final String TYPE = "limit";
    
    private String name = "lim";
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    private final float PARENTHESES_RATIO = 0.5f / 1.61803398874989f;
    
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** Constructor
     * @param start What variable should be approach the value of <tt>end</tt>
     * @param end The value that <tt>start</tt> should approach
     * @param expression The expression to calculate the limit of
     */
    public Limit(Expression start, Expression end, Expression expression)
    {
        super(Arrays.asList(start, end, expression));
    }
    
    /** Default constructor */
    public Limit()
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
    public Expression getStart()
    {
        return getChild(0);
    }

    /**
     * Retrieve the mathematical end state of this limit. E.g. <tt>34</tt>.
     * @return A mathematical expression indicating the end state.
     * @see #getStart()
     */
    public Expression getEnd()
    {
        return getChild(1);
    }

    /**
     * Retrieve the mathematical expression the limit manipulates upon.
     * @return A mathematical expression.
     */
    public Expression getExpression()
    {
        return getChild(2);
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
        Rect Base = getChild(2).getBoundingBox();
        
        // Return the Sizes
        return new Rect[] {leftSize, rightSize, Base};
    }
    
    
    /**
     * Assign a mathematical expression indicating the start state of this limit.
     * @param start The start state.
     */
    public void setStart(Expression start)
    {
        setChild(0, start);
    }
    
    /**
     * Assign a mathematical expression indicating the end state of this limit.
     * @param end The end state.
     */
    public void setEnd(Expression end)
    {
        setChild(1, end);
    }
    
    //Complete bounding box
    @Override
    public Rect getBoundingBox()
    {
        Rect[] operatorSizes = getOperatorBoundingBoxes();
        Rect[] child = this.getChildrenSize();
        
        return new Rect(0,Math.min(operatorSizes[0].top, child[2].top), operatorSizes[0].width() + operatorSizes[2].width() + operatorSizes[3].width() + child[2].width(),Math.max(operatorSizes[0].bottom + child[1].bottom, child[2].bottom));
    }

    
    /**
     * Assign a mathematical expression that this limit manipulates.
     * @param expr A mathematical expression.
     */
    public void setExpression(Expression expr)
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
    /** Calculates the right text size for the given level
     * @return The right text size for the given level */
    protected float findTextSize()
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, level + 1);
    }   
    
    
    /** Calculates the size of this function when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathConstant}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        operatorPaint.setTextSize(fontSize);

        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        operatorPaint.getTextBounds(this.name, 0, this.name.length(), out);
        out.offsetTo(0, 0);
        
        // Return the size
        return out;
    }
    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
    	  // Get the sizes
        final Rect[] child = getChildrenSize();
        final int parentheseWidth = (int)(child[1].height() * PARENTHESES_RATIO);
        Rect textBounding = getSize(findTextSize());
        final int arrowWidth = textBounding.width() - child[0].width() - child[1].width();
        
        // Make sure everything is aligned nicely
        final int childCenterY = getChild(1).getCenter().y;
        final int childTop = Math.max(textBounding.centerY() - childCenterY, 0);
        textBounding.offsetTo(0, Math.max(childCenterY - textBounding.centerY(), 0));
        
        // Return the bounding boxes
        return new Rect[]{ textBounding,
        		 new Rect(child[0].width(), textBounding.bottom, arrowWidth, child[0].height()),
                 new Rect(textBounding.width() + child[1].width(), childTop, textBounding.width() + parentheseWidth +child[1].width(), childTop + child[1].height()), 
                 new Rect(textBounding.width() + parentheseWidth +  child[2].width(), childTop, textBounding.width() + child[2].width() + 2 * parentheseWidth, childTop + child[1].height())};
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
    	// Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes
        Rect[] operatorSize = getOperatorBoundingBoxes();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        Rect mainChild = getChild(2).getBoundingBox();
        
        // Translate and return the operand's bounding box
        if(index == 0)
        {
            leftChild.offsetTo(0, operatorSize[0].height());
            return leftChild;
        }
        else if(index == 1)
        {
            rightChild.offsetTo(operatorSize[1].width() + leftChild.width(), operatorSize[0].height());
            return rightChild;
        }
        else 
        {
        	mainChild.offsetTo(operatorSize[0].width() + operatorSize[2].width(), 0);
        	return mainChild;
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        throw new RuntimeException("stub");
    }
}
