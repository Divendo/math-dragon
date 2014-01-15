package org.teaminfty.math_dragon.view.fragments;

import java.util.ArrayList;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.math.Symbol;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FragmentSubstitutionEditor extends DialogFragment
{
    /** We'll keep a list of all variable buttons */
    private ArrayList<ToggleButton> varButtons = new ArrayList<ToggleButton>();
    
    /** The currently active variable */
    private char varName = 'a';
    
    /** The value that is to be substituted for the variable */
    private Symbol mathSymbol = new Symbol(0);
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitution_editor, container, false);
        
        // Click listeners for the confirm, edit and cancel buttons
        view.findViewById(R.id.btn_ok).setOnClickListener(new ButtonOkOnClickListener());
        view.findViewById(R.id.btn_cancel).setOnClickListener(new ButtonCancelOnClickListener());
        view.findViewById(R.id.btn_edit_substitute_for).setOnClickListener(new ButtonEditOnClickListener());

        // Generate the variable buttons
        ButtonVarOnClickListener buttonVarOnClickListener = new ButtonVarOnClickListener();
        LinearLayout varTable = (LinearLayout) view.findViewById(R.id.var_buttons_container);
        final String[] varNames = {"a", "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        varButtons = new ArrayList<ToggleButton>();
        for(int i = 0; i < varNames.length; )
        {
            inflater.inflate(R.layout.keyboard_variable_button_row, varTable, true);
            LinearLayout row = (LinearLayout) varTable.getChildAt(varTable.getChildCount() - 1);
            for(int j = 0; j < row.getChildCount(); ++j)
            {
                if(row.getChildAt(j) instanceof ToggleButton)
                {
                    if(i < varNames.length)
                    {
                        ToggleButton btn = (ToggleButton) row.getChildAt(j);
                        btn.setText(varNames[i]);
                        btn.setTextOn(varNames[i]);
                        btn.setTextOff(varNames[i]);
                        btn.setOnClickListener(buttonVarOnClickListener);
                        varButtons.add(btn);
                        ++i;
                    }
                    else
                        row.removeViewAt(j);
                }
            }
        }
        
        // Check the right button
        refreshButtonState();
        
        // Set the text of the substitution value
        final String symbolStr = mathSymbol.toString();
        ((TextView) view.findViewById(R.id.text_substitute_for)).setText(symbolStr.substring(1, symbolStr.length() - 1));
        
        // Return the content view
        return view;
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
            params.height = getResources().getDimensionPixelSize(R.dimen.evaluation_dlg_height);
        }
        else
        {
            // Make sure the dialog takes up all width and height it can take up
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        getDialog().getWindow().setAttributes(params);
    }
    
    /** Sets the initial variable name (should be called before this dialog is shown
     * @param name The initial variable name */
    public void initVarName(char name)
    { varName = name; }
    
    /** Sets the initial substitution value (should be called before this dialog is shown
     * @param val The initial substitution value */
    public void initValue(Symbol val)
    { mathSymbol = val; }
    
    /** A listener that's called when the substitution has been confirmed */
    public interface OnConfirmListener
    {
        /** Called when the substitution has been confirmed
         * @param varName The variable that should be substituted
         * @param mathSymbol The value that has to substituted for the variable */
        public void confirmed(char varName, Symbol mathSymbol);
    }
    
    /** The current {@link OnConfirmListener} */
    private OnConfirmListener onConfirmListener = null;
    
    /** Set the {@link OnConfirmListener}
     * @param listener The new {@link OnConfirmListener} */
    public void setOnConfirmListener(OnConfirmListener listener)
    { onConfirmListener = listener; }

    /** Returns the {@link OnConfirmListener}
     * @return The {@link OnConfirmListener} */
    public OnConfirmListener getOnConfirmListener()
    { return onConfirmListener; }
    
    /** Calls the {@link OnConfirmListener}
     * @param mathSymbol The input */
    private void callOnConfirmListener()
    {
        if(onConfirmListener != null)
            onConfirmListener.confirmed(varName, mathSymbol);
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    /** Refreshes the state of the buttons. That is, the right variable will be highlighted. */
    private void refreshButtonState()
    {
        for(ToggleButton btn : varButtons)
            btn.setChecked(varName == btn.getText().charAt(0));
    }

    /** The OnClickListener for the variable buttons */
    private class ButtonVarOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Get the button that is clicked
            ToggleButton btn = (ToggleButton) v;
            
            // Change the currently active variable
            varName = btn.getText().charAt(0);
            
            // Make sure the right button is checked
            refreshButtonState();
        }
    }

    /** The OnClickListener for the OK button */
    private class ButtonOkOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            callOnConfirmListener();
            dismiss();
        }
    }

    /** The OnClickListener for the cancel button */
    private class ButtonCancelOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        { dismiss(); }
    }

    /** The tag for the keyboard fragment */
    private static final String KEYBOARD_TAG = "keyboard";

    /** The OnClickListener for the cancel button */
    private class ButtonEditOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // If a keyboard is already shown, stop here
            if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
                return;
            
            // Create a keyboard
            FragmentKeyboard fragmentKeyboard = new FragmentKeyboard();
            
            // Set the listener
            fragmentKeyboard.setOnConfirmListener(new SetSubstitutionValueListener());
            fragmentKeyboard.setMathSymbol(mathSymbol);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), KEYBOARD_TAG);
        }
    }
    
    /** Sets the substitute for value when the keyboard returns a value */
    private class SetSubstitutionValueListener implements FragmentKeyboard.OnConfirmListener
    {
        @Override
        public void confirmed(Symbol newMathSymbol)
        {
            // Store the new value
            mathSymbol = newMathSymbol;
            
            // Show the new value
            final String symbolStr = mathSymbol.toString();
            ((TextView) getView().findViewById(R.id.text_substitute_for)).setText(symbolStr.substring(1, symbolStr.length() - 1));
        }
    }
}
