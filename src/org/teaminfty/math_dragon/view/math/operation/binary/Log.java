package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Precedence;
import org.teaminfty.math_dragon.view.math.operation.Binary;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Mathematical function that takes only one argument. Currently, only
 * trigonometric functions and the natural logarithm are implemented.
 * 
 * @author denu12
 * @author Divendo
 * @author FolkertVanVerseveld
 */
public class Log extends Binary
{
    /** the Type of the operation */
	public final static String TYPE = "log";
	
    /** The name of the function type */
    private String name;
    
    /** The xml-safe name of the function type */
    private String xmlName;

    /** Returns the name of the function type
     * @return The name of the function type as a string (may contain high unicode characters) */
    public String getName()
    { return name; }

    /** Returns the XML safe name of the function type
     * @return The XML safe name of the function type as a string */
    public String getXmlName()
    { return xmlName; }
    
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    private final float PARENTHESES_RATIO = 0.5f / 1.61803398874989f;
    
    /** Default constructor */
    public Log()
    { this(null, null); }

    /** Constructor */
    public Log(Expression base, Expression parameter)
    {
        super(base, parameter);
        this.name = "log";
        
        levelDeltas = new int[] {2, 0};
        
        initPaint();
    }
    
    /** Initialises the paint */
    private void initPaint()
    {
        operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
        operatorPaint.setAntiAlias(true);
    }
    
    @Override
    public String toString()
    {
        String baseString = getChild(0).toString();
        if(baseString.startsWith("(") && baseString.endsWith(")"))
            baseString = baseString.substring(1, baseString.length() - 1);
        
        String argString = getChild(0).toString();
        if(argString.startsWith("(") && argString.endsWith(")"))
            argString = argString.substring(1, argString.length() - 1);
        
        return "log(" + baseString + ',' + argString + ')';
    }
    
    public int getPrecedence()
    { return Precedence.FUNCTION; }

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
        
        // Make sure everything is aligned nicely
        final int childCenterY = getChild(1).getCenter().y;
        final int childTop = Math.max(textBounding.centerY() - childCenterY, 0);
        textBounding.offsetTo(0, Math.max(childCenterY - textBounding.centerY(), 0));
        
        // Return the bounding boxes
        return new Rect[]{ textBounding,
                 new Rect(textBounding.width() + child[0].width(), childTop, textBounding.width() + child[0].width() + parentheseWidth, childTop + child[1].height()), 
                 new Rect(textBounding.width() + parentheseWidth + child[0].width() + child[1].width(), childTop, textBounding.width() + child[0].width() + child[1].width() + 2 * parentheseWidth, childTop + child[1].height())};
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
        
        // Translate and return the operand's bounding box
        if(index == 0)
        {
            leftChild.offsetTo(operatorSize[0].width(), rightChild.height() - leftChild.height()/3);
            return leftChild;
        }
        else
        {
            rightChild.offsetTo(operatorSize[0].width() + operatorSize[1].width() + leftChild.width(), 0);
            return rightChild;
        }
    }
    
    @Override
    public void calculateAllChildBoundingBox()
    {
        // Get the sizes
        Rect[] operatorSize = getOperatorBoundingBoxes();
        Rect leftChild = getChild(0).getBoundingBox();
        Rect rightChild = getChild(1).getBoundingBox();
        
        // Translate and return the operand's bounding box
        leftChild.offsetTo(operatorSize[0].width(), rightChild.height() - leftChild.height()/3);
        rightChild.offsetTo(operatorSize[0].width() + operatorSize[1].width() + leftChild.width(), 0);
        
        // Add the bounding boxes
        childrenBoundingBoxes.add( leftChild);
        childrenBoundingBoxes.add( rightChild);
    }
    
    //Complete bounding box
    @Override
    public Rect calculateBoundingBox()
    {
        Rect[] operatorSizes = getOperatorBoundingBoxes();
        Rect[] child = this.getChildrenSize();
        
        return new Rect(0,Math.min(operatorSizes[0].top, child[1].top), operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + child[0].width() + child[1].width(), child[0].bottom + child[1].bottom- child[0].height()/3);
    }

    
    @Override
    public Point calculateCenter()
    {        
        return new Point(this.getBoundingBox().centerX(), getChild(1).getCenter().y);
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
        operatorPaint.setStrokeWidth(lineWidth);
        
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
    
    /** The name of the XML node for this class */
    public static final String NAME = "function";
    
    /** The XML attribute for which type of function this is */
    public static final String ATTR_TYPE = "type";

	@Override
	protected String getType() {return TYPE;}

    @Override
    public boolean isCompleted()
    {
        return getChild(1).isCompleted();
    }
}
