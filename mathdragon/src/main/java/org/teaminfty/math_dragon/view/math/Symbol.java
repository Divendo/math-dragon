package org.teaminfty.math_dragon.view.math;

import java.util.Arrays;

import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.operation.Integral;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** This class represents a math constant */
public class Symbol extends Expression
{
    /**
     * Cached mathematical symbolic constant for the mathematical <tt>-1</tt> in
     * order to speed up helpers and parsers so they don't need to make this
     * symbolic constant themselves.
     */
    public static final Symbol M_ONE = new Symbol(-1);
    /**
     * Cached mathematical symbolic constant for the mathematical <tt>0</tt> in
     * order to speed up helpers and parsers so they don't need to make this
     * symbolic constant themselves.
     */
    public static final Symbol ZERO = new Symbol();
    /**
     * Cached mathematical symbolic constant for the mathematical <tt>1</tt> in
     * order to speed up helpers and parsers so they don't need to make this
     * symbolic constant themselves.
     */
    public static final Symbol ONE = new Symbol(1);
    /**
     * Cached mathematical symbolic constant for the mathematical <tt>10</tt> in
     * order to speed up helpers and parsers so they don't need to make this
     * symbolic constant themselves.
     */
    public static final Symbol TEN = new Symbol(10);
    
    /** The factor of this constant */
    private double factor = 0;
    /** The power of the E constant */
    private long ePow = 0;
    /** The power of the PI constant */
    private long piPow = 0;
    /** The power of the imaginary unit */
    private long iPow = 0;
    /** The powers of the variables */
    private long varPows[] = new long[VAR_POWS_LENGTH];
    
    /**
     * Maximum supported number of variable powers. Specifying arrays with more
     * elements than {@code VAR_POWS_LENGTH} will be accepted, but only the
     * first {@code VAR_POWS_LENGTH} elements will be copied.
     */
    public static final int VAR_POWS_LENGTH = 26;
    
    /** The paint that is used to draw the factor and the constants */
    protected Paint paint = new Paint();
    
    /** Superscript character lookup table */
    private final static char[] SUPERSCRIPT = new char[] {'\u2070', '\u00b9', '\u00b2', '\u00b3','\u2074','\u2075','\u2076','\u2077', '\u2078', '\u2079'};

    /** Default constructor */
    public Symbol()
    { 
        this(0);
    }
    
    /**
     * Simple constructor with a factor just for simplicity.
     * @param factor The base number
     */
    public Symbol(double factor)
    {
        this(factor, 0, 0, 0);
    }
    
    /** Construct mathematical constant using specified values for simplicity.
     * @param factor The base number
     * @param ePow The Euler power
     * @param piPow The pi power
     * @param iPow The imaginary power
     */
    public Symbol(double factor, long ePow, long piPow, long iPow)
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
    public Symbol(double factor, long ePow, long piPow, long iPow, long[] varPows)
    {
        initPaints();
        setFactor(factor);
        this.ePow = ePow;
        this.piPow = piPow;
        this.iPow = iPow;
        if(varPows != null)
        {
            // arraycopy is safer and more efficient.
            System.arraycopy(varPows, 0, this.varPows, 0, Math.min(VAR_POWS_LENGTH, Math.min(varPows.length, this.varPows.length)));
        }
    }
    
    /** Returns a symbol that simply consists out of the given variable
     * @param var The variable that should be put in the symbol
     * @return The symbol with the given variable */
    public static Symbol createVarSymbol(char var)
    {
        Symbol out = new Symbol(1);
        out.setVarPow(var, 1);
        return out;
    }
    
    /** Initialises the paints */
    private void initPaints()
    {
        paint.setAntiAlias(true);
        paint.setTypeface(TypefaceHolder.dejavuSans);
    }

