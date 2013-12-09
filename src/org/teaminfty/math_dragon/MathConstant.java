package org.teaminfty.math_dragon;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class MathConstant extends MathObject
{
	protected long factorPow = 0;
	protected long ePow = 0;
	protected long piPow = 0;
	protected long iPow = 0;
	protected char variable = 0;
	protected long variablePow = 0;
	protected boolean negative = false;
	private int i = 0;
	private PowerType type = PowerType.factor;
	
	
	private enum PowerType
	{
		factor,
		i,
		e,
		pi,
		variable,
	}
    
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
    { 
    	super(defWidth, defHeight);
    }

    /** Constructor, constructs with the given value
     * @param v The value that this constant should be initialized with
     * @param defWidth The default maximum width
     * @param defHeight The default maximum height
     */
    public MathConstant(String value, int defWidth, int defHeight)
    {
        super(defWidth, defHeight);
        this.readString(value);
    }
    
    private void reset()
    {
    	factorPow = 0;
    	iPow = 0;
    	ePow = 0;
    	piPow = 0;
    	variablePow = 0;
    	i = 0;
    	type = PowerType.factor;
    	factor = 0;
    	negative = false;
    }
    
    public void readString (String value)
    {
    //resets the current values, just in case.
    this.reset();   
    //a while loop gave more freedom that a for loop.
    	while (i < value.length() )
    	{
    		//if the value is a number, add that number
    		if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    				factor = factor * 10 + (value.charAt(i) - '0');
    		//if it is one of the mathconstants, add them and change the PowerType
    		else
    		{
    			if(value.charAt(i) == '-')
    				negative = !negative;

    			if(value.charAt(i) == 'i')
    			{
    				iPow += 1;
    				type = PowerType.i;
    			}
    			else if(value.charAt(i) == 'e')
    			{
    				ePow += 1;
    				type = PowerType.e;
    			}
    			//if you see a 'p', check if they mean 'pi'.
    			else if(value.charAt(i) == 'p')
    			{
    				i++;
    				if (value.charAt(i) == 'i')
    				{
    					piPow += 1;
    					type = PowerType.pi;
    				}
    				//if they don't, they named their variable 'p'
    				else
    				{
    					if (variable == 'p' || variable == '0')
    					{
    						variable = 'p';
    						variablePow += 1;
    						type = PowerType.variable;
    					}
    					i--;
    				}
    			}
    			//if you see any brackets, we might do something
    			else if(value.charAt(i) == '(' || value.charAt(i) == ')')
    				;
    			//If you see the sign for power, check which power you were handling and handle that
    			else if(value.charAt(i) == '^')
    			{
    				i++;
    				long tempPow = 0;
    				while(i<value.length())
    				{
    					if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
    					{
    					tempPow = 10*tempPow + value.charAt(i) - '0';
    					i++;
    					}
    					else if(value.charAt(i) == '(')
    						i++;
    					// i want an exception here
    					else
    						break;
    				}
    				//add the power to the right powerlong, and do it minus 1 because the power is automattically added 1 for constants
    				if (type == PowerType.factor)
						factorPow += tempPow;
					if (type == PowerType.i)
						iPow += tempPow - 1;
					if (type == PowerType.e)
						ePow += tempPow - 1;
					if (type == PowerType.pi)
						piPow +=tempPow - 1;
					if (type == PowerType.variable)
						variablePow += tempPow - 1;
    			}
    			//Else it must be a variable, or nothing important
    			else if (value.charAt(i)>= 'a' && value.charAt(i) <= 'z' 
    					&& value.charAt(i) != 'i' && value.charAt(i) != 'e' && value.charAt(i) != 'p')
    			{
    				if (value.charAt(i) == variable || variable == '0')
					{
						variable = value.charAt(i);
						variablePow += 1;
						type = PowerType.variable;
					}	
    			}
    		}
    		i++;
    	}
    }
    
    public MathObject makeMathObject()
    {
    	MathObject result = new FinalMathConstant("1",100,100);
    	if (factor != 0)
    		result = new FinalMathConstant(Long.toString(factor), 100, 100);
    	if(iPow != 0)
    	{
    		if (iPow % 2 == 0)
    		{
    			factor *= -1;
    			result = new FinalMathConstant(Long.toString(factor), 100, 100);
    		}
    		else
    		{   result = new MathOperationMultiply(result, new FinalMathConstant("i",100,100),100,100);	}
    	}
    	if (piPow != 0)
    	{	result = new MathOperationMultiply(result, new FinalMathConstant("pi",100,100),100,100); }
    	if (ePow != 0)
    	{ 
    		result = new MathOperationMultiply(result, new FinalMathConstant("e", 100, 100),100,100); 
    	}
    	
    	return result;
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
        final String string = Long.toString(factor);
        Rect bounds = new Rect();
        while(delta >= 0.1f)
        {
            // Set the text size and calculate the bounds
            paint.setTextSize(textSize);
            paint.getTextBounds(string, 0, string.length(), bounds);
            
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
    
    public void draw(Canvas canvas, int maxWidth, int maxHeight)
    {
    	
    }
    
    public IExpr eval()
    {
    	return F.ZZ(1);
    }
    
    public double approximate()throws NotConstantException, EmptyChildException
    {
    	return 1;
    }
    
}
