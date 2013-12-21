package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.MathConstant;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MathConstantView extends View {
	public long typeSelected = 0;
	public long factor = 0;
	public long piPow = 0;
	public long ePow = 0; 
	private MathConstant mathConstant = new MathConstant(0,0,0,0);
	
	public MathConstantView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MathConstantView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
	}

	public MathConstantView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	 @Override
	 protected void onDraw(Canvas canvas)
	 {
        // Save the canvas
        canvas.save();
        
        // Simply draw the math object
        mathConstant.draw(canvas);
        
        // Restore the canvas
        canvas.restore();
    }
	 public void refreshMathConstant(){
		 mathConstant = new MathConstant(factor,ePow,piPow,0);
		 
		 invalidate();
	 }
	 public void setMathConstant(MathConstant newMathConstant)
	 {
		 mathConstant = newMathConstant;
	       
	 	 // Redraw
	     invalidate();
	 }
}
