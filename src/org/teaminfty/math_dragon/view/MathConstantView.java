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
	}

	public MathConstantView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MathConstantView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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
		 //Simply take the values presented, and update the MathConstant
		 mathConstant = new MathConstant(factor,ePow,piPow,0);
		 
		 //Redraw
		 invalidate();
	 }
	 
	 public void btnPressed(int num){
		 //if factor is selected
		 if (typeSelected == 0){
			 if (facTemp){ 
				 factor = num;
				 facTemp = false;
			 }
			 else
				 factor = factor * 10 + num;
		 }
		 //if piPow is selected
		 else if (typeSelected == 1){
			 if (piTemp){ 
				 piPow = num;
				 piTemp = false;
			 }
			 else 
				 piPow = piPow * 10 + num;
		 }
		 //if ePow is selected
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
		 //if factor is selected
		 if (typeSelected == 0)
			 factor /= 10;
		 	
		 	//if all numbers in factor are deleted but there is still an active power of pi or e,
		 	//activate facTemp and make the factor temporarily 1.
		 	if (factor == 0 && (piPow !=0 || ePow !=0)){
		 		facTemp = true;
		 		factor = 1;
		 	}
		 
		 //if piPow is selected 
		 else if (typeSelected == 1){
		     piPow /= 10;
		     //if there is only a sole pi, delete the pi.
		 	 if (piTemp){
		 		
		 	    piPow = 0;
	 			piTemp = false;	 
	 			typeSelected = 0;
	 			// if factor was made 1 to display the pi, undo this.
	 			if (facTemp){
	 				factor = 0;
	 				facTemp = false;
	 			} 		

		 	}
		 	// if there was a power, but that now is zero, make it a sole pi instead.
		 	else if (piPow == 0){
		 		piTemp = true;
		 		piPow = 1;
		 	}
		 }
		 // if ePow was selected
		 else{ 
			 ePow /= 10;
			 // if there was a sole e, delete it.
			 if (eTemp){
				 
		 	    ePow = 0;
		 		eTemp = false;
		 		// if piPow is also zero, return to factor, else, return to piPow. 
		 		if (piPow == 0){
	 			    typeSelected = 0;
	 			 	if (facTemp){
	 			 		factor = 0;
		 				facTemp = false;
	 			 	}
	 			 }
	 			 else
	 				 typeSelected = 1;
			 }
		 	 
		 	 else if (ePow == 0){
		 	     eTemp = true;
		 		 ePow = 1;
		 	 }
		 }
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
