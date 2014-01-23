package org.teaminfty.math_dragon.view.math;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.view.HoverState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/** This class represents a mathematical object that can be drawn */
public abstract class Expression
{
    /** The line width that is to be used to draw operators */
    public static float lineWidth = 2.0f;
    
    /** The children of this {@link Expression} */
    protected ArrayList<Expression> children = new ArrayList<Expression>();

    /** The center of this object */
    protected Point center;
    
    /** The bounding boxes of this object */
    protected ArrayList<Rect> childrenBoundingBoxes = new ArrayList<Rect>();
    protected ArrayList<Rect> operatorBoundingBoxes = new ArrayList<Rect>();
    protected Rect totalBoundingBox = null;
    
    /** The boolean to keep track if the bounding boxes are still valid */
    protected boolean operatorBoundingBoxValid = false;
    /** The boolean to keep track if the child bounding boxes are still valid */
    protected boolean childrenBoundingBoxValid = false;
    /** The boolean to keep track if the total bounding box is still valid */
    protected boolean totalBoundingBoxValid = false;
    /** The boolean to keep track if the center is still valid */
    protected boolean centerValid = false;
    
    /** The default height of an object */
    protected int defaultHeight = 100;
    
    /** The maximum level depth */
    public final static int MAX_LEVEL = 4;

    /** The current hover state */
    protected HoverState state = HoverState.NONE;
    
    /** The current 'level' of the object*/
    protected int level = 0;
    
    /** Returns the precedence of this operation.
     * The highest precedence is 0, greater values are lower precedences.
     * @return The precedence
     */
    public int getPrecedence()
    { return Precedence.HIGHEST; }

    /**
     * Returns the number of children this {@link Expression} has
     * 
     * @return The number of children this {@link Expression} has
     */
    public int getChildCount()
    { return children.size(); }

    /**
     * Returns the child at the given index
     * 
     * @param index
     *        The index of the child to return
     * @return The requested child
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public Expression getChild(int index) throws IndexOutOfBoundsException
    { return children.get(index); }

    /**
     * Sets the child at the given index
     * 
     * @param index
     *        The index of the child that is to be changed
     * @param child
     *        The {@link Expression} that should become the child at the given index
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public void setChild(int index, Expression child) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Create an MathObjectEmpty if null is given
        if(child == null)
            child = new Empty();
        
        // Set the child
        children.set(index, child);

        // Refresh all levels and default heights
        // Also reset the bounding box cache
        setAll(level, defaultHeight, false);
    }

    /** Sets the child at the given index without refreshing the level, default height or bounding box cache.
     * You'll have to do this yourself.
     * 
     * @param index
     *        The index of the child that is to be changed
     * @param child
     *        The {@link Expression} that should become the child at the given index
     * @throws IndexOutOfBoundsException
     *         thrown when the index number is invalid (i.e. out of range).
     */
    public void setChildWithoutRefresh(int index, Expression child) throws IndexOutOfBoundsException
    {
        // Check the child index
        checkChildIndex(index);
        
        // Create an MathObjectEmpty if null is given
        if(child == null)
            child = new Empty();
        
        // Set the child
        children.set(index, child);
    }
    
    /** Returns the default height for this {@link Expression}
     * @return The default height for this {@link Expression} */
    public int getDefaultHeight()
    { return defaultHeight; }
    
    /** Sets the default height for this {@link Expression} and all of its children
     * @param height The default height */
    public void setDefaultHeight(int height)
    {
        // Invalidate the cache (if necessary)
        if(height != defaultHeight)
            invalidateBoundingBoxCacheForSelf();
        
        // Set the default height
        defaultHeight = height;
        
        // Pass the new default height to all children
        for(Expression child : children)
            child.setDefaultHeight(defaultHeight);
    }
    
    /** Invalidate or validate all bounding boxes in the cache (for this expression and all of its children) */
    public void invalidateBoundingBoxCache()
    {
        // Invalidate the whole cache
        invalidateBoundingBoxCacheForSelf();
        
        // Invalidate the whole cache for every child as well
        for(Expression child : children)
            child.invalidateBoundingBoxCache();
    }

    /** Invalidate or validate all bounding boxes in the cache (only for this expression) */
    public void invalidateBoundingBoxCacheForSelf()
    {
        // Invalidate the whole cache
        operatorBoundingBoxValid = false;
        childrenBoundingBoxValid = false;
        totalBoundingBoxValid = false;
        centerValid = false;
    }
    
