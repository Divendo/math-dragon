package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.MathConstant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MathSymbolEditor extends View
{
    /** An enum that represents the symbol we're currently editing */
    public enum EditingSymbol
    {
        FACTOR, PI, E, I
    }
    
    /** The factor of this constant */
    private String factor = "0";
    /** The power of the PI constant */
    private String piPow = "";
    /** Whether the PI constant is shown or not */
    private boolean showPi = false;
    /** The power of the E constant */
    private String ePow = "";
    /** Whether the E constant is shown or not */
    private boolean showE = false;
    /** The power of the imaginary unit */
    private String iPow = "";
    /** Whether the I constant is shown or not */
    private boolean showI = false;
    
    /** The symbol we're currently editing */
    EditingSymbol editingSymbol = EditingSymbol.FACTOR;
    
    /** The paint that is used to draw the factor and the constants */
    protected Paint paint = new Paint();

    /** The paint that is used to draw the exponents */
    protected Paint exponentPaint = new Paint();
    
    /** The text size factor for exponents */
    protected static final float EXPONENT_FACTOR = 1.0f / 2;

    public MathSymbolEditor(Context context)
    {
        super(context);
        initPaints();
    }

    public MathSymbolEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPaints();
    }

    public MathSymbolEditor(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    /** Initialises the paints */
    private void initPaints()
    {
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.math_symbol_editor_font_size));
        exponentPaint.setAntiAlias(true);
        exponentPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.math_symbol_editor_font_size) * EXPONENT_FACTOR);
    }
    
    /** Set the symbol we're currently editing
     * @param newSymbol The symbol we're editing from now on */
    public void setEditingSymbol(EditingSymbol newSymbol)
    {
        editingSymbol = newSymbol;
        invalidate();
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

    /** Resets the constant in this editor */
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
        
        // We'll be editing the factor again
        setEditingSymbol(EditingSymbol.FACTOR);

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Copies the values from the given {@link MathConstant}
     * @param mathConstant The {@link MathConstant} to copy the values from */
    public void fromMathConstant(MathConstant mathConstant)
    {
        // Reset all values
        reset();
        
        // Set the factor
        factor = Long.toString(mathConstant.getFactor());
        
        // If the factor is not 0, we need to set the powers (and their visibility)
        if(mathConstant.getFactor() != 0)
        {
            // PI
            if(mathConstant.getPiPow() != 0)
            {
                if(mathConstant.getPiPow() != 1)
                    piPow = Long.toString(mathConstant.getPiPow());
                showPi = true;
            }
            
            // E
            if(mathConstant.getEPow() != 0)
            {
                if(mathConstant.getEPow() != 1)
                    ePow = Long.toString(mathConstant.getEPow());
                showE = true;
            }

            // I
            if(mathConstant.getIPow() != 0)
            {
                if(mathConstant.getIPow() != 1)
                    iPow = Long.toString(mathConstant.getIPow());
                showI = true;
            }
        }
        
        // Beautify the display
        if(factor.equals("1") && (showPi || showE || showI))
            factor = "";

        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
    
    /** Constructs a {@link MathConstant} from the current values
     * @return The constructed {@link MathConstant} */
    public MathConstant getMathConstant()
    {
        // The MathConstant we're going to return
        MathConstant out = new MathConstant();
        
        // Set the factor
        if(factor.isEmpty())
            out.setFactor(showPi || showE || showI ? 1 : 0);
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
        
        // Return the result
        return out;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec)
    {
        // Get the size we want to take
        final Rect size = getTextSize();
        
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
    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // Translate the canvas
        final Rect textBounding = getTextSize();
        canvas.translate((canvas.getWidth() - textBounding.width()) / 2, (canvas.getHeight() - textBounding.height()) / 2);

        // The padding between each symbol
        final int symbolPadding = getResources().getDimensionPixelSize(R.dimen.math_object_line_width) * 2;
        
        // Keep track the x-coordinate where the next string should be drawn
        int x = 0;
        
        // The colours that are used for drawing
        final int COLOR_EDITING = getResources().getColor(R.color.blue);
        final int COlOR_NORMAL = getResources().getColor(R.color.black);
        
        // Draw the factor
        Rect bounds = new Rect();
        paint.setColor(editingSymbol == EditingSymbol.FACTOR ? COLOR_EDITING : COlOR_NORMAL);
        paint.getTextBounds(factor, 0, factor.length(), bounds);
        canvas.drawText(factor, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
        x += bounds.width();
        
        // Draw the PI constant
        if(showPi)
        {
            // Add the padding
            if(x != 0)
                x += symbolPadding;
            
            // Set the colours
            paint.setColor(editingSymbol == EditingSymbol.PI ? COLOR_EDITING : COlOR_NORMAL);
            exponentPaint.setColor(editingSymbol == EditingSymbol.PI ? COLOR_EDITING : COlOR_NORMAL);
            
            // The PI sign
            String tmpStr = "\u03C0";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
            x += bounds.width();

            // Draw the exponent
            exponentPaint.getTextBounds(piPow, 0, piPow.length(), bounds);
            canvas.drawText(piPow, x - bounds.left, -bounds.top, exponentPaint);
            x += bounds.width();
        }
        
        // Draw the E constant
        if(showE)
        {
            // Add the padding
            if(x != 0)
                x += symbolPadding;
            
            // Set the colours
            paint.setColor(editingSymbol == EditingSymbol.E ? COLOR_EDITING : COlOR_NORMAL);
            exponentPaint.setColor(editingSymbol == EditingSymbol.E ? COLOR_EDITING : COlOR_NORMAL);
            
            // The E sign
            String tmpStr = "e";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
            x += bounds.width();

            // Draw the exponent
            exponentPaint.getTextBounds(ePow, 0, ePow.length(), bounds);
            canvas.drawText(ePow, x - bounds.left, -bounds.top, exponentPaint);
            x += bounds.width();
        }
        
        // Draw the imaginary unit
        if(showI)
        {
            // Add the padding
            if(x != 0)
                x += symbolPadding;
            
            // Set the colours
            paint.setColor(editingSymbol == EditingSymbol.I ? COLOR_EDITING : COlOR_NORMAL);
            exponentPaint.setColor(editingSymbol == EditingSymbol.I ? COLOR_EDITING : COlOR_NORMAL);
            
            // The imaginary unit sign
            String tmpStr = "i";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            canvas.drawText(tmpStr, x - bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
            x += bounds.width();

            // Draw the exponent
            exponentPaint.getTextBounds(iPow, 0, iPow.length(), bounds);
            canvas.drawText(iPow, x - bounds.left, -bounds.top, exponentPaint);
            x += bounds.width();
        }
        
        // Restore the canvas translation
        canvas.restore();
    }

    /** Calculates the size of the text in this {@link MathSymbolEditor}
     * @return The size of the text in this {@link MathSymbolEditor}
     */
    protected Rect getTextSize()
    {
        // The padding between each symbol
        final int symbolPadding = getResources().getDimensionPixelSize(R.dimen.math_object_line_width) * 2;
        
        // We'll store the total width and the height of the text in here
        Rect out = new Rect(0, 0, 0, 0);
        Rect bounds = new Rect();
        
        // First add the width of the factor
        paint.getTextBounds(factor, 0, factor.length(), bounds);
        out.right += bounds.width();
        out.bottom = bounds.height();

        // Add the width of the PI constant
        if(showPi)
        {
            // Add the padding
            if(out.right != 0)
                out.right += symbolPadding;
            
            // The PI sign
            String tmpStr = "\u03C0";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            out.right += bounds.width();
            out.bottom = Math.max(out.bottom, bounds.height());

            // The exponent
            exponentPaint.getTextBounds(piPow, 0, piPow.length(), bounds);
            out.right += bounds.width();
        }
        
        // Add the width of the E constant
        if(showE)
        {
            // Add the padding
            if(out.right != 0)
                out.right += symbolPadding;
            
            // The e sign
            String tmpStr = "e";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            out.right += bounds.width();
            out.bottom = Math.max(out.bottom, bounds.height());

            // The exponent
            exponentPaint.getTextBounds(ePow, 0, ePow.length(), bounds);
            out.right += bounds.width();
        }
        
        // Add the width of the imaginary unit
        if(showI)
        {
            // Add the padding
            if(out.right != 0)
                out.right += symbolPadding;
            
            // The i sign
            String tmpStr = "i";
            paint.getTextBounds(tmpStr, 0, tmpStr.length(), bounds);
            out.right += bounds.width();
            out.bottom = Math.max(out.bottom, bounds.height());
            
            // The exponent
            exponentPaint.getTextBounds(iPow, 0, iPow.length(), bounds);
            out.right += bounds.width();
        }
        
        // Return the result
        return out;
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
        }
        
        // If nothing is shown, we show a 0
        if(factor.length() == 0 && !(showPi || showE || showI))
            factor = "0";
        
        // Redraw and recalculate the size
        invalidate();
        requestLayout();
    }
}
