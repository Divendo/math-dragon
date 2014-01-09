package org.teaminfty.math_dragon.view.math;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public abstract class MathObjectLog extends MathBinaryOperationLinear
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
    
    public MathObjectLog()
    {
        operatorPaint.setStyle(Paint.Style.STROKE);
        operatorPaint.setAntiAlias(true);
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
		Rect[]	childrenSize = getChildrenSize();
    	final int width = (int)(childrenSize[1].height() * RATIO);
    	final int height = childrenSize[1].height();
    	
    	Rect vierkant = sizeAddPadding(getSize(findTextSize(level)));
    	vierkant.offsetTo(childrenSize[0].width(), 0);
    	
        
		 return new Rect[]{ vierkant,
				 new Rect(vierkant.width() + childrenSize[0].width(), 0, vierkant.width() + width + childrenSize[0].width(), height), 
				 new Rect(vierkant.width() + width + childrenSize[0].width() + childrenSize[1].width(), 0,vierkant.width() + childrenSize[0].width() + childrenSize[1].width() + 2* width, height)};
	}
	
	@Override
	public void setLevel(int l)
	{
		level = l;
		getChild(0).setLevel(level + 2);
		getChild(1).setLevel(level);
	}
	
	public Rect[] getChildrenSize()
	{
		// Get the sizes both operands want to take
        Rect leftSize = getChild(0).getBoundingBox();
        Rect rightSize = getChild(1).getBoundingBox();
        
        // Return the Sizes
		return new Rect[] {leftSize, rightSize};
	}
	
	@Override
	public Rect getChildBoundingBox( int index) throws IndexOutOfBoundsException 
	{
		
		// Make sure the child index is valid
        checkChildIndex(index);
        
		//Gets the needed sizes and centers
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect[] childSize = getChildrenSize();
		int centerY = this.getCenter().y;
		int centerY_child = this.getChild(1).getCenter().y;
		
		//offsets the child.
		childSize[0].offsetTo(0, centerY - (operatorSizes[0].height() + childSize[0].height())/2);
		childSize[1].offsetTo(childSize[0]. width() + operatorSizes[0].width() + operatorSizes[1].width(), centerY - centerY_child );
		
		return childSize[index];
	}
	
	//Complete bounding box
	@Override
	public Rect getBoundingBox()
	{
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect[] childrenSize = getChildrenSize();
		
		return new Rect(0,0, operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + childrenSize[0].width() + childrenSize[1].width(), childrenSize[0].height() + childrenSize[1].height());
	}

	
	@Override
	public Point getCenter()
	{		
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect[] childrenSize = getChildrenSize();
		
		Rect betaBounding = new Rect(0,0, operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + childrenSize[0].width() + childrenSize[1].width(), childrenSize[0].height() + childrenSize[1].height());
		return new Point(betaBounding.centerX(), betaBounding.centerY());
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		Rect[] boxes = getOperatorBoundingBoxes();
		
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
	}
}
