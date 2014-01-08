package org.teaminfty.math_dragon.view.math;


import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathOperationFunction extends MathObject
{
    /** An enumeration that describes the types of functions this function can be */
    public static enum FunctionType
    {
        SIN("sin"),
        COS("cos"),
        TAN("tan"),
        SINH("sinh"),
        COSH("cosh"),
        ARCSIN("sin\u207B\u00B9", "arcsin"),
        ARCCOS("cos\u207B\u00B9", "arccos"),
        ARCTAN("tan\u207B\u00B9", "arctan"),
        LN("ln");
        
        /** The name of the function type */
        private String name;
        
        /** The xml-safe name of the function type */
        private String xmlName;
        
        /** Constructor
         * @param nameAndXmlName The name of the function type and its xml-safe name
         */
        private FunctionType(String nameAndXmlName)
        {
            name = nameAndXmlName;
            xmlName = nameAndXmlName;
        }

        /** Constructor
         * @param normalName The name of the function type
         * @param xmlSafeName The xml-safe name of the function type
         */
        private FunctionType(String normalName, String xmlSafeName)
        {
            name = normalName;
            xmlName = xmlSafeName;
        }
        
        /** Returns the name of the function type
         * @return The name of the function type as a string (may contain high unicode characters) */
        public String getName()
        { return name; }

        /** Returns the XML safe name of the function type
         * @return The XML safe name of the function type as a string */
        public String getXmlName()
        { return xmlName; }
        
        /** Returns the {@link FunctionType} that belongs to the given XML safe name
         * @param xmlName The XML safe name
         * @return The {@link FunctionType}, or <tt>null</tt> if none belong to the given XML safe name */
        public static FunctionType getByXmlName(String xmlName)
        {
            if(xmlName.equals("sin"))
                return SIN;
            else if(xmlName.equals("cos"))
                return COS;
            else if(xmlName.equals("tan"))
                return TAN;
            else if(xmlName.equals("sinh"))
                return SINH;
            else if(xmlName.equals("cosh"))
                return COSH;
            else if(xmlName.equals("arcsin"))
                return ARCSIN;
            else if(xmlName.equals("arccos"))
                return ARCCOS;
            else if(xmlName.equals("arctan"))
                return ARCTAN;
            else if(xmlName.equals("ln"))
                return LN;
            return null;
        }
    }
    
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    private final float PARENTHESES_RATIO = 0.5f / 1.61803398874989f;

    /** Which function we represent */
    public FunctionType type = null;
    
    /** Default constructor */
    public MathOperationFunction()
    {
        this(FunctionType.SIN);
    }
    
    /** Constructor
     * @param t The kind of function that we're constructing */
    public MathOperationFunction(FunctionType t)
    {
        type = t;
        children.add(new MathObjectEmpty());
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStrokeWidth(MathObject.lineWidth);
        operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
    }
    
    /** Returns the current function type */
    public FunctionType getType()
    { return type; }
    
    @Override
    public String toString()
    {
        String childString = getChild(0).toString();
        if(childString.startsWith("(") && childString.endsWith(")"))
            childString = childString.substring(1, childString.length() - 1);
        return type.getXmlName() + '(' + childString + ')';
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

        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        operatorPaint.getTextBounds(type.getName(), 0, type.getName().length(), out);
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
        Rect textBounding = new Rect();
        operatorPaint.getTextBounds(type.getName(), 0, type.getName().length(), textBounding);
        canvas.translate((operatorBounding[0].width() - textBounding.width()) / 2, (operatorBounding[0].height() - textBounding.height()) / 2);
        canvas.drawText(type.getName(), operatorBounding[0].left - textBounding.left, operatorBounding[0].top - textBounding.top, operatorPaint);
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
    
    /** The name of the XML node for this class */
    public static final String NAME = "function";
    
    /** The XML attribute for which type of function this is */
    public static final String ATTR_TYPE = "type";

    @Override
    public final void writeToXML(Document doc, Element el)
    {
        Element e = doc.createElement(NAME);
        e.setAttribute(ATTR_TYPE, type.getXmlName());
        getChild(0).writeToXML(doc, el);
        el.appendChild(e);
    }
}
