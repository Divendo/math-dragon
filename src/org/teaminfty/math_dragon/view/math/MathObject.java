package org.teaminfty.math_dragon.view.math;

import java.util.ArrayList;

import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;
import org.teaminfty.math_dragon.view.HoverState;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/** This class represents a mathematical object that can be drawn */
public abstract class MathObject
{
    /** The children of this {@link MathObject} */
    protected ArrayList<MathObject> children = new ArrayList<MathObject>();

    /** The default maximum width of an object */
    protected int defaultMaxWidth = 100;

    /** The default maximum height of an object */
    protected int defaultMaxHeight = 100;

    /** To be used there is no maximum width or height */
    public static final int NO_MAXIMUM = -1;

    /** The current hover state */
    protected HoverState state = HoverState.NONE;

    /**
     * Constructor
     * 
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathObject(int defWidth, int defHeight)
    {
        // Remember the default maximum size
        defaultMaxWidth = defWidth;
        defaultMaxHeight = defHeight;
    }

    /**
     * Creates an instance of the right subclass of {@link MathObject} for the
     * given {@link IExpr}
     * 
     * @param expr
     *        The {@link IExpr} for which the instance of a subclass should be created
     * @return The created instance
     */
    public static MathObject buildFromIExpr(IExpr expr)
    {
        // This function is simply an overload
        return buildFromIExpr(expr, 100, 100);
    }

    /**
     * Creates an instance of the right subclass of {@link MathObject} for the
     * given {@link IExpr}
     * 
     * @param expr
     *        The {@link IExpr} for which the instance of a subclass should be
     *        created
     * @param defWidth
     *        The default maximum width
     * @param defHeight
     *        The default maximum height
     * @return The created instance
     */
    public static MathObject buildFromIExpr(IExpr expr, int defWidth, int defHeight)
    {
        // TODO Unimplemented method
        return null;
    }
    
    /** Returns the precedence of this operation.
     * The highest precedence is 0, greater values are lower precedences.
     * @return The precedence
     */
    public int getPrecedence()
    { return MathObjectPrecedence.HIGHEST; }

    /**
     * Returns the number of children this {@link MathObject} has
     * 
     * @return The number of children this {@link MathObject} has
     */
    public int getChildCount()
    {
        return children.size();
    }

    /**
     * Returns the child at the given index
     * 
     * @param index
     *        The index of the child to return
     * @return The requested child
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public MathObject getChild(int index) throws IndexOutOfBoundsException
    {
        return children.get(index);
    }

    /**
     * Sets the child at the given index
     * 
     * @param index
     *        The index of the child that is to be changed
     * @param child
     *        The {@link MathObject} that should become the child at the given index
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public void setChild(int index, MathObject child) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Create an MathObjectEmpty if null is given
        if(child == null)
            child = new MathObjectEmpty(defaultMaxWidth, defaultMaxHeight);
        
        // Set the child
        children.set(index, child);
    }

    /**
     * Symbolically evaluates this {@link MathObject}
     * 
     * @return The symbolic solution of this {@link MathObject}
     * @throws EmptyChildException
     *         If an empty child is detected where no empty child is allowed
     */
    public abstract IExpr eval() throws EmptyChildException;

    /**
     * Approximates the value of this {@link MathObject}
     * 
     * @return The approximated value of this {@link MathObject}
     * @throws NotConstantException
     *         If this {@link MathObject} doesn't evaluate into a constant value
     * @throws EmptyChildException
     *         If an empty child is detected where no empty child is allowed
     */
    public abstract double approximate() throws NotConstantException, EmptyChildException;

