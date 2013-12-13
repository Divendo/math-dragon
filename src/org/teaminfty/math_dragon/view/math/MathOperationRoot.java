package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class MathOperationRoot extends MathBinaryOperation
{
     protected Paint operatorPaint = new Paint();
    
    public MathOperationRoot(int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
        
        // Initialise the paint
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public IExpr eval() throws EmptyChildException 
    {
        // Check the children
        this.checkChildren();
        
        // Return the result
        return F.Power( getChild(1).eval(), F.Divide(F.ZZ(1), getChild(0).eval()));
    }

    @Override
    public double approximate() throws NotConstantException, EmptyChildException 
    {
        // Check the children
        this.checkChildren();
        
        // Return the result
        return Math.pow( getChild(1).approximate(), 1/getChild(0).approximate() );
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight) 
    {
        // Get the children sizes
        Rect[] sizes = getChildrenSize(maxWidth, maxHeight);
        
        // We'll need 3 bounding boxes to contain the operator
        return new Rect[] {
                new Rect(0, sizes[0].height(), 7 * sizes[0].width() / 5, sizes[0].height() + sizes[1].height()),
                new Rect(sizes[0].width(), 0, 7 * sizes[0].width() / 5, sizes[0].height()),
                new Rect(7 * sizes[0].width() / 5, 0, sizes[0].width() + sizes[1].width(), sizes[0].height() / 2)
            };
    }

    /**
     * Returns the sizes of the bounding of the children.
     * At index 0 the exponent's size is stored, at index 1 the base's size is stored
     * 
     * @param maxWidth
     *        The maximum width the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link MathObject} can have (can be {@link MathObject#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    public Rect[] getChildrenSize(int maxWidth, int maxHeight)
    {
        // Get the sizes both operands want to take
         Rect leftSize = getChild(0).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
         Rect rightSize = getChild(1).getBoundingBox(NO_MAXIMUM, NO_MAXIMUM);
        
        // Make sure the exponent is smaller than the base
        leftSize.bottom = 2 * leftSize.bottom / 3;
        leftSize.right = 2 * leftSize.right / 3;
        
        //Since the exponent's size has changed, 
        leftSize = getChild(0).getBoundingBox(leftSize.bottom, leftSize.right);
        
        // If the boxes fit within the specified maximum, we're done
        if((maxWidth == NO_MAXIMUM || 7 * leftSize.width() / 5 + rightSize.width() < maxWidth) && (maxHeight == NO_MAXIMUM || leftSize.height() / 2 + rightSize.height() < maxHeight))
            return new Rect[] {leftSize, rightSize};
        
        // Calculate the shrinking factor
        final float widthFactor = maxWidth == NO_MAXIMUM ? 1.0f : ((float) maxWidth) / (7 * leftSize.width() / 5 + rightSize.width());
        final float heightFactor = maxHeight == NO_MAXIMUM ? 1.0f : ((float) maxHeight) / (leftSize.height() / 2 + rightSize.height());
        final float factor = Math.min(widthFactor, heightFactor);
        
        // Calculate the sizes
        leftSize.set(0, 0, (int) (leftSize.width() * factor), (int) (leftSize.height() * factor));
        rightSize.set(0, 0, (int) (rightSize.width() * factor), (int) (rightSize.height() * factor));
        
        // Return the Sizes
        return new Rect[] {leftSize, rightSize};
    }

    @Override
    public Rect getBoundingBox(int maxWidth, int maxHeight)
    {
        // Get the sizes
        Rect[] sizes = getChildrenSize(maxWidth, maxHeight);
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0, 7 * sizes[0].width() / 5 + sizes[1].width(), sizes[0].height() + sizes[1].height());
    }
    
    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException 
    {
        // Check if the child exists
        this.checkChildIndex(index);
        
        // Get the Size of the children
        Rect[] childrenSize = getChildrenSize(maxWidth, maxHeight);
        
        // Move the bounding boxes to the correct position
        childrenSize[1].offsetTo(7 * childrenSize[0].width() / 5, childrenSize[0].height() / 2);
        
        // Return the right bounding box
        return childrenSize[index];
    }

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight) 
    {
        // Get the bounding boxes
        final Rect[] childSize = getChildrenSize(maxWidth, maxHeight);

        /*Paint tmp = new Paint();
        tmp.setColor(Color.GREEN);
        canvas.drawRect(getBoundingBox(maxWidth, maxHeight), tmp);
        tmp.setColor(Color.RED);
        for(int i = 0; i < getChildCount(); ++i)
            canvas.drawRect(getChildBoundingBox(i, maxWidth, maxHeight), tmp);*/
        
        // Draw the operator
        canvas.save();
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(childSize[0].width() / 10);
        Path path = new Path();
        path.moveTo(0, (childSize[0].height() + childSize[1].height()) / 2);
        path.lineTo(6 * childSize[0].width() / 5, childSize[0].height() / 2 + childSize[1].height());
        path.lineTo(6 * childSize[0].width() / 5, childSize[0].height() / 4);
        path.lineTo(7 * childSize[0].width() / 5 + childSize[1].width(), childSize[0].height() / 4);
        canvas.drawPath(path, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas, maxWidth, maxHeight);
    }
}
