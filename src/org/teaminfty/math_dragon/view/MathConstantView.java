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
	public boolean piTemp = false;
	public boolean facTemp = false;
	public boolean eTemp = false; 
	
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
	 
	 public void btnPressed(int num){
		 if (typeSelected == 0)
			 factor = factor * 10 + num;
		 else if (typeSelected == 1){
			 if (piTemp){ 
				 piPow = num;
				 piTemp = false;
			 }
			 else 
				 piPow = piPow * 10 + num;
		 }
		 else{ 
			 if (eTemp){
				 ePow = num;
				 eTemp = false;
			 }
			 else
				 ePow = ePow * 10 + num;
		 }
		 refreshMathConstant();
	 }
	 
	 public void delete(){
		 if (typeSelected == 0)
			 factor /= 10;
		 else if (typeSelected == 1)
			 piPow /= 10;
		 else 
			 ePow /= 10;
		 refreshMathConstant();
	 }
	 public void setMathConstant(long tfactor, long tpiPow, long tePow, long tiPow)
	 {
		 factor = tfactor;
		 piPow = tpiPow;
		 ePow = tePow;
		 mathConstant = new MathConstant(factor, piPow, ePow, tiPow);
	       
	 	 // Redraw
	     refreshMathConstant();
	 }
}
