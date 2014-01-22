package org.teaminfty.math_dragon.view.math;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class Parentheses extends Expression
{
    /** The name of the XML element for this class */
    public static final String NAME = "parentheses";
    
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    final float RATIO = 0.5f / 1.61803398874989f;
    
    /** The paint that's used to draw the parentheses */
    private Paint paint = new Paint();
    
    /** Default constructor */
    public Parentheses(int defWidth, int defHeight)
    { this(null); }

    /** Constructor
     * 
     * @param child The child to wrap the parentheses around
     */
    public Parentheses(Expression child)
    {
        // We have one child
        children.add(child == null ? new Empty() : child);
        
        // Initialise the paint
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }
    
    @Override
    public String toString()
    {
        final String str = getChild(0).toString();
        if(str.startsWith("(") && str.endsWith(")"))
            return str;
        return "(" + str + ")";
    }
    
    @Override
    public Rect[] calculateOperatorBoundingBoxes()
    {
    	final Rect childRect = getChild(0).getBoundingBox();
    	final int width = (int)(childRect.height() * RATIO);
        
        // Return the bounding boxes
        return new Rect[] {new Rect(0, 0, width, childRect.height()), new Rect(width + childRect.width(), 0, width * 2 + childRect.width(), childRect.height())};
    }
    
    @Override
    public Rect calculateBoundingBox()
    {
    	final Rect childRect = getChild(0).getBoundingBox();
    	return new Rect(0, 0, 2 * (int)(childRect.height() * RATIO) + childRect.width(), childRect.height());
    }
    
    public Expression getChild()
    {
        return getChild(0);
    }
    
    public void setChild(Expression child)
    {
        setChild(0, child);
    }

    @Override
    public Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Get the sizes
        Rect childRect = getChild(0).getBoundingBox();
        
        // Position the bounding box of the child
        childRect.offset((int)(childRect.height() * RATIO), 0);
        
        // Return the bounding box
        return childRect;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the operator bounding boxes
        Rect[] boxes = getOperatorBoundingBoxes();
        
        // Prepare the paint and canvas for drawing the brackets
        paint.setColor(getColor());
        paint.setStrokeWidth(lineWidth);
        
        // Draw the left bracket
        canvas.save();
        canvas.clipRect(boxes[0], Region.Op.INTERSECT);
        RectF bracket = new RectF(boxes[0]);
        bracket.inset(0, -paint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, paint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        canvas.clipRect(boxes[1], Region.Op.INTERSECT);
        bracket = new RectF(boxes[1]);
        bracket.inset(0, -paint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, paint);
        canvas.restore();
        
        // Draw the child
        drawChildren(canvas);
    }

    @Override
    public void writeToXML(Document doc, Element el)
    {
        Element e = doc.createElement(NAME);
        children.get(0).writeToXML(doc, e);
        el.appendChild(e);
    }
    
    @Override
    public Point calculateCenter()
    {
    	return new Point( this.getBoundingBox().centerX(), this.getChild(0).getCenter().y);
    }
}
