package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathConstantView;
import org.teaminfty.math_dragon.view.math.MathConstant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentKeyboard extends Fragment {
	private View myFragmentView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
    	myFragmentView = inflater.inflate(R.layout.fragment_keyboard, container, false);
		final MathConstantView mathConstantView = (MathConstantView) myFragmentView.findViewById(R.id.mCV); 
		
    	//Acquire access to all buttons
    	final Button button1 =  (Button) myFragmentView.findViewById(R.id.button1);
    	final Button button2 =  (Button) myFragmentView.findViewById(R.id.button2);
    	final Button button3 =  (Button) myFragmentView.findViewById(R.id.button3);
    	final Button button4 =  (Button) myFragmentView.findViewById(R.id.button4);
    	final Button button5 =  (Button) myFragmentView.findViewById(R.id.button5);
    	final Button button6 =  (Button) myFragmentView.findViewById(R.id.button6);
    	final Button button7 =  (Button) myFragmentView.findViewById(R.id.button7);
    	final Button button8 =  (Button) myFragmentView.findViewById(R.id.button8);
    	final Button button9 =  (Button) myFragmentView.findViewById(R.id.button9);
    	final Button button0 =  (Button) myFragmentView.findViewById(R.id.button11);
    	final Button buttonpi = (Button) myFragmentView.findViewById(R.id.button10);
    	final Button buttone =  (Button) myFragmentView.findViewById(R.id.button12);
    	final Button buttonClr = (Button) myFragmentView.findViewById(R.id.button13);
    	final Button buttonDel = (Button) myFragmentView.findViewById(R.id.button14);
    	
    	//Set the OnClickListener for all buttons with a number
    	final OnClickListener onClickListenerNum = new OnClickListener() {
    		public void onClick(final View v) {


	        	Button button = (Button) v;
    			mathConstantView.btnPressed(Integer.parseInt(button.getText().toString()));
    		} 
    	};
    	//Set the OnClickListener for all buttons with a symbol
    	final OnClickListener onClickListenerType = new OnClickListener() {
    		public void onClick(final View v) {
	        	Button button = (Button) v;
	        	
	        	if (button==buttonpi){
	        		if (mathConstantView.typeSelected == 1)
	        			mathConstantView.typeSelected = 0;
	        		else 
	        			mathConstantView.typeSelected = 1;
	        			if (mathConstantView.piPow ==0){
	        			mathConstantView.piPow = 1;
	        			mathConstantView.piTemp = true;
	        			}
	        	}
	        	
	        	if (button==buttone){
	        		if (mathConstantView.typeSelected == 2)
	        			mathConstantView.typeSelected = 0;
	        		else 
	        			mathConstantView.typeSelected = 2;
        				if (mathConstantView.ePow ==0){
        					mathConstantView.ePow = 1;
        					mathConstantView.eTemp = true;
        				}

	        	}
        		mathConstantView.refreshMathConstant();
	        	
	        	if (button==buttonDel)
	        		mathConstantView.delete();
	        		        	
	        	if (button==buttonClr){
		        	mathConstantView.setMathConstant(0,0,0,0);
		        	mathConstantView.typeSelected = 0;
	        	}
    		} 
    	};
    	
    	//Attach the OnClicklistener to the buttons
    	button1.setOnClickListener(onClickListenerNum);
    	button2.setOnClickListener(onClickListenerNum);
    	button3.setOnClickListener(onClickListenerNum);
    	button4.setOnClickListener(onClickListenerNum);
    	button5.setOnClickListener(onClickListenerNum);
    	button6.setOnClickListener(onClickListenerNum);
    	button7.setOnClickListener(onClickListenerNum);
    	button8.setOnClickListener(onClickListenerNum);
    	button9.setOnClickListener(onClickListenerNum);
    	button0.setOnClickListener(onClickListenerNum);
    	buttonpi.setOnClickListener(onClickListenerType);
    	buttone.setOnClickListener(onClickListenerType);
    	buttonDel.setOnClickListener(onClickListenerType);
    	buttonClr.setOnClickListener(onClickListenerType);

        return myFragmentView;
    }
    
    
}

