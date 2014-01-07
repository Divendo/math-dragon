package org.teaminfty.math_dragon.view.math;


import org.teaminfty.math_dragon.view.MathSourceOperationSinoid.OperatorType;
import org.teaminfty.math_dragon.view.TypefaceHolder;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class MathOperationFunction extends MathObject 
{
    /** The paint that is used for drawing the operator */
    protected Paint operatorPaint = new Paint();
    /** The paint that is used for drawing the exponent */
    protected Paint exponentPaint = new Paint();
    /** The ratio (width : height) of a bracket (i.e. half the golden ratio) */
    final float RATIO = 0.5f / 1.61803398874989f;
    
    protected String operatorName = "";
    protected Rect bounds = new Rect();
    public OperatorType type = null;
    protected Boolean arc = false;
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;
    
    /** The ratio (width : height) of a bracket (i.e. the golden ratio) */
    final float HALF_RATIO = 0.5f / 1.61803398874989f;
    public final float FULL_RATIO = 1 / 1.61803398874989f;
    
    public MathOperationFunction(OperatorType t)
    {
    	operatorName = getName(t);
    	type = t;
        children.add(new MathObjectEmpty());
        operatorPaint.setAntiAlias(true);
        operatorPaint.setStrokeWidth(MathObject.lineWidth);
    }
    
    public String getName(OperatorType t)
    {
    	if(t == OperatorType.ARCCOS)
    	{	arc = true;
    	  	return "cos\u207B\u00B9";}
    	if(t == OperatorType.ARCSIN)
    	{	arc = true;
    	  	return "sin\u207B\u00B9";}
    	if(t == OperatorType.ARCTAN)
    	{	arc = true;
    	  	return "tan\u207B\u00B9";}
    	if(t == OperatorType.COS)
    		return "cos";
    	if(t == OperatorType.SIN)
    		return "sin";
    	if(t == OperatorType.TAN)
    		return "tan";
    	if(t == OperatorType.COSH)
    		return "cosh";
    	if(t == OperatorType.SINH)
    		return "sinh";
    	if(t == OperatorType.LN)
    		return "ln";
    	return "error";
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
        
        if(!arc)
        {
        // Add the padding
        out.inset(-out.width() / 10, -out.height() / 10);
        out.offsetTo(0, 0);
        }
        
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
    
        //Draws the operator
        operatorPaint.getTextBounds(operatorName, 0, operatorName.length(), bounds);
        out.right += bounds.width();
        out.bottom =  bounds.height();
    	
        return out;
    }
    
	//Returns the bounding boxes of the Operator
	@Override
	public Rect[] getOperatorBoundingBoxes() 
	{
		final Rect childRect = getChild(0).getBoundingBox();
    	final int width = (int)(childRect.height() * RATIO);
    	Rect vierkant = sizeAddPadding(getSize(findTextSize(level)));
        
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
		
		//offsets the child.
		childSize.offsetTo(operatorSizes[0].width() + operatorSizes[1].width(), 0 );
		return childSize;
	}
	
	//Complete bounding box
	@Override
	public Rect getBoundingBox()
	{
		Rect[] operatorSizes = getOperatorBoundingBoxes();
		Rect child = getChild(0).getBoundingBox();
		
		return new Rect(0,Math.min(operatorSizes[0].top, child.top), operatorSizes[0].width() + operatorSizes[1].width() + operatorSizes[2].width() + child.width(), Math.max(getChild(0).getBoundingBox().height(), operatorSizes[0].height()));
	}

	
	@Override
	public Point getCenter()
	{		
		return new Point(getBoundingBox().centerX(), getBoundingBox().centerY());
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		this.drawBoundingBoxes(canvas);
		Rect[] operatorBounding = this.getOperatorBoundingBoxes();
		
		operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
        operatorPaint.setAntiAlias(true);
        operatorPaint.setColor(this.getColor());
		
        //Draws the operator
        canvas.save();
        canvas.drawText(operatorName, 0, getChild(0).getCenter().y + operatorBounding[0].height()/2, operatorPaint);
        canvas.restore();
        

		// Use stroke style for the parentheses
        operatorPaint.setStyle(Paint.Style.STROKE);
        
		
		// Draw the left bracket
        canvas.save();
        canvas.clipRect(operatorBounding[1], Region.Op.INTERSECT);
        RectF bracket = new RectF(operatorBounding[1]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
        
        // Draw the right bracket
        canvas.save();
        canvas.clipRect(operatorBounding[2], Region.Op.INTERSECT);
        bracket = new RectF(operatorBounding[2]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, operatorPaint);
        canvas.restore();

        // Set the paint back to fill style
        operatorPaint.setStyle(Paint.Style.FILL);
        this.drawChildren(canvas);
	}
}
