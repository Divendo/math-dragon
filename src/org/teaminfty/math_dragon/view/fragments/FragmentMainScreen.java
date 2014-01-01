package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard.OnConfirmListener;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationArcCos;
import org.teaminfty.math_dragon.view.math.MathOperationCosine;
import org.teaminfty.math_dragon.view.math.MathOperationLn;
import org.teaminfty.math_dragon.view.math.MathOperationLog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMainScreen extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        
        // Disable the undo and redo buttons
        view.findViewById(R.id.btn_undo).setEnabled(false);
        view.findViewById(R.id.btn_redo).setEnabled(false);
        
        // Listen for keyboard show requests
        ((MathView) view.findViewById(R.id.mathView)).setOnShowKeyboardListener(new ShowKeyboardListener());
        
        // Return the view
        return view;
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        
        /* Create MathObjects to test the functionality 
        MathConstant five = new MathConstant("5", 100,100);
        MathOperationDivide div = new MathOperationDivide(100,100);
        div.setChild(0, five);
        div.setChild(1, five);
        MathOperationAdd add = new MathOperationAdd(100,100);
        add.setChild(0, five);
        add.setChild(1, five);
        MathOperationAdd add2 = new MathOperationAdd(100,100);
        add2.setChild(0, add);
        add2.setChild(1, five);
        MathOperationDivide div2 = new MathOperationDivide(100,100);
        div2.setChild(0, add2);
        div2.setChild(1, five);
        MathOperationRoot root = new MathOperationRoot(100,100);
        root.setChild(0, div);
        root.setChild(1, div2);
        
        // Just to test MathView
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(root);*/
        MathOperationLog cos = new MathOperationLog();
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(cos);
        
        
    }

    /** Clears the current formula */
    public void clear()
    {
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(null);
        mathView.invalidate();
    }
    
    /** Returns the current {@link MathObject}
     * @return The current {@link MathObject} */
    public MathObject getMathObject()
    {
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        return mathView.getMathObject();
    }
    
    /** We'll want to listen for keyboard show requests from the {@link MathView} */
    private class ShowKeyboardListener implements MathView.OnShowKeyboardListener
    {
        @Override
        public void showKeyboard(MathConstant mathConstant, OnConfirmListener listener)
        {
            // Create a keyboard
            FragmentKeyboard fragmentKeyboard = new FragmentKeyboard();
            
            // Set the listener and the math symbol
            fragmentKeyboard.setOnConfirmListener(listener);
            fragmentKeyboard.setMathSymbol(mathConstant);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), "keyboard");
        }
    }
}
