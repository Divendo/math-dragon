package org.teaminfty.math_dragon.view;

import java.util.ArrayDeque;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;

/** A view that can hold and draw a mathematical formula */
public class MathView extends View
{
    /** The top-level {@link MathObject} */
    private MathObject mathObject = null;
    
    /** The GestureDetector we're going to use for detecting scrolling and clicking */
    private GestureDetector gestureDetector = null;

    /** The ScaleGestureDetector we're going to use for detecting scale events */
    private ScaleGestureDetector scaleGestureDetector = null;
    
    /** The translation that's applied to the canvas because of the scrolling */
    private Point scrollTranslate = new Point(0, 0);
    
    public MathView(Context context)
    {
        super(context);
        mathObjectDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
        initGestureDetector();
    }

    public MathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mathObjectDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
        initGestureDetector();
    }

    public MathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mathObjectDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setMathObject(null);    // Setting the MathObject to null will construct a MathObjectEmpty
        initGestureDetector();
    }
    
    /** Set the top-level {@link MathObject}
     * @param newMathObject The new value for the top-level {@link MathObject}
     */
    public void setMathObject(MathObject newMathObject)
    {
        // Reset the translation
        scrollTranslate.set(0, 0);
        
        // Set the MathObject
        setMathObjectHelper(newMathObject);
    }
    
    /** Private helper for {@link MathView#setMathObject(MathObject) setMathObject()} */
    private void setMathObjectHelper(MathObject newMathObject)
    {
        // Remember the new MathObject, if it is null we create a MathObjectEmpty
        if((mathObject = newMathObject) == null)
            mathObject = new MathObjectEmpty();
        
        // Set the default size and the level for the MathObject
        mathObject.setDefaultHeight((int) mathObjectDefaultHeight);
        mathObject.setLevel(0);
        
        // Redraw
        invalidate();
    }
    
    /** Get the top-level {@link MathObject}
     * @return The top-level {@link MathObject}
     */
    public MathObject getMathObject()
    { return mathObject; }
    
    /** Initialises the gesture detector */
    private void initGestureDetector()
    {
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    /** Recursively sets the given state for the given {@link MathObject} and all of its children
     * @param mo The {@link MathObject} to set the given state for
     * @param state The state that the {@link MathObject}s should be set to */
    private void setHoverState(MathObject mo, HoverState state)
    {
        // Set the state
        mo.setState(state);
        
        // Loop through the children and set their states
        for(int i = 0; i < mo.getChildCount(); ++i)
            setHoverState(mo.getChild(i), state);
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Save the canvas
        canvas.save();
        
        // Translate the canvas
        canvas.translate(scrollTranslate.x, scrollTranslate.y);
        
        // Simply draw the math object
        Rect boundingBox = mathObject.getBoundingBox();
        canvas.translate((canvas.getWidth() - boundingBox.width()) / 2, (canvas.getHeight() - boundingBox.height()) / 2);
        mathObject.draw(canvas);
        
        // Restore the canvas
        canvas.restore();
    }
    
    /** Bounds the scrolling translation to make sure there is always a part of the current {@link MathObject} visible */
    private void boundScrollTranslation()
    {
        // Get the bounding box of the MathObject
        Rect boundingBox = mathObject.getBoundingBox();
        
        // The least size that should still be visible of the current MathObject
        final int leastHorSize = Math.min(boundingBox.width() / 2, getResources().getDimensionPixelSize(R.dimen.math_object_default_size));
        final int leastVertSize = Math.min(boundingBox.height() / 2, getResources().getDimensionPixelSize(R.dimen.math_object_default_size));
        
        // Set a left bounding for the scrolling
        scrollTranslate.x = Math.max(scrollTranslate.x, leastHorSize - (getWidth() - boundingBox.width()) / 2 - boundingBox.width());
        scrollTranslate.y = Math.max(scrollTranslate.y, leastVertSize - (getHeight() - boundingBox.height()) / 2 - boundingBox.height());
        
        // Set a right bounding for the scrolling
        scrollTranslate.x = Math.min(scrollTranslate.x, (getWidth() - boundingBox.width()) / 2 + boundingBox.width() - leastHorSize);
        scrollTranslate.y = Math.min(scrollTranslate.y, (getHeight() - boundingBox.height()) / 2 + boundingBox.height() - leastVertSize);
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            // Translate the canvas
            scrollTranslate.offset((int) -distanceX, (int) -distanceY);
            
            // Bound the scroll translation
            boundScrollTranslation();
            
            // Redraw
            invalidate();
            
            // Always return true
            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent me)
        {
            // Determine click position
            Point clickPos = new Point((int) me.getX(), (int) me.getY());
            
            // Determine how the canvas will be translated when drawing the current MathObject
            Rect boundingBox = mathObject.getBoundingBox();
            boundingBox.offset(scrollTranslate.x, scrollTranslate.y);
            boundingBox.offset((getWidth() - boundingBox.width()) / 2, (getHeight() - boundingBox.height()) / 2);
            
            // Keep track of which objects still need to be checked
            ArrayDeque<HoverInformation> queue = new ArrayDeque<HoverInformation>();
            queue.addLast(new HoverInformation(mathObject, boundingBox, null, 0));
            
            // Keep going until the queue is empty
            while(!queue.isEmpty())
            {
                // Pop off an element of the queue
                HoverInformation info = queue.pollFirst();
                
                // If the MathObject is a MathObjectEmpty or MathConstant, we check if we clicked on it
                if(info.mathObject instanceof MathObjectEmpty || info.mathObject instanceof MathConstant)
                {
                    // If we click inside the object, we're done looking
                    if(info.boundingBox.contains(clickPos.x, clickPos.y))
                    {
                        // Remember the info about the box we clicked
                        emptyBoxReplaceInfo = info;
                        
                        // Light up the box we clicked
                        info.mathObject.setState(HoverState.HOVER);
                        
                        // Create a dialog
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Enter the constant");
                        alert.setMessage("Enter the value, then press OK!");

                        // Set an EditText view to get user input   
                        final EditText input = new EditText(getContext());
                        alert.setView(input);

                        // Create an OK button
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                // Create a MathConstant from the user input
                                MathConstant mathConstant = new MathConstant(input.getText().toString());
                                
                                // Replace the empty box we clicked with the new MathConstant
                                replaceEmptyBox(mathConstant);
                            }
                        });

                        // Create a cancel button
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                emptyBoxReplaceInfo.mathObject.setState(HoverState.NONE);
                            }
                        });

                        // Show the dialog
                        alert.show();
                    }
                }
                else
                {
                    // Add the children we click on to the queue
                    for(int i = 0; i < info.mathObject.getChildCount(); ++i)
                    {
                        // Get the bounding box for the child
                        Rect childBoundingBox = info.mathObject.getChildBoundingBox(i);
                        childBoundingBox.offset(info.boundingBox.left, info.boundingBox.top);

                        // Add the child to the queue if we click inside the child
                        if(childBoundingBox.contains(clickPos.x, clickPos.y))
                            queue.addLast(new HoverInformation(info.mathObject.getChild(i), childBoundingBox, info.mathObject, i));
                    }
                }
            }
            
            // Always return true
            return true;
        }
    }
    
    /** The default height of the MathObjects as a float */
    private float mathObjectDefaultHeight = 0.0f;
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            mathObjectDefaultHeight = Math.max(mathObjectDefaultHeight * detector.getScaleFactor(), getResources().getDimensionPixelSize(R.dimen.math_object_min_default_size));
            mathObject.setDefaultHeight((int) mathObjectDefaultHeight);
            invalidate();
            return true;
        }
    }
    
    @Override
    public boolean onDragEvent(DragEvent event)
    {
        // Retrieve the shadow
        MathShadow mathShadow = (MathShadow) event.getLocalState();
        
        switch(event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:
            return true;
            
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
            {
                // Calculate the coordinates of the top-left corner of the MathObject
                Rect dragBoundingBox = mathShadow.getMathObjectBounding();
                dragBoundingBox.offset((int) event.getX(), (int) event.getY());
                
                // Show where we're hovering above
                respondToDrag(mathShadow.getMathObject(), dragBoundingBox, false);
                invalidate();
            }
            return true;
            
            case DragEvent.ACTION_DRAG_EXITED:
                setHoverState(mathObject, HoverState.NONE);
                invalidate();
            return true;
            
            case DragEvent.ACTION_DROP:
            {
                // Calculate the coordinates of the top-left corner of the MathObject
                Rect dragBoundingBox = mathShadow.getMathObjectBounding();
                dragBoundingBox.offset((int) event.getX(), (int) event.getY());
                
                // Show where we're hovering above
                respondToDrag(mathShadow.getMathObject(), dragBoundingBox, true);
                invalidate();
            }
            return true;

            case DragEvent.ACTION_DRAG_ENDED:
                setHoverState(mathObject, HoverState.NONE);
                invalidate();
            return true;
        }
        return false;
    }
    
    /** Holds information about the {@link MathObject} that's being dragged to */
    private static class HoverInformation
    {
        /** Constructor
         * @param mathObject The {@link MathObject} we're hovering over 
         * @param boundingBox The bounding box of {@link HoverInformation#mathObject mathObject}
         * @param parent The parent of the {@link MathObject} we're hovering over (null if we're hovering over the root {@link MathObject})
         * @param childIndex The child index of the {@link MathObject} we're hovering over (undefined if we're hovering over the root {@link MathObject})
         */
        public HoverInformation(MathObject mathObject, Rect boundingBox, MathObject parent, int childIndex)
        {
            this.mathObject = mathObject;
            this.boundingBox = boundingBox;
            this.parent = parent;
            this.childIndex = childIndex;
        }
        
        /** The {@link MathObject} we're hovering over */
        public MathObject mathObject = null;
        
        /** The bounding box of {@link HoverInformation#mathObject mathObject} */
        public Rect boundingBox = null;
        
        /** The parent of the {@link MathObject} we're hovering over (null if we're hovering over the root {@link MathObject}) */
        public MathObject parent = null;
        
        /** The child index of the {@link MathObject} we're hovering over (undefined if we're hovering over the root {@link MathObject}) */
        public int childIndex = 0;
    }

    /** Calculates the square of the distance from the given point to the centre of the given rectangle
     * @param p The point
     * @param r The rectangle
     * @return The square of the distance from the given point to the centre of the given rectangle
     */
    private int getDst(Point p, Rect r)
    {
        return (r.centerX() - p.x) * (r.centerX() - p.x) + (r.centerY() - p.y) * (r.centerY() - p.y);
    }

    /** Responds to a {@link MathObject} that is being dragged over this view.
     * It can either respond by lighting up the right part of the current {@link MathObject} (i.e. <tt>dropped == false</tt>).
     * Or it can respond by inserting the dropped {@link MathObject} into the current {@link MathObject} (i.e. <tt>dropped == true</tt>).
     * @param dragMathObject The {@link MathObject} that is being dragged
     * @param dragBoundingBox The bounding box of the {@link MathObject} that is being dragged
     * @param dropped Whether or not the {@link MathObject} is being dropped
     */
    private void respondToDrag(MathObject dragMathObject, Rect dragBoundingBox, boolean dropped)
    {
        // Reset the state of the current MathObject and all of its descendants
        setHoverState(mathObject, HoverState.NONE);
        
        // The aiming point of the MathObject that is being dragged (the main aiming point)
        Point aimPoint = new Point(dragBoundingBox.centerX(), dragBoundingBox.centerY());
        
        // Determine the aiming points of the children of the MathObject that is being dragged
        // But only if they are empty children (otherwise they aren't interesting, so we set their aiming points to null)
        Point[] childAimPoints = new Point[dragMathObject.getChildCount()];
        for(int i = 0; i < childAimPoints.length; ++i)
        {
            if(dragMathObject.getChild(i) instanceof MathObjectEmpty)
            {
                Rect rect = dragMathObject.getChildBoundingBox(i);
                childAimPoints[i] = new Point(dragBoundingBox.left + rect.centerX(), dragBoundingBox.top + rect.centerY());
            }
            else
                childAimPoints[i] = null;
        }
        
        // Determine how the canvas will be translated when drawing the current MathObject
        Rect boundingBox = mathObject.getBoundingBox();
        boundingBox.offset(scrollTranslate.x, scrollTranslate.y);
        boundingBox.offset((getWidth() - boundingBox.width()) / 2, (getHeight() - boundingBox.height()) / 2);
        
        // If we don't intersect with the bounding box at all, we can stop here
        if(!Rect.intersects(dragBoundingBox, boundingBox))
            return;
        
        // Some variables that will keep track of where we're hovering above
        int sourceChild = -1;                   // The source child that's causing the hover (-1 means the complete mathObject)
        int dst = -1;                           // The best distance (squared) we've found so far (-1 means that no hover has been found yet)
        HoverInformation currHover = null;      // The hover information of the MathObject we're currently hovering over
        
        // Keep track of which objects still need to be checked
        ArrayDeque<HoverInformation> queue = new ArrayDeque<HoverInformation>();
        queue.addLast(new HoverInformation(mathObject, boundingBox, null, 0));
        
        // Keep going until the queue is empty
        while(!queue.isEmpty())
        {
            // Pop off an element of the queue
            HoverInformation info = queue.pollFirst();
            
            // If the MathObject is a MathObjectEmpty, we check the distance to the main aiming point
            if(info.mathObject instanceof MathObjectEmpty)
            {
                // If the aim point is not in the rectangle at all, we've nothing to do
                if(!info.boundingBox.contains(aimPoint.x, aimPoint.y))
                    continue;
                
                // Check if the distance is smaller than what we've found so far
                final int tmpDst = getDst(aimPoint, info.boundingBox);
                if(dst == -1 || tmpDst < dst)
                {
                    sourceChild = -1;
                    dst = tmpDst;
                    currHover = info;
                }
            }
            else
            {
                // Determine if we're aiming at this object itself
                Rect[] operatorBounds = info.mathObject.getOperatorBoundingBoxes();
                for(Rect rect : operatorBounds)
                    rect.offset(info.boundingBox.left, info.boundingBox.top);
                for(int i = 0; i < childAimPoints.length; ++i)
                {
                    // If the current child has no aim point, we skip
                    if(childAimPoints[i] == null) continue;
                    
                    // Determine the distance to the centre of every operator bounding box
                    for(Rect rect : operatorBounds)
                    {
                        // If the aim point is not in the rectangle at all, we've nothing to do
                        if(!rect.contains(childAimPoints[i].x, childAimPoints[i].y))
                            continue;
                        
                        // Check if the distance is smaller than what we've found so far
                        final int tmpDst = getDst(childAimPoints[i], rect);
                        if(dst == -1 || tmpDst < dst)
                        {
                            sourceChild = i;
                            dst = tmpDst;
                            currHover = info;
                        }
                    }
                }
                
                // Add the children we intersect with to the queue
                for(int i = 0; i < info.mathObject.getChildCount(); ++i)
                {
                    // Get the bounding box for the child
                    Rect childBoundingBox = info.mathObject.getChildBoundingBox(i);
                    childBoundingBox.offset(info.boundingBox.left, info.boundingBox.top);
                    
                    // If we don't intersect with the bounding box at all, we're not interested
                    if(!Rect.intersects(dragBoundingBox, childBoundingBox))
                        continue;
                    
                    // Add the child to the queue
                    queue.addLast(new HoverInformation(info.mathObject.getChild(i), childBoundingBox, info.mathObject, i));
                }
            }
        }
        
        // If we've found a MathObject we're hovering over, do the right thing with it
        if(currHover != null)
        {
            // If we're not dropping, just light up the part we're hovering over
            // Otherwise we insert the MathObject that's being dragged at the right point in current MathObject
            if(!dropped)
                currHover.mathObject.setState(HoverState.HOVER);
            else
            {
                // Determine whether or not we're dropping the whole thing in an empty box
                if(sourceChild == -1)
                {
                    if(currHover.parent == null)
                        setMathObjectHelper(dragMathObject);
                    else
                        ParenthesesHelper.makeChild(currHover.parent, dragMathObject, currHover.childIndex);
                }
                else
                {
                    ParenthesesHelper.makeChild(dragMathObject, currHover.mathObject, sourceChild);
                    if(currHover.parent == null)
                        setMathObjectHelper(dragMathObject);
                    else
                        ParenthesesHelper.makeChild(currHover.parent, dragMathObject, currHover.childIndex);
                }
                
                // Make sure the MathObject and all of its descendants have the right state
                setHoverState(mathObject, HoverState.NONE);
                
                // Make sure every MathObject has the right level
                mathObject.setLevel(0);
            }
        }
    }
    
    /** The information about the {@link MathObject} that is to be replaced in the next call to {@link MathView#replaceEmptyBox(MathConstant) replaceEmptyBox()} */
    private HoverInformation emptyBoxReplaceInfo = null;
    
    /** Replaces the {@link MathObject} (whose information is stored in {@link MathView#emptyBoxReplaceInfo emptyBoxReplaceInfo}) with the given {@link MathConstant}.
     * @param constant The {@link MathConstant} to replace the {@link MathObject} with
     */
    private void replaceEmptyBox(MathConstant constant)
    {
        // Place the constant
        if(emptyBoxReplaceInfo.parent == null)
            setMathObjectHelper(constant);
        else
            emptyBoxReplaceInfo.parent.setChild(emptyBoxReplaceInfo.childIndex, constant);
        
        // Redraw
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        // Pass the touch event to the gesture detector
        gestureDetector.onTouchEvent(me);
        scaleGestureDetector.onTouchEvent(me);
        
        // Always consume the event
        return true;
    }
}
