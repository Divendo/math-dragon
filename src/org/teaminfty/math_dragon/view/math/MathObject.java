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
public abstract class MathObject
{
    /** The line width that is to be used to draw operators */
    public static float lineWidth = 2.0f;
    
    /** The children of this {@link MathObject} */
    protected ArrayList<MathObject> children = new ArrayList<MathObject>();

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
    { return MathObjectPrecedence.HIGHEST; }

    /**
     * Returns the number of children this {@link MathObject} has
     * 
     * @return The number of children this {@link MathObject} has
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
    public MathObject getChild(int index) throws IndexOutOfBoundsException
    { return children.get(index); }

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
            child = new MathObjectEmpty();
        
        // Set the child
        children.set(index, child);

        // Refresh all levels and default heights
        setLevel(level);
        setDefaultHeight(defaultHeight);
    }
    
    /** Returns the default height for this {@link MathObject}
     * @return The default height for this {@link MathObject} */
    public int getDefaultHeight()
    { return defaultHeight; }
    
    /** Sets the default height for this {@link MathObject} and all of its children
     * @param height The default height */
    public void setDefaultHeight(int height)
    {
        // Set the default height
        defaultHeight = height;
        
        // Pass the new default height to all children
        for(MathObject child : children)
            child.setDefaultHeight(defaultHeight);
    }

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
    public abstract Rect[] getOperatorBoundingBoxes();

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
    public abstract Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException;

    /**
     * Returns the bounding box for the entire {@link MathObject}.
     * The aspect ratio of the box should always be the same.
     * 
     * @return The bounding box for the entire {@link MathObject}
     */
    public Rect getBoundingBox()
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
    
    /** Returns the centre of the {@link MathObject}
     * @return The centre of the {@link MathObject}
     */
    public Point getCenter()
    {
    	Rect bounding = this.getBoundingBox();
    	return new Point(bounding.centerX(), bounding.centerY());
    }
    
	/** Sets the new level for this {@link MathObject} and all of its children
	 * @param l The new level */
	public void setLevel(int l)
	{
		level = l;
		for(MathObject child : children)
			child.setLevel(l);
	}
    
    /** Whether or not to draw the bounding boxes */
    private final static boolean DRAW_BOUNDING = false;
    
    /** Draws the bounding box and the bounding boxes of the children (for debug purposes).
     * The boxes will only be drawn if {@link MathObject#DRAW_BOUNDING DRAW_BOUNDING} is set to true.
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
    
    /** Creates an empty XML document that can be used for the {@link MathObject#writeToXML(Document, Element) writeToXML()} method
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
    	parent.appendChild(doc.createElement(MathObjectEmpty.NAME));
    }
    
    /**
     * Checks if all children have been fully filled in, and also their children and so on recursively down the tree.
     * @return returns true if all children of this MathObject have been filled in.
     */
    public boolean isCompleted()
    {
        if(this instanceof MathObjectEmpty)
            return false;
        
        for(MathObject child : children)
        {
            if(child instanceof MathObjectEmpty || !child.isCompleted())
                return false;
        }
        
        return true;
    }
}
