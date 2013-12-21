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

    private final ISymbol[] symbols = new ISymbol[] {F.a, F.b, F.c, F.d, F.e,
            F.f, F.g, F.h, F.i, F.j, F.k, F.l, F.m, F.n, F.o, F.p, F.q, F.r,
            F.s, F.t, F.u, F.v, F.w, F.x, F.y, F.z};

    public MathVariable(String c)
    {
        this.c = c;
        Typeface face = Typeface.create("Droid Sans Mono", Typeface.NORMAL);
        
        paint.setTypeface(face);

    }

    protected Rect sizeAddPadding(Rect size)
    {
        // Copy the rectangle
        Rect out = new Rect(size);
        
        // Add the padding
        out.inset(-out.width() / 10, -out.height() / 6);
        out.offsetTo(0, 0);
        
        // Return the result
        return out;
    }

    @Override
    public IExpr eval() throws EmptyChildException
    {
       return symbols[c.charAt(0) - 'a'];
    }

    @Override
    public Rect[] getOperatorBoundingBoxes()
    {
        return new Rect[] {sizeAddPadding(getSize(defaultHeight))};
    }

    @Override
    public Rect getChildBoundingBox(int index) throws IndexOutOfBoundsException
    {
        // TODO Auto-generated method stub
        return null;
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
        paint.getTextBounds("b", 0, 1, bounds);
        canvas.drawText(c, -bounds.left, textBounding.height() - bounds.height() - bounds.top, paint);
        canvas.restore();
    }

    protected Rect getSize(float fontSize)
    {
        paint.setTextSize(fontSize);
        Rect out = new Rect(0, 0, 0, 0);
        Rect bounds = new Rect();

        paint.getTextBounds("b", 0, 1, bounds);
        out.right += bounds.width();
        out.bottom = bounds.height();

        return out;
    }

}
