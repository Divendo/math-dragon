package org.teaminfty.math_dragon.view.math;


import org.teaminfty.math_dragon.view.MathSourceOperationSinoid.OperatorType;
import org.teaminfty.math_dragon.view.TypefaceHolder;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathOperationFunction extends MathObject 
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    private final float PARENTHESES_RATIO = 0.5f / 1.61803398874989f;

    /** Which function we represent */
    public OperatorType type = null;
    
    /** Default constructor */
    public MathOperationFunction()
    {
        this(OperatorType.SIN);
    }
    
    /** Constructor
     * @param t The kind of function that we're constructing */
    public MathOperationFunction(OperatorType t)
    {
        type = t;
        children.add(new MathObjectEmpty());
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStrokeWidth(MathObject.lineWidth);
        operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
    }
    
    /** Returns the name of this function as a string
     * @return The name of this function as a string (may contain high unicode characters) */
    public String getName()
    {
        switch(type)
        {
            case ARCCOS:
                  return "cos\u207B\u00B9";
            case ARCSIN:
                  return "sin\u207B\u00B9";
            case ARCTAN:
                  return "tan\u207B\u00B9";
            case COS:
                return "cos";
            case SIN:
                return "sin";
            case TAN:
                return "tan";
            case COSH:
                return "cosh";
            case SINH:
                return "sinh";
            case LN:
                return "ln";
        }
        return "error";
    }
    
    public int getPrecedence()
    { return MathObjectPrecedence.FUNCTION; }

    /** Calculates the right text size for the given level
     * @return The right text size for the given level */
    protected float findTextSize()
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, level + 1);
    }   
    
    /** Adds padding to the given size rectangle
     * @param size The size where the padding should be added to
     * @return The size with the padding
     */
    protected Rect sizeAddPadding(Rect size)
    {
        // Copy the rectangle
        Rect out = new Rect(size);
        
        // Add the padding
        out.inset(-(int)(MathObject.lineWidth  * 2.5), -(int)(MathObject.lineWidth * 2.5));
        out.offsetTo(0, 0);
        
        // Return the result
        return out;
    }
    
    /** Calculates the size of this function when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathConstant}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        operatorPaint.setTextSize(fontSize);
        
        // Get the name of this function
        final String operatorName = getName();

        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        operatorPaint.getTextBounds(operatorName, 0, operatorName.length(), out);
        out.offsetTo(0, 0);
        
        // Return the size
        return out;
    }
    
    @Override
    public Rect[] getOperatorBoundingBoxes() 
    {
        // Get the sizes
        final Rect childRect = getChild(0).getBoundingBox();
        final int parentheseWidth = (int)(childRect.height() * PARENTHESES_RATIO);
        Rect textBounding = sizeAddPadding(getSize(findTextSize()));
        
        // Make sure everything is aligned nicely
        final int childCenterY = getChild(0).getCenter().y;
        final int childTop = Math.max(textBounding.centerY() - childCenterY, 0);
        textBounding.offsetTo(0, Math.max(childCenterY - textBounding.centerY(), 0));
        
        // Return the bounding boxes
        return new Rect[]{ textBounding,
                 new Rect(textBounding.width(), childTop, textBounding.width() + parentheseWidth, childTop + childRect.height()), 
                 new Rect(textBounding.width() + parentheseWidth + childRect.width(), childTop, textBounding.width() + childRect.width() + 2 * parentheseWidth, childTop + childRect.height())};
    }
    
    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException 
    {
        // Make sure the child index is valid
        checkChildIndex(index);
        
        // Gets the needed sizes and centres
        final Rect childRect = getChild(index).getBoundingBox();
        final int parentheseWidth = (int)(childRect.height() * PARENTHESES_RATIO);
        Rect textBounding = sizeAddPadding(getSize(findTextSize()));
        
        // Align the child's vertical centre with the text's vertical centre
        // also offset it horizontally to place it behind the opening bracket
        childRect.offsetTo(textBounding.width() + parentheseWidth, Math.max(textBounding.centerY() - getChild(index).getCenter().y, 0));
        
        // Return the result
        return childRect;
    }
    
    //Complete bounding box
    @Override
    public Rect getBoundingBox()
    {
        Rect[] operatorSizes = getOperatorBoundingBoxes();
        Rect child = getChild(0).getBoundingBox();
        
        return new Rect(0,Math.min(operatorSizes[0].top, child.top), operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + child.width(), Math.max(getChild(0).getBoundingBox().height(), operatorSizes[0].height()));
    }

    
    @Override
    public Point getCenter()
    {        
        return new Point(getBoundingBox().centerX(), getBoundingBox().centerY());
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
        final String operatorName = getName();
        Rect textBounding = new Rect();
        operatorPaint.getTextBounds(operatorName, 0, operatorName.length(), textBounding);
        canvas.translate((operatorBounding[0].width() - textBounding.width()) / 2, (operatorBounding[0].height() - textBounding.height()) / 2);
        canvas.drawText(operatorName, operatorBounding[0].left - textBounding.left, operatorBounding[0].top - textBounding.top, operatorPaint);
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