    /**
     * Helper method for appending literals.
     * @param sb The {@link StringBuilder} to append the string to
     * @param c The character of the symbol
     * @param pow The power of the symbol
     */
    private static void appendLit(StringBuilder sb, char c, long pow)
    {
        if(pow != 0)
        {
            sb.append(c);
            if(pow != 1)
            {
                if(pow < 0)
                {
                    sb.append('\u207b');
                    pow *= -1;
                }
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
    
    /** Calculates the size of this {@link Symbol} when using the given font size
     * @param fontSize The font size
     * @return The size of this {@link Symbol}
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
        out.inset(-(int)(Expression.lineWidth  * 2.5), -(int)(Expression.lineWidth * 2.5));
        out.offsetTo(0, 0);
        
        // Return the result
        return out;
    }
    
    /** Calculates the right text size for the given level
     * @param lvl The level
     * @return The right text size for the given level */
    protected float findTextSize(int lvl)
    {
        return defaultHeight * (float) Math.pow(2.0 / 3.0, Math.min(lvl, MAX_LEVEL));
    }

    @Override
    public Rect[] calculateOperatorBoundingBoxes()
    {
        // Find the right text size and return the bounding box for it
        return new Rect[]{ sizeAddPadding(getSize(findTextSize(level))) };
    }

    @Override
    public Rect calculateChildBoundingBox(int index) throws IndexOutOfBoundsException
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
        if(!(o instanceof Symbol))
            return false;
        Symbol c = (Symbol) o;

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
            sb.append(factor == -1 ? '-' : (factor == 1 ? "" : doubleToString(factor)) );
        else
            sb.append(doubleToString(factor));
        
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
    
    /** Converts a <tt>double</tt> to a string, dropping <tt>".0"</tt> if necessary.
     * Also returns, for example, <tt>"0.002"</tt> instead of <tt>"2.0E-3"</tt>.
     * @param x The <tt>double</tt> to convert
     * @return The <tt>double</tt> as a string */
    private String doubleToString(double x)
    {
        // Convert the double to a string
        String str = Double.toString(x);
        
        // Search for an 'E'
        final int ePos = str.indexOf('E');
        if(ePos != -1)
        {
            // Determine the amount of zeros and whether they need to be appended or prepended
            int zeros = Integer.parseInt(str.substring(ePos + 1));
            final boolean append = zeros >= 0;
            if(!append)
                zeros = (-zeros) - 1;
            
            // Remember the part before the 'E'
            String before = str.substring(0, ePos);
            final int dotPos = before.indexOf('.');
            if(dotPos != -1)
            {
                String tmp = before.substring(dotPos + 1);
                while(tmp.endsWith("0"))
                    tmp = tmp.substring(0, tmp.length() - 1);
                before = before.substring(0, dotPos) + tmp;
                
                if(append)
                    zeros -= tmp.length();
                if(zeros < 0)
                    before = before.substring(0, before.length() + zeros) + '.' + before.substring(before.length() + zeros);
            }
            boolean negative = before.startsWith("-");
            if(negative)
                before = before.substring(1);
            
            // Prepend/append the zeros
            while(zeros > 0)
            {
                if(append)
                    before += '0';
                else
                    before = '0' + before;
                --zeros;
            }
            if(zeros == 0 && !append)
                before = "0." + before;
            
            // Put back the minus sign
            if(negative)
                before = '-' + before;
            
            // Remember the result
            str = before;
        }
        
        // Chop off unnecessary '.' and '0'
        while(str.contains(".") && (str.endsWith(".") || str.endsWith("0")))
            str = str.substring(0, str.length() - 1);
        
        // Return the string
        return str;
    }

    /**
     * Checks whether only {@code factor != 0}.
     * 
     * @return <tt>true</tt> if all other variables equal <tt>0</tt>.
     *         <tt>false</tt> otherwise.
     */
    public boolean isFactorOnly()
    {
        for(int i = 0; i < varPows.length; ++i)
        {
            if(varPows[i] != 0)
            {
                return false;
            }
        }
        return ePow == 0 && piPow == 0 && iPow == 0;
    }

    /** Retrieve the factor
     * @return The base number */
    public double getFactor()
    { return factor; }

    /** Assign the new factor to <tt>factor</tt>
     * @param factor the new <tt>factor</tt> */
    public void setFactor(double factor)
    {
        // Round the factor to 6 decimals
        double decimals = factor > 0 ? factor - Math.floor(factor) : factor - Math.ceil(factor);
        decimals = Math.round(decimals * 1000000) / 1000000.0;
        
        // Set the factor
        this.factor = (factor > 0 ? Math.floor(factor) : Math.ceil(factor)) + decimals;
    }
    
    /**
     * Invert the current factor and return the new value.
     * @return <tt>-</tt>{@link factor}
     */
    public double invertFactor()
    {
        return this.factor = -factor;
    }

    /** Get the current power for <tt>pi</tt>
     * @return The current power for <tt>pi</tt> */
    public long getPiPow()
    { return piPow; }

    /** Set the new power for <tt>pi</tt>
     * @param piPow the new power for <tt>pi</tt> */
    public void setPiPow(long piPow)
    { this.piPow = piPow; }

    /** Get the current power for <tt>e</tt>
     * @return The current power for <tt>e</tt> */
    public long getEPow()
    { return ePow; }

    /** Set the new power for <tt>e</tt>
     * @param ePow the new power for <tt>e</tt> */
    public void setEPow(long ePow)
    { this.ePow = ePow; }

    /** Get the current power for <tt>i</tt>
     * @return The current power for <tt>i</tt> */
    public long getIPow()
    { return iPow; }

    /** Set the new power for <tt>i</tt>
     * @param iPow the new power for <tt>i</tt> */
    public void setIPow(long iPow)
    { this.iPow = iPow; }

    /** Get the current power for the given variable
     * @param index The variable index
     * @return The current power for the given variable */
    public long getVarPow(int index)
    { return varPows[index]; }

    /** Set the new power for the given variable
     * @param index The variable index
     * @param pow the new power for the variable */
    public void setVarPow(int index, long pow)
    { varPows[index] = pow; }
    
    public void setVarPow(char index, long pow)
    { setVarPow(index > 'Z' ? index - 'a' : index - 'A', pow); }
    
    public int getVarCount()
    {
        int count = 0;
        for (int i = 0; i < varPows.length; ++i)
        {
            if (varPows[i] != 0)
                ++count;
        }
        return count;
    }
    
    /** The amount of variables that this symbol supports */
    public int varPowCount()
    { return varPows.length; }
    
    /**
     * Raises the current symbol to the given power. That is, all powers are multiplied with the given power.
     * @param power The power to multiply the current powers with.
     * @return True if successful, or false otherwise (e.g. when one of the powers wouldn't be an integer).
     */
    public boolean tryRaisePower(double power)
    {
        try
        {
            // Try multiplying the powers
            long newPiPow = tryMultiplyWithIntegerResult(piPow, power);
            long newEPow = tryMultiplyWithIntegerResult(ePow, power);
            long newIPow = tryMultiplyWithIntegerResult(iPow, power);
            long[] newVarPows = new long[varPows.length];
            for(int i = 0; i < varPows.length; ++i)
            {
                newVarPows[i] = tryMultiplyWithIntegerResult(varPows[i], power);
            }

            // If we've come here, all powers could be multiplied successfully and we can use the new powers
            piPow = newPiPow;
            ePow = newEPow;
            iPow = newIPow;
            varPows = newVarPows;

            // We have successfully raised the symbol to a power
            return true;
        }
        catch(Exception exc)
        {
            return false;
        }
    }

    /**
     * Reset all numerical values to new specified values.
     * @param factor The new factor
     * @param ePow The new euler power
     * @param piPow The new pi power
     * @param iPow The new imaginary power
     */
    public void set(double factor, long ePow, long piPow, long iPow)
    {
        setFactor(factor);
        setEPow(ePow);
        setPiPow(piPow);
        setIPow(iPow);
    }

    /** Returns whether or not some symbols (i.e. variables or the constants pi, e, i) are visible (i.e. their power != 0)
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

    /** The precision for determining roundoff errors in the powers. */
    private static final double EPSILON = 0.0000001;

    /**
     * Tries to multiply the given integer and real number to get an integer result.
     * Throws an exception if the result is not an integer.
     * @param integer The integer to multiply with the real value.
     * @param real The real value to multiply with the integer.
     * @return The multiplied value.
     * @throws Exception When the result is not an integer.
     */
    private long tryMultiplyWithIntegerResult(long integer, double real) throws Exception
    {
        double result = integer * real;
        long resultAsInteger = Math.round(result);
        if(Math.abs(result - resultAsInteger) >= EPSILON)
            throw new Exception("Multiplying " + Long.toString(integer) + " and " + Double.toString(real) + " does not give an integer.");
        return resultAsInteger;
    }
}
