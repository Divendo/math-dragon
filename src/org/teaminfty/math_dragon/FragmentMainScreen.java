package org.teaminfty.math_dragon;

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
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        
        /*// Create MathObjects to test the functionality 
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
    }

    /** Clears the current formula */
    public void clear()
    {
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(null);
        mathView.invalidate();
    }
}
