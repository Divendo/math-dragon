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
        
        // Create MathObjects to test the functionality
        MathOperationRoot root = new MathOperationRoot(100, 100);
        root.setChild(0, new MathConstant("2", 100,100));
        root.setChild(1, new MathConstant("25", 100,100));

        
        // Test the MathObject
        /*try
        {
            Log.i(getClass().getCanonicalName(), EvalEngine.eval(F.Simplify(root.eval())).toString());
            Log.i(getClass().getCanonicalName(), Double.toString(root.approximate()));
        }
        catch(EmptyChildException e)
        {
            e.printStackTrace();
        }
        catch(NotConstantException e)
        {
            e.printStackTrace();
        }*/
        
        // Just to test MathView
        MathView mathView = (MathView) getView().findViewById(R.id.mathView);
        mathView.setMathObject(root);
    }


}
