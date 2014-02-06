package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class FragmentAbout extends DialogFragment
{
    
    private View.OnClickListener listener;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Set the click listener for the close tutorial button
        view.findViewById(R.id.btn_close).setOnClickListener(new OnCloseClicked());
        
        // Set the click listener for the start tutorial button
        if(listener != null)
            view.findViewById(R.id.btn_start_tutorial).setOnClickListener(listener);
        
        // Return the content view
        return view;
    }

    public View.OnClickListener getListener()
    {
        return listener;
    }

    public void setListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Set the right size for the keyboard dialog
        Configuration resConfig = getResources().getConfiguration();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        if((resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
           (resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            // Set the size of the dialog
            params.width = getResources().getDimensionPixelSize(R.dimen.keyboard_dlg_width);
            params.height = getResources().getDimensionPixelSize(R.dimen.keyboard_dlg_height);
        }
        else
        {
            // Make sure the dialog takes up all width and height it can take up
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        getDialog().getWindow().setAttributes(params);
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    /** Listens for click events of the close button */
    private class OnCloseClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            dismiss();
        }
    }
}
