package org.teaminfty.math_dragon.view;

import java.util.ArrayList;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    
    /** A class that represents a single symbol */
    private static class SymbolRepresentation
    {
        /** The factor of this symbol */
        public String factor = "0";
        /** The power of the PI symbol */
        public String piPow = "";
        /** Whether the PI symbol is shown or not */
        public boolean showPi = false;
        /** The power of the E symbol */
        public String ePow = "";
        /** Whether the E symbol is shown or not */
        public boolean showE = false;
        /** The power of the imaginary unit */
        public String iPow = "";
        /** Whether the I symbol is shown or not */
        public boolean showI = false;
        /** The powers of the variables */
        public String[] varPowers = new String[26];
        /** The whether or not to show the variables */
        public boolean[] showVars = new boolean[26];
        
        /** Constructor */
        public SymbolRepresentation()
        {
            for(int i = 0; i < varPowers.length; ++i)
                varPowers[i] = "";
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
        
        /** Stores the representation as a bundle */
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
            
            // Return the result
            return out;
        }
        
        /** Constructs a {@link SymbolRepresentation} from the given {@link Bundle}
         * @param bundle The bundle to construct from
         * @return The created {@link SymbolRepresentation} */
        public static SymbolRepresentation fromBundle(Bundle bundle)
        {
            // This will be our output
            SymbolRepresentation out = new SymbolRepresentation();
            
            // Load the values
            out.factor = bundle.getString(BUNDLE_FACTOR);
            out.piPow = bundle.getString(BUNDLE_PI_POW);
            out.showPi = bundle.getBoolean(BUNDLE_PI_SHOW);
            out.ePow = bundle.getString(BUNDLE_E_POW);
            out.showE = bundle.getBoolean(BUNDLE_E_SHOW);
            out.iPow = bundle.getString(BUNDLE_I_POW);
            out.showI = bundle.getBoolean(BUNDLE_I_SHOW);
            out.varPowers = bundle.getStringArray(BUNDLE_VAR_POWS);
            out.showVars = bundle.getBooleanArray(BUNDLE_VAR_SHOW);
            
            // Return the result
            return out;
        }
        
        /** Constructs a {@link Symbol} from the current values
         * @return The constructed {@link Symbol} */
        public Symbol getMathSymbol()
        {
            // The MathSymbol we're going to return
            Symbol out = new Symbol();
            
            // Set the factor
            if(factor.isEmpty())
                out.setFactor(symbolVisible() ? 1 : 0);
            else
                out.setFactor(factor.equals("-") ? -1 : Double.parseDouble(factor));
            
            // Set the PI power
            if(showPi)
                out.setPiPow(piPow.isEmpty() ? 1 : (piPow.equals("-") ? -1 : Long.parseLong(piPow)) );

            // Set the E power
            if(showE)
                out.setEPow(ePow.isEmpty() ? 1 : (ePow.equals("-") ? -1 : Long.parseLong(ePow)) );

            // Set the I power
            if(showI)
                out.setIPow(iPow.isEmpty() ? 1 : (iPow.equals("-") ? -1 : Long.parseLong(iPow)) );
            
            // Set the powers for the variables
            for(int i = 0; i < varPowers.length && i < out.varPowCount(); ++i)
            {
                if(showVars[i])
                    out.setVarPow(i, varPowers[i].isEmpty() ? 1 : (varPowers[i].equals("-") ? -1 : Long.parseLong(varPowers[i])) );
            }
            
            // Return the result
            return out;
        }
        
        /** Returns whether or not some symbols (i.e. variables or the constants pi, e, i) are visible
         * @return True if one or more symbols are visible, false otherwise */
        public boolean symbolVisible()
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
    
    /** The symbols in this editor */
    private ArrayList<SymbolRepresentation> symbols = new ArrayList<SymbolRepresentation>();
    
    /** The symbol representation we're currently editing */
    private int editingIndex = 0;
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
        symbols.add(new SymbolRepresentation());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    public MathSymbolEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPaints();
        symbols.add(new SymbolRepresentation());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    public MathSymbolEditor(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initPaints();
        symbols.add(new SymbolRepresentation());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    /** Initialises the paints */
    private void initPaints()
    {
        paint.setTypeface(TypefaceHolder.dejavuSans);
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.math_symbol_editor_font_size));
    }
    
    /** The prefix for the bundles containing the symbol representations */
    private static final String BUNDLE_SYMBOL_REP_PREFIX = "symbol_rep_";
    /** An int containing the index of the symbol representation we were editing */
    private static final String BUNDLE_EDITING_INDEX = "editing_index";
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
        for(int i = 0; i < symbols.size(); ++i)
            out.putBundle(BUNDLE_SYMBOL_REP_PREFIX + Integer.toString(i), symbols.get(i).toBundle());
        
        // Store the state
        out.putInt(BUNDLE_EDITING_INDEX, editingIndex);
        out.putByte(BUNDLE_CURR_SYMBOL, editingSymbol.toByte());
        out.putChar(BUNDLE_CURR_VAR, currVar);
        
        // Return the result
        return out;
    }
    
    /** Loads the state from the given bundle
     * @param bundle The bundle to load the state from */
    public void fromBundle(Bundle bundle)
    {
        // Load all values
        symbols = new ArrayList<MathSymbolEditor.SymbolRepresentation>();
        for(int i = 0; bundle.containsKey(BUNDLE_SYMBOL_REP_PREFIX + Integer.toString(i)); ++i)
            symbols.add(SymbolRepresentation.fromBundle(bundle.getBundle(BUNDLE_SYMBOL_REP_PREFIX + Integer.toString(i))));
        
        // Restore the state
        editingIndex = bundle.getInt(BUNDLE_EDITING_INDEX);
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
                if(!symbols.get(editingIndex).showVars[currVarIndex])
                {
                    symbols.get(editingIndex).varPowers[currVarIndex] = "";
                    symbols.get(editingIndex).showVars[currVarIndex] = true;
                    if(symbols.get(editingIndex).factor.equals("0"))
                        symbols.get(editingIndex).factor = "";
                    else if(symbols.get(editingIndex).factor.equals("-0"))
                        symbols.get(editingIndex).factor = "-";
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
                    if(!symbols.get(editingIndex).showPi)
                    {
                        symbols.get(editingIndex).piPow = "";
                        symbols.get(editingIndex).showPi = true;
                        if(symbols.get(editingIndex).factor.equals("0"))
                            symbols.get(editingIndex).factor = "";
                        else if(symbols.get(editingIndex).factor.equals("-0"))
                            symbols.get(editingIndex).factor = "-";
                    }
                    setEditingSymbol(EditingSymbol.PI);
                }
            break;

            case E:
                if(editingSymbol == EditingSymbol.E)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    if(!symbols.get(editingIndex).showE)
                    {
                        symbols.get(editingIndex).ePow = "";
                        symbols.get(editingIndex).showE = true;
                        if(symbols.get(editingIndex).factor.equals("0"))
                            symbols.get(editingIndex).factor = "";
                        else if(symbols.get(editingIndex).factor.equals("-0"))
                            symbols.get(editingIndex).factor = "-";
                    }
                    setEditingSymbol(EditingSymbol.E);
                }
            break;

            case I:
                if(editingSymbol == EditingSymbol.I)
                    setEditingSymbol(EditingSymbol.FACTOR);
                else
                {
                    if(!symbols.get(editingIndex).showI)
                    {
                        symbols.get(editingIndex).iPow = "";
                        symbols.get(editingIndex).showI = true;
                        if(symbols.get(editingIndex).factor.equals("0"))
                            symbols.get(editingIndex).factor = "";
                        else if(symbols.get(editingIndex).factor.equals("-0"))
                            symbols.get(editingIndex).factor = "-";
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
                    if(!symbols.get(editingIndex).showVars[currVarIndex])
                    {
                        symbols.get(editingIndex).varPowers[currVarIndex] = "";
                        symbols.get(editingIndex).showVars[currVarIndex] = true;
                        if(symbols.get(editingIndex).factor.equals("0"))
                            symbols.get(editingIndex).factor = "";
                        else if(symbols.get(editingIndex).factor.equals("-0"))
                            symbols.get(editingIndex).factor = "-";
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
            if(!append)
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

    /** Resets the symbol in this editor */
    public void reset()
    {
        // Start with a single 0 again
        symbols = new ArrayList<MathSymbolEditor.SymbolRepresentation>();
        symbols.add(new SymbolRepresentation());
        editingIndex = 0;
        
        // We'll be editing the factor again
        setEditingSymbol(EditingSymbol.FACTOR);

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Copies the values from the given {@link Symbol}
     * @param expr The {@link Expression} to copy the values from */
    public void fromExpression(Expression expr)
    {
        // Reset all values
        reset();
        
        // Set the expression
        symbols.clear();
        fromExprHelper(expr, false);
        if(symbols.size() == 0)
            symbols.add(new SymbolRepresentation());

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Helper for {@link MathSymbolEditor#fromExpression(Expression) fromExpression()}
     * @param expr The expression to add
     * @param negate Whether or not to negate the expression */
    private void fromExprHelper(Expression expr, boolean negate)
    {
        if(expr instanceof Symbol)
        {
            Symbol mathSymbol = (Symbol) expr;
            SymbolRepresentation symbol = new SymbolRepresentation();
        
            // Set the factor
            symbol.factor = doubleToString(mathSymbol.getFactor());
            
            // If the factor is not 0, we need to set the powers (and their visibility)
            if(mathSymbol.getFactor() != 0)
            {
                // PI
                if(mathSymbol.getPiPow() != 0)
                {
                    if(mathSymbol.getPiPow() != 1)
                        symbol.piPow = Long.toString(mathSymbol.getPiPow());
                    symbol.showPi = true;
                }
                
                // E
                if(mathSymbol.getEPow() != 0)
                {
                    if(mathSymbol.getEPow() != 1)
                        symbol.ePow = Long.toString(mathSymbol.getEPow());
                    symbol.showE = true;
                }
    
                // I
                if(mathSymbol.getIPow() != 0)
                {
                    if(mathSymbol.getIPow() != 1)
                        symbol.iPow = Long.toString(mathSymbol.getIPow());
                    symbol.showI = true;
                }
                
                // Variables
                for(int i = 0; i < mathSymbol.varPowCount() && i < symbol.varPowers.length; ++i)
                {
                    if(mathSymbol.getVarPow(i) != 0)
                    {
                        if(mathSymbol.getVarPow(i) != 1)
                            symbol.varPowers[i] = Long.toString(mathSymbol.getVarPow(i));
                        symbol.showVars[i] = true;
                    }
                }
            }
            
            // Negate (if necessary)
            if(negate)
            {
                if(symbol.factor.startsWith("-"))
                    symbol.factor = symbol.factor.substring(1);
                else
                    symbol.factor = '-' + symbol.factor;
            }
            
            // Beautify the display
            if(symbol.symbolVisible())
            {
                if(symbol.factor.equals("1"))
                    symbol.factor = "";
                else if(symbol.factor.equals("-1"))
                    symbol.factor = "-";
            }
            
            // Add the symbol to the list
            symbols.add(symbol);
        }
        else if(expr instanceof Empty)
        {
            // Simply add a 0
            symbols.add(new SymbolRepresentation());
        }
        else if(expr instanceof Add)
        {
            fromExprHelper(expr.getChild(0), false);
            fromExprHelper(expr.getChild(1), false);
        }
        else if(expr instanceof Subtract)
        {
            fromExprHelper(expr.getChild(0), false);
            fromExprHelper(expr.getChild(1), true);
        }
    }
    
    /** Constructs a {@link Expression} from the current values
     * @return The constructed {@link Expression} */
    public Expression getExpression()
    {
        // If we contain only one symbol, we simply return that symbol
        if(symbols.size() == 1)
            return symbols.get(0).getMathSymbol();
        
        // This will be the left operand of the next add or subtract operation
        Expression nextLeft = symbols.get(0).getMathSymbol();
        
        // Loop through all symbols
        for(int i = 1; i < symbols.size(); ++i)
        {
            // Determine whether we'll create a add or a subtract operation
            if(symbols.get(i).factor.startsWith("-"))
            {
                symbols.get(i).factor = symbols.get(i).factor.substring(1);
                nextLeft = new Subtract(nextLeft, symbols.get(i).getMathSymbol());
                symbols.get(i).factor = '-' + symbols.get(i).factor;
            }
            else
                nextLeft = new Add(nextLeft, symbols.get(i).getMathSymbol());
        }
        
        // Return the result
        return nextLeft;
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
        
        // Loop through all symbols
        for(int i = 0; i < symbols.size(); ++i)
        {
            SymbolRepresentation symbol = symbols.get(i);
            
            if(editingIndex == i && editingSymbol == EditingSymbol.FACTOR)
                ++currentPart;
            if(i != 0 && !symbol.factor.startsWith("-"))
                parts[currentPart] += '+';
            parts[currentPart] += symbol.factor;
            if(editingIndex == i && editingSymbol == EditingSymbol.FACTOR)
                ++currentPart;
                
            if(editingIndex == i && editingSymbol == EditingSymbol.PI)
                ++currentPart;
            if(symbol.showPi)
                parts[currentPart] += '\u03c0' + toSuperScript(symbol.piPow);
            if(editingIndex == i && editingSymbol == EditingSymbol.PI)
                ++currentPart;
            
            if(editingIndex == i && editingSymbol == EditingSymbol.E)
                ++currentPart;
            if(symbol.showE)
                parts[currentPart] += 'e' + toSuperScript(symbol.ePow);
            if(editingIndex == i && editingSymbol == EditingSymbol.E)
                ++currentPart;
            
            if(editingIndex == i && editingSymbol == EditingSymbol.I)
                ++currentPart;
            if(symbol.showI)
                parts[currentPart] += '\u03b9' + toSuperScript(symbol.iPow);
            if(editingIndex == i && editingSymbol == EditingSymbol.I)
                ++currentPart;
            
            for(int j = 0; j < symbol.varPowers.length; ++j)
            {
                final char chr = (char) ('a' + j);
                
                if(editingIndex == i && editingSymbol == EditingSymbol.VAR && currVar == chr)
                    ++currentPart;
                if(symbol.showVars[j])
                    parts[currentPart] += chr + toSuperScript(symbol.varPowers[j]);
                if(editingIndex == i && editingSymbol == EditingSymbol.VAR && currVar == chr)
                    ++currentPart;
            }
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
        String drawMe = "";
        for(SymbolRepresentation symbol : symbols)
        {
            if(!drawMe.isEmpty() && !symbol.factor.startsWith("-"))
                drawMe += '+';
            drawMe += symbol.factor;
            if(symbol.showPi)
                drawMe += '\u03c0' + toSuperScript(symbol.piPow);
            if(symbol.showE)
                drawMe += 'e' + toSuperScript(symbol.ePow);
            if(symbol.showI)
                drawMe += '\u03b9' + toSuperScript(symbol.iPow);
            for(int i = 0; i < symbol.varPowers.length; ++i)
            {
                if(symbol.showVars[i])
                    drawMe += (char) ('a' + i) + toSuperScript(symbol.varPowers[i]);
            }
        }
        
        // Determine and return the text bounds
        Rect bounds = new Rect();
        paint.getTextBounds(drawMe, 0, drawMe.length(), bounds);
        return bounds;
    }
    
    /** Negates the factor or the power of the symbol we're currently editing */
    public void negate()
    {
        // Negate the right string
        switch(editingSymbol)
        {
            case FACTOR:
                if(symbols.get(editingIndex).factor.startsWith("-"))
                    symbols.get(editingIndex).factor = symbols.get(editingIndex).factor.substring(1);
                else
                    symbols.get(editingIndex).factor = '-' + symbols.get(editingIndex).factor;
            break;
            
            case PI:
                if(symbols.get(editingIndex).piPow.startsWith("-"))
                    symbols.get(editingIndex).piPow = symbols.get(editingIndex).piPow.substring(1);
                else
                    symbols.get(editingIndex).piPow = '-' + symbols.get(editingIndex).piPow;
            break;
            
            case E:
                if(symbols.get(editingIndex).ePow.startsWith("-"))
                    symbols.get(editingIndex).ePow = symbols.get(editingIndex).ePow.substring(1);
                else
                    symbols.get(editingIndex).ePow = '-' + symbols.get(editingIndex).ePow;
            break;
            
            case I:
                if(symbols.get(editingIndex).iPow.startsWith("-"))
                    symbols.get(editingIndex).iPow = symbols.get(editingIndex).iPow.substring(1);
                else
                    symbols.get(editingIndex).iPow = '-' + symbols.get(editingIndex).iPow;
            break;
            
            case VAR:
            {
                final int currVarIndex = currVar - 'a';
                if(symbols.get(editingIndex).varPowers[currVarIndex].startsWith("-"))
                    symbols.get(editingIndex).varPowers[currVarIndex] = symbols.get(editingIndex).varPowers[currVarIndex].substring(1);
                else
                    symbols.get(editingIndex).varPowers[currVarIndex] = '-' + symbols.get(editingIndex).varPowers[currVarIndex];
            }
            break;
        }
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Whether or not the factor currently contains a dot
     * @return <tt>true</tt> if the factor contains a dot, <tt>false</tt> otherwise */
    public boolean containsDot()
    { return symbols.get(editingIndex).factor.contains("."); }
    
    /** The amount of decimals the current factor contains
     * @return The amount of decimals as an integer */
    public int decimalCount()
    {
        if(!containsDot()) return 0;
        return symbols.get(editingIndex).factor.length() - symbols.get(editingIndex).factor.indexOf('.') - 1;
    }

    /** Adds a dot to the factor */
    public void dot()
    {
        // We only add a dot to the factor
        if(editingSymbol != EditingSymbol.FACTOR) return;
        
        // If the factor already contains a dot, we do nothing
        if(symbols.get(editingIndex).factor.contains(".")) return;
        
        // Add the dot
        if(symbols.get(editingIndex).factor.equals("-") || symbols.get(editingIndex).factor.isEmpty())
            symbols.get(editingIndex).factor += "0.";
        else
            symbols.get(editingIndex).factor += '.';
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Adds a new symbol */
    public void plus()
    {
        // Whether or not we still have to add a new symbol
        boolean addSym = true;
        
        // If the last symbol is nothing but a 0, we make that our new symbol
        SymbolRepresentation lastSym = symbols.get(symbols.size() - 1);
        if(!lastSym.symbolVisible())
        {
            if(lastSym.factor.equals("0"))
                addSym = false;
            if(lastSym.factor.equals("-0"))
            {
                lastSym.factor = "0";
                addSym = false;
            }
        }
        
        // Add a 0
        if(addSym)
            symbols.add(new SymbolRepresentation());
        
        // Set the editing index
        editingIndex = symbols.size() - 1;
        
        // We'll be editing the factor
        setEditingSymbol(EditingSymbol.FACTOR);
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Subtracts a new symbol */
    public void subtract()
    {
        // Whether or not we still have to add a new symbol
        boolean addSym = true;
        
        // If the last symbol is nothing but a 0, we make that our new symbol
        SymbolRepresentation lastSym = symbols.get(symbols.size() - 1);
        if(!lastSym.symbolVisible())
        {
            if(lastSym.factor.equals("0"))
            {
                lastSym.factor = "-0";
                addSym = false;
            }
            if(lastSym.factor.equals("-0"))
                addSym = false;
        }
        
        // Subtract a 0
        if(addSym)
        {
            symbols.add(new SymbolRepresentation());
            symbols.get(symbols.size() - 1).factor = "-0";
        }
        
        // Set the editing index
        editingIndex = symbols.size() - 1;
        
        // We'll be editing the factor
        setEditingSymbol(EditingSymbol.FACTOR);
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
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
                if(symbols.get(editingIndex).factor.equals("0"))
                    symbols.get(editingIndex).factor = nStr;
                else if(symbols.get(editingIndex).factor.equals("-0"))
                    symbols.get(editingIndex).factor = '-' + nStr;
                else if(!symbols.get(editingIndex).factor.isEmpty() || !nStr.equals("0"))
                    symbols.get(editingIndex).factor += nStr;
            break;
            
            case PI:
                if(symbols.get(editingIndex).piPow.equals("0"))
                    symbols.get(editingIndex).piPow = nStr;
                else if(symbols.get(editingIndex).piPow.equals("-0"))
                    symbols.get(editingIndex).piPow = '-' + nStr;
                else if(!symbols.get(editingIndex).piPow.isEmpty() || !nStr.equals("0"))
                    symbols.get(editingIndex).piPow += nStr;
            break;
            
            case E:
                if(symbols.get(editingIndex).ePow.equals("0"))
                    symbols.get(editingIndex).ePow = nStr;
                else if(symbols.get(editingIndex).ePow.equals("-0"))
                    symbols.get(editingIndex).ePow = '-' + nStr;
                else if(!symbols.get(editingIndex).ePow.isEmpty() || !nStr.equals("0"))
                    symbols.get(editingIndex).ePow += nStr;
            break;
            
            case I:
                if(symbols.get(editingIndex).iPow.equals("0"))
                    symbols.get(editingIndex).iPow = nStr;
                else if(symbols.get(editingIndex).iPow.equals("-0"))
                    symbols.get(editingIndex).iPow = '-' + nStr;
                else if(!symbols.get(editingIndex).iPow.isEmpty() || !nStr.equals("0"))
                    symbols.get(editingIndex).iPow += nStr;
            break;
            
            case VAR:
            {
                final int currVarIndex = currVar - 'a';
                if(symbols.get(editingIndex).varPowers[currVarIndex].equals("0"))
                    symbols.get(editingIndex).varPowers[currVarIndex] = nStr;
                else if(symbols.get(editingIndex).varPowers[currVarIndex].equals("-0"))
                    symbols.get(editingIndex).varPowers[currVarIndex] = '-' + nStr;
                else if(!symbols.get(editingIndex).varPowers[currVarIndex].isEmpty() || !nStr.equals("0"))
                    symbols.get(editingIndex).varPowers[currVarIndex] += nStr;
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
                if(symbols.get(editingIndex).factor.length() != 0)
                    symbols.get(editingIndex).factor = symbols.get(editingIndex).factor.substring(0, symbols.get(editingIndex).factor.length() - 1);
            break;
            
            case PI:
                if(symbols.get(editingIndex).piPow.length() != 0)
                    symbols.get(editingIndex).piPow = symbols.get(editingIndex).piPow.substring(0, symbols.get(editingIndex).piPow.length() - 1);
                else
                {
                    symbols.get(editingIndex).showPi = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;
            
            case E:
                if(symbols.get(editingIndex).ePow.length() != 0)
                    symbols.get(editingIndex).ePow = symbols.get(editingIndex).ePow.substring(0, symbols.get(editingIndex).ePow.length() - 1);
                else
                {
                    symbols.get(editingIndex).showE = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;

            case I:
                if(symbols.get(editingIndex).iPow.length() != 0)
                    symbols.get(editingIndex).iPow = symbols.get(editingIndex).iPow.substring(0, symbols.get(editingIndex).iPow.length() - 1);
                else
                {
                    symbols.get(editingIndex).showI = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            break;
            
            case VAR:
            {
                final int currVarIndex = currVar - 'a';
                if(symbols.get(editingIndex).varPowers[currVarIndex].length() != 0)
                    symbols.get(editingIndex).varPowers[currVarIndex] = symbols.get(editingIndex).varPowers[currVarIndex].substring(0, symbols.get(editingIndex).varPowers[currVarIndex].length() - 1);
                else
                {
                    symbols.get(editingIndex).showVars[currVarIndex] = false;
                    setEditingSymbol(EditingSymbol.FACTOR);
                }
            }
            break;
        }
        
        // If nothing is shown, we show a 0
        if(symbols.get(editingIndex).factor.length() == 0 && !symbols.get(editingIndex).symbolVisible())
            symbols.get(editingIndex).factor = "0";
        
        // If we're only showing a 0 and we're not the only symbol, delete the entire symbol and start editing the previous one
        if(symbols.get(editingIndex).factor.equals("0") && symbols.size() > 1)
        {
            symbols.remove(editingIndex);
            setEditingSymbol(EditingSymbol.FACTOR);
            if(editingIndex != 0) --editingIndex;
        }
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** A listener that can be implemented to listen for state change events.
     * This events are only fired if the state change is initiated by this View itself. */
    public interface OnStateChangeListener
    {
        /** Called when the state changes */
        public void stateChanged();
    }
    
    /** The current {@link OnStateChangeListener} */
    private OnStateChangeListener onStateChangeListener = null;
    
    /** Set the current {@link OnStateChangeListener}
     * @param listener The new {@link OnStateChangeListener} */
    public void setStateChangeListener(OnStateChangeListener listener)
    { onStateChangeListener = listener; }
    
    /** The {@link GestureDetector} */
    private GestureDetector gestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        // Pass the touch event to the gesture detector
        gestureDetector.onTouchEvent(me);
        
        // Never consume the event
        return true;
    }
    
    /** Listens for click events to switch the symbol we're editing */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        /** Represents a part of the string with information about what is displayed in that part */
        private class StringPart
        {
            /** The starting position of the string part */
            public int start;
            /** The end position of the string part (exclusive) */
            public int end;
            
            /** The index of the symbol representation of this part */
            public int symbolIndex;
            /** The symbol type of this part */
            public EditingSymbol editingSymbol;
            /** The variable name of this part (ignored if this part isn't a variable) */
            public char varName = 'a';
            
            /** Constructor
             * @param s The starting position of the string part
             * @param e The end position of the string part (exclusive)
             * @param index The index of the symbol representation of this part
             * @param symbol The symbol type of this part */
            public StringPart(int s, int e, int index, EditingSymbol symbol)
            {
                start = s;
                end = e;
                symbolIndex = index;
                editingSymbol = symbol;
            }

            /** Constructor
             * @param s The starting position of the string part
             * @param e The end position of the string part (exclusive)
             * @param index The index of the symbol representation of this part
             * @param var The variable name of this part */
            public StringPart(int s, int e, int index, char var)
            {
                this(s, e, index, EditingSymbol.VAR);
                varName = var;
            }
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent me)
        {
            // Determine the string we'd draw while keeping track of the positions of each part
            ArrayList<StringPart> stringParts = new ArrayList<StringPart>(symbols.size() * 3);
            String totalStr = "";
            int oldLength = 0;
            for(int index = 0; index < symbols.size(); ++index)
            {
                // Get the symbol
                SymbolRepresentation symbol = symbols.get(index);
                
                // Factor
                oldLength = totalStr.length();
                if(!totalStr.isEmpty() && !symbol.factor.startsWith("-"))
                    totalStr += '+';
                totalStr += symbol.factor;
                stringParts.add(new StringPart(oldLength, totalStr.length(), index, EditingSymbol.FACTOR));
                
                // Pi
                if(symbol.showPi)
                {
                    oldLength = totalStr.length();
                    totalStr += '\u03c0' + toSuperScript(symbol.piPow);
                    stringParts.add(new StringPart(oldLength, totalStr.length(), index, EditingSymbol.PI));
                }
                
                // Eulers number
                if(symbol.showE)
                {
                    oldLength = totalStr.length();
                    totalStr += 'e' + toSuperScript(symbol.ePow);
                    stringParts.add(new StringPart(oldLength, totalStr.length(), index, EditingSymbol.E));
                }
                
                // Imaginary unit
                if(symbol.showI)
                {
                    oldLength = totalStr.length();
                    totalStr += '\u03b9' + toSuperScript(symbol.iPow);
                    stringParts.add(new StringPart(oldLength, totalStr.length(), index, EditingSymbol.I));
                }
                
                // Variables
                for(int i = 0; i < symbol.varPowers.length; ++i)
                {
                    if(symbol.showVars[i])
                    {
                        oldLength = totalStr.length();
                        totalStr += (char) ('a' + i) + toSuperScript(symbol.varPowers[i]);
                        stringParts.add(new StringPart(oldLength, totalStr.length(), index, (char) ('a' + i)));
                    }
                }
            }

            // Determine the point where the user clicks
            Rect totalTextBounds = new Rect();
            paint.getTextBounds(totalStr, 0, totalStr.length(), totalTextBounds);
            Point pos = new Point((int) me.getX(), (int) me.getY());
            pos.offset(-(getWidth() - totalTextBounds.width()) / 2, -(getHeight() - totalTextBounds.height()) / 2);
            
            // Check which part of the string the user clicked (if the user clicked one)
            Rect bounds = new Rect();
            int x = 0;
            for(StringPart part : stringParts)
            {
                // Skip empty parts
                if(part.start == part.end) continue;
                
                // Get the bounds of the current string part
                paint.getTextBounds(totalStr, part.start, part.end, bounds);
                bounds.offset(x - totalTextBounds.left, -totalTextBounds.top);
                x += paint.measureText(totalStr, part.start, part.end);
                
                // Check if the user clicked inside the current string part
                if(bounds.contains(pos.x, pos.y))
                {
                    // Change the state
                    editingIndex = part.symbolIndex;
                    editingSymbol = part.editingSymbol;
                    currVar = part.varName;
                    
                    // Notify any listeners that the state has changed
                    if(onStateChangeListener != null)
                        onStateChangeListener.stateChanged();
                    
                    // Redraw
                    invalidate();
                    
                    // Stop
                    break;
                }
                
                // We can stop if we're past all parts that can possibly contain the click position
                if(pos.x < bounds.left)
                    break;
            }
            
            // Always return true
            return true;
        }
    }
}
