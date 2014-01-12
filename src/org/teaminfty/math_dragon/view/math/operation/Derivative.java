package org.teaminfty.math_dragon.view.math.operation;

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
    final int maxFontSize = 500;
    final float RATIO = 0.5f / 1.61803398874989f;
    public static final String TYPE = "derivative";
    
    public Derivative()
    { }
    
    public Derivative(Expression left, Expression right)
    {
    	super(left, right);
    }
    
    public String toString()
    {
        return "d(" + getLeft().toString() + "," + getRight().toString() + ")";
    }

    @Override
    public int getPrecedence()
    { return Precedence.ADD; }
    
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
        
        // Calculate the bounding box of the "D" letters
        Rect boundstop = new Rect();
        operatorPaint.setTextSize( Math.min( maxFontSize, topSize.height()) );
        operatorPaint.getTextBounds("d", 0, "d".length(), boundstop);
        
        Rect boundsbottom = new Rect();
        operatorPaint.setTextSize( Math.min( maxFontSize, bottomSize.height()) );
        operatorPaint.getTextBounds("d", 0, "d".length(), boundsbottom);
    	// Add a small amount to get a gap between "d" and the variable
        boundstop.right += boundstop.width() * 0.2;
        boundsbottom.right += boundsbottom.width() * 0.2;
        // Calculate the height the operator wants to take
        int operatorHeight = Math.max((topSize.height() + bottomSize.height()) / 15 , 5);

        
        // Calculate the bounding boxes of the brackets
    	final int width = (int)(topSize.height() * RATIO);
        
        // Return the bounding boxes
        Rect leftBracket = new Rect(0, 0, width, topSize.height());
        Rect rightBracket = leftBracket;//new Rect(width + topSize.width(), 0, width * 2 + topSize.width(), topSize.height());
         
        
        // If we have no maximum height or it isn't breached, we're done
       // if(maxHeight == NO_MAXIMUM )//topSize.height()+ operatorHeight + bottomSize.height() < maxHeight )
            return new Rect[] 
            		{
        			new Rect(0, 0, Math.max(boundsbottom.width() + bottomSize.width(), topSize.width() + boundstop.width() + leftBracket.width() + rightBracket.width()), operatorHeight),
        			topSize, 
        			bottomSize,
        			boundstop,
        			boundsbottom,
        			leftBracket,
        			rightBracket
        			};
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        // Get the sizes
        Rect[] sizes = getSizes();

        // Position the bounding box and return it
        sizes[0].offsetTo( 0, sizes[1].height());
        sizes[3].offsetTo( Math.max(0, sizes[0].width() - sizes[1].width() - sizes[3].width() - sizes[5].width() - sizes[6].width()) / 2, sizes[1].height() - sizes[3].height());
        sizes[4].offsetTo( Math.max(0, sizes[0].width() - sizes[2].width() - sizes[4].width()) / 2, sizes[1].height() + sizes[0].height() + (sizes[2].height() - sizes[4].height()));
        
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
        Point center_one = getChild(0).getCenter();
        Point center_two = getChild(1).getCenter();
        Point center_this = this.getCenter();
        
        // Translate the operand's bounding box
        if(index == 0)
            sizes[1].offsetTo((int) (center_this.x - center_one.x + 0.5 * sizes[3].width()), 0);
        else
            sizes[2].offsetTo((int) (center_this.x - center_two.x + 0.5 * sizes[4].width()), sizes[0].height() + sizes[1].height());

        // Return the requested bounding box
        return sizes[index + 1];
    }
    
    @Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getSizes();
        
        // Return a bounding box, containing the bounding boxes of the children
       
        int width = Math.max(sizes[1].width() + sizes[3].width() + sizes[5].width() + sizes[6].width(), sizes[2].width() + sizes[4].width());
        int height = sizes[0].height() + sizes[1].height() + sizes[2].height();

        return new Rect(0, 0, width, height);
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
        
        // Get the bounding boxes
        final Rect operator = getOperatorBoundingBoxes()[0];
        
        // Draw the operator
        canvas.save();
        operatorPaint.setStyle(Paint.Style.FILL);
        operatorPaint.setColor(getColor());
        operatorPaint.setStrokeWidth(lineWidth);
        canvas.drawLine(operator.left, operator.centerY(), operator.right, operator.centerY(), operatorPaint);
        //canvas.drawRect(operator.left, operator.top + operator.height() / 6, operator.right, operator.bottom - operator.height() / 3, operatorPaint);
        
        // Get the sizes of the boundingboxes
        Rect[] sizes = getSizes();
        
        // Draw the text at the correct position
        operatorPaint.setTextSize( Math.min( maxFontSize, sizes[2].height()) );
        canvas.drawText( "d", Math.max(0, operator.width() - sizes[2].width() - sizes[4].width()) / 2, sizes[1].height() + sizes[2].height() + operator.height(), operatorPaint);
        operatorPaint.setTextSize( Math.min( maxFontSize, sizes[1].height()) );
        canvas.drawText( "d", Math.max(0, operator.width() - sizes[1].width() - sizes[3].width() - sizes[5].width() - sizes[6].width()) / 2, sizes[1].height(), operatorPaint);
        canvas.restore();

        
        // Draw the brackets
        operatorPaint.setStyle(Paint.Style.STROKE);
        operatorPaint.setAntiAlias(true);
        
        // Draw the left bracket
        canvas.save();
        sizes[5].offset( sizes[3].width() + Math.max( 0, operator.width() - sizes[1].width() - sizes[3].width() - sizes[5].width() - sizes[6].width()) / 2, 0);
        canvas.clipRect(sizes[5], Region.Op.INTERSECT);
        RectF bracket = new RectF(sizes[5]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        sizes[6].offset( sizes[5].width() + sizes[1].width(), 0);
        canvas.clipRect(sizes[6], Region.Op.INTERSECT);
        bracket = new RectF(sizes[6]);
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
