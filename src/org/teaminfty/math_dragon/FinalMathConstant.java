package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Rect;

public class FinalMathConstant extends MathConstant{

	public FinalMathConstant(String value, int defWidth, int defHeight) {
		super(value, defWidth, defHeight);
	}

    @Override
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
        // Get the bounding box
        Rect boundingBox = getOperatorBoundingBoxes(maxWidth, maxHeight)[0];
        
        // Find and set the text size
        // Deliberately take 80% of the text size to create a small padding
        paint.setTextSize(0.8f * findTextSize(maxWidth, maxHeight));
        
        // Get the text and the text bounds
        String str = "0";
        if(factor != 0)
        {
        	if(negative)
        		str = Long.toString(factor*-1);
        	else
        		str = Long.toString(factor);
        }
        else if(iPow != 0)
        	{ str = "i";}
        else if(piPow != 0)
        	{str = "pi";}
        else if(ePow != 0)
        	{str = "e";}
      
        
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        
        // Draw the text
        canvas.drawText(str, (boundingBox.width() - bounds.width()) / 2 - bounds.left, (boundingBox.height() - bounds.height()) / 2 - bounds.top, paint);
    }

    @Override
    public IExpr eval()
    { 
    	if (factor != 0)
    		return F.ZZ(factor);
    	else if(iPow != 0)
    	{
    		if(iPow %2 == 0)
    			return F.ZZ(1);
    		else return F.i;
    	}
    	else if(piPow != 0)
    		return F.Pi;
    	else if(ePow != 0)
    		return F.e;
    	else
    		return F.ZZ(0);
    	
    }

    @Override
    public double approximate() throws NotConstantException
    { 
    	if (factor != 0)
    		return factor;
    	else if(iPow != 0)
    	{
    		if(iPow %2 == 0)
    			return -1;
    		else return 0;
    	}
    	else if(piPow != 0)
    		return Math.PI;
    	else if(ePow != 0)
    		return Math.E;
    	else
    		return 0;
    }

}
