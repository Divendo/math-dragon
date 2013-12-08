package org.teaminfty.math_dragon;

import java.util.ArrayList;

import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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

    /** The paint that is used to draw empty children */
    protected Paint emptyChildPaint = new Paint();

    /** To be used there is no maximum width or height */
    public static final int NO_MAXIMUM = -1;

    /** The ratio of the bounding box of an empty child (i.e. the golden ratio) */
    protected final static float EMPTY_CHILD_RATIO = 1 / 1.61803398874989f;

    /** The current hover state */
    protected HoverState state = HoverState.NONE;

    /**
     * Constructor
     * 
     * @param defWidth
     *        The default maximum width
     * @param defHeight
     *        The default maximum height
     */
    public MathObject(int defWidth, int defHeight)
    {
        // Remember the default maximum size
        defaultMaxWidth = defWidth;
        defaultMaxHeight = defHeight;

        // Initialise the empty child paint
        emptyChildPaint.setColor(Color.rgb(0x88, 0x88, 0x88));
        emptyChildPaint.setStyle(Paint.Style.STROKE);
        emptyChildPaint.setPathEffect(new DashPathEffect(new float[] {16.0f,
                8.0f}, 0));
    }

    /**
     * Creates an instance of the right subclass of {@link MathObject} for the
     * given {@link IExpr}
     * 
     * @param expr
     *        The {@link IExpr} for which the instance of a subclass should be
     *        created
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
    public static MathObject buildFromIExpr(IExpr expr, int defWidth,
            int defHeight)
    {
        // TODO Unimplemented method
        return null;
    }

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
     *        The index of the child to return
     * @param child
     *        The {@link MathObject} that should become the child at the given
     *        index
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public void setChild(int index, MathObject child)
            throws IndexOutOfBoundsException
    {
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
    public abstract double approximate() throws NotConstantException,
            EmptyChildException;

    /**
     * Returns the bounding boxes of the operator of this {@link MathObject}
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
     * Returns the bounding box of the child at the given index
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
    public abstract Rect getChildBoundingBox(int index, int maxWidth,
            int maxHeight) throws IndexOutOfBoundsException;

    /**
     * Returns the bounding box for the entire {@link MathObject}
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
        if(maxWidth == NO_MAXIMUM) // If the width is unrestricted, the bounding
                                   // box depends on maxHeight
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
     * Draws an empty child box
     * 
     * @param canvas
     *        The canvas to draw on
     * @param rect
     *        The rectangle describing the coordinates of the empty child box
     */
    public void drawEmptyChild(Canvas canvas, Rect rect)
    {
        emptyChildPaint.setStrokeWidth(rect.width() / 10);
        rect.inset((int) Math.ceil(emptyChildPaint.getStrokeWidth() / 2),
                (int) Math.ceil(emptyChildPaint.getStrokeWidth() / 2));
        canvas.drawRect(rect, emptyChildPaint);
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
    protected final void checkChildIndex(int index)
            throws IndexOutOfBoundsException
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
    
    protected int getColor()
    {
    	if(this.state == HoverState.DRAG)
    		return Color.rgb(250, 50, 50);
    	if(this.state == HoverState.HOVER)
    		return Color.rgb(0,0,225);
    	return Color.BLACK;
    }
}
