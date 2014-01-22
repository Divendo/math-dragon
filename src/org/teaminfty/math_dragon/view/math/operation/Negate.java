package org.teaminfty.math_dragon.view.math.operation;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Operation;
import org.teaminfty.math_dragon.view.math.Precedence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Negate extends Operation
{
    /** The type of the XML element for this class */
    public static final String TYPE = "negate";
    
    /** The paint that's used to draw the operator */
    private Paint paint = new Paint();
    
    /** Default constructor */
    public Negate()
    {
        super(1);
        children.add(new Empty());
        
        initPaint();
    }
    
    /** Constructor
     * @param child The child of this negate operation */
    public Negate(Expression child)
    {
        this();
        setChild(0, child);
    }
    
    /** Initialises the paint */
    private void initPaint()
    {
        paint.setTypeface(TypefaceHolder.dejavuSans);
    }

    @Override
    public String toString()
    { return "(-" + getChild(0).toString() + ")"; }

    @Override
    protected String getType()
    { return TYPE; }

    @Override
    protected void writeChildrenToXML(Document doc, Element el)
    { getChild(0).writeToXML(doc, el); }
    
    /** Returns the text size for the current level
     * @return The text size */
    private float getTextSize()
    { return defaultHeight * (float) Math.pow(2.0 / 3.0, level); }
    
    @Override
    public int getPrecedence()
    { return Precedence.NEGATE; }

    @Override
    public Rect[] calculateOperatorBoundingBoxes()
    {
        // Get the child centre
        final Point childCenter = getChild(0).getCenter();
        
        // Determine the size of our operator bounding box
        Rect out = new Rect();
        paint.setTextSize(getTextSize());
        paint.getTextBounds("-", 0, 1, out);
        
        // Add some padding
        out.inset((int) (-2 * lineWidth), 0);
        
        // Position the operator bounding box and return it
        out.offsetTo(0, Math.max(0, childCenter.y - out.height() / 2));
        return new Rect[]{ out };
    }

    @Override
    public Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Determine the size of our operator bounding box
        Rect operatorBounding = new Rect();
        paint.setTextSize(getTextSize());
        paint.getTextBounds("-", 0, 1, operatorBounding);
        
        // Add some padding
        operatorBounding.inset((int) (-2 * lineWidth), 0);
        
        // Get the size and centre of our child bounding box
        Rect out = getChild(0).getBoundingBox();
        final Point childCenter = getChild(0).getCenter();
        
        // Position the child bounding box and return it
        out.offsetTo(operatorBounding.width(), Math.max(0, operatorBounding.height() / 2 - childCenter.y));
        return out;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the bounding box
        final Rect operator = getOperatorBoundingBoxes()[0];
        
        // Draw the operator
        paint.setColor(getColor());
        paint.setTextSize(getTextSize());
        Rect textBounds = new Rect();
        paint.getTextBounds("-", 0, 1, textBounds);
        canvas.drawText("-", operator.left - textBounds.left, operator.top - textBounds.top, paint);
        
        // Draw the children
        drawChildren(canvas);
    }
}