    /** Get the state of the bounding box cache */
    public boolean getOperatorBoundingBoxValid()
    { return operatorBoundingBoxValid; }
    
    /** Get the state of the children bounding box cache */
    public boolean getChildrenBoundingBoxValid()
    { return childrenBoundingBoxValid; }
    
    /** Get the state of the total bounding box cache */
    public boolean getTotalBoundingBoxValid()
    { return totalBoundingBoxValid; }
    
    
    /**
     * Returns the bounding boxes of the operator of this {@link Expression}.
     * The aspect ratio of the bounding boxes should always be the same.
     * 
     * @param maxWidth
     *        The maximum width the {@link Expression} can have (can be
     *        {@link Expression#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link Expression} can have (can be
     *        {@link Expression#NO_MAXIMUM})
     * @return An array containing the requested bounding boxes
     */
    public abstract Rect[] calculateOperatorBoundingBoxes();

    public Rect[] getOperatorBoundingBoxes()
    {    	
        // If the cache is invalid or there are no bounding boxes in the current cache, recalculate them
        if( !getOperatorBoundingBoxValid() || operatorBoundingBoxes.isEmpty())
        {
            // First clear the current list
            operatorBoundingBoxes.clear();
            
            // Recalculate the bounding boxes
            Rect[] operatorBB = calculateOperatorBoundingBoxes();
            
            for(int i = 0; i < operatorBB.length; i ++)
                operatorBoundingBoxes.add( operatorBB[i]);
            
            // Set the cache to valid
            operatorBoundingBoxValid = true;
        }
        
        // Create a new array and get a copy of all the bounding boxes
        Rect[] result = new Rect[ operatorBoundingBoxes.size()];
        
        for(int i = 0; i < result.length; i ++)
            result[i] = new Rect(operatorBoundingBoxes.get(i));
        
        // Return the array with the rectangles
        return result;
    }
    /**
     * Returns the bounding box of the child at the given index.
     * The aspect ratio of the box should always be the same.
     * 
     * @param index
     *        The index of the child whose bounding box is to be returned
     * @return An array containing the requested bounding boxes
     * @throws IndexOutOfBoundsException
     *         If an invalid child index is given
     */
    public abstract Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException;

    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {    	
        // If the cache is invalid or the amount of bounding boxes isn't the amount of children, recalculate them
        if( !getChildrenBoundingBoxValid() || (getChildCount() != childrenBoundingBoxes.size()))
        {
            childrenBoundingBoxes.clear();
            
            this.calculateAllChildBoundingBox();
             
            childrenBoundingBoxValid = true;
        }
        
        // Return a copy of the requested bounding box
        return new Rect(childrenBoundingBoxes.get(index));
    }
    
    /** Calculate all the children's bounding boxes and add them to the children arraylist */
    public void calculateAllChildBoundingBox()
    {
    	int size = getChildCount();
        
        for(int i = 0; i < size; i ++)
            childrenBoundingBoxes.add( calculateChildBoundingBox(i));
    }
    
    /**
     * Returns the bounding box for the entire {@link Expression}.
     * The aspect ratio of the box should always be the same.
     * 
     * @return The bounding box for the entire {@link Expression}
     */
    public Rect calculateBoundingBox()
    {    	
        // This will be our result
        Rect out = new Rect();

        // Add all operator bounding boxes
        Rect[] operatorBoundingBoxes = getOperatorBoundingBoxes();
        for(Rect tmp : operatorBoundingBoxes)
            out.union(tmp);

        // Add all child bounding boxes
        for(int i = 0; i < getChildCount(); ++i)
            out.union(getChildBoundingBox(i));
        int width = out.width();
        int height = out.height();

        // Return the result
        return new Rect(0,0,width, height);
    }
    
    public Rect getBoundingBox()
    {
        // If the cache is invalid or there is no bounding box in the cache, recalculate
        if( !getTotalBoundingBoxValid() || totalBoundingBox == null)
        {
            totalBoundingBox = calculateBoundingBox();
            totalBoundingBoxValid = true;
        }
        
        // Return a copy of the bounding box
        return new Rect(totalBoundingBox);
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
     * Draws the {@link Expression}
     * 
     * @param canvas
     *        The canvas to draw the {@link Expression} on
     */
    public abstract void draw(Canvas canvas);
    
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
        getChild(index).draw(canvas);
        canvas.restore();
    }
    
