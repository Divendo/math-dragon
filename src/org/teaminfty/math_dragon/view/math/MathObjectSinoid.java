package org.teaminfty.math_dragon.view.math;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public abstract class MathObjectSinoid extends MathObject 
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    /** The paint that is used for drawing the exponent */
    protected Paint exponentPaint = new Paint();
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    final float RATIO = 0.5f / 1.61803398874989f;
    
    protected String tmpStr = "";
    protected String tmpStr2 = "-1";
    protected Rect bounds = new Rect();
    protected Rect bounds2 = new Rect();
    protected int arc = 0;
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;
    
    /** The ratio (width : height) of a bracket (i.e. the golden ratio) */
    final float HALF_RATIO = 0.5f / 1.61803398874989f;
    public final float FULL_RATIO = 1 / 1.61803398874989f;
    
    public MathObjectSinoid()
    {
        children.add(new MathObjectEmpty());
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStrokeWidth(MathObject.lineWidth);
    }
    
    public int getPrecedence()
    { return MathObjectPrecedence.FUNCTION; }

    /** Calculates the right text size for the given level
     * @param lvl The level
     * @return The right text size for the given level */
    protected float findTextSize(int lvl)
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, lvl);
    }   
    
    /** Adds padding to the given size rectangle
     * @param size The size where the padding should be added to
     * @return The size with the padding
     */
    protected Rect sizeAddPadding(Rect size)
    {
        // Copy the rectangle
        Rect out = new Rect(size);
        
        // Add the padding
        out.inset(-out.width() / 10, -out.height() / 10);
        out.offsetTo(0, 0);
        
        // Return the result
        return out;
    }
    
    /** Calculates the size of this Sinoid when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathConstant}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        operatorPaint.setTextSize(fontSize);
        exponentPaint.setTextSize(fontSize * EXPONENT_FACTOR);

        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        
        //if the operator is arcsin, arctan or arccos, get add the size of the -1
    	if(arc == 1)
    	{
    	operatorPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
    	exponentPaint.getTextBounds(tmpStr2, 0, tmpStr2.length(), bounds2);
    	out.right += bounds.width() + bounds2.width();
        out.bottom = Math.max(out.bottom, bounds.height());
    	}
    	
    	// If not, get the size without -1
    	else 
    	{
        operatorPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
        out.right += bounds.width();
        out.bottom =  bounds.height();
    	}
        return out;
    }
    
	//Returns the bounding boxes of the Operator
	@Override
	public Rect[] getOperatorBoundingBoxes() 
	{
		final Rect childRect = getChild(0).getBoundingBox();
    	final int width = (int)(childRect.height() * RATIO);
    	Rect vierkant = sizeAddPadding(getSize(findTextSize(level)));
    	System.out.println(width);
    	System.out.println("vierkant" + vierkant.width());
        
		 return new Rect[]{ vierkant,
				 new Rect(vierkant.width(), 0, vierkant.width() + width, childRect.height()), 
				 new Rect(vierkant.width() + width + childRect.width(), 0,vierkant.width() + childRect.width() + 2* width, childRect.height())};
	}
	
	@Override
	public Rect getChildBoundingBox( int index) throws IndexOutOfBoundsException 
	{
		
		// Make sure the child index is valid
        checkChildIndex(index);
        
		//Gets the needed sizes and centers
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect childSize = getChild(index).getBoundingBox();
		int centerY = this.getCenter().y;
		int centerY_child = this.getChild(0).getCenter().y;
		
		//offsets the child.
		childSize.offsetTo(operatorSizes[0].width() + operatorSizes[1].width(), centerY - centerY_child );
		return childSize;
	}
	
	//Complete bounding box
	@Override
	public Rect getBoundingBox()
	{
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		System.out.println("operator" + operatorSizes[1].width());
		
		return new Rect(0,0, operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + getChild(0).getBoundingBox().width(), Math.max(getChild(0).getBoundingBox().height(), operatorSizes[0].height()));
	}

	
	@Override
	public Point getCenter()
	{		
		return new Point(getBoundingBox().centerX(), getBoundingBox().centerY());
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		Rect[] boxes = getOperatorBoundingBoxes();
		
		// Use stroke style for the parentheses
        operatorPaint.setStyle(Paint.Style.STROKE);
		
		// Draw the left bracket
        canvas.save();
        canvas.clipRect(boxes[1], Region.Op.INTERSECT);
        RectF bracket = new RectF(boxes[1]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        canvas.clipRect(boxes[2], Region.Op.INTERSECT);
        bracket = new RectF(boxes[2]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, operatorPaint);
        canvas.restore();

        // Set the paint back to fill style
        operatorPaint.setStyle(Paint.Style.FILL);
	}
}
