package org.teaminfty.math_dragon.view;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard;
import org.teaminfty.math_dragon.view.math.ExpressionDuplicator;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.operation.Integral;
import org.teaminfty.math_dragon.view.math.operation.binary.Linear;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/** A view that can hold and draw a mathematical formula */
public class MathView extends View
{
    /** The top-level {@link Expression} */
    private Expression expression = null;
    
    /** The GestureDetector we're going to use for detecting scrolling and clicking */
    private GestureDetector gestureDetector = null;

    /** The ScaleGestureDetector we're going to use for detecting scale events */
    private ScaleGestureDetector scaleGestureDetector = null;
    
    /** The translation that's applied to the canvas because of the scrolling */
    private Point scrollTranslate = new Point(0, 0);
    
    /** Whether or not this {@link MathView} is enabled (i.e. it can be edited) */
    private boolean enabled = true;
    
    /** Whether or not caching is currently enabled */
    private boolean cacheEnabled = true;
    
    /** Cache for the {@link Expression} */
    private Bitmap cache = null;
    
    /** For which size the {@link Expression} has been cached */
    private int cachedForSize = -1;
    
    /** The paint that's used to draw the cache */
    private Paint cachePaint = new Paint();
    
    public MathView(Context context)
    {
        super(context);
        expressionDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setExpression(null);    // Setting the Expression to null will construct a Empty
        initGestureDetector();
    }

