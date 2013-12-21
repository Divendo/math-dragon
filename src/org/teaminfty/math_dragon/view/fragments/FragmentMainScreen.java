package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationArcTangent;
import org.teaminfty.math_dragon.view.math.MathOperationSine;
import org.teaminfty.math_dragon.view.math.MathOperationSinh;

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
   
        MathConstant five = new MathConstant("5");
        MathOperationArcTangent arctan = new MathOperationArcTangent();
        MathOperationSinh sin = new MathOperationSinh();
        MathOperationAdd add = new MathOperationAdd();
        add.setChild(0, sin);
        add.setChild(1, five);

        
        // Just to test MathView
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(add);
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
}