    /**
     * Returns the bounding boxes of the operator of this {@link MathObject}.
     * The aspect ratio of the bounding boxes should always be the same.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @return An array containing the requested bounding boxes
     */
    public abstract Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight);

    /**
     * Returns the bounding box of the child at the given index.
     * The aspect ratio of the box should always be the same.
     * 
     * @param index
     *        The index of the child whose bounding box is to be returned
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @return An array containing the requested bounding boxes
     * @throws IndexOutOfBoundsException
     *         If an invalid child index is given
     */
    public abstract Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException;

    /**
     * Returns the bounding box for the entire {@link MathObject}.
     * The aspect ratio of the box should always be the same.
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @return The bounding box for the entire {@link MathObject}
     */
    public Rect getBoundingBox(int maxWidth, int maxHeight)
    {
        // This will be our result
        Rect out = new Rect();

        // Add all operator bounding boxes
        Rect[] operatorBoundingBoxes = getOperatorBoundingBoxes(maxWidth,
                maxHeight);
        for(Rect tmp : operatorBoundingBoxes)
            out.union(tmp);

        // Add all child bounding boxes
        for(int i = 0; i < getChildCount(); ++i)
            out.union(getChildBoundingBox(i, maxWidth, maxHeight));

        // Return the result
        return out;
    }

    /**
     * Returns a rectangle of the given ratio (width : height) that fits exactly
     * in the given maximum rectangle. If no restrictions are given, the default
     * maximum size is used.
     * 
     * @param maxWidth
     *        The maximum width of the rectangle (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height of the rectangle (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @return The rectangle fitting in the given maximum size
     */
    protected Rect getRectBoundingBox(int maxWidth, int maxHeight, float ratio)
    {
        if(maxWidth == NO_MAXIMUM) // If the width is unrestricted, the bounding box depends on maxHeight
        {
            // If the height is also unrestricted, use the default maximum size
            if(maxHeight == NO_MAXIMUM)
                return getRectBoundingBox(defaultMaxWidth, defaultMaxHeight,
                        ratio);

            // We'll use as much space as possible
            return new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
        }
        else if(maxHeight == NO_MAXIMUM) // If the height is unrestricted, the
                                         // bounding box depends on maxWidth
            return new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
        else
        // If both the height and the width are restricted, the bounding box
        // depends on both maxWidth and maxHeight
        {
            if(maxWidth / ratio <= maxHeight)
                return new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
            else
                return new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
        }
    }
    
    /**
     * Retrieve the current hover state.
     * @return {@link #state}
     */
    public final HoverState getState()
    { return state; }

    /**
     * Modifies current state and return the old state. Subclasses may override
     * this method in order to perform actions (e.g. fire a <q>
     * HoverStateChangedListener</q> or something like that).
     * 
     * @param state
     *        The new state
     * @return The old state.
     * @throws NullPointerException
     *         thrown if {@link #state}<tt> == null</tt>
     */
    public HoverState setState(HoverState state)
    {
        if(state == null)
            throw new NullPointerException("state");
        HoverState old = this.state;
        this.state = state;
        return old;
    }

    /**
     * Draws the {@link MathObject}
     * 
     * @param canvas
     *        The canvas to draw the {@link MathObject} on
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be
     *        {@link MathObject#NO_MAXIMUM})
     */
    public abstract void draw(Canvas canvas, int maxWidth, int maxHeight);
    
    /**
     * Draw the child with the given index child on <tt>canvas</tt> within the specified bounding box.
     * @param index The index of the child that is to be drawn
     * @param canvas The graphical instance to draw on
     * @param box The bounding box of the child
     */
    protected void drawChild(int index, Canvas canvas, final Rect box)
    {
        // Draw the child
        canvas.save();
        canvas.translate(box.left, box.top);
        getChild(index).draw(canvas, box.width(), box.height());
        canvas.restore();
    }
    
    /** Draws all children
     * @param canvas The canvas that the children should be drawn on
     * @param maxWidth The maximum width the child can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the child can have (can be {@link MathObject#NO_MAXIMUM})
     */
    protected void drawChildren(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Loop through all children and draw them
        for(int i = 0; i < children.size(); ++i)
            drawChild(i, canvas, getChildBoundingBox(i, maxWidth, maxHeight));
    }

    /**
     * Checks if the given child index is valid, and throws an exception if it
     * isn't.
     * 
     * @param index
     *        The child index that is to be checked
     * @throws IndexOutOfBoundsException
     *         If the child index is invalid
     */
    protected final void checkChildIndex(int index) throws IndexOutOfBoundsException
    {
        final int childCount = getChildCount();

        if(childCount == 0)
            throw new IndexOutOfBoundsException(getClass().getCanonicalName()
                    + " doesn't have any children.");
        else if(childCount == 1 && index != 0)
            throw new IndexOutOfBoundsException("Invalid child index "
                    + Integer.toString(index) + ", "
                    + getClass().getCanonicalName() + " has only 1 child.");
        else if(index < 0 || index >= childCount)
            throw new IndexOutOfBoundsException("Invalid child index "
                    + Integer.toString(index) + ", "
                    + getClass().getCanonicalName() + " has only "
                    + Integer.toString(childCount) + " children.");
    }
    
    /** Returns the color for the current state
     * @return The color for the current state
     */
    protected int getColor()
    {
    	if(this.state == HoverState.DRAG)
    		return Color.rgb(0x88, 0x88, 0x88);
    	if(this.state == HoverState.HOVER)
    		return Color.rgb(0x44, 0x44, 0xff);
    	return Color.BLACK;
    }
    
    /** Returns the centre of the {@link MathObject}
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The centre of the {@link MathObject}
     */
    public Point getCenter(int maxWidth, int maxHeight)
    {
    	Rect bounding = this.getBoundingBox(maxWidth, maxHeight);
    	return new Point(bounding.centerX(), bounding.centerY());
    }
    
    /** Whether or not to draw the bounding boxes */
    private final static boolean DRAW_BOUNDING = false;
    
    /** Draws the bounding box and the bounding boxes of the children (for debug purposes).
     * The boxes will only be drawn if {@link MathObject#DRAW_BOUNDING DRAW_BOUNDING} is set to true.
     * @param canvas The canvas to draw on
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     */
    protected void drawBoundingBoxes(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Check if we should draw the bounding boxes
        if(!DRAW_BOUNDING) return;

        // Draw the bounding boxes
        Paint paint = new Paint();
        paint.setColor(0x4400ff00);
        canvas.drawRect(getBoundingBox(maxWidth, maxHeight), paint);
        paint.setColor(0x44ff0000);
        for(int i = 0; i < getChildCount(); ++i)
            canvas.drawRect(getChildBoundingBox(i, maxWidth, maxHeight), paint);
    }
}
