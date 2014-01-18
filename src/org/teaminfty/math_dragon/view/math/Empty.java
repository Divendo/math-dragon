package org.teaminfty.math_dragon.view.math;

import org.teaminfty.math_dragon.view.HoverState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;

/** Represents an empty child */
public class Empty extends Expression
{
	public static final String NAME = "empty";
	
    /** The ratio (width : height) of the empty child box (i.e. the golden ratio) */
    public final static float RATIO = 1 / 1.61803398874989f;
    
    /** The paint that's used to draw the child */
    private Paint paint = new Paint();
    
    /** Constructor */
    public Empty()
    {
        // Initialise the paint
        paint.setColor(Color.rgb(0x88, 0x88, 0x88));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {16.0f, 16.0f}, 0));
    }
    
    @Override
    public String toString()
    { return " "; }
    
    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Will always throw an error since empty boxes do not have children
        checkChildIndex(index);
        return null;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the bounding box
        Rect rect = this.getOperatorBoundingBoxes()[0];
        
        // Set the right color of the paint
        paint.setColor(getColor());
        
        // Draw the dashed rectangle
        paint.setStrokeWidth(lineWidth);
        rect.inset((int) Math.ceil(paint.getStrokeWidth() / 2), (int) Math.ceil(paint.getStrokeWidth() / 2));
        canvas.drawRect(rect, paint);
        
        //If you're trying to drag the item, make an aiming cross
        if(this.state == HoverState.DRAG)
        {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, Math.min(rect.height(), rect.width()) / 10, paint);
            paint.setStyle(Paint.Style.STROKE);
        }
    }
    
    @Override
    public Rect getBoundingBox()
    {
    	int width = (int)(defaultHeight*RATIO);
    	int height = defaultHeight;
    	
    	for(int t = 0; t < this.level && t < MAX_LEVEL; t++)
    	{
    		width = 2*width/3;
    		height = 2*height/3;
    	}
    	
    	return new Rect(0, 0, width, height);
    }
    
    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
    	//emptyObjects don't have an operator, but they do need one for the drag and drop
    	
    	return new Rect[] { this.getBoundingBox()};
    }

    @Override
    public void writeToXML(Document doc, Element el)
    {
        el.appendChild(doc.createElement(NAME));
    }

    @Override
    public boolean isCompleted()
    { return false; }
}
