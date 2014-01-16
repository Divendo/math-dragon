package org.teaminfty.math_dragon.view.math.operation;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Precedence;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class Derivative extends Binary
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    final float RATIO = 0.5f / 1.61803398874989f;
    public static final String TYPE = "derivative";
    
    public Derivative()
    { initPaint(); }
    
    public Derivative(Expression left, Expression right)
    {
        super(left, right);
        initPaint();
    }
    
    private void initPaint()
    {
        operatorPaint.setAntiAlias(true);
        operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
    }
    
    @Override
    public String toString()
    {
        return "Derive(" + getLeft().toString() + "," + getRight().toString() + ")";
    }

    @Override
    public int getPrecedence()
    { return Precedence.MULTIPLY; }
    
    /**
     * Returns the sizes of the bounding boxes.
     * The first rectangle is the size of the operator, the second and third rectangle are the sizes of the children.
     * 
     * @param maxWidth
     *        The maximum width the {@link Expression} can have (can be {@link Expression#NO_MAXIMUM})
     * @param maxHeight
     *        The maximum height the {@link Expression} can have (can be {@link Expression#NO_MAXIMUM})
     * @return The size of the child bounding boxes
     */
    protected Rect[] getSizes()
    {    
        /* Index of what the sizes are:
         * 0: bounding box size of the breakline
         * 1: bounding box size of the top child
         * 2: bounding box size of the bottom child
         * 3: bounding box size of the top "d"
         * 4: bounding box size of the bottom "d"
         * 5: bounding box size of the top left bracket 
         * 6: bounding box size of the top right bracket
         */
        
        // Get the size both operands want to take
        Rect topSize = getChild(0).getBoundingBox();
        Rect bottomSize = getChild(1).getBoundingBox();
        
        // Calculate the bounding box of the "d" letters
        Rect boundsTop = new Rect();
        operatorPaint.setTextSize(defaultHeight * (float) Math.pow(2.0 / 3.0, level));
        operatorPaint.getTextBounds("d", 0, 1, boundsTop);
        Rect boundsBottom = new Rect(boundsTop);
        
        // Add a small amount of padding to the "d"s
        boundsTop.inset((int) (-3 * lineWidth), 0);
        boundsBottom.inset((int) (-3 * lineWidth), 0);
        boundsTop.offsetTo(0, 0);
        boundsBottom.offsetTo(0, 0);
        
        // Calculate the height the operator wants to take
        int operatorHeight = Math.max((topSize.height() + bottomSize.height()) / 15 , 5);
        
        // Return the bounding boxes
        final int bracketWidth = (int)(topSize.height() * RATIO);
        Rect leftBracket = new Rect(0, 0, bracketWidth, topSize.height());
        Rect rightBracket = new Rect(0, 0, bracketWidth, topSize.height());
        
        // If we have no maximum height or it isn't breached, we're done
            return new Rect[] 
                    {
                    new Rect(0, 0, Math.max(boundsBottom.width() + bottomSize.width(), topSize.width() + boundsTop.width() + leftBracket.width() + rightBracket.width()), operatorHeight),
                    topSize, 
                    bottomSize,
                    boundsTop,
                    boundsBottom,
                    leftBracket,
                    rightBracket
                    };
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        // Get the sizes
        Rect[] sizes = getSizes();

        // Position the bounding boxes and return them
        sizes[0].offsetTo(0, Math.max(sizes[1].height(), sizes[3].height()));
        sizes[3].offsetTo(Math.max(0, sizes[0].width() - sizes[1].width() - sizes[3].width() - sizes[5].width() - sizes[6].width()) / 2, (sizes[1].height() - sizes[3].height()) / 2);
        sizes[4].offsetTo(Math.max(0, sizes[0].width() - sizes[2].width() - sizes[4].width()) / 2, sizes[0].bottom + Math.max(0, (sizes[2].height() - sizes[4].height()) / 2));
        
        // Make a rectangle the size of the brackets
        Rect leftBracket = new Rect( 0, 0, sizes[5].width(), sizes[5].height());
        Rect rightBracket = new Rect( 0, 0, sizes[6].width(), sizes[6].height());
        
        // Move them to the correct position
        leftBracket.offsetTo( sizes[3].width() + Math.max( 0, sizes[0].width() - sizes[3].width() - sizes[1].width() - rightBracket.width() - leftBracket.width()) / 2, 0);
        rightBracket.offsetTo( sizes[3].width() + sizes[1].width() + leftBracket.width() + Math.max( 0, sizes[0].width() - sizes[3].width() - sizes[1].width() - rightBracket.width() - leftBracket.width()) / 2, 0);
        
        return new Rect[] {sizes[0], sizes[3], sizes[4], leftBracket, rightBracket};
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Make sure the child index is valid
        checkChildIndex(index);

        // Get the sizes and the total height
        Rect[] sizes = getSizes();
        final int ownCenterX = sizes[0].width() / 2;
        
        // Translate the operand's bounding box
        if(index == 0)
        {
            Point childCenter = getChild(0).getCenter();
            sizes[1].offsetTo(ownCenterX - childCenter.x + sizes[3].width() / 2, Math.max(0, (sizes[3].height() - sizes[1].height()) / 2));
        }
        else
        {
            Point childCenter = getChild(1).getCenter();
            sizes[2].offsetTo(ownCenterX - childCenter.x + sizes[4].width() / 2, Math.max(sizes[3].height(), sizes[1].height()) + sizes[0].height() + Math.max(0, (sizes[4].height() - sizes[2].height()) / 2));
        }

        // Return the requested bounding box
        return sizes[index + 1];
    }
    
    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getSizes();
        
        // Return a bounding box, containing the bounding boxes of the children
        return new Rect(0, 0,
                sizes[0].width(),
                sizes[0].height() + Math.max(sizes[1].height(), sizes[3].height()) + Math.max(sizes[2].height(), sizes[4].height()));
    }
    
    @Override
    public Point getCenter()
    {
        // Get the operator bounding box
        Rect operatorBounding = getOperatorBoundingBoxes()[0];
        
        // Return the centre, which is the centre of the operator
        return new Point(operatorBounding.centerX(), operatorBounding.centerY());
    }
    
    @Override
      public void setLevel(int l)
      {
          level = l;
          getChild(0).setLevel(level+1);
          getChild(1).setLevel(level+1);
      }

    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the operator bounding boxes
        final Rect[] operatorBoxes = getOperatorBoundingBoxes();
        
        // Draw the operator
        operatorPaint.setStyle(Paint.Style.FILL);
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(lineWidth);
        canvas.drawLine(operatorBoxes[0].left, operatorBoxes[0].centerY(), operatorBoxes[0].right, operatorBoxes[0].centerY(), operatorPaint);
        
        // Draw the text at the correct position
        Rect dBounds = new Rect();
        operatorPaint.setTextSize(defaultHeight * (float) Math.pow(2.0 / 3.0, level));
        operatorPaint.getTextBounds("d", 0, 1, dBounds);
        canvas.drawText("d", operatorBoxes[1].left - dBounds.left, operatorBoxes[1].top - dBounds.top, operatorPaint);
        canvas.drawText("d", operatorBoxes[2].left - dBounds.left, operatorBoxes[2].top - dBounds.top, operatorPaint);

        
        // Draw the brackets
        operatorPaint.setStyle(Paint.Style.STROKE);
        
        // Draw the left bracket
        canvas.save();
        canvas.clipRect(operatorBoxes[3], Region.Op.INTERSECT);
        RectF bracket = new RectF(operatorBoxes[3]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        canvas.clipRect(operatorBoxes[4], Region.Op.INTERSECT);
        bracket = new RectF(operatorBoxes[4]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the children
        drawChildren(canvas);
    }

    @Override
    protected String getType() 
    {
        return TYPE;
    }
}
