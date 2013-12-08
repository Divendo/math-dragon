package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class MathConstant extends MathObject
{
	public long ePow = 0;
	public long piPow = 0;
	public long iPow = 0;
	public char variable = 0;
	public char variablePow = 0;
	protected boolean constantOccurred = false;
    
    // TODO Support real values and constants like pi, e, etc
    /** The value this constant currently holds */
    protected long factor = 0;
    
    /** The paint that is used to draw the constant */
    protected Paint paint = new Paint();

    /** Constructor
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathConstant(int defWidth, int defHeight)
    { super(defWidth, defHeight); }

    /** Constructor, constructs with the given value
     * @param v The value that this constant should be initialized with
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathConstant(String value, int defWidth, int defHeight) throws MathException
    {
        super(defWidth, defHeight);
        this.readString(value);
    }
    
    //Reads the string and converts it to the right constant
    public void readString(String value) throws MathException
    {
    	factor = 0;
    	for(int i = 0; i<value.length(); i++)
    	{
    		//if it is a number, add it to the factor
    		if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    			if(!constantOccurred)
    				factor = factor * 10 + (int)(value.charAt(i));
    		
    		//if it is the letter p, check if they mean 'pi'
    		if(value.charAt(i) == 'p')
    		{
    			i++;
    			//if it is pi,add one to the power, in that way pi
    			//a constant has occurred in this string, so set that to true
    			if(value.charAt(i) == 'i')
    			{
    			piPow++;
    			constantOccurred = true;
    			i++;
    			//Check to see if that pi has a power, if it has, go and add that power
    			if(value.charAt(i) == '^')
    			{
    				piPow--;
    				int local_piPow = 0;
    				i++;
    				while(i<value.length())
    				{
    					if(value.charAt(i)== '(')
    						i++;
    					else if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    					{
    						local_piPow = local_piPow*10 + (int)(value.charAt(i));
    						i++;
    					}
    					else
    						break;
    				}
    				piPow+= local_piPow;
    			}
    			else
    				i--;
    			}
    			
    			// if it's not pi, it's a variable!
    			else
    			{
    				if(variable != 0)
    				{
    					variable = 'p';
    					variablePow++;
    				}
    			}
    		}
  
    		//If it is the number e, add one to the e powers
    		if(value.charAt(i) == 'e')
    		{
    			ePow++;
       			i++;
       			//Check and see if the e has a power
    			if(value.charAt(i) == '^')
    			{
    				ePow--;
    				int local_ePow = 0;
    				i++;
    				while(i<value.length())
    				{
    					if(value.charAt(i)== '(')
    						i++;
    					else if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    					{
    						local_ePow = local_ePow*10 + (int)(value.charAt(i));
    						i++;
    					}
    					else
    						break;
    				}
    				ePow+= local_ePow;
    			}
    			else
    				i--;
    		}
    		
    		//if the value is the number i, add the complex constant!
    		if (value.charAt(i) == 'i')
    		{
    			iPow++;
       			i++;
       			//Check and see if the e has a power
    			if(value.charAt(i) == '^')
    			{
    				iPow--;
    				int local_iPow = 0;
    				i++;
    				while(i<value.length())
    				{
    					if(value.charAt(i)== '(')
    						i++;
    					else if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    					{
    						local_iPow = local_iPow*10 + (int)(value.charAt(i));
    						i++;
    					}
    					else
    						break;
    				}
    				iPow+= local_iPow;
    			}
    			else
    				i--;
    		}
    		
    		//if the value is a variable, do what you do with variables!
    		if(value.charAt(i) >= 'a' && value.charAt(i) <= 'z' 
    				&& value.charAt(i) != 'i' && value.charAt(i) != 'e' && value.charAt(i) != 'p')
    		{
    			if(value.charAt(i) == variable || value.charAt(i) == 0)
    			{ 
    				variable = value.charAt(i);
    				
    				variablePow++;
           			i++;
           			//Check and see if the e has a power
        			if(value.charAt(i) == '^')
        			{
        				variablePow--;
        				int local_variablePow = 0;
        				i++;
        				while(i<value.length())
        				{
        					if(value.charAt(i)== '(')
        						i++;
        					else if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
        					{
        						local_variablePow = local_variablePow*10 + (int)(value.charAt(i));
        						i++;
        					}
        					else
        						break;
        				}
        				variablePow+= local_variablePow;
        			}
        			else
        				i--;
        		}
    			//else
    				//throw MathException;
    				
			}
		}
	}
    
    
    /** Uses binary search to find the right text size so that the text fits the given bounding box
     * @return The right text size so that the text fits the given bounding box */
    protected float findTextSize(int maxWidth, int maxHeight)
    {
        // If both the width and height are unrestricted, restrict the height
        if(maxWidth == NO_MAXIMUM && maxHeight == NO_MAXIMUM)
            return findTextSize(NO_MAXIMUM, defaultMaxHeight);
        
        // We don't want a text size bigger than 128 or smaller than 8
        final float maxTextSize = 128.0f;
        final float minTextSize = 8.0f;
        
        // Our initial text size and delta
        float textSize = (maxTextSize - minTextSize) / 2;
        float delta = (maxTextSize - textSize) / 2;
        
        // Keep searching until the text fits or until delta becomes too small
        // Note that we will never reach the maximum or minimum text size this way
        final String str = Long.toString(factor);
        Rect bounds = new Rect();
        while(delta >= 0.1f)
        {
            // Set the text size and calculate the bounds
            paint.setTextSize(textSize);
            paint.getTextBounds(str, 0, str.length(), bounds);
            
            // Determine if the text size should become smaller or bigger
            if((maxWidth != NO_MAXIMUM && bounds.width() > maxWidth) || (maxHeight != NO_MAXIMUM && bounds.height() > maxHeight))
                textSize -= delta;
            else if((maxWidth == NO_MAXIMUM || bounds.width() == maxWidth) && (maxHeight == NO_MAXIMUM || bounds.height() == maxHeight))
                break;
            else
                textSize += delta;
            
            // Calculate the new delta
            delta /= 2;
        }
        
        // Return the text size
        return textSize;
    }

    @Override
    public Rect[] getOperatorBoundingBoxes(int maxWidth, int maxHeight)
    {
        // Find and set the text size
        paint.setTextSize(findTextSize(maxWidth, maxHeight));
        
        // Get the text bounds
        final String str = Long.toString(factor);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        bounds.offsetTo(0, 0);
        
        // Make sure that bounds is contained within the maximum bounds
        if(maxWidth != NO_MAXIMUM && bounds.right > maxWidth)
            bounds.right = maxWidth;
        if(maxHeight != NO_MAXIMUM && bounds.bottom > maxHeight)
            bounds.bottom = maxHeight;
        
        // Return the bounds
        return new Rect[]{ bounds };
    }

    @Override
    public Rect getChildBoundingBox(int index, int maxWidth, int maxHeight) throws IndexOutOfBoundsException
    {
        // Will always throw an error since constants do not have children
        checkChildIndex(index);
        return null;
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
        final String str = Long.toString(factor);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        
        // Draw the text
        canvas.drawText(str, (boundingBox.width() - bounds.width()) / 2 - bounds.left, (boundingBox.height() - bounds.height()) / 2 - bounds.top, paint);
    }

    @Override
    public IExpr eval()
    { 
    	if(piPow != 0)
    	{ 
    		if(ePow != 0)
    			return F.Times(F.ZZ(factor), F.Times(F.Power(F.Pi, piPow),F.Power(F.e, ePow)));
    		return F.Times(F.ZZ(factor), F.Power(F.Pi, piPow));
    	}
    	
    	else if(ePow!= 0)
    	{
    		return F.Times(F.ZZ(factor), F.Power(F.e, ePow));
    	}
    	return F.ZZ(factor); 
    }

    @Override
    public double approximate() throws NotConstantException
    { return factor; }

}
