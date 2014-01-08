package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.math.MathObject;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentEvaluation extends DialogFragment
{
    /** The {@link MathView} in this fragment */
    private MathView mathView = null;
    
    /** The {@link MathObject} to show when the {@link MathView} is created */
    private MathObject showMathObject = null;
    
    /** The evaluation type, <tt>true</tt> if an exact evaluation is shown, <tt>false</tt> for an approximation */
    private boolean exactEvaluation = true;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        
        // Disable the the MathView
        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setEnabled(false);
        if(showMathObject != null)
            mathView.setMathObject(showMathObject);
        
        // The close button
        ((ImageButton) view.findViewById(R.id.btn_close)).setOnClickListener(new OnCloseBtnClickListener());
        
        // The title
        ((TextView) view.findViewById(R.id.textViewEvalType)).setText(exactEvaluation ? R.string.evaluate_exact : R.string.evaluate_approximate);
        
        // Return the content view
        return view;
    }
    
    /** Sets the {@link MathObject} that is to be shown
     * @param mathObject The {@link MathObject} that is to be shown */
    public void showMathObject(MathObject mathObject)
    {
    	if(mathView == null)
    	    showMathObject = mathObject;
    	else
    	    mathView.setMathObject(mathObject);
    }

    /** Sets whether an approximation or exact evaluation is shown.
     * Should be called before {@link FragmentEvaluation#show(android.app.FragmentManager, String) show()} is called to have effect.
     * @param exact Set to <tt>true</tt> if an exact evaluation is shown, set to <tt>false</tt> for an approximation */
    public void setEvalType(boolean exact)
    { exactEvaluation = exact; }

    @Override
    public void onResume()
    {
        super.onResume();

        // Make sure the dialog takes up all width it can take up
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        mathView = null;
    }
    
    private class OnCloseBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        { dismiss(); }
    }
}

