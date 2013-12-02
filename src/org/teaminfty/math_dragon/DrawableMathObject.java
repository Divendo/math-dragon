package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.engine.MathObject;
import org.teaminfty.math_dragon.engine.MathOperationAdd;
import org.teaminfty.math_dragon.engine.MathOperationSubtract;

import android.graphics.Canvas;
import android.graphics.Rect;

/** This class is used to draw a {@link MathObject} */
public abstract class DrawableMathObject
{
    
    /** The {@link MathObject} that is to be drawn */
    protected MathObject mathObject;
    
    /** Instances of {@link DrawableMathObject} that are used to draw the children of {@link DrawableMathObject#mathObject mathObject}.
     * The index of each {@link DrawableMathObject} corresponds with the index of the child in {@link DrawableMathObject#mathObject mathObject} that it has to draw. */
    protected DrawableMathObject[] drawableMathObjects;
    
    /** The default width of an object */
    protected int defaultWidth = 100;

    /** The default height of an object */
    protected int defaultHeight = 100;
    
    /** Constructor
     * @param mObject The {@link MathObject} that is to be drawn by this instance (null is not allowed)
     * @param defWidth The default width
     * @param defHeight The default height
     */
    public DrawableMathObject(MathObject mObject, int defWidth, int defHeight)
    {
        // Keep a pointer to the MathObject (null is not allowed)
        if((mathObject = mObject) == null)
            throw new InvalidMathObjectException("No MathObject (null) was passed to the constructor of " + getClass().getCanonicalName());
        
        // Remember the default size
        defaultWidth = defWidth;
        defaultHeight = defHeight;
        
        // Initialise the array with drawables for the children
        rebuild();
    }
    
    /** Creates an instance of the right subclass of {@link DrawableMathObject} for the given {@link MathObject}
     * @param mObject The {@link MathObject} for which the instance of a subclass should be created
     * @return The created instance
     */
    public static DrawableMathObject buildFromMathObject(MathObject mObject)
    {
        // This function is simply an overload
        return buildFromMathObject(mObject, 100, 100);
    }
    
    /** Creates an instance of the right subclass of {@link DrawableMathObject} for the given {@link MathObject}
     * @param mObject The {@link MathObject} for which the instance of a subclass should be created
     * @param defWidth The default width
     * @param defHeight The default height
     * @return The created instance
     */
    public static DrawableMathObject buildFromMathObject(MathObject mObject, int defWidth, int defHeight)
    {
        // Linear binary operation
        if(mObject instanceof MathOperationAdd || mObject instanceof MathOperationSubtract)
            return new DrawableMathOperationBinaryLinear(mObject, defWidth, defHeight);
        
        // No right class was found, throw an exception
        throw InvalidMathObjectException.createUnsupportedTypeException(DrawableMathObject.class.getCanonicalName() + ".buildFromMathObject()", mObject);
    }
    
    /** Returns the {@link MathObject} that is currently associated with this object
     * @return The {@link MathObject} that is currently associated with this object */
    public MathObject getMathObject()
    { return mathObject; }
    
    /** Reads the children from the {@link MathObject} that is currently associated with this object,
     * and creates drawables for them.
     */
    public void rebuild()
    {
        drawableMathObjects = new DrawableMathObject[mathObject.getChildrenCount()];
        for(int i = 0; i < mathObject.getChildrenCount(); ++i)
        {
            if(mathObject.getChild(i) == null)
                drawableMathObjects[i] = null;
            else
                drawableMathObjects[i] = DrawableMathObject.buildFromMathObject(mathObject.getChild(i), defaultWidth, defaultHeight);
        }
    }
    
    /** To be used there is no maximum width or height */ 
    public static final int NO_MAXIMUM = -1;
    