    public MathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        expressionDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setExpression(null);    // Setting the Expression to null will construct a Empty
        initGestureDetector();
    }

    public MathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        expressionDefaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        setExpression(null);    // Setting the Expression to null will construct a Empty
        initGestureDetector();
    }
    
    /** Set whether or not this {@link MathView} is enabled (i.e. it can be edited)
     * @param enable Whether or not to enable this {@link MathView} */
    public void setEnabled(boolean enable)
    {
        if(!(enabled = enable))
        {
            setHoverState(expression, HoverState.NONE);
            invalidate();
        }
    }
    
    /** Set the top-level {@link Expression}
     * @param newExpression The new value for the top-level {@link Expression} */
    public void setExpression(Expression newExpression)
    {
        // Reset the translation
        resetScroll();
        
        // Set the Expression
        setExpressionHelper(newExpression);
        
        // Notify the listener of the change
        expressionChanged();
    }

    /** Set the top-level {@link Expression} without sending an {@link OnExpressionChangeListener} event.
     * This also won't reset the scroll translation.
     * @param newExpression The new value for the top-level {@link Expression} */
    public void setExpressionSilent(Expression newExpression)
    {
        // Set the Expression
        setExpressionHelper(newExpression);
        
        // Make sure the scroll translation is still bounded
        boundScrollTranslation();
    }
    
    /** Private helper for {@link MathView#setExpression(Expression) setExpression()} */
    private void setExpressionHelper(Expression newExpression)
    {
        // Remember the new Expression, if it is null we create a Empty
        if((expression = newExpression) == null)
            expression = new Empty();
        
        // Set the default size and the level for the Expression
        expression.setDefaultHeight((int) expressionDefaultHeight);
        expression.setLevel(0);
        
        // Invalidate the cache
        cache = null;
        
        // Redraw
        invalidate();
    }
    
    /** Resets the scroll position */
    public void resetScroll()
    {
        scrollTranslate.set(0, 0);
        invalidate();
    }
    
    /** Get the top-level {@link Expression}
     * @return The top-level {@link Expression}
     */
    public Expression getExpression()
    { return expression; }
    
    /** Initialises the gesture detector */
    private void initGestureDetector()
    {
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }
    
    /** A listener that can be implemented by the parent fragment to listen for events of this {@link MathView} */
    public interface OnEventListener
    {
        /** Called when the {@link Expression} has changed
         * @param expression The current {@link Expression} */
        public void changed(Expression expression);
        
        /** Called when a keyboard with the given confirm listener should be shown
         * @param mathSymbol The initial value for the input (can be <tt>null</tt>)
         * @param listener The confirm listener */
        public void showKeyboard(Symbol mathSymbol, FragmentKeyboard.OnConfirmListener listener);

        /** Called when a warning with the given information should be shown
     * @param title The ID of the string resource that should be the title
     * @param msg The ID of the string resource that should be the message */
        public void showWarning(int title, int msg);
    }
    
    /** The current {@link OnEventListener} */
    private OnEventListener onEventListener = null;
    
    /** Set the current {@link OnEventListener}
     * @param listener The new {@link OnEventListener} */
    public void setEventListener(OnEventListener listener)
    { onEventListener = listener; }
    
    /** Asks the parent fragment to show the keyboard with the given confirm listener
     * @param mathSymbol The initial value for the input (can be <tt>null</tt>)
     * @param listener The confirm listener */
    protected void showKeyboard(Symbol mathSymbol, FragmentKeyboard.OnConfirmListener listener)
    {
        if(onEventListener != null)
            onEventListener.showKeyboard(mathSymbol, listener);
    }
    
    /** Call {@link OnEventListener#change() change()} on the current {@link OnEventListener} */
    protected void expressionChanged()
    {
        if(onEventListener != null)
            onEventListener.changed(expression);
    }
    
    /** Asks the parent fragment to show a warning with the given texts
     * @param title The ID of the string resource that should be the title
     * @param msg The ID of the string resource that should be the message */
    protected void showWarning(int title, int msg)
    {
        if(onEventListener != null)
            onEventListener.showWarning(title, msg);
    }

    /** Recursively sets the given state for the given {@link Expression} and all of its children
     * @param mo The {@link Expression} to set the given state for
     * @param state The state that the {@link Expression}s should be set to */
    private void setHoverState(Expression mo, HoverState state)
    {
        // Set the state
        mo.setState(state);
        
        // Loop through the children and set their states
        for(int i = 0; i < mo.getChildCount(); ++i)
            setHoverState(mo.getChild(i), state);
        
        // Invalidate the cache
        cache = null;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        // If the expression isn't in the cache yet, cache it
        if(cacheEnabled && (cache == null || cachedForSize != expression.getDefaultHeight()))
        {
            Rect boundingBox = expression.getBoundingBox();
            cache = Bitmap.createBitmap(boundingBox.width(), boundingBox.height(), Bitmap.Config.ARGB_8888);
            expression.draw(new Canvas(cache));
            cachedForSize = expression.getDefaultHeight();
        }
        
        // Save the canvas
        canvas.save();
        
        // Translate the canvas
        canvas.translate(scrollTranslate.x, scrollTranslate.y);
        
        // Draw the expression from the cache (if cache is enabled), otherwise draw it directly
        if(cacheEnabled)
            canvas.drawBitmap(cache, (canvas.getWidth() - cache.getWidth()) / 2, (canvas.getHeight() - cache.getHeight()) / 2, cachePaint);
        else
        {
            Rect boundingBox = expression.getBoundingBox();
            canvas.translate((canvas.getWidth() - boundingBox.width()) / 2, (canvas.getHeight() - boundingBox.height()) / 2);
            expression.draw(canvas);
        }
        
        // Restore the canvas
        canvas.restore();
    }
    
    /** Bounds the scrolling translation to make sure there is always a part of the current {@link Expression} visible */
    private void boundScrollTranslation()
    {
        // Get the bounding box of the Expression
        Rect boundingBox = expression.getBoundingBox();
        
        // The least size that should still be visible of the current Expression
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
            // If we're disabled, ignore
            if(!enabled) return true;
            
            // Determine click position
            Point clickPos = new Point((int) me.getX(), (int) me.getY());
            
            // Determine how the canvas will be translated when drawing the current Expression
            Rect boundingBox = expression.getBoundingBox();
            boundingBox.offset(scrollTranslate.x, scrollTranslate.y);
            boundingBox.offset((getWidth() - boundingBox.width()) / 2, (getHeight() - boundingBox.height()) / 2);
            
            // Keep track of which objects still need to be checked
            ArrayDeque<HoverInformation> queue = new ArrayDeque<HoverInformation>();
            queue.addLast(new HoverInformation(expression, boundingBox, null, 0));
            
            // Keep going until the queue is empty
            while(!queue.isEmpty())
            {
                // Pop off an element of the queue
                HoverInformation info = queue.pollFirst();
                
                // If the Expression is Empty or Symbol, we check if we clicked on it
                if(info.expression instanceof Empty || info.expression instanceof Symbol)
                {
                    // If we click inside the object, we're done looking
                    if(info.boundingBox.contains(clickPos.x, clickPos.y))
                    {
                        // Show the keyboard with the given confirm listener
                        if(info.expression instanceof Symbol)
                            showKeyboard((Symbol) info.expression, new ExpressionReplacer(info));
                        else
                            showKeyboard(null, new ExpressionReplacer(info));
                    }
                }
                else
                {
                    // Add the children we click on to the queue
                    for(int i = 0; i < info.expression.getChildCount(); ++i)
                    {
                        // Get the bounding box for the child
                        Rect childBoundingBox = info.expression.getChildBoundingBox(i);
                        childBoundingBox.offset(info.boundingBox.left, info.boundingBox.top);

                        // Add the child to the queue if we click inside the child
                        if(childBoundingBox.contains(clickPos.x, clickPos.y))
                            queue.addLast(new HoverInformation(info.expression.getChild(i), childBoundingBox, info.expression, i));
                    }
                }
            }
            
            // Always return true
            return true;
        }
        
        @Override
        public void onLongPress(MotionEvent me)
        {
            // If we're disabled, ignore
            if(!enabled) return;
            
            // The point we're the user is pressing
            Point aimPoint = new Point((int) me.getX(), (int) me.getY());
            
            // Determine how the canvas will be translated when drawing the current Expression
            Rect boundingBox = expression.getBoundingBox();
            boundingBox.offset(scrollTranslate.x, scrollTranslate.y);
            boundingBox.offset((getWidth() - boundingBox.width()) / 2, (getHeight() - boundingBox.height()) / 2);
            
            // If we don't intersect with the bounding box at all, we can stop here
            if(!boundingBox.contains(aimPoint.x, aimPoint.y))
                return;
            
            // The hover information of the expression we're clicking
            HoverInformation info = new HoverInformation(expression, boundingBox, null, 0);
            
            // Keep going until we break
            while(true)
            {
                // Check if we're clicking inside one of the children
                boolean continueAfterLoop = false;
                for(int i = 0; i < info.expression.getChildCount(); ++i)
                {
                    // Get the bounding box for the child
                    Rect childBoundingBox = info.expression.getChildBoundingBox(i);
                    childBoundingBox.offset(info.boundingBox.left, info.boundingBox.top);

                    // If we clicked inside the child, we'll check that child
                    if(childBoundingBox.contains(aimPoint.x, aimPoint.y))
                    {
                        info = new HoverInformation(info.expression.getChild(i), childBoundingBox, info.expression, i);
                        continueAfterLoop = true;
                        break;
                    }
                }
                if(continueAfterLoop) continue;
                
                // Check if we're clicking on the operator
                Rect[] operatorBoundingBoxes = info.expression.getOperatorBoundingBoxes();
                boolean breakAfterLoop = false;
                for(int i = 0; i < operatorBoundingBoxes.length; ++i)
                {
                    // Offset the operator bounding box
                    operatorBoundingBoxes[i].offset(info.boundingBox.left, info.boundingBox.top);

                    // Check if we clicked the operator bounding box
                    if(operatorBoundingBoxes[i].contains(aimPoint.x, aimPoint.y))
                    {
                        breakAfterLoop = true;
                        break;
                    }
                }
                if(breakAfterLoop) break;
                
                // If we've come here we didn't click inside a child nor did we click on a part of the operator
                // That means we clicked in a void area
                info = null;
                break;
            }
            
            // If we've found a part we're clicking
            // Also, we can't delete an empty box
            if(info != null && !(info.expression instanceof Empty))
            {
                // In case the target is a linear binary operation, we only want the operands directly next to it
                // So we rearrange the Expression tree to make that happen
                freeExpression(info);
                
                // Create a deep copy of the expression we're about to drag
                Expression copy = ExpressionDuplicator.deepCopy(info.expression);
                copy.setDefaultHeight(getResources().getDimensionPixelSize(R.dimen.math_object_drag_default_size));

                // Start dragging the deletion object
                MathDeleteShadow mathDeleteShadow = new MathDeleteShadow(copy, getResources());
                mathDeleteShadow.setOnDeleteConfirmListener(new ExpressionRemover(info));
                startDrag(ClipData.newPlainText("", ""), mathDeleteShadow, mathDeleteShadow, 0);
            }
        }
    }
    
    /** The default height of the Expressions as a float */
    private float expressionDefaultHeight = 0.0f;
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            expressionDefaultHeight = Math.min(getResources().getDimensionPixelSize(R.dimen.math_object_max_default_size),
                    Math.max(expressionDefaultHeight * detector.getScaleFactor(), getResources().getDimensionPixelSize(R.dimen.math_object_min_default_size)));
            expression.setDefaultHeight((int) expressionDefaultHeight);
            invalidate();
            return true;
        }
        
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            cacheEnabled = false;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            cacheEnabled = true;
        }
    }
    
    @Override
    public boolean onDragEvent(DragEvent event)
    {
        // If we're disabled, ignore
        if(!enabled) return false;
        
        // If we're not dragging a MathShadow, we're not interested
        if(!(event.getLocalState() instanceof MathShadow))
            return false;
        
        // Retrieve the shadow
        MathShadow mathShadow = (MathShadow) event.getLocalState();
        
        switch(event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:
                cacheEnabled = false;
                cache = null;
            return true;
            
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
            {
                // Calculate the coordinates of the top-left corner of the Expression
                Rect dragBoundingBox = mathShadow.getExpressionBounding();
                dragBoundingBox.offset((int) event.getX(), (int) event.getY());
                
                // Show where we're hovering above
                respondToDrag(mathShadow.getExpression(), dragBoundingBox, false);
                invalidate();
            }
            return true;
            
            case DragEvent.ACTION_DRAG_EXITED:
                setHoverState(expression, HoverState.NONE);
                invalidate();
            return true;
            
            case DragEvent.ACTION_DROP:
            {
                // Calculate the coordinates of the top-left corner of the Expression
                Rect dragBoundingBox = mathShadow.getExpressionBounding();
                dragBoundingBox.offset((int) event.getX(), (int) event.getY());
                
                // Show where we're hovering above
                respondToDrag(mathShadow.getExpression(), dragBoundingBox, true);
                invalidate();
            }
            return true;

            case DragEvent.ACTION_DRAG_ENDED:
                setHoverState(expression, HoverState.NONE);
                cacheEnabled = true;
                invalidate();
            return true;
        }
        return false;
    }
    
    /** Holds information about the {@link Expression} that's being dragged to */
    private static class HoverInformation
    {
        /** Constructor
         * @param expression The {@link Expression} we're hovering over 
         * @param boundingBox The bounding box of {@link HoverInformation#expression expression}
         * @param parent The parent of the {@link Expression} we're hovering over (null if we're hovering over the root {@link Expression})
         * @param childIndex The child index of the {@link Expression} we're hovering over (undefined if we're hovering over the root {@link Expression})
         */
        public HoverInformation(Expression expression, Rect boundingBox, Expression parent, int childIndex)
        {
            this.expression = expression;
            this.boundingBox = boundingBox;
            this.parent = parent;
            this.childIndex = childIndex;
        }
        
        /** The {@link Expression} we're hovering over */
        public Expression expression = null;
        
        /** The bounding box of {@link HoverInformation#expression expression} */
        public Rect boundingBox = null;
        
        /** The parent of the {@link Expression} we're hovering over (null if we're hovering over the root {@link Expression}) */
        public Expression parent = null;
        
        /** The child index of the {@link Expression} we're hovering over (undefined if we're hovering over the root {@link Expression}) */
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
    
    /** Reorders the expression tree so that the {@link Expression} in <tt>info.expression</tt> becomes free.
     * That means that if it's a binary linear operation, only the operands displayed directly next to it will be its children.
     * @param info The information about the {@link Expression} to free (note that values in this object may be changed as well) */
    private void freeExpression(HoverInformation info)
    {
        if(!(info.expression instanceof Linear)) return;

        // The linear binary operation we're going to modify
        Linear binOp = (Linear) info.expression;
        
        // Get the right operand
        Expression operand = binOp.getRight();
        Expression newParent = null;
        while(operand instanceof Linear)
        {
            newParent = operand;
            operand = operand.getChild(0);
        }
        
        // Change the structure of the expression tree (if necessary)
        if(newParent != null)
        {
            Expression newSuperParent = binOp.getRight();
            binOp.setRight(operand);
            newParent.setChild(0, binOp);
            
            if(info.parent == null)
                setExpressionHelper(newSuperParent);
            else
                ParenthesesHelper.makeChild(info.parent, newSuperParent, info.childIndex);
            
            info.parent = newParent;
            info.childIndex = 0;
        }
        
        // Now we do the same thing for the left operand
        operand = binOp.getLeft();
        newParent = null;
        while(operand instanceof Linear)
        {
            newParent = operand;
            operand = operand.getChild(1);
        }
        
        // Change the structure of the expression tree (if necessary)
        if(newParent != null)
        {
            Expression newSuperParent = binOp.getLeft();
            binOp.setLeft(operand);
            newParent.setChild(1, binOp);
            
            if(info.parent == null)
                setExpressionHelper(newSuperParent);
            else
                ParenthesesHelper.makeChild(info.parent, newSuperParent, info.childIndex);
            
            info.parent = newParent;
            info.childIndex = 1;
        }
    }

    /** Responds to a {@link Expression} that is being dragged over this view.
     * It can either respond by lighting up the right part of the current {@link Expression} (i.e. <tt>dropped == false</tt>).
     * Or it can respond by inserting the dropped {@link Expression} into the current {@link Expression} (i.e. <tt>dropped == true</tt>).
     * @param dragExpr The {@link Expression} that is being dragged
     * @param dragBoundingBox The bounding box of the {@link Expression} that is being dragged
     * @param dropped Whether or not the {@link Expression} is being dropped
     */
    private void respondToDrag(Expression dragExpr, Rect dragBoundingBox, boolean dropped)
    {
        // Reset the state of the current Expression and all of its descendants
        setHoverState(expression, HoverState.NONE);
        
        // The aiming point of the Expression that is being dragged (the main aiming point)
        Point aimPoint = new Point(dragBoundingBox.centerX(), dragBoundingBox.centerY());
        
        // Determine the aiming points of the children of the Expression that is being dragged
        // But only if they are empty children (otherwise they aren't interesting, so we set their aiming points to null)
        Point[] childAimPoints = new Point[dragExpr.getChildCount()];
        for(int i = 0; i < childAimPoints.length; ++i)
        {
            if(dragExpr.getChild(i) instanceof Empty)
            {
                Rect rect = dragExpr.getChildBoundingBox(i);
                childAimPoints[i] = new Point(dragBoundingBox.left + rect.centerX(), dragBoundingBox.top + rect.centerY());
            }
            else
                childAimPoints[i] = null;
        }
        
        // Determine how the canvas will be translated when drawing the current Expression
        Rect boundingBox = expression.getBoundingBox();
        boundingBox.offset(scrollTranslate.x, scrollTranslate.y);
        boundingBox.offset((getWidth() - boundingBox.width()) / 2, (getHeight() - boundingBox.height()) / 2);
        
        // If we don't intersect with the bounding box at all, we can stop here
        if(!Rect.intersects(dragBoundingBox, boundingBox))
            return;
        
        // Some variables that will keep track of where we're hovering above
        int sourceChild = -1;                   // The source child that's causing the hover (-1 means the complete expression)
        int dst = -1;                           // The best distance (squared) we've found so far (-1 means that no hover has been found yet)
        HoverInformation currHover = null;      // The hover information of the Expression we're currently hovering over
        
        // Keep track of which objects still need to be checked
        ArrayDeque<HoverInformation> queue = new ArrayDeque<HoverInformation>();
        queue.addLast(new HoverInformation(expression, boundingBox, null, 0));
        
        // Keep going until the queue is empty
        while(!queue.isEmpty())
        {
            // Pop off an element of the queue
            HoverInformation info = queue.pollFirst();
            
            // If the Expression is Empty, we check the distance to the main aiming point
            if(info.expression instanceof Empty)
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
                Rect[] operatorBounds = info.expression.getOperatorBoundingBoxes();
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
                for(int i = 0; i < info.expression.getChildCount(); ++i)
                {
                    // Get the bounding box for the child
                    Rect childBoundingBox = info.expression.getChildBoundingBox(i);
                    childBoundingBox.offset(info.boundingBox.left, info.boundingBox.top);
                    
                    // If we don't intersect with the bounding box at all, we're not interested
                    if(!Rect.intersects(dragBoundingBox, childBoundingBox))
                        continue;
                    
                    // Add the child to the queue
                    queue.addLast(new HoverInformation(info.expression.getChild(i), childBoundingBox, info.expression, i));
                }
            }
        }
        
        // If we've found a Expression we're hovering over, do the right thing with it
        if(currHover != null)
        {
            // If we're not dropping, just light up the part we're hovering over
            // Otherwise we insert the Expression that's being dragged at the right point in current Expression
            if(!dropped)
            {
                currHover.expression.setState(HoverState.HOVER);
                
                // Invalidate the cache
                cache = null;
            }
            else
            {
                // Determine whether or not we're dropping the whole thing in an empty box
                if(sourceChild == -1)
                {
                    if(currHover.parent == null)
                        setExpressionHelper(dragExpr);
                    else
                        ParenthesesHelper.makeChild(currHover.parent, dragExpr, currHover.childIndex);
                }
                else
                {
                    // In case the target is a linear binary operation, we only want the operands directly next to it
                    // So we rearrange the Expression tree to make that happen
                    freeExpression(currHover);
                    
                    // Insert the Expression into to Expression tree
                    ParenthesesHelper.makeChild(dragExpr, currHover.expression, sourceChild);
                    if(currHover.parent == null)
                        setExpressionHelper(dragExpr);
                    else
                        ParenthesesHelper.makeChild(currHover.parent, dragExpr, currHover.childIndex);
                }
                
                // Make sure the Expression and all of its descendants have the right state
                setHoverState(expression, HoverState.NONE);
                
                // Make sure every Expression has the right level
                expression.setLevel(0);
                
                // Notify the listener of the change
                expressionChanged();
            }
        }
    }
    
    /** Converts the given {@link FragmentKeyboard.OnConfirmListener} to a bundle, given that it was created by this class */
    public Bundle keyboardListenerToBundle(FragmentKeyboard.OnConfirmListener listener)
    {
        if(!(listener instanceof ExpressionReplacer))
            return null;
        
        return ((ExpressionReplacer) listener).toBundle(expression);
    }
    
    /** Creates a {@link ExpressionReplacer} from the given bundle
     * @param bundle The bundle to create the {@link ExpressionReplacer} from */
    public FragmentKeyboard.OnConfirmListener keyboardListenerFromBundle(Bundle bundle)
    {
        // Get the path to the clicked part
        ArrayList<Integer> path = bundle.getIntegerArrayList(ExpressionReplacer.BUNDLE_MATH_OBJECT_INFO);
        
        // If the path is empty, it was the root
        if(path.isEmpty())
            return new ExpressionReplacer(new HoverInformation(expression, null, null, 0));
        // If it wasn't we follow the path to create an instance of HoverInformation
        else
        {
            HoverInformation hoverInformation = new HoverInformation(expression.getChild(path.get(0)), null, expression, path.get(0));
            for(int i = 1; i < path.size(); ++i)
            {
                hoverInformation.childIndex = path.get(i);
                hoverInformation.parent = hoverInformation.expression;
                hoverInformation.expression = hoverInformation.expression.getChild(path.get(i));
            }
            return new ExpressionReplacer(hoverInformation);
        }
    }
    
    /** Replaces an {@link Expression} with the {@link Symbol} that the keyboard returns */
    private class ExpressionReplacer implements FragmentKeyboard.OnConfirmListener
    {
        /** The info about the {@link Expression} that is to replaced */
        private HoverInformation expressionInfo = null;
        
        /** Constructs the replacer with the given info
         * @param info The info about the {@link Expression} that is to replaced */
        public ExpressionReplacer(HoverInformation info)
        {
            expressionInfo = info;
        }

        @Override
        public void confirmed(Symbol mathSymbol)
        {
            // Keep track of whether a warning should be shown or not
            int warningId = 0;
            
            // Place the symbol
            if(expressionInfo.parent == null)
                setExpressionHelper(mathSymbol);
            else
            {
                // In the 'integrate over' child only a single variable is allowed
                if(expressionInfo.parent instanceof Integral && expressionInfo.childIndex == 1)
                {
                    // No constants allowed
                    if(mathSymbol.getFactor() != 1 || (mathSymbol.getPiPow() | mathSymbol.getEPow() | mathSymbol.getIPow()) != 0)
                        warningId = R.string.invalid_integrate_over;
                    else
                    {
                        // Check if exactly one variable is used
                        boolean varVisible = false;
                        for(int i = 0; i < mathSymbol.varPowCount(); ++i)
                        {
                            if(mathSymbol.getVarPow(i) == 0)
                                continue;
                            else if(mathSymbol.getVarPow(i) == 1)
                            {
                                if(varVisible)
                                {
                                    warningId = R.string.invalid_integrate_over;
                                    break;
                                }
                                else
                                    varVisible = true;
                            }
                            else
                            {
                                warningId = R.string.invalid_integrate_over;
                                break;
                            }
                        }
                        if(!varVisible)
                            warningId = R.string.invalid_integrate_over;
                    }
                }
                        
                // Only insert the symbol if no problems have been found
                if(warningId == 0)
                {
                    expressionInfo.parent.setChild(expressionInfo.childIndex, mathSymbol);
                    ParenthesesHelper.setParentheses(expression);
                }
            }
            
            // Show the warning (if necessary)
            if(warningId != 0)
                showWarning(R.string.invalid_input, warningId);
            else
            {
                // Invalidate cache and redraw
                cache = null;
                invalidate();
                
                // Notify the listener of the change
                expressionChanged();
            }
        }
        
        /** An integer ArrayList in the state bundle that contains the path (in child numbers) to the child in expressionInfo */
        public static final String BUNDLE_MATH_OBJECT_INFO = "math_object_info";
        
        /** Returns the information about this {@link ExpressionReplacer} as a bundle
         * @param root The root {@link Expression} */
        public Bundle toBundle(Expression root)
        {
            // The path to the clicked part
            ArrayList<Integer> path = new ArrayList<Integer>();
            
            // Only create a path if the clicked part wasn't the root
            if(expressionInfo.parent != null)
            {
                if(find(root, expressionInfo.expression, path))
                    Collections.reverse(path);
            }
            
            // Create the bundle and return the result
            Bundle out = new Bundle();
            out.putIntegerArrayList(BUNDLE_MATH_OBJECT_INFO, path);
            return out;
        }
        
        /** Appends the index of the child that contains (or is) the given {@link Expression} to the given list
         * @param parent The parent to search in
         * @param findMe The {@link Expression} to search for
         * @param list The list to append the index to
         * @return <tt>true</tt> if <tt>findMe</tt> was found, <tt>false</tt> otherwise */
        private boolean find(Expression parent, Expression findMe, ArrayList<Integer> list)
        {
            // Loop through all children
            for(int i = 0; i < parent.getChildCount(); ++i)
            {
                if(parent.getChild(i) == findMe || find(parent.getChild(i), findMe, list))
                {
                    list.add(i);
                    return true;
                }
            }
            
            // If we've come here, we haven't found the child
            return false;
        }
    }
    
    /** Removes the {@link Expression} that's described in a {@link HoverInformation} */
    private class ExpressionRemover implements MathDeleteShadow.OnDeleteConfirmListener
    {
        /** The info about the {@link Expression} that is to removed */
        private HoverInformation info = null;
        
        /** Constructs the remover with the given info
         * @param i The info about the {@link Expression} that is to be removed */
        public ExpressionRemover(HoverInformation i)
        { info = i; }

        @Override
        public void confirmed()
        {
            // Remove the expression
            if(info.parent == null)
                setExpressionHelper(null);
            else
            {
                info.parent.setChild(info.childIndex, null);
                ParenthesesHelper.setParentheses(expression);
            }

            // Invalidate cache and redraw
            cache = null;
            invalidate();
            
            // Notify the listener of the change
            expressionChanged();
        }
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