    /** Draws all children
     * @param canvas The canvas that the children should be drawn on

     */
    protected void drawChildren(Canvas canvas)
    {
        // Loop through all children and draw them
        for(int i = 0; i < children.size(); ++i)
            drawChild(i, canvas, getChildBoundingBox(i));
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
    
    /** Set the state of the center point*/
    public void validateCenter(boolean bool)
    {
    	centerValid = bool;
    	
    	if(!bool)
    		for(Expression child : children)
    			child.validateCenter(false);
    }
    
    /** Get the state of the center point */
    public boolean getCenterValid()
    { return centerValid; }
    
    /** Returns the centre of the {@link Expression}
     * @return The centre of the {@link Expression}
     */
    public Point getCenter()
    {
    	if(!getCenterValid() || center == null)
    	{
    		center = calculateCenter();
    		this.validateCenter(true);
    	}
    	
        return center;
    }
    
    /** Calculate the center point */
    public Point calculateCenter()
    {
    	Rect bounding = this.getBoundingBox();
		return new Point(bounding.centerX(), bounding.centerY());
    }
    
    /** The deltas that should be added to the level for each corresponding child */
    protected int[] levelDeltas = null;
    
    /** Sets the new level for this {@link Expression} and all of its children
     * @param l The new level */
    public void setLevel(int l)
    {
        // Invalidate the cache (if necessary)
        if(l != level)
            invalidateBoundingBoxCacheForSelf();
        
        // Set the level
        level = l;
        
        // Set the level every child
        for(int i = 0; i < getChildCount(); ++i)
        {
            if(levelDeltas != null && i < levelDeltas.length)
                getChild(i).setLevel(l + levelDeltas[i]);
            else
                getChild(i).setLevel(l);
        }
    }
    
    /** Sets the level, bounding boxes validation and default height for this expression and all of it's children at once
     * @param lvl The new level
     * @param defHeight The new default height
     * @param valid Whether or not the bounding boxes should be valid */
    public void setAll(int lvl, int defHeight, boolean valid)
    {
        // Set the values
        level = lvl;
        defaultHeight = defHeight;
        operatorBoundingBoxValid = valid;
        childrenBoundingBoxValid = valid;
        totalBoundingBoxValid = valid;
        centerValid = valid;
        
        // Set the values for all children
        for(int i = 0; i < getChildCount(); ++i)
        {
            if(levelDeltas != null && i < levelDeltas.length)
                getChild(i).setAll(lvl + levelDeltas[i], defHeight, valid);
            else
                getChild(i).setAll(lvl, defHeight, valid);
        }
    }
    
    /** Whether or not to draw the bounding boxes */
    private final static boolean DRAW_BOUNDING = false;
    
    /** Draws the bounding box and the bounding boxes of the children (for debug purposes).
     * The boxes will only be drawn if {@link Expression#DRAW_BOUNDING DRAW_BOUNDING} is set to true.
     * @param canvas The canvas to draw on
     */
    protected void drawBoundingBoxes(Canvas canvas)
    {
        // Check if we should draw the bounding boxes
        if(!DRAW_BOUNDING) return;

        // Draw the bounding boxes
        Paint paint = new Paint();
        paint.setColor(0x4400ff00);
        canvas.drawRect(getBoundingBox(), paint);
        paint.setColor(0x44ff0000);
        for(int i = 0; i < getChildCount(); ++i)
            canvas.drawRect(getChildBoundingBox(i), paint);
    }
    
    /** The name of the XML root element */
    public static final String XML_ROOT = "root";

    /** The current version of the XML structure */
    public static final int XML_VERSION = 1;
    
    /** Creates an empty XML document that can be used for the {@link Expression#writeToXML(Document, Element) writeToXML()} method
     * @return The created document 
     * @throws ParserConfigurationException If something goes wrong while creating the document */
    public static Document createXMLDocument() throws ParserConfigurationException
    {
        // Create an empty document
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        // Create a root element
        Element root = doc.createElement(XML_ROOT);
        root.setAttribute("version", Integer.toString(XML_VERSION));
        doc.appendChild(root);
        
        // Return the document
        return doc;
    }
    
    /** Serialize current instance in a XML document in a specified element. 
     * @param doc The XML document
     * @param parent The parent XML element
     */
    public void writeToXML(Document doc, Element parent)
    {
        Log.w("XML", "not a writable element yet");
        parent.appendChild(doc.createElement(Empty.NAME));
    }
    
    /**
     * Checks if all children have been fully filled in, and also their children and so on recursively down the tree.
     * @return returns true if all children of this MathObject have been filled in.
     */
    public boolean isCompleted()
    {
        for(Expression child : children)
        {
            if(!child.isCompleted())
                return false;
        }
        
        return true;
    }
}
