package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.MathSymbol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class MathSymbolEditor extends View
{
    /** An enum that represents the symbol we're currently editing */
    public enum EditingSymbol
    {
        FACTOR((byte) 0), PI((byte) 1), E((byte) 2), I((byte) 3), VAR((byte) 4);
        
        private byte b;
        
        private EditingSymbol(byte b)
        { this.b = b; }
        
        public byte toByte()
        { return b; }
        
        public static EditingSymbol fromByte(byte b)
        {
            switch(b)
            {
                case 0: return FACTOR;
                case 1: return PI;
                case 2: return E;
                case 3: return I;
                case 4: return VAR;
            }
            return FACTOR;
        }
    }
    
    /** The factor of this symbol */
    private String factor = "0";
    /** The power of the PI symbol */
    private String piPow = "";
    /** Whether the PI symbol is shown or not */
    private boolean showPi = false;
    /** The power of the E symbol */
    private String ePow = "";
    /** Whether the E symbol is shown or not */
    private boolean showE = false;
    /** The power of the imaginary unit */
    private String iPow = "";
    /** Whether the I symbol is shown or not */
    private boolean showI = false;
    /** The powers of the variables */
    private String[] varPowers = new String[26];
    /** The whether or not to show the variables */
    private boolean[] showVars = new boolean[26];
    
    /** The symbol we're currently editing */
    private EditingSymbol editingSymbol = EditingSymbol.FACTOR;
    /** The variable we're currently editing */
    private char currVar = 'a';
    
    /** The paint that is used to draw the factor and the symbols */
    protected Paint paint = new Paint();
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;

    public MathSymbolEditor(Context context)
    {
        super(context);
        initPaints();
        for(int i = 0; i < varPowers.length; ++i)
            varPowers[i] = "";
    }

    public MathSymbolEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPaints();
        for(int i = 0; i < varPowers.length; ++i)
            varPowers[i] = "";
    }

    public MathSymbolEditor(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initPaints();
        for(int i = 0; i < varPowers.length; ++i)
            varPowers[i] = "";
    }

    /** Initialises the paints */
    private void initPaints()
    {
        paint.setTypeface(TypefaceHolder.dejavuSans);
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.math_symbol_editor_font_size));
    }
    
    /** The factor as a string */
    private static final String BUNDLE_FACTOR = "factor";
    /** The PI power as a string */
    private static final String BUNDLE_PI_POW = "pi_pow";
    /** Whether or not to show PI, as a boolean */
    private static final String BUNDLE_PI_SHOW = "pi_show";
    /** The E power as a string */
    private static final String BUNDLE_E_POW = "e_pow";
    /** Whether or not to show E, as a boolean */
    private static final String BUNDLE_E_SHOW = "e_show";
    /** The i power as a string */
    private static final String BUNDLE_I_POW = "i_pow";
    /** Whether or not to show i, as a boolean */
    private static final String BUNDLE_I_SHOW = "i_show";
    /** The powers of the variables as a string array */
    private static final String BUNDLE_VAR_POWS = "var_pows";
    /** Whether or not to show the variables as an boolean array */
    private static final String BUNDLE_VAR_SHOW = "var_show";
    
    /** A byte containing which symbol we were editing */
    private static final String BUNDLE_CURR_SYMBOL = "curr_symbol";
    /** A char containing which variable we were editing */
    private static final String BUNDLE_CURR_VAR = "curr_var";
    
    /** Saves the current state to a bundle
     * @return The bundle containing the current state */
    public Bundle toBundle()
    {
        // The bundle we're going to return
        Bundle out = new Bundle();
        
        // Store all values
        out.putString(BUNDLE_FACTOR, factor);
        out.putString(BUNDLE_PI_POW, piPow);
        out.putBoolean(BUNDLE_PI_SHOW, showPi);
        out.putString(BUNDLE_E_POW, ePow);
        out.putBoolean(BUNDLE_E_SHOW, showE);
        out.putString(BUNDLE_I_POW, iPow);
        out.putBoolean(BUNDLE_I_SHOW, showI);
        out.putStringArray(BUNDLE_VAR_POWS, varPowers);
        out.putBooleanArray(BUNDLE_VAR_SHOW, showVars);
        
        // Store the state
        out.putByte(BUNDLE_CURR_SYMBOL, editingSymbol.toByte());
        out.putChar(BUNDLE_CURR_VAR, currVar);
        
        // Return the result
        return out;
    }
    
    /** Loads the state from the given bundle
     * @param bundle The bundle to load the state from */
    public void fromBundle(Bundle bundle)
    {
        // Load the values
        factor = bundle.getString(BUNDLE_FACTOR);
        piPow = bundle.getString(BUNDLE_PI_POW);
        showPi = bundle.getBoolean(BUNDLE_PI_SHOW);
        ePow = bundle.getString(BUNDLE_E_POW);
        showE = bundle.getBoolean(BUNDLE_E_SHOW);
        iPow = bundle.getString(BUNDLE_I_POW);
        showI = bundle.getBoolean(BUNDLE_I_SHOW);
        varPowers = bundle.getStringArray(BUNDLE_VAR_POWS);
        showVars = bundle.getBooleanArray(BUNDLE_VAR_SHOW);
        
        // Restore the state
        editingSymbol = EditingSymbol.fromByte(bundle.getByte(BUNDLE_CURR_SYMBOL));
        currVar = bundle.getChar(BUNDLE_CURR_VAR);
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Sets the variable we're currently editing.
     * @param name The name of the variable */
    public void setCurrVar(char name)
    {
        currVar = name;
        invalidate();
    }
    
    /** Returns the name of the variable we're currently editing
     * @return The name of the variable we're currently editing */
    public char getCurrVar()
    { return currVar; }
    
    /** Set the symbol we're currently editing
     * @param newSymbol The symbol we're editing from now on */
    public void setEditingSymbol(EditingSymbol newSymbol)
    {
        editingSymbol = newSymbol;
        invalidate();
    }
    
    /** Toggle the symbol we're currently editing for the given variable name
     * @param varName The name of the variable we're toggling */
    public void toggleEditingSymbol(char varName)
    {
        if(editingSymbol == EditingSymbol.VAR)
        {
            if(currVar == varName)
                toggleEditingSymbol(EditingSymbol.FACTOR);
            else
            {
                // Set the current variable
                setCurrVar(varName);
                
                // Show the current variable (if it isn't visible already)
                final int currVarIndex = currVar - 'a';
                if(!showVars[currVarIndex])
                {
                    varPowers[currVarIndex] = "";
                    showVars[currVarIndex] = true;
                    if(factor.equals("0"))
                        factor = "";
                }

                // Redraw and recalculate the size
                invalidate();
                requestLayout();
            }
        }
        else
        {
            setCurrVar(varName);
            toggleEditingSymbol(EditingSymbol.VAR);
        }
    }

    /** Toggle the symbol we're currently editing
     * @param symbol The symbol we're toggling */
    public void toggleEditingSymbol(EditingSymbol newSymbol)
    {
        // We may wan't to adjust something in the factor / powers
        switch(newSymbol)
        {
            case PI:
                if(editingSymbol == EditingSymbol.PI)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    if(!showPi)
                    {
                        piPow = "";
                        showPi = true;
                        if(factor.equals("0"))
                            factor = "";
                    }
                    setEditingSymbol(EditingSymbol.PI);
                }
            break;

            case E:
                if(editingSymbol == EditingSymbol.E)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    if(!showE)
                    {
                        ePow = "";
                        showE = true;
                        if(factor.equals("0"))
                            factor = "";
                    }
                    setEditingSymbol(EditingSymbol.E);
                }
            break;

            case I:
                if(editingSymbol == EditingSymbol.I)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    if(!showI)
                    {
                        iPow = "";
                        showI = true;
                        if(factor.equals("0"))
                            factor = "";
                    }
                    setEditingSymbol(EditingSymbol.I);
                }
            break;

            case VAR:
                if(editingSymbol == EditingSymbol.VAR)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    final int currVarIndex = currVar - 'a';
                    if(!showVars[currVarIndex])
                    {
                        varPowers[currVarIndex] = "";
                        showVars[currVarIndex] = true;
                        if(factor.equals("0"))
                            factor = "";
                    }
                    setEditingSymbol(EditingSymbol.VAR);
                }
            break;
            
            default:
                setEditingSymbol(newSymbol);
            break;
        }

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }

    /** Get the symbol we're currently editing
     * @param The symbol we're currently editing */
    public EditingSymbol getEditingSymbol()
    { return editingSymbol; }

    /** Resets the symbol in this editor */
    public void reset()
    {
        // Set all fields back to their initial values
        factor = "0";
        piPow = "";
        showPi = false;
        ePow = "";
        showE = false;
        iPow = "";
        showI = false;
        varPowers = new String[26];
        for(int i = 0; i < varPowers.length; ++i)
            varPowers[i] = "";
        showVars = new boolean[26];
        
        // We'll be editing the factor again
        setEditingSymbol(EditingSymbol.FACTOR);

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Copies the values from the given {@link MathSymbol}
     * @param mathSymbol The {@link MathSymbol} to copy the values from */
    public void fromMathSymbol(MathSymbol mathSymbol)
    {
        // Reset all values
        reset();
        
        // Set the factor
        factor = Long.toString(mathSymbol.getFactor());
        
        // If the factor is not 0, we need to set the powers (and their visibility)
        if(mathSymbol.getFactor() != 0)
        {
            // PI
            if(mathSymbol.getPiPow() != 0)
            {
                if(mathSymbol.getPiPow() != 1)
                    piPow = Long.toString(mathSymbol.getPiPow());
                showPi = true;
            }
            
            // E
            if(mathSymbol.getEPow() != 0)
            {
                if(mathSymbol.getEPow() != 1)
                    ePow = Long.toString(mathSymbol.getEPow());
                showE = true;
            }

            // I
            if(mathSymbol.getIPow() != 0)
            {
                if(mathSymbol.getIPow() != 1)
                    iPow = Long.toString(mathSymbol.getIPow());
                showI = true;
            }
            
            // Variables
            for(int i = 0; i < mathSymbol.varPowCount() && i < varPowers.length; ++i)
            {
                if(mathSymbol.getVarPow(i) != 0)
                {
                    if(mathSymbol.getVarPow(i) != 1)
                        varPowers[i] = Long.toString(mathSymbol.getVarPow(i));
                    showVars[i] = true;
                }
            }
        }
        
        // Beautify the display
        if(factor.equals("1") && symbolVisible())
            factor = "";

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Constructs a {@link MathSymbol} from the current values
     * @return The constructed {@link MathSymbol} */
    public MathSymbol getMathSymbol()
    {
        // The MathSymbol we're going to return
        MathSymbol out = new MathSymbol();
        
        // Set the factor
        if(factor.isEmpty())
            out.setFactor(symbolVisible() ? 1 : 0);
        else
            out.setFactor(Long.parseLong(factor));
        
        // Set the PI power
        if(showPi)
            out.setPiPow(piPow.isEmpty() ? 1 : Long.parseLong(piPow));

        // Set the E power
        if(showE)
            out.setEPow(ePow.isEmpty() ? 1 : Long.parseLong(ePow));

        // Set the I power
        if(showI)
            out.setIPow(iPow.isEmpty() ? 1 : Long.parseLong(iPow));
        
        // Set the powers for the variables
        for(int i = 0; i < varPowers.length && i < out.varPowCount(); ++i)
        {
            if(showVars[i])
                out.setVarPow(i, varPowers[i].isEmpty() ? 1 : Long.parseLong(varPowers[i]));
        }
        
        // Return the result
        return out;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec)
    {
        // Get the size we want to take
        final Rect size = getTextBounds();
        
        // Determine the width we'll take
        int width = size.width() + 2 * getResources().getDimensionPixelSize(R.dimen.math_symbol_editor_padding);
        switch(View.MeasureSpec.getMode(widthSpec))
        {
            case View.MeasureSpec.EXACTLY:
                width = View.MeasureSpec.getSize(widthSpec);
            break;
            
            case View.MeasureSpec.AT_MOST:
                width = Math.min(width, View.MeasureSpec.getSize(widthSpec));
            break;
        }
        
        // Determine the height we'll take
        int height = size.height();
        switch(View.MeasureSpec.getMode(heightSpec))
        {
            case View.MeasureSpec.EXACTLY:
                height = View.MeasureSpec.getSize(heightSpec);
            break;
            
            case View.MeasureSpec.AT_MOST:
                height = Math.min(height, View.MeasureSpec.getSize(heightSpec));
            break;
        }
        
        // Return our size
        setMeasuredDimension(width, height);
    }
    
    /** Converts the number in the given string to superscript
     * @param str The string that is to be converted to superscript
     * @return The superscript of the given string
     */
    private String toSuperScript(String str)
    {
        // The string we're going to return
        String out = "";
        
        // Loop through all characters
        for(int i = 0; i < str.length(); ++i)
        {
            switch(str.charAt(i))
            {
                case '-': out += '\u207b'; break;
                case '0': out += '\u2070'; break;
                case '1': out += '\u00b9'; break;
                case '2': out += '\u00b2'; break;
                case '3': out += '\u00b3'; break;
                case '4': out += '\u2074'; break;
                case '5': out += '\u2075'; break;
                case '6': out += '\u2076'; break;
                case '7': out += '\u2077'; break;
                case '8': out += '\u2078'; break;
                case '9': out += '\u2079'; break;
            }
        }
        
        // Return the result
        return out;
    }
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Translate the canvas
        final Rect totalTextBounds = getTextBounds();
        canvas.translate((canvas.getWidth() - totalTextBounds.width()) / 2, (canvas.getHeight() - totalTextBounds.height()) / 2);
        
        // Distinguish the parts of the string
        String[] parts = {"", "", ""};
        int currentPart = 0;
        
        if(editingSymbol == EditingSymbol.FACTOR)
            ++currentPart;
        parts[currentPart] = factor;
        if(editingSymbol == EditingSymbol.FACTOR)
            ++currentPart;
            
        if(editingSymbol == EditingSymbol.PI)
            ++currentPart;
        if(showPi)
            parts[currentPart] += '\u03c0' + toSuperScript(piPow);
        if(editingSymbol == EditingSymbol.PI)
            ++currentPart;
        
        if(editingSymbol == EditingSymbol.E)
            ++currentPart;
        if(showE)
            parts[currentPart] += 'e' + toSuperScript(ePow);
        if(editingSymbol == EditingSymbol.E)
            ++currentPart;
        
        if(editingSymbol == EditingSymbol.I)
            ++currentPart;
        if(showI)
            parts[currentPart] += '\u03b9' + toSuperScript(iPow);
        if(editingSymbol == EditingSymbol.I)
            ++currentPart;
        
        for(int i = 0; i < varPowers.length; ++i)
        {
            final char chr = (char) ('a' + i);
            
            if(editingSymbol == EditingSymbol.VAR && currVar == chr)
                ++currentPart;
            if(showVars[i])
                parts[currentPart] += chr + toSuperScript(varPowers[i]);
            if(editingSymbol == EditingSymbol.VAR && currVar == chr)
                ++currentPart;
        }
        
        // Keep track of the current x position
        int x = 0;
        
        // Draw the parts
        if(parts[0] != "")
        {
            paint.setColor(getResources().getColor(R.color.black));
            canvas.drawText(parts[0], x - totalTextBounds.left, -totalTextBounds.top, paint);
            
            x += paint.measureText(parts[0]);
        }
        if(parts[1] != "")
        {
            paint.setColor(getResources().getColor(R.color.blue));
            canvas.drawText(parts[1], x - totalTextBounds.left, -totalTextBounds.top, paint);
            
            x += paint.measureText(parts[1]);
        }
        if(parts[2] != "")
        {
            paint.setColor(getResources().getColor(R.color.black));
            canvas.drawText(parts[2], x - totalTextBounds.left, -totalTextBounds.top, paint);
            
            x += paint.measureText(parts[2]);
        }
        
        // Restore the canvas translation
        canvas.restore();
    }

    /** Calculates the bounds of the text in this {@link MathSymbolEditor}
     * @return The bounds of the text in this {@link MathSymbolEditor}
     */
    protected Rect getTextBounds()
    {
        // Determine the string we're going to draw
        String drawMe = factor;
        if(showPi)
            drawMe += '\u03c0' + toSuperScript(piPow);
        if(showE)
            drawMe += 'e' + toSuperScript(ePow);
        if(showI)
            drawMe += '\u03b9' + toSuperScript(iPow);
        for(int i = 0; i < varPowers.length; ++i)
        {
            if(showVars[i])
                drawMe += (char) ('a' + i) + toSuperScript(varPowers[i]);
        }
        
        // Determine and return the text bounds
        Rect bounds = new Rect();
        paint.getTextBounds(drawMe, 0, drawMe.length(), bounds);
        return bounds;
    }
    
    /** Adds the given number to the symbol we're currently editing
     * @param number The number to add */
    public void addNumber(int number)
    {
        // The number as a string
        final String nStr = Integer.toString(number);
        
        // Add the number
        switch(editingSymbol)
        {
            case FACTOR:
                if(factor.equals("0"))
                    factor = nStr;
                else if(!factor.isEmpty() || !nStr.equals("0"))
                    factor += nStr;
            break;
            
            case PI:
                if(piPow.equals("0"))
                    piPow = nStr;
                else if(!piPow.isEmpty() || !nStr.equals("0"))
                    piPow += nStr;
            break;
            
            case E:
                if(ePow.equals("0"))
                    ePow = nStr;
                else if(!ePow.isEmpty() || !nStr.equals("0"))
                    ePow += nStr;
            break;
            
            case I:
                if(iPow.equals("0"))
                    iPow = nStr;
                else if(!iPow.isEmpty() || !nStr.equals("0"))
                    iPow += nStr;
            break;
            
            case VAR:
            {
                final int currVarIndex = currVar - 'a';
                if(varPowers[currVarIndex].equals("0"))
                    varPowers[currVarIndex] = nStr;
                else if(!varPowers[currVarIndex].isEmpty() || !nStr.equals("0"))
                    varPowers[currVarIndex] += nStr;
            }
            break;
        }
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Deletes the last number from the current symbol.
     * Note: this method might change the symbol that currently being editted. */
    public void deleteNumber()
    {
        // Delete one character from the number
        switch(editingSymbol)
        {
            case FACTOR:
                if(factor.length() != 0)
                    factor = factor.substring(0, factor.length() - 1);
            break;
            
            case PI:
                if(piPow.length() != 0)
                    piPow = piPow.substring(0, piPow.length() - 1);
                else
                {
                    showPi = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;
            
            case E:
                if(ePow.length() != 0)
                    ePow = ePow.substring(0, ePow.length() - 1);
                else
                {
                    showE = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;

            case I:
                if(iPow.length() != 0)
                    iPow = iPow.substring(0, iPow.length() - 1);
                else
                {
                    showI = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;
            
            case VAR:
            {
                final int currVarIndex = currVar - 'a';
                if(varPowers[currVarIndex].length() != 0)
                    varPowers[currVarIndex] = varPowers[currVarIndex].substring(0, varPowers[currVarIndex].length() - 1);
                else
                {
                    showVars[currVarIndex] = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            }
            break;
        }
        
        // If nothing is shown, we show a 0
        if(factor.length() == 0 && !symbolVisible())
            factor = "0";
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Returns whether or not some symbols (i.e. variables or the constants pi, e, i) are visible
     * @return True if one or more symbols are visible, false otherwise */
    private boolean symbolVisible()
    {
        if(showPi || showE || showI)
            return true;
        for(int i = 0; i < showVars.length; ++i)
        {
            if(showVars[i])
                return true;
        }
        return false;
    }
}
