package org.teaminfty.math_dragon.view.math;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;


public class MathVariable extends MathObject
{
    private final String c;
    private final Paint paint = new Paint();

    /**
     * Symbol lookup table
     */
    private final ISymbol[] symbols = new ISymbol[] {F.a, F.b, F.c, F.d, F.e,
            F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q, F.r,
            F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};

    /**
     * 
     * @param c The letter this MathVariable represents.  (Is a string so we can later support multiple-letter variables)
     */
    public MathVariable(String c)
    {
        this.c = c;
        Typeface face = Typeface.create("Droid Sans Mono", Typeface.NORMAL);
        
        paint.setTypeface(face);

    }

    /**
     * Adds necessary padding given a standard size
     * @param size
     * @return
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

    /**
     * simply looks up the correct Symja symbol in a symbol table
     */
    @Override
    public IExpr eval() throws EmptyChildException
    {
       return symbols[c.charAt(0) - 'a'];
    }
    
    /**
     * returns the bounding boxes with the required padding
     */
    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        return new Rect[] {sizeAddPadding(getSize(defaultHeight))};
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // Has no children
        throw new IndexOutOfBoundsException("No children");
    }

    @Override
    public void draw(Canvas canvas)
    {
        drawBoundingBoxes(canvas);
        Rect textBounding = getSize(defaultHeight);
        Rect totalBounding = sizeAddPadding(textBounding);
        Rect bounds = new Rect();
        canvas.save();
        canvas.translate((totalBounding.width() - textBounding.width()) / 2, (totalBounding.height() - textBounding.height()) / 2);
        paint.setColor(getColor());
        paint.getTextBounds(c, 0, 1, bounds);
        canvas.drawText(c, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
        canvas.restore();
    }

    /**
     * returns the size of a symbol given the specific fontsize
     * @param fontSize
     * @return the size of the symbol
     */
    protected Rect getSize(float fontSize)
    {
        paint.setTextSize(fontSize);
        Rect out = new Rect(0, 0, 0, 0);
        Rect bounds = new Rect();

        paint.getTextBounds(c, 0, 1, bounds);
        out.right += bounds.width();
        out.bottom = bounds.height();

        return out;
    }

}
