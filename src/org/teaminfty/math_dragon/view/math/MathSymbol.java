package org.teaminfty.math_dragon.view.math;

import java.util.Arrays;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class MathSymbol extends MathObject
{
    /** The factor of this constant */
    private long factor = 0;
    /** The power of the E constant */
    private long ePow = 0;
    /** The power of the PI constant */
    private long piPow = 0;
    /** The power of the imaginary unit */
    private long iPow = 0;
    /** The powers of the variables */
    private long varPows[] = new long[26];
    
    /** The paint that is used to draw the factor and the constants */
    protected Paint paint = new Paint();
    
    /** Superscript character lookup table */
    private final static char[] SUPERSCRIPT = new char[] {'\u2070', '\u00b9', '\u00b2', '\u00b3','\u2074','\u2075','\u2076','\u2077', '\u2078', '\u2079'};

    /** Default constructor */
    public MathSymbol()
    { 
        this(0);
    }
    
    /**
     * Simple constructor with a factor just for simplicity.
     * @param factor The base number
     */
    public MathSymbol(long factor)
    {
        this(factor, 0, 0, 0);
    }
    
    /** Construct mathematical constant using specified values for simplicity.
     * @param factor The base number
     * @param ePow The Euler power
     * @param piPow The pi power
     * @param iPow The imaginary power
     */
    public MathSymbol(long factor, long ePow, long piPow, long iPow)
    {
        this(factor, ePow, piPow, iPow, new long[0]);
    }
    
    /** Construct mathematical constant using specified values.
     * @param factor The base number
     * @param ePow The Euler power
     * @param piPow The pi power
     * @param iPow The imaginary power
     * @param varPows The first <tt>varPows.length</tt> powers for the variables (may be <tt>null</tt>)
     */
    public MathSymbol(long factor, long ePow, long piPow, long iPow, long[] varPows)
    {
    	initPaints();
    	this.factor = factor;
    	this.ePow = ePow;
    	this.piPow = piPow;
    	this.iPow = iPow;
    	if(varPows != null)
    	{
    	    // arraycopy is safer and more efficient.
    	    System.arraycopy(varPows, 0, this.varPows, 0, Math.min(varPows.length, this.varPows.length));
    	}
    }
    
    /** Initialises the paints */
    private void initPaints()
    {
        paint.setAntiAlias(true);
        paint.setTypeface(TypefaceHolder.dejavuSans);
    }

    /**
     * Helper method for appending literals.
     * @param sb
     * @param c
     * @param pow
     */
    private static void appendLit(StringBuilder sb, char c, long pow)
    {
        if (pow != 0)
        {
            sb.append(c);
            if (pow != 1)
            {
                long num = pow;
                StringBuilder sb2 = new StringBuilder();
                while (num > 0)
                {
                    sb2.append(SUPERSCRIPT[(int) (num % 10)]);
                    num /= 10;
                }
                sb.append(sb2.reverse());
            }
        }
    }
    
    /** Calculates the size of this {@link MathSymbol} when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link MathSymbol}
     */
    protected Rect getSize(float fontSize)
    {
        // Set the text size
        paint.setTextSize(fontSize);
        
        // Calculate the total width and the height of the text
        Rect out = new Rect(0, 0, 0, 0);
        Rect bounds = new Rect();
        String str = toString();
        str = str.substring(1, str.length() - 1);
        paint.getTextBounds(str, 0, str.length(), bounds);      
        out.right = bounds.width();
        out.bottom = bounds.height();
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
        out.inset(-(int)(MathObject.lineWidth  * 2.5), -(int)(MathObject.lineWidth * 2.5));
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

        // Set the text size and colour
        paint.setTextSize(textSize);
        paint.setColor(getColor());
        
        // Translate the canvas
        canvas.save();
        canvas.translate((totalBounding.width() - textBounding.width()) / 2, (totalBounding.height() - textBounding.height()) / 2);

        String str = toString();
        str = str.substring(1, str.length() - 1);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        canvas.drawText(str, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
        
        // Restore the canvas translation
        canvas.restore();
    }
    
    /**
     * Check whether the current instance and <tt>o</tt> are identical. It is
     * heavily called by unit test cases, so do not remove this method!
     * @param o the instance to compare with
     * @return <tt>true</tt> when equal, <tt>false</tt> otherwise.
     */
    public boolean equals(Object o)
    {
        if(!(o instanceof MathSymbol))
            return false;
        MathSymbol c = (MathSymbol) o;

        return c.factor == factor && c.ePow == ePow && c.piPow == piPow && c.iPow == iPow && Arrays.equals(c.varPows, varPows);
    }
    
    /**
     * Gives the constant as a string
     * @return The constant as a string
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if(symbolVisible())
            sb.append(factor == -1 ? '-' : (factor == 1 ? "" : Long.toString(factor)) );
        else
            sb.append(Long.toString(factor));
        
        if(factor != 0)
        {
            appendLit(sb, '\u03c0', piPow);
            appendLit(sb, 'e', ePow);
            appendLit(sb, '\u03b9', iPow);
            for(int i = 0; i < varPows.length; i++)
                appendLit(sb, (char) (i + 'a'), varPows[i]);
        }
        return "(" + sb.toString() + ")";
    }

    /** Retrieve the ground base number factor.
     * @return The base number.
     */
	public long getFactor()
	{ return factor; }

	/** Assign the new factor to <tt>factor</tt>
	 * @param factor the new <tt>factor</tt> */
	public void setFactor(long factor)
	{ this.factor = factor; }

	/** Get the current power for <tt>pi</tt>
	 * @return The current power for <tt>pi</tt> */
	public long getPiPow()
	{ return piPow; }

    /** Set the new power for <tt>pi</tt>
     * @param factor the new power for <tt>pi</tt> */
	public void setPiPow(long piPow)
	{ this.piPow = piPow; }

    /** Get the current power for <tt>e</tt>
     * @return The current power for <tt>e</tt> */
	public long getEPow()
	{ return ePow; }

    /** Set the new power for <tt>e</tt>
     * @param factor the new power for <tt>e</tt> */
	public void setEPow(long ePow)
	{ this.ePow = ePow; }

    /** Get the current power for <tt>i</tt>
     * @return The current power for <tt>i</tt> */
	public long getIPow()
	{ return iPow; }

    /** Set the new power for <tt>i</tt>
     * @param factor the new power for <tt>i</tt> */
	public void setIPow(long iPow)
	{ this.iPow = iPow; }

    /** Get the current power for the given variable
     * @param index The variable index
     * @return The current power for the given variable */
    public long getVarPow(int index)
    { return varPows[index]; }

    /** Set the new power for the given variable
     * @param index The variable index
     * @param factor the new power for the variable */
	public void setVarPow(int index, long pow)
	{ varPows[index] = pow; }
	
	/** The amount of variables that this symbol supports */
	public int varPowCount()
	{ return varPows.length; }

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

    /** Returns whether or not some symbols (i.e. variables or the constants pi, e, i) are visible (i.e. their power >= 1)
     * @return True if one or more symbols are visible, false otherwise */
    public boolean symbolVisible()
    {
        if((piPow | ePow | iPow) != 0)
            return true;
        for(int i = 0; i < varPows.length; ++i)
        {
            if(varPows[i] != 0)
                return true;
        }
        return false;
    }
	
	/** The XML element name */
    public static final String NAME = "constant";
    /** The factor XML element attribute */
    public static final String ATTR_FACTOR = "factor";
    /** The E constant XML element attribute */
    public static final String ATTR_E = "eulers_number";
    /** The PI constant XML element attribute */
    public static final String ATTR_PI = "pi";
    /** The I constant XML element attribute */
    public static final String ATTR_I = "imaginary_unit";
    /** The prefix for a variable XML element attribute (followed by the name of the variable) */
    public static final String ATTR_VAR = "var_";
    
	@Override
    public void writeToXML(Document doc, Element el)
    {
        Element e = doc.createElement(NAME);
        e.setAttribute(ATTR_FACTOR, String.valueOf(factor));
        e.setAttribute(ATTR_E, String.valueOf(ePow));
        e.setAttribute(ATTR_PI, String.valueOf(piPow));
        e.setAttribute(ATTR_I, String.valueOf(iPow));
        for(int i = 0; i < varPows.length; i++)
        {
            if(varPows[i] != 0)
                e.setAttribute(ATTR_VAR + (char) ('a' + i), String.valueOf(varPows[i]));
        }
        el.appendChild(e);
    }
}
