package org.teaminfty.math_dragon;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.teaminfty.math_dragon.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentEvaluation extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        
        
       
        return inflater.inflate(R.layout.fragment_evaluation, container, false);
    }

    public void onStart()
    {
    	 super.onStart();
    	 
    	 MathOperationAdd add = new MathOperationAdd(100, 100);
         add.setChild(0, new MathConstant(20, 100, 100));
         add.setChild(1, new MathConstant(5, 100, 100));
         
         try
         {
             Log.i(getClass().getCanonicalName(), EvalEngine.eval(F.Simplify(add.eval())).toString());
             Log.i(getClass().getCanonicalName(), Double.toString(add.approximate()));
         }
         catch(EmptyChildException e)
         {
             e.printStackTrace();
         }
         catch(NotConstantException e)
         {
             e.printStackTrace();
         }
         
         MathView mathView = (MathView) getView().findViewById(R.id.mathView);
         mathView.setMathObject(add);
    }
}

