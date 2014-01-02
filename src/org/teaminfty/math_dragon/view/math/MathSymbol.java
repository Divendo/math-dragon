package org.teaminfty.math_dragon.view.math;

import java.util.Arrays;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.NotConstantException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

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
    
    
    private long varPows[];
    
    /** The paint that is used to draw the factor and the constants */
    protected Paint paint = new Paint();

    /** The paint that is used to draw the exponents */
    protected Paint exponentPaint = new Paint();
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;
    
    /** Symbol lookup table */
    private final static ISymbol[] SYMBOLS = new ISymbol[] {F.a, F.b, F.c, F.d, F.e,
            F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q, F.r,
            F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};
    
    private final static char[] POWS = new char[] {'\u2070', '\u00b9', '\u00b2',
            '\u00b3','\u2074','\u2075','\u2076','\u2077', '\u2078', '\u2079'};

    /** Default constructor */
    public MathSymbol()
    { 
        this(0, 0, 0, 0, new long[0]);
    }
    
    /** Construct mathematical constant using specified values for simplicity.
     * @param factor The base number
     * @param ePow The euler power
     * @param piPow The pi power
     * @param iPow The imaginary power
     */
    public MathSymbol(long factor, long ePow, long piPow, long iPow, long[]varPows)
    {
    	initPaints();
    	this.factor = factor;
    	this.ePow = ePow;
    	this.piPow = piPow;
    	this.iPow = iPow;
    	this.varPows = varPows;
    }
    
    /** Initialises the paints */
    private void initPaints()
    {
        paint.setAntiAlias(true);
        exponentPaint.setAntiAlias(true);
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
                    sb2.append(POWS[(int) (num % 10)]);
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
        exponentPaint.setTextSize(fontSize * EXPONENT_FACTOR);
        
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

        // Set the text size
        paint.setTextSize(textSize);
        exponentPaint.setTextSize(textSize * EXPONENT_FACTOR);
        
        // Set the paint color
        paint.setColor(getColor());
        exponentPaint.setColor(getColor());
        
        // Translate the canvas
        canvas.save();
        canvas.translate((totalBounding.width() - textBounding.width()) / 2, (totalBounding.height() - textBounding.height()) / 2);

        
        StringBuilder sb = new StringBuilder();
        
        if (factor == -1)
        {
            sb.append("-");
        }
        else if (factor != 1)
        {
            sb.append(Long.toString(factor));
        }
        
        if (factor != 0)
        {
            appendLit(sb,'\u03c0', piPow);
            appendLit(sb, 'e', ePow);
            appendLit(sb, 'i', iPow);
            for (int i = 0; i < varPows.length; i++) appendLit(sb, (char)(i+'a'), varPows[i]);
        }
        String str = toString();
        str = str.substring(1, str.length() - 1);
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        canvas.drawText(str, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
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
        
        
        for (int i = 0; i < varPows.length; i++)
        {
            if (varPows[i] != 0)
            {
                result = F.Times(result, F.Power(SYMBOLS[i], varPows[i]));
            }
        }
        
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
        if(!(o instanceof MathSymbol))
            return false;
        MathSymbol c = (MathSymbol) o;

        return c.factor == factor && c.ePow == ePow && c.piPow == piPow
                && c.iPow == iPow && Arrays.equals(c.varPows, varPows);

    }
    
    /**
     * Gives the constant as a string
     * @return The constant as a string
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(factor == -1 ? '-' : Long.toString(factor));
        if(factor != 0)
        {
            appendLit(sb, '\u03c0', piPow);
            appendLit(sb, 'e', ePow);
            appendLit(sb, 'i', iPow);
            for(int i = 0; i < varPows.length; i++)
                appendLit(sb, (char) (i + 'a'), varPows[i]);
        }
        return "(" + sb.toString() + ")";
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
