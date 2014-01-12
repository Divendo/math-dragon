package org.teaminfty.math_dragon.view.math;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Ternary mathematical operation that implements a mathematical limit using a
 * starting expression into an ending expression upon a mathematical expression.
 * 
 * @author Folkert van Verseveld
 */
public class MathOperationLimit extends MathObject
{
	/**the name of the type of the operation*/
    public static final String TYPE = "limit";
    
    /**the name of the operation as it will be put on the screen*/
    public final String name = "log";
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    private final float PARENTHESES_RATIO = 0.5f / 1.61803398874989f;
    
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** sorry */
    /*
    /** Constructor
     * @param start What variable should be approach the value of <tt>end</tt>
     * @param end The value that <tt>start</tt> should approach
     * @param expression The expression to calculate the limit of
     
    public MathOperationLimit(MathObject start, MathObject end, MathObject expression)
    {
        super(Arrays.asList(start, end, expression));
    }*/
    
    /** Default constructor */
    public MathOperationLimit()
    { 
    		
    }
    
    public String toString()
    {
        return "lim" + getStart() + "->" + getEnd() + "," + getExpression() + ")";
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
/*
    @Override
    protected void writeChildrenToXML(Document doc, Element el)
    {
        getStart().writeToXML(doc, el);
        getEnd().writeToXML(doc, el);
        getExpression().writeToXML(doc, el);
    } */
    
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
        
        // Make sure everything is aligned nicely
        final int childCenterY = getChild(1).getCenter().y;
        final int childTop = Math.max(textBounding.centerY() - childCenterY, 0);
        textBounding.offsetTo(0, Math.max(childCenterY - textBounding.centerY(), 0));
        
        // Return the bounding boxes
        return new Rect[]{ textBounding,
        		 new Rect(textBounding.left, textBounding.top, textBounding.right - child[0].width(), textBounding.height()/3),
                 new Rect(textBounding.width() + child[0].width(), childTop, textBounding.width() + child[0].width() + parentheseWidth, childTop + child[1].height()), 
                 new Rect(textBounding.width() + parentheseWidth + child[0].width() + child[1].width(), childTop, textBounding.width() + child[0].width() + child[1].width() + 2 * parentheseWidth, childTop + child[1].height())};
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
        
        // Translate and return the operand's bounding box
        if(index == 0)
        {
            leftChild.offsetTo(operatorSize[1].width(), operatorSize[0].height());
            return leftChild;
        }
        else
        {
            rightChild.offsetTo(operatorSize[0].width() + operatorSize[2].width(), 0);
            return rightChild;
        }
    }
	
    //Complete bounding box
    @Override
    public Rect getBoundingBox()
    {
        Rect[] operatorSizes = getOperatorBoundingBoxes();
        Rect[] child = this.getChildrenSize();
        
        return new Rect(0,Math.min(operatorSizes[0].top, child[1].top), operatorSizes[0].width() + operatorSizes[2].width() + operatorSizes[2].width() + child[1].width(), child[0].bottom + child[1].bottom);
    }


    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes 
        drawBoundingBoxes(canvas);
        
        // Set the right values for the paint
        operatorPaint.setColor(getColor());
        operatorPaint.setTextSize(findTextSize());
        
        // Get our operator bounding boxes
        Rect[] operatorBounding = this.getOperatorBoundingBoxes();
        
        //Draws the operator
        canvas.save();
        Rect textBounding = new Rect();
        operatorPaint.getTextBounds(this.name, 0, this.name.length(), textBounding);
        canvas.translate((operatorBounding[0].width() - textBounding.width()) / 2, (operatorBounding[0].height() - textBounding.height()) / 2);
        canvas.drawText(name, operatorBounding[0].left - textBounding.left, operatorBounding[0].top - textBounding.top, operatorPaint);
        canvas.restore();
        

        // Use stroke style for the parentheses
        operatorPaint.setStyle(Paint.Style.STROKE);
        operatorPaint.setColor(getColor());
        
        // Draw the left bracket
        canvas.save();
        canvas.clipRect(operatorBounding[1], Region.Op.INTERSECT);
        RectF bracket = new RectF(operatorBounding[1]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        canvas.clipRect(operatorBounding[2], Region.Op.INTERSECT);
        bracket = new RectF(operatorBounding[2]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, operatorPaint);
        canvas.restore();

        // Set the paint back to fill style
        operatorPaint.setStyle(Paint.Style.FILL);
        
        // Draw the children
        drawChildren(canvas);
    }
}