    /** Returns the bounding boxes of the operator of this {@link MathObject}
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @return An array containing the requested bounding boxes
     */
    public abstract Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight);

    /** Returns the bounding box of the child at the given index
     * @param index The index of the child whose bounding box is to be returned
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @return An array containing the requested bounding boxes
     * @throws IndexOutOfBoundsException If an invalid child index is given
     */
    public abstract Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException;
    
    /** Returns the bounding box for the entire {@link MathObject}
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @return The bounding box for the entire {@link MathObject}
     */
    public Rect getBoundingBox(int maxWidth, int maxHeight)
    {
        // This will be our result
        Rect out = new Rect();
        
        // Add all operator bounding boxes
        Rect[] operatorBoundingBoxes = getOperatorBoundingBoxes(maxWidth, maxHeight);
        for(Rect tmp : operatorBoundingBoxes)
            out.union(tmp);
        
        // Add all child bounding boxes
        for(int i = 0; i < mathObject.getChildrenCount(); ++i)
            out.union(getChildBoundingBox(i, maxWidth, maxHeight));
        
        // Return the result
        return out;
    }
    
    /** The ratio of the bounding box of an empty child (i.e. the golden ratio) */
    protected final static float EMPTY_CHILD_RATIO = 1 / 1.61803398874989f;
    
    /** Returns a rectangle of the given ratio (width : height) that fits exactly in the given maximum rectangle.
     * If no restrictions are given, the default size is used.
     * @param maxWidth The maximum width of the rectangle (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height of the rectangle (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @return The rectangle fitting in the given maximum size
     */
    public Rect getRectBoundingBox(int maxWidth, int maxHeight, float ratio)
    {
        if(maxWidth == NO_MAXIMUM)          // If the width is unrestricted, the bounding box depends on maxHeight
        {
            // If the height is also unrestricted, use the default size
            if(maxHeight == NO_MAXIMUM)
                return getRectBoundingBox(defaultWidth, defaultHeight, ratio);
            
            // We'll use as much space as possible
            return new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
        }
        else if(maxHeight == NO_MAXIMUM)    // If the height is unrestricted, the bounding box depends on maxWidth
            return new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
        else                                // If both the height and the width are restricted, the bounding box depends on both maxWidth and maxHeight
        {
            if(maxWidth / ratio <= maxHeight)
                return new Rect(0, 0, maxWidth, (int) (maxWidth / ratio));
            else
                return new Rect(0, 0, (int) (maxHeight * ratio), maxHeight);
        }
    }
    
    /** Draws the {@link MathObject}
     * @param canvas The canvas to draw the {@link MathObject} on
     * @param maxWidth The maximum width the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     * @param maxHeight The maximum height the {@link MathObject} can have (can be {@link DrawableMathObject#NO_MAXIMUM})
     */
    public abstract void draw(Canvas canvas, int maxWidth, int maxHeight);

    /**
     * Checks if the given child index is valid, and throws an exception is it isn't
     * @param index The child index that is to be checked
     * @throws IndexOutOfBoundsException If the child index is invalid
     */
    protected final void checkChildIndex(int index) throws IndexOutOfBoundsException
    {
        final int childCount = mathObject.getChildrenCount();
        if(childCount == 0)
            throw new IndexOutOfBoundsException(mathObject.getClass().getCanonicalName() + " doesn't have any children.");
        else if(childCount == 1 && index != 0)
            throw new IndexOutOfBoundsException("Invalid child index " + Integer.toString(index) + ", "
                    + mathObject.getClass().getCanonicalName() + " has only 1 child.");
        else if(index < 0 || index >= childCount)
            throw new IndexOutOfBoundsException("Invalid child index " + Integer.toString(index) + ", "
                    + mathObject.getClass().getCanonicalName() + " has only "
                    + Integer.toString(childCount) + " children.");
    }
    
    /** An exception that can be thrown when an invalid type of {@link MathObject} is passed to one of the subclasses of {@link DrawableMathObject} */
    public static class InvalidMathObjectException extends RuntimeException
    {
        private static final long serialVersionUID = -437505296082991577L;
        
        /** Default constructor */
        public InvalidMathObjectException()
        {}

        /** Constructor
         * @param msg A message describing the error
         */
        public InvalidMathObjectException(String msg)
        { super(msg); }
        
        /** Creates an "Unsupported type" exception from the given {@link MathObject}
         * @param in The name of the method where the error occurred
         * @param mObject The {@link MathObject} for which the error occurred
         * @return The {@link InvalidMathObjectException} that's created from the given arguments
         */
        public static InvalidMathObjectException createUnsupportedTypeException(String in, MathObject mObject)
        {
            return new InvalidMathObjectException("An instance of the unsupported type " + mObject.getClass().getCanonicalName() + " was passed to " + in);
        }
    }
    
}
