package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class FragmentWarningDialog extends DialogFragment
{
    /** The ID of the title resource string */
    private int titleId = 0;
    
    /** The ID of the message resource string */
    private int msgId = 0;
    
    /** The {@link FragmentWarningDialog#OnConfirmListener OnConfirmListener} */
    private OnConfirmListener onConfirmListener = null;
    
    /** Default constructor */
    public FragmentWarningDialog() {}
    
    /** Constructor
     * @param title The ID of the title resource string
     * @param msg The ID of the message resource string */
    public FragmentWarningDialog(int title, int msg)
    {
        titleId = title;
        msgId = msg;
    }

    /** Constructor, constructs the dialog as a question dialog with yes and no buttons
     * @param title The ID of the title resource string
     * @param msg The ID of the message resource string
     * @param listener The {@link FragmentWarningDialog#OnConfirmListener OnConfirmListener} that listens for confirmation(i.e. the user clicks 'yes') */
    public FragmentWarningDialog(int title, int msg, OnConfirmListener listener)
    {
        titleId = title;
        msgId = msg;
        onConfirmListener = listener;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warning_dlg, container, false);
        
        // Set the title
        if(titleId != 0)
            ((TextView) view.findViewById(R.id.text_title)).setText(titleId);
        if(savedInstanceState != null && savedInstanceState.getString(TITLE_TEXT) != null)
            ((TextView) view.findViewById(R.id.text_title)).setText(savedInstanceState.getString(TITLE_TEXT));
        
        // Set the message
        if(msgId != 0)
            ((TextView) view.findViewById(R.id.text_msg)).setText(msgId);
        if(savedInstanceState != null && savedInstanceState.getString(MSG_TEXT) != null)
            ((TextView) view.findViewById(R.id.text_msg)).setText(savedInstanceState.getString(MSG_TEXT));
        
        // Set the listener for the ok and cancel buttons
        view.findViewById(R.id.btn_ok).setOnClickListener(new ButtonOkOnClickListener());
        view.findViewById(R.id.btn_cancel).setOnClickListener(new ButtonCancelOnClickListener());
        
        // Check if we're a confirmation dialog
        if(onConfirmListener != null)
        {
            ((Button) view.findViewById(R.id.btn_ok)).setText(R.string.yes);
            view.findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
        }
        
        // Return the content view
        return view;
    }
    
    /** A string containing the title text */
    private static final String TITLE_TEXT = "title_text";
    
    /** A string containing the message text */
    private static final String MSG_TEXT = "msg_text";

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store the title text
        outState.putString(TITLE_TEXT, ((TextView) getView().findViewById(R.id.text_title)).getText().toString());
        
        // Store the message text
        outState.putString(MSG_TEXT, ((TextView) getView().findViewById(R.id.text_msg)).getText().toString());
    }
    
    /** An interface that can be implemented to listen for confirms */
    public interface OnConfirmListener
    {
        public void confirm();
    }

    /** Sets the {@link FragmentWarningDialog#onConfirmListener OnConfirmListener}
     * @param listener The new {@link FragmentWarningDialog#onConfirmListener OnConfirmListener} */
    public void setOnConfirmListener(OnConfirmListener listener)
    {
        // Set the listener
        onConfirmListener = listener;
        
        // Update the layout
        if(getView() != null)
        {
            if(onConfirmListener != null)
            {
                ((Button) getView().findViewById(R.id.btn_ok)).setText(R.string.yes);
                getView().findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
            }
            else
            {
                ((Button) getView().findViewById(R.id.btn_ok)).setText(R.string.ok);
                getView().findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            }
        }
    }
    
    /** Returns the current {@link FragmentWarningDialog#onConfirmListener OnConfirmListener} */
    public OnConfirmListener getOnConfirmListener()
    { return onConfirmListener; }
    
    /** The listener for the ok button */
    private class ButtonOkOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(onConfirmListener != null)
                onConfirmListener.confirm();
            dismiss();
        }
    }
    

    /** The listener for the cancel button */
    private class ButtonCancelOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        { dismiss(); }
    }
}
