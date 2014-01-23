package org.teaminfty.math_dragon.view.math.operation;

import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Symbol;
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
        super(3);
        
        setChildWithoutRefresh(0, start);
        setChildWithoutRefresh(1, end);
        setChildWithoutRefresh(2, expression);
        setAll(level, defaultHeight, false);
        
        levelDeltas = new int[] {2, 2, 0};
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
        if (end != null)
        {
            if (!(end instanceof Symbol))
            {
                throw new IllegalArgumentException("'end' should be null or a symbolic constant");
            }
            Symbol sym = (Symbol) end;
            if (sym.getVarCount() > 1)
            {
                throw new IllegalArgumentException("multiple variables not allowed for 'over'");
            }
        }
        setChild(1, end);
    }
    
    //Complete bounding box
    @Override
    public Rect getBoundingBox()
    {
        Rect[] operatorSizes = getOperatorBoundingBoxes();
        Rect[] child = this.getChildrenSize();
        
        return new Rect(Math.min(operatorSizes[0].left, getChildBoundingBox(0).left),Math.min(operatorSizes[0].top, child[2].top), operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + child[2].width(),Math.max(operatorSizes[0].bottom + child[1].bottom, child[2].bottom));
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
    public Rect[] calculateOperatorBoundingBoxes()
    {
    	  // Get the sizes
        final Rect[] child = getChildrenSize();
        final int parentheseWidth = (int)(child[1].height() * PARENTHESES_RATIO);
        Rect textBounding = getSize(findTextSize());
        final int arrowWidth = textBounding.width()/4;
        
        // Make sure everything is aligned nicely
        final int childCenterY = getChild(2).getCenter().y;
        final int childTop = Math.max(textBounding.centerY() - childCenterY, 0);
        textBounding.offsetTo(0,this.getChild(2).getCenter().y - textBounding.centerY());
        
        // Return the bounding boxes
        return new Rect[]{ textBounding,
                 new Rect(textBounding.width(), childTop, textBounding.width() + parentheseWidth, childTop + child[2].height()), 
                 new Rect(textBounding.width() + parentheseWidth +  child[2].width(), childTop, textBounding.width() + child[2].width() + 2 * parentheseWidth, childTop + child[2].height()),
                 new Rect(textBounding.centerX() - arrowWidth, textBounding.bottom,textBounding.centerX() + arrowWidth, child[0].height()),};
    }

    @Override
    public Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException
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
            leftChild.offsetTo(operatorSize[0].centerX() - operatorSize[3].width()/2 - leftChild.width(),operatorSize[0].bottom);
            return leftChild;
        }
        else if(index == 1)
        {
            rightChild.offsetTo(operatorSize[0].centerX() + operatorSize[3].width()/2, operatorSize[0].bottom);
            return rightChild;
        }
        else 
        {
        	mainChild.offsetTo(operatorSize[0].width() + operatorSize[2].width(), 0);
        	return mainChild;
        }
    }
    
    @Override
    public void calculateAllChildBoundingBox()
    {
        // Get the sizes
        Rect[] operatorSize = getOperatorBoundingBoxes();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        Rect mainChild = getChild(2).getBoundingBox();
        
        // Translate and return the operand's bounding box
        leftChild.offsetTo(operatorSize[0].centerX() - operatorSize[3].width()/2 - leftChild.width(),operatorSize[0].bottom);
        rightChild.offsetTo(operatorSize[0].centerX() + operatorSize[3].width()/2, operatorSize[0].bottom);
        mainChild.offsetTo(operatorSize[0].width() + operatorSize[2].width(), 0);
        
        // Add the bounding boxes
        childrenBoundingBoxes.add( leftChild);
        childrenBoundingBoxes.add( rightChild);
        childrenBoundingBoxes.add( mainChild);
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
        operatorPaint.getTextBounds(name, 0, name.length(), textBounding);
        canvas.translate((operatorBounding[0].width() - textBounding.width()) / 2, (operatorBounding[0].height() - textBounding.height()) / 2);
        canvas.drawText(name, operatorBounding[0].left - textBounding.left, operatorBounding[0].top - textBounding.top, operatorPaint);
        canvas.drawLine(operatorBounding[3].left, operatorBounding[3].bottom - operatorBounding[3].height()/2, operatorBounding[3].right, operatorBounding[3].bottom - operatorBounding[3].height()/2, operatorPaint);
        canvas.restore();
        

        // Use stroke style for the parentheses
        operatorPaint.setStyle(Paint.Style.STROKE);
        
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
