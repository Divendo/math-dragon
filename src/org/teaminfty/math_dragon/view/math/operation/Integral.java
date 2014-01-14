package org.teaminfty.math_dragon.view.math.operation;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Precedence;
import org.teaminfty.math_dragon.view.math.Operation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class Integral extends Operation
{

	protected Paint operatorPaint = new Paint();
	public static final String TYPE = "integral";
	final float signHeightAdd = 1.5f;
	final float maxSignWidth = 100;
	final float RATIO = 0.5f / 1.61803398874989f;
	final int maxFontSize = 500;
	final String integralSign = "\u222B"; // Unicode for the integral sign
	
	public Integral()
	{
	    super(4);
		children.add(new Empty());
		children.add(new Empty());
		children.add(new Empty());
		children.add(new Empty());
		
        initPaint();
	}
	
	public Integral( Expression integrate, Expression over)
	{
	    super(4);
		children.add(new Empty());
        children.add(new Empty());
        children.add(new Empty());
		children.add(new Empty());
		
		set(integrate, over);
        initPaint();
	}
	
	public Integral( Expression integrate, Expression over, Expression from, Expression to)
	{
	    super(4);
		children.add(new Empty());
        children.add(new Empty());
        children.add(new Empty());
        children.add(new Empty());
        
		set(integrate, over, from, to);
		initPaint();
	}
	
	private void initPaint()
	{
	    operatorPaint.setTypeface(TypefaceHolder.dejavuSans);
	    operatorPaint.setAntiAlias(true);
	}
	
	public String toString()
	{
		if(getIntegrateFrom() instanceof Empty && getIntegrateTo() instanceof Empty)
			return "Integrate(" + getIntegratePart().toString() + "," + getIntegrateOver().toString() + ")";
		else
			return "Integrate(" + getIntegratePart().toString() + ",{" + getIntegrateOver().toString() + "," + getIntegrateFrom().toString() + "," + getIntegrateTo().toString() + "})";
	}

    public int getPrecedence()
    { return Precedence.INTEGRAL; }
	
	public Rect[] getSizes()
	{
		/* Index of the rectangles
		 * 0: bb of the integral sign
		 * 1: bb of the thing to integrate
		 * 2: bb of the thing to integrate over
		 * 3: bb of the left bracket
		 * 4: bb of the right bracket
		 * 5: bb of the "d"
		 * 6: bb of the to integral
		 * 7: bb of the from integral
		 */
		
		// Get the bounding boxes of the children
		Rect main = getChild(0).getBoundingBox();
		Rect over = getChild(1).getBoundingBox();
		
		Rect from = getChild(2).getBoundingBox();
		Rect to = getChild(3).getBoundingBox();
		
		// Calculate the height and width of the integral sign
		int signHeight = (int) (Math.max( main.height(), over.height()) * signHeightAdd);
		Rect sign = new Rect( );
		
		operatorPaint.setTextSize( Math.min( maxFontSize, signHeight));
		operatorPaint.getTextBounds( integralSign, 0, integralSign.length(), sign);
		
		// Add some padding
		sign.bottom += sign.height() * 0.2;
		sign.right += sign.width() * 0.2;
		
		// Calculate the sizes of the brackets
		int bracketHeight = main.height();
		int bracketWidth = (int) (bracketHeight * RATIO);
		Rect leftBracket = new Rect( 0, 0, bracketWidth, bracketHeight);
		Rect rightBracket = new Rect( leftBracket );
		
		// Get the bounding box of the d
		operatorPaint.setTextSize( Math.min( maxFontSize, over.height()));
		Rect bounds = new Rect();
		operatorPaint.getTextBounds( "d", 0, "d".length(), bounds);
		
		// add a nice padding between the d and the last child
		bounds.right += bounds.width() * 0.2;
		
		// Return all the bounding boxes
		return new Rect[]	{
								sign,
								main,
								over,
								leftBracket,
								rightBracket,
								bounds,
								to,
								from
							};
	}
	
	
	@Override
	public Rect[] getOperatorBoundingBoxes() {
		
		// Get all the sizes of the bounding boxes
		Rect sizes[] = getSizes();
		int horizontalOffset = getHorizontalOffset( sizes );
		int height = sizes[0].height();
		
		// Offset all the bounding boxes
		sizes[0].offsetTo( horizontalOffset, sizes[7].height());
		sizes[3].offsetTo( horizontalOffset + sizes[0].width(), sizes[6].height() + (height - sizes[3].height()) / 2);
		sizes[4].offsetTo( horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width(), sizes[6].height() + (height - sizes[4].height()) / 2);
		sizes[5].offsetTo( horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width() + sizes[4].width(), sizes[6].height() + (height - sizes[5].height()) / 2);		
		
		// Return them
		return new Rect[]	{	
								sizes[0],
								sizes[3],
								sizes[4],
								sizes[5]
							};
	}

	@Override
	public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException {
		// Get the sizes of the bounding boxes
		Rect[] sizes = getSizes();
		
		int horizontalOffset = getHorizontalOffset( sizes );
		int signWidth = Math.max( Math.max( sizes[0].width(), sizes[6].width()), sizes[7].width());
		int height = sizes[0].height();
		
		// Offset all the bounding boxes
		sizes[1].offsetTo( horizontalOffset + sizes[0].width() + sizes[3].width(), sizes[6].height() + (height - sizes[1].height()) / 2);
		sizes[2].offsetTo( horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width() + sizes[4].width() + sizes[5].width(), sizes[6].height() + (height - sizes[2].height()) / 2);
		sizes[6].offsetTo( Math.max( 0, signWidth - sizes[6].width()) / 2, 0);
		sizes[7].offsetTo( Math.max( 0, signWidth - sizes[7].width()) / 2, sizes[0].height() + sizes[6].height());
		
		// Switch to return the correct bounding box
		switch( index) 
		{
		case 0:
			return sizes[1];
			
		case 1:
			return sizes[2];

		case 2:
			return sizes[7];
			
		case 3:
			return sizes[6];
		}
		
		return null;
	}
	
	
	@Override
    public Rect getBoundingBox()
    {
        // Get the sizes
        Rect[] sizes = getSizes();
        int horizontalOffset = getHorizontalOffset( sizes );
        
        // Return a bounding box, containing the bounding boxes of the children
        int width = horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width() + sizes[4].width() + sizes[5].width() + sizes[2].width();
        width = Math.max( Math.max( width,  sizes[6].width()), sizes[7].width());
        int height = sizes[0].height() + sizes[6].height() + sizes[7].height();

        return new Rect(0, 0, width, height);
    }

	@Override
	public void draw(Canvas canvas) {
		// Draw the bounding boxes
		drawBoundingBoxes( canvas);
		
		operatorPaint.setColor(getColor());
		operatorPaint.setStrokeWidth(lineWidth);
		
		// Get the sizes
		Rect[] sizes = getSizes();
		int horizontalOffset = getHorizontalOffset( sizes );
		
		int height = sizes[0].height();
		
		// Draw the brackets
        operatorPaint.setStyle(Paint.Style.STROKE);
		
		// Draw the left bracket
        canvas.save();
        sizes[3].offset( horizontalOffset + sizes[0].width(), sizes[6].height() + (height - sizes[3].height()) / 2);
        canvas.clipRect(sizes[3], Region.Op.INTERSECT);
        RectF bracket = new RectF(sizes[3]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(bracket.width() / 4, 0);
        canvas.drawArc(bracket, 100.0f, 160.0f, false, operatorPaint);
        canvas.restore();
		
        // Draw the right bracket
        canvas.save();
        sizes[4].offset( horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width(), sizes[6].height() + (height - sizes[4].height()) / 2);
        canvas.clipRect(sizes[4], Region.Op.INTERSECT);
        bracket = new RectF(sizes[4]);
        bracket.inset(0, -operatorPaint.getStrokeWidth());
        bracket.offset(-bracket.width() / 4, 0);
        canvas.drawArc(bracket, -80.0f, 160.0f, false, operatorPaint);
        canvas.restore();     
        
        
        // Draw the D
        operatorPaint.setStyle(Paint.Style.FILL);
        operatorPaint.setTextSize( Math.min( maxFontSize, sizes[2].height()) );
        canvas.drawText( "d", horizontalOffset + sizes[0].width() + sizes[3].width() + sizes[1].width() + sizes[4].width(), sizes[6].height() + (height - sizes[5].height()) / 2 + sizes[5].height(), operatorPaint);
        
        // Draw the integral sign
        operatorPaint.setTextSize( (int) (Math.min( maxFontSize, Math.max( sizes[1].height(), sizes[2].height()) * signHeightAdd)));
        sizes[0].offsetTo( (int) ((sizes[0].width() / 1.2) * 0.1 + horizontalOffset), (int) (sizes[6].height() + sizes[0].height() * 0.75)); // We need to decrease the height by a little bit, because the integral sign isn't draw with the origin at the bottom.
        canvas.drawText( integralSign, sizes[0].left, sizes[0].top, operatorPaint);
        
        // Draw the children
		drawChildren( canvas);
	}

	int getHorizontalOffset(Rect[] sizes)
	{
		// Get the offset between the integral sign and the left side of the bounding box
		return Math.max( 0, Math.max( sizes[6].width(), sizes[7].width()) - sizes[0].width()) / 2;
	}
	
	public void set(Expression integrate, Expression over)
    {
		// Only set the integrate child and the child to integrate over
        setChild(0, integrate);
        setChild(1, over);
    }
	
	public void set(Expression integrate, Expression over, Expression from, Expression to)
    {
		// Set all the children
        set(integrate, over);
        setChild(2, from);
        setChild(3, to);
    }
	
	/** Returns the child that should be integrated */
	public Expression getIntegratePart()
	{ return getChild(0); }

    /** Returns the child over which should be integrated */
    public Expression getIntegrateOver()
    { return getChild(1); }
    
    /** Returns the child from whose value should be integrated */
    public Expression getIntegrateFrom()
    { return getChild(2); }

    /** Returns the child to whose value should be integrated */
    public Expression getIntegrateTo()
    { return getChild(3); }
	
	@Override
  	public void setLevel(int l)
  	{
		// Set the level of the children
  		level = l;
  		getChild(0).setLevel(level+1);
  		getChild(1).setLevel(level+1);
  		
  		getChild(2).setLevel(level+1);
  		getChild(3).setLevel(level+1);
  	}
	
	@Override
	protected String getType()
	{
		return TYPE;
	}

	@Override
	protected void writeChildrenToXML(Document doc, Element el)
	{
	    for(Expression child : children)
	        child.writeToXML(doc, el);
	}

	@Override
    public boolean isCompleted()
    {
	    if(!getIntegratePart().isCompleted() || !getIntegrateOver().isCompleted())
	        return false;
        if(getIntegrateFrom() instanceof Empty && getIntegrateTo() instanceof Empty)
            return true;
        if(getIntegrateFrom().isCompleted() && getIntegrateTo().isCompleted())
            return true;
        return false;
    }
}
