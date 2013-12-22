package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class MathConstant extends MathObject
{
    /** The factor of this constant */
    private long factor = 0;
    /** The power of the E constant */
    private long ePow = 0;
    /** The power of the PI constant */
    private long piPow = 0;
    /** The power of the imaginary unit */
    private long iPow = 0;
    
    /** The paint that is used to draw the factor and the constants */
    protected Paint paint = new Paint();

    /** The paint that is used to draw the exponents */
    protected Paint exponentPaint = new Paint();
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;

    /** Default constructor */
    public MathConstant()
    { 
        this(0, 0, 0, 0);
    }

    /** Constructor, constructs with the given value
     * @param v The value that this constant should be initialised with
     */
    public MathConstant(String value)
    {
        super();
        initPaints();
        readString(value);
    }
    
    /** Construct mathematical constant using specified values for simplicity.
     * @param factor The base number
     * @param ePow The euler power
     * @param piPow The pi power
     * @param iPow The imaginary power
     */
    public MathConstant(long factor, long ePow, long piPow, long iPow)
    {
    	initPaints();
    	this.factor = factor;
    	this.ePow = ePow;
    	this.piPow = piPow;
    	this.iPow = iPow;
    }

    /** Initialises the paints */
    private void initPaints()
    {
        paint.setAntiAlias(true);
        exponentPaint.setAntiAlias(true);
    }
    
    /** Resets the value of this constant */
    private void reset()
    {
    	set(0, 0, 0, 0);
    }
    
    /** Used in {@link MathConstant#readString(String) readString()} to keep track of the current power type */
    private enum PowerType { factor, i, e, pi }
    
    /** Reads the constant from the given string.
     * The string should be in the form: <tt>&lt;factor&gt;&lt;constant 1&gt;^&lt;constant 1 exponent&gt;&lt;constant 2&gt;^&lt;constant 2 exponent&gt;&lt;etc&gt;</tt>
     * The constants can be: pi, e or i, the factor and exponents may only be numbers.
     * For example: "5pi^3" or  "2piei^3".
     * Note that this method does not much error detection, so passing invalid strings may result in undefined behaviour.
     * @param value The string representation of the constant
     */
    // FIXME fails to parse if <tt>value</tt> equals <tt>Long.MIN_VALUE</tt>
    public void readString(String value)
    {
        // Reset the current values
        reset();
        
        // Loop through the string
        PowerType type = PowerType.factor;
        boolean negative = false;
        for(int i = 0; i < value.length(); ++i)
        {
            // If the value is a number, add that number
            if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
                    setFactor(getFactor() * 10 + (value.charAt(i) - '0'));
            // If it is one of the mathematical constants, add them and change the PowerType
            else
            {
                // A minus sign negates the sign of the constant
                if(value.charAt(i) == '-')
                {
                    negative = !negative;
                    continue;
                }

                // If we're still the first character, then there is an implicit 1
                if(i == 0)
                    setFactor(1);
                
                // Check for constants
                if(value.charAt(i) == 'i')
                {
                    setIPow(getIPow() + 1);
                    type = PowerType.i;
                }
                else if(value.charAt(i) == 'e')
                {
                    setEPow(getEPow() + 1);
                    type = PowerType.e;
                }
                // If you see a 'p', check if they mean 'pi'.
                else if(value.charAt(i) == 'p')
                {
                    i++;
                    if (value.charAt(i) == 'i')
                    {
                        setPiPow(getPiPow() + 1);
                        type = PowerType.pi;
                    }
                }
                // If you see the sign for power, check which power you were handling and handle that
                else if(value.charAt(i) == '^')
                {
                    // Determine whether or not the power is negative
                    boolean powNegative = value.charAt(++i) == '-';
                    if(powNegative) ++i;
                    
                    // Determine the power
                    long tempPow = 0;
                    while(i < value.length())
                    {
                        if(value.charAt(i) >= '0' && value.charAt(i)<= '9')
                        {
                            tempPow = 10 * tempPow + value.charAt(i) - '0';
                            i++;
                        }
                        else   // Don't forget the useful information that you can't use here!
                        {
                            i--;
                            break;
                        }
                    }
                    
                    // Add the power to the right power variable
                    // Subtract 1 from the power because that was added before (in case no exponent would be specified)
                    if(type == PowerType.factor)
                        setFactor((long) Math.pow(getFactor(), tempPow));
                    else if(type == PowerType.i)
                        setIPow(getIPow() + tempPow - 1);
                    else if(type == PowerType.e)
                        setEPow(getEPow() + tempPow - 1);
                    else if(type == PowerType.pi)
                        setPiPow(getPiPow() + tempPow - 1);
                }
            }
        }
        
        // If value is negative, just negate the factor
        if(negative)
            setFactor(getFactor() * -1);
    }
    
    /** Calculates the size of this {@link MathConstant} when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathConstant}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        paint.setTextSize(fontSize);
        exponentPaint.setTextSize(fontSize * EXPONENT_FACTOR);
        
        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        Rect bounds = new Rect();
        
        // First add the width of the factor part
        String tmpStr = Long.toString(getFactor());
        if((getPiPow() | getEPow() | getIPow()) != 0)
        {
            if(getFactor() == 1)
                tmpStr = "";
            else if(getFactor() == -1)
                tmpStr = "-";
        }
        if(tmpStr != "")
        {
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            out.right += bounds.width();
            out.bottom = bounds.height();
        }

        // We only show the other constants if the factor is not 0
        if(getFactor() != 0)
        {
            // Add the width of the PI constant
            if(getPiPow() != 0)
            {
                // The PI sign
                tmpStr = "\u03C0";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                out.right += bounds.width();
                out.bottom = Math.max(out.bottom, bounds.height());

                // The exponent
                if(getPiPow() != 1)
                {
                    tmpStr = Long.toString(getPiPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    out.right += bounds.width();
                }
            }
            
            // Add the width of the E constant
            if(getEPow() != 0)
            {
                // The e sign
                tmpStr = "e";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                out.right += bounds.width();
                out.bottom = Math.max(out.bottom, bounds.height());
                
                // The exponent
                if(getEPow() != 1)
                {
                    tmpStr = Long.toString(getEPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    out.right += bounds.width();
                }
            }
            
            // Add the width of the imaginary unit
            if(getIPow() != 0)
            {
                // The i sign
                tmpStr = "i";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                out.right += bounds.width();
                out.bottom = Math.max(out.bottom, bounds.height());
                
                // The exponent
                if(getIPow() != 1)
                {
                    tmpStr = Long.toString(getIPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    out.right += bounds.width();
                }
            }
        }
        
        // Return the result
        return out;
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
    
    /** Calculates the right text size for the given level
     * @param lvl The level
     * @return The right text size for the given level */
    protected float findTextSize(int lvl)
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, lvl);
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        // Find the right text size and return the bounding box for it
        return new Rect[]{ sizeAddPadding(getSize(findTextSize(level))) };
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Will always throw an error since constants do not have children
        checkChildIndex(index);
        return null;
    }
    
    public void draw(Canvas canvas)
    {
        // Draw the bounding boxes
        drawBoundingBoxes(canvas);
        
        // Get the text size and the bounding box
        final float textSize = findTextSize(level);
        Rect textBounding = getSize(textSize);
        Rect totalBounding = sizeAddPadding(textBounding);

        // Set the text size
        paint.setTextSize(textSize);
        exponentPaint.setTextSize(textSize * EXPONENT_FACTOR);
        
        // Set the paint color
        paint.setColor(getColor());
        exponentPaint.setColor(getColor());
        
        // Translate the canvas
        canvas.save();
        canvas.translate((totalBounding.width() - textBounding.width()) / 2, (totalBounding.height() - textBounding.height()) / 2);
        
        // Keep track the x-coordinate where the next string should be drawn
        int x = 0;
        
        // Draw the factor part
        String tmpStr = Long.toString(getFactor());
        Rect bounds = new Rect();
        boolean addMinusSign = false;
        if((getPiPow() | getEPow() | getIPow()) != 0)
        {
            if(getFactor() == 1)
                tmpStr = "";
            else if(getFactor() == -1)
                addMinusSign = true;    // Just remember to add a minus sign to the next constant
        }
        if(tmpStr != "" && !addMinusSign)
        {
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            canvas.drawText(tmpStr, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
            x += bounds.width();
            tmpStr = "";
        }
        
        // Only draw the other constants if the factor is not 0
        if(getFactor() != 0)
        {
            // Draw the PI constant
            if(getPiPow() != 0)
            {
                // The PI sign
                tmpStr = (addMinusSign ? "-" : "") + "\u03C0";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
                x += bounds.width();
                
                // The exponent
                if(getPiPow() != 1)
                {
                    tmpStr = Long.toString(getPiPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    canvas.drawText(tmpStr, x - bounds.left, -bounds.top, exponentPaint);
                    x += bounds.width();
                }
                
                // If we've been here the minus sign has been added (if it was to be added)
                addMinusSign = false;
            }
            
            // Draw the E constant
            if(getEPow() != 0)
            {
                // The E sign
                tmpStr = (addMinusSign ? "-" : "") + "e";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
                x += bounds.width();
                tmpStr = "";
                
                // The exponent
                if(getEPow() != 1)
                {
                    tmpStr = Long.toString(getEPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    canvas.drawText(tmpStr, x - bounds.left, -bounds.top, exponentPaint);
                    x += bounds.width();
                }
                
                // If we've been here the minus sign has been added (if it was to be added)
                addMinusSign = false;
            }
            
            // Draw the imaginary unit
            if(getIPow() != 0)
            {
                // The imaginary unit sign
                tmpStr = (addMinusSign ? "-" : "") + "i";
                paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
                x += bounds.width();
                tmpStr = "";
                
                // The exponent
                if(getIPow() != 1)
                {
                    tmpStr = Long.toString(getIPow());
                    exponentPaint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
                    canvas.drawText(tmpStr, x - bounds.left, -bounds.top, exponentPaint);
                    x += bounds.width();
                }
                
                // If we've been here the minus sign has been added (if it was to be added)
                addMinusSign = false;
            }
        }
        
        // Restore the canvas translation
        canvas.restore();
    }
    
    public IExpr eval()
    {
        // This will be our result, and if the factor is 0, we're done already
        IExpr result = F.ZZ(getFactor());
        if(getFactor() == 0) return result;

        // Add the other constants and their powers
        if(getPiPow() != 0)
            result =F.Times(result, F.Power(F.Pi, getPiPow()));
        if(getEPow() != 0)
            result = F.Times(result, F.Power(F.E, getEPow()));
        if(getIPow() != 0)
            result = F.Times(result, F.Power(F.I, getIPow()));
        
        // Return the result
        return result;
    }
    
    public double approximate()throws NotConstantException, EmptyChildException
    {
        // If the factor is 0, we're done
        if(getFactor() == 0) return 0;
        
        // Keep track of the result
        double result = getFactor();
        
        // If the power of the imaginary unit is even and not a multiple of four, we negate the value
        if(getIPow() % 2 == 0 && getIPow() % 4 != 0)
            result *= -1;
        else
            throw new NotConstantException("Can't approximate the value of an imaginary constant.");
        
        // Add the constants PI and E
        if(getPiPow() != 0)
            result *= Math.pow(Math.PI, getPiPow());
        if(getEPow() != 0)
            result *= Math.pow(Math.E, getEPow());
        
        // Return the result
        return result;
    }
    
    /**
     * Check whether the current instance and <tt>o</tt> are identical. It is
     * heavily called by unit test cases, so do not remove this method!
     * @param o the instance to compare with
     * @return <tt>true</tt> when equal, <tt>false</tt> otherwise.
     */
    public boolean equals(Object o)
    {
    	if (!(o instanceof MathConstant))
    		return false;
    	MathConstant c = (MathConstant) o;
    	return c.factor == factor && c.ePow == ePow && c.piPow == piPow && c.iPow == iPow;
    }
    
    /**
     * Gives the constant as a string (in the format that's supported by {@link MathConstant#readString(String) readString()}
     * @return The constant as a string
     */
    public String toString()
    {
        // Add the factor to the string, if that is 0 we're done
        String str = Long.toString(getFactor());
        if(getFactor() == 0) return str;

        // Add PI to the string
        if(getPiPow() != 0)
        {
            str += "\u03C0";
            if(getPiPow() != 1)
                str += "^" + getPiPow();
        }
        
        // Add the constant E to the string
        if(getEPow() != 0)
        {
            str += "e";
            if(getEPow() != 1)
                str += "^" + getEPow();
        }
        
        // Add the imaginary unit to the string
        if(getIPow() != 0)
        { 
            str += "i";
            if(getIPow() != 1)
                str += "^" + getIPow();
        }
        
        // Return the string
        return str;
    }

    /** Retrieve the ground base number factor.
     * @return The base number.
     */
	public long getFactor()
	{
		return factor;
	}

	/** Assign the new factor to <tt>factor</tt>.
	 * @param factor the new <tt>factor</tt>.
	 */
	public void setFactor(long factor)
	{
		this.factor = factor;
	}

	public long getPiPow()
	{
		return piPow;
	}

	public void setPiPow(long piPow)
	{
		this.piPow = piPow;
	}

	public long getEPow()
	{
		return ePow;
	}

	public void setEPow(long ePow)
	{
		this.ePow = ePow;
	}

	public long getIPow()
	{
		return iPow;
	}

	public void setIPow(long iPow)
	{
		this.iPow = iPow;
	}

	/**
	 * Reset all numerical values to new specified values.
	 * @param factor The new factor
	 * @param ePow The new euler power
	 * @param piPow The new pi power
	 * @param iPow The new imaginary power
	 */
	public void set(long factor, long ePow, long piPow, long iPow)
	{
		setFactor(factor);
		setEPow(ePow);
		setPiPow(piPow);
		setIPow(iPow);
	}
}
