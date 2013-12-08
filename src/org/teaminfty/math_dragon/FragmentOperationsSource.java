package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOperationsSource extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_operations_source, container, false);
        
        // Set the MathObjects for the MathSourceViews
        // Note: default size isn't necessary since we'll always have a maximum size
        ((MathSourceView) layout.findViewById(R.id.mathSourceAdd)).setMathObject(new MathOperationAdd(0, 0));
        ((MathSourceView) layout.findViewById(R.id.mathSourceSubtract)).setMathObject(new MathOperationSubtract(0, 0));
        ((MathSourceView) layout.findViewById(R.id.mathSourceMultiply)).setMathObject(new MathOperationMultiply(0, 0));
        
        // Return the layout
        return layout;
    }

}
