package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.teaminfty.math_dragon.view.math.MathOperationFunction;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MathSourceOperationFunction extends MathSourceObject
{
    /** The type this source object holds */
    private MathOperationFunction.FunctionType type;
    
    /** The paint we use to draw the operator */
    private Paint paintOperator = new Paint();
    
    /** Constructor
     * @param t The type this source object holds */
	public MathSourceOperationFunction(MathOperationFunction.FunctionType t) 
	{ type = t;	}

    @Override
    public MathObject createMathObject()
    {
        return new MathOperationFunction(type);
    }
	
    public void draw(Canvas canvas, int w, int h)
    {
    	// Set the size of the the strings: sin, cos and tan
    	paintOperator.setTextSize( (h - 3 * MathObject.lineWidth) / 2 );
    	
    	// Get a boxes that fit twice in the given height (we'll use it to draw the empty boxes)
    	Rect emptyBox = getRectBoundingBox(3 * (w - (int) (3 * MathObject.lineWidth)) / 5, 3 * h / 4, MathObjectEmpty.RATIO);
        Rect textBox = new Rect();
        switch(type)
        {
            case SIN:
            case COS:
            case TAN:
            {
            	paintOperator.setAntiAlias(true);
                if(type == MathOperationFunction.FunctionType.SIN)
                {
	                paintOperator.getTextBounds("sin", 0, "sin".length(), textBox);
	                emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
	                drawEmptyBox(canvas, emptyBox);
	                canvas.drawText("sin", (w - textBox.width() - emptyBox.width()) / 2, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                }
                if(type == MathOperationFunction.FunctionType.COS)
                {
                    paintOperator.getTextBounds("cos", 0, "cos".length(), textBox);
                    emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                    drawEmptyBox(canvas, emptyBox);
                    canvas.drawText("cos", (w - textBox.width() - emptyBox.width()) / 2, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                }
                if(type == MathOperationFunction.FunctionType.TAN)
                {
                	 paintOperator.getTextBounds("tan", 0, "cos".length(), textBox);
                     emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                     drawEmptyBox(canvas, emptyBox);
                     canvas.drawText("tan", (w - textBox.width() - emptyBox.width()) / 2, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                } 
            }
            break;
                
            case SINH:
            case COSH:
            case LN:
            {
            	 if(type == MathOperationFunction.FunctionType.SINH)
                 {
                	 paintOperator.getTextBounds("sinh", 0, "cos".length(), textBox);
                     emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                     drawEmptyBox(canvas, emptyBox);
                     canvas.drawText("sinh ", 0, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                 } 	
                 if(type == MathOperationFunction.FunctionType.COSH)
                 {
                	 paintOperator.getTextBounds("cosh", 0, "cos".length(), textBox);
                     emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                     drawEmptyBox(canvas, emptyBox);
                     canvas.drawText("cosh", 0, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                 } 
                 }
            break;
            
            case ARCSIN:
            case ARCCOS:
            case ARCTAN:
            {
            	paintOperator.setAntiAlias(true);
                paintOperator.setTypeface(TypefaceHolder.dejavuSans);
                if(type ==  MathOperationFunction.FunctionType.ARCSIN)
                {
	                paintOperator.getTextBounds("sin" + "\u207b" + "\u00b9", 0, ("sin" + "\u207b" + "\u00b9").length(), textBox);
	                emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
	                drawEmptyBox(canvas, emptyBox);
	                canvas.drawText("sin" + "\u207b" + "\u00b9", 0, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                }
                if(type ==  MathOperationFunction.FunctionType.ARCCOS)
                {
                    paintOperator.getTextBounds("cos" + "\u207b" + "\u00b9", 0, ("cos" + "\u207b" + "\u00b9").length(), textBox);
                    emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                    drawEmptyBox(canvas, emptyBox);
                    canvas.drawText("cos" + "\u207b" + "\u00b9", 0, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                }
                if(type ==  MathOperationFunction.FunctionType.ARCTAN)
                {
                	 paintOperator.getTextBounds("tan" + "\u207b" + "\u00b9", 0, ("tan" + "\u207b" + "\u00b9").length(), textBox);
                     emptyBox.offsetTo(w - emptyBox.width(), (h - emptyBox.height()) / 2);
                     drawEmptyBox(canvas, emptyBox);
                     canvas.drawText("tan" + "\u207b" + "\u00b9", 0, (h - textBox.height() - emptyBox.height()) / 2 + emptyBox.height(), paintOperator);
                } 	
            }
            break;
        }
    }
	
}
