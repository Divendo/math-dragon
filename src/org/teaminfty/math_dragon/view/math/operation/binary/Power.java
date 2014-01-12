package org.teaminfty.math_dragon.view.math.operation.binary;

import org.teaminfty.math_dragon.view.HoverState;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Precedence;
import org.teaminfty.math_dragon.view.math.operation.Binary;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Power extends Binary
{
    public static final String TYPE = "power";
    
    /** A paint that's used to draw the operator when the user is hovering over this object */
    private Paint operatorPaint = new Paint();
    
    public Power()
    { this(null, null); }
    
    public Power(Expression base, Expression power)
    {
        super(base, power);
        
        // Initialise the paint
        operatorPaint.setColor(0xcc4444ff);
    }
    
    public String toString()
    {
        return "(" + getLeft().toString() + "^" + getRight().toString() + ")";
    }
    
    @Override
    public int getPrecedence()
    { return Precedence.POWER; }
    
    /**
     * Assign <tt>o</tt> to mathematical expression to base expression.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setBase(Expression o)
    {
        setLeft(o);
    }

    /**
     * Retrieve the base mathematical expression. <b>Note:</b> <tt>null</tt>
     * may be returned.
     * 
     * @return The base mathematical expression.
     */
    public Expression getBase()
    {
        return getLeft();
    }
    
    /**
     * Assign <tt>o</tt> to mathematical expression to exponent expression.
     * 
     * @param o
     *        The mathematical expression.
     */
    public void setExponent(Expression o)
    {
        setRight(o);
    }

    /**
     * Retrieve the exponent mathematical expression. <b>Note:</b> <tt>null</tt>
     * may be returned.
     * 
     * @return The exponent mathematical expression.
     */
    public Expression getExponent()
    {
        return getRight();
    }

    @Override
    public Rect[] getOperatorBoundingBoxes() 
    {
        // Get the children sizes
        Rect[] sizes = getChildrenSize();
        
        // Powers don't have a visible operator
        // However, they need an operator bounding box (for dropping other operations on them)
        return new Rect[] {
                new Rect(0, 0, sizes[0].width(), sizes[1].height()),
                new Rect(sizes[0].width(), sizes[1].height(), sizes[0].width() + sizes[1].width(), sizes[0].height() + sizes[1].height())
            };
    }

    /**
     * Returns the sizes of the bounding of the children.
     * 
     * @param maxWidth
     *        The maximum width the {@link Expression} can have (can be {@link Expression#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link Expression} can have (can be {@link Expression#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    public Rect[] getChildrenSize()
    {
        // Get the sizes both operands want to take
        Rect leftSize = getChild(0).getBoundingBox();
        Rect rightSize = getChild(1).getBoundingBox();
        
        // Return the Sizes
        return new Rect[] {leftSize, rightSize};
    }

    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getChildrenSize();
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, sizes[0].width() + sizes[1].width(), sizes[0].height() + sizes[1].height());
    }
    
    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException 
    {
        // Check if the child exists
        this.checkChildIndex(index);
        
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize();
        
        // Move the bounding boxes to the correct position
        childrenSize[0].offsetTo(0, childrenSize[1].height());
        childrenSize[1].offsetTo(childrenSize[0].width(), 0);
        
        // Return the right bounding box
        return childrenSize[index];
    }
    
    @Override
    public void setLevel(int l)
    {
        level = l;
        getBase().setLevel(level);
        getExponent().setLevel(level + 1);
    }
    
    //We regard the base operand as the vertical center of the mathObject
    @Override
    public Point getCenter()
    {
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize();
        
        // Return the centre, that is:
        // Horizontally: simply the centre of our total width
        // Vertically: the vertical centre of the base operand
        return new Point((childrenSize[0].width() + childrenSize[1].width()) / 2, childrenSize[1].height() + getBase().getCenter().y);
    }

    @Override
    public void draw(Canvas canvas) 
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Draw the operator if we're hovering
        if(state == HoverState.HOVER)
        {
            final Rect[] boxes = getOperatorBoundingBoxes();
            for(Rect box : boxes)
                canvas.drawRect(box, operatorPaint);
        }
        
        // Only draw the children
        drawChildren(canvas);
    }
    
    @Override
    protected String getType()
    {
        return TYPE;
    }
}
