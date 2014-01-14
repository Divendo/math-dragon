package org.teaminfty.math_dragon.view.fragments;

import java.util.ArrayList;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathSymbolEditor;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class FragmentKeyboard extends DialogFragment
{
    /** The {@link MathSymbolEditor} in this fragment */
    private MathSymbolEditor mathSymbolEditor = null;
    
    /** A {@link Symbol} we saved for later to set to {@link FragmentKeyboard#mathSymbolEditor mathSymbolEditor} */
    private Symbol mathSymbolForLater = null;
    
    /** We'll keep a list of all variable buttons */
    private ArrayList<ToggleButton> varButtons = new ArrayList<ToggleButton>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
    	View myFragmentView = inflater.inflate(R.layout.fragment_keyboard, container, false);
    	
    	// Get the MathSymbolEditor
    	mathSymbolEditor = (MathSymbolEditor) myFragmentView.findViewById(R.id.mathSymbolEditor);
        if(mathSymbolForLater != null)
            mathSymbolEditor.fromMathSymbol(mathSymbolForLater);
        else if(savedInstanceState != null && savedInstanceState.getBundle(BUNDLE_MATH_SYMBOL_EDITOR_STATE) != null)
            mathSymbolEditor.fromBundle(savedInstanceState.getBundle(BUNDLE_MATH_SYMBOL_EDITOR_STATE));
		
    	// Acquire access to all buttons
    	final Button button1 =  (Button) myFragmentView.findViewById(R.id.keyboardButton1);
    	final Button button2 =  (Button) myFragmentView.findViewById(R.id.keyboardButton2);
    	final Button button3 =  (Button) myFragmentView.findViewById(R.id.keyboardButton3);
    	final Button button4 =  (Button) myFragmentView.findViewById(R.id.keyboardButton4);
    	final Button button5 =  (Button) myFragmentView.findViewById(R.id.keyboardButton5);
    	final Button button6 =  (Button) myFragmentView.findViewById(R.id.keyboardButton6);
    	final Button button7 =  (Button) myFragmentView.findViewById(R.id.keyboardButton7);
    	final Button button8 =  (Button) myFragmentView.findViewById(R.id.keyboardButton8);
    	final Button button9 =  (Button) myFragmentView.findViewById(R.id.keyboardButton9);
    	final Button button0 =  (Button) myFragmentView.findViewById(R.id.keyboardButton0);
    	final ImageButton buttonClr = (ImageButton) myFragmentView.findViewById(R.id.keyboardButtonClear);
    	final ImageButton buttonDel = (ImageButton) myFragmentView.findViewById(R.id.keyboardButtonDelete);
        final ImageButton buttonCancel = (ImageButton) myFragmentView.findViewById(R.id.keyboardButtonCancel);
    	final ImageButton buttonOK  = (ImageButton) myFragmentView.findViewById(R.id.keyboardButtonConfirm);
    	final Button buttonNegate = (Button) myFragmentView.findViewById(R.id.keyboardButtonNegate);
        final ToggleButton buttonPi = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonPi);
        final ToggleButton buttonE  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonE);
        final ToggleButton buttonI  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonI);
        final ToggleButton buttonX  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonX);
        final ToggleButton buttonY  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonY);
        final ToggleButton buttonZ  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonZ);
        final ToggleButton buttonTabNumpad = (ToggleButton) myFragmentView.findViewById(R.id.btn_tab_numpad);
        final ToggleButton buttonTabVariables  = (ToggleButton) myFragmentView.findViewById(R.id.btn_tab_variables);
    	
    	// Create the OnClickListeners we're going to use multiple times
    	final ButtonNumberOnClickListener buttonNumberOnClickListener = new ButtonNumberOnClickListener();
        final ButtonSymbolOnClickListener buttonSymbolOnClickListener = new ButtonSymbolOnClickListener();
        final ButtonTabOnClickListener buttonTabOnClickListener = new ButtonTabOnClickListener();
        final ButtonVarOnClickListener buttonVarOnClickListener = new ButtonVarOnClickListener();
    	
    	// Attach the OnClicklisteners to the buttons
    	button1.setOnClickListener(buttonNumberOnClickListener);
    	button2.setOnClickListener(buttonNumberOnClickListener);
    	button3.setOnClickListener(buttonNumberOnClickListener);
    	button4.setOnClickListener(buttonNumberOnClickListener);
    	button5.setOnClickListener(buttonNumberOnClickListener);
    	button6.setOnClickListener(buttonNumberOnClickListener);
    	button7.setOnClickListener(buttonNumberOnClickListener);
    	button8.setOnClickListener(buttonNumberOnClickListener);
    	button9.setOnClickListener(buttonNumberOnClickListener);
    	button0.setOnClickListener(buttonNumberOnClickListener);
    	buttonPi.setOnClickListener(buttonSymbolOnClickListener);
    	buttonE.setOnClickListener(buttonSymbolOnClickListener);
    	buttonI.setOnClickListener(buttonSymbolOnClickListener);
    	buttonDel.setOnClickListener(new ButtonDeleteOnClickListener());
    	buttonClr.setOnClickListener(new ButtonClearOnClickListener());
        buttonCancel.setOnClickListener(new ButtonCancelOnClickListener());
    	buttonOK.setOnClickListener(new ButtonOkOnClickListener());
    	buttonNegate.setOnClickListener(new ButtonNegateOnClickListener());
    	buttonTabNumpad.setOnClickListener(buttonTabOnClickListener);
    	buttonTabVariables.setOnClickListener(buttonTabOnClickListener);
    	buttonX.setOnClickListener(buttonVarOnClickListener);
        buttonY.setOnClickListener(buttonVarOnClickListener);
        buttonZ.setOnClickListener(buttonVarOnClickListener);
        
        // Show the right tab and highlight the right button
        if(savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_NUMPAD_VISIBLE))
            showTab(myFragmentView, savedInstanceState.getBoolean(BUNDLE_NUMPAD_VISIBLE));
        else
            showTab(myFragmentView, true);
        
        // Generate the buttons for the variables keyboard
        LinearLayout varTable = (LinearLayout) myFragmentView.findViewById(R.id.table_keyboard_variables);
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
        
        // Set the buttons to the right state
        refreshButtonState(myFragmentView);

    	// Return the content view
        return myFragmentView;
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
    
    /** A boolean containing which tab is currently shown (<tt>true</tt> means the numpad tab is shown, <tt>false</tt> means the variables tab is shown) */
    private static final String BUNDLE_NUMPAD_VISIBLE = "numpad_visible";
    
    /** A bundle containing the state of the {@link MathSymbolEditor} */
    private static final String BUNDLE_MATH_SYMBOL_EDITOR_STATE = "math_symbol_editor_state";
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store which tab is shown
        outState.putBoolean(BUNDLE_NUMPAD_VISIBLE, ((ToggleButton) getView().findViewById(R.id.btn_tab_numpad)).isChecked());
        
        // Save the current MathSymbolEditor state
        outState.putBundle(BUNDLE_MATH_SYMBOL_EDITOR_STATE, mathSymbolEditor.toBundle());
    }
    
    /** Sets the current value from the given {@link Symbol}
     * @param mathSymbol The {@link Symbol} to set the current value to (can be <tt>null</tt> in which case the value will be reset) */
    public void setMathSymbol(Symbol mathSymbol)
    {
        if(mathSymbolEditor == null)
            mathSymbolForLater = mathSymbol;
        else if(mathSymbol == null)
            mathSymbolEditor.reset();
        else
            mathSymbolEditor.fromMathSymbol(mathSymbol);
        
        refreshButtonState();
    }
    
    /** A listener that's called when the symbol has been confirmed */
    public interface OnConfirmListener
    {
        /** Called when the symbol has been confirmed
         * @param mathSymbol The input */
        public void confirmed(Symbol mathSymbol);
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
    private void callOnConfirmListener(Symbol mathSymbol)
    {
        if(onConfirmListener != null)
            onConfirmListener.confirmed(mathSymbol);
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        mathSymbolEditor = null;
    }

    /** Convenience method for calling {@link FragmentKeyboard#refreshButtonState(View) refreshButtonState(View)},
     * where <tt>getView()</tt> is passed as argument. */
    private void refreshButtonState()
    { refreshButtonState(getView()); }
    
    /** Refreshes the state of the buttons.
     * That is, the right symbol will be highlighted according to the value of {@link FragmentKeyboard#editingSymbol editingSymbol}
     * @param view The view that contains the buttons */
    private void refreshButtonState(View view)
    {
        // Only execute if we're loaded
        if(mathSymbolEditor == null) return;
        
        // Get the buttons
        final ToggleButton buttonPi = (ToggleButton) view.findViewById(R.id.keyboardButtonPi);
        final ToggleButton buttonE  = (ToggleButton) view.findViewById(R.id.keyboardButtonE);
        final ToggleButton buttonI  = (ToggleButton) view.findViewById(R.id.keyboardButtonI);
        final ToggleButton buttonX  = (ToggleButton) view.findViewById(R.id.keyboardButtonX);
        final ToggleButton buttonY  = (ToggleButton) view.findViewById(R.id.keyboardButtonY);
        final ToggleButton buttonZ  = (ToggleButton) view.findViewById(R.id.keyboardButtonZ);
        
        // Uncheck all buttons
        buttonPi.setChecked(false);
        buttonE.setChecked(false);
        buttonI.setChecked(false);
        buttonX.setChecked(false);
        buttonY.setChecked(false);
        buttonZ.setChecked(false);
        for(ToggleButton btn : varButtons)
            btn.setChecked(false);
        
        // Check the right button
        switch(mathSymbolEditor.getEditingSymbol())
        {
            case PI:    buttonPi.setChecked(true);      break;
            case E:     buttonE.setChecked(true);       break;
            case I:     buttonI.setChecked(true);       break;
            case VAR:
                for(ToggleButton btn : varButtons)
                {
                    if(btn.getText().charAt(0) == mathSymbolEditor.getCurrVar())
                    {
                        btn.setChecked(true);
                        switch(mathSymbolEditor.getCurrVar())
                        {
                            case 'x': buttonX.setChecked(true); break;
                            case 'y': buttonY.setChecked(true); break;
                            case 'z': buttonZ.setChecked(true); break;
                        }
                        
                        break;
                    }
                }
            break;
            default:  /* Just to suppress warnings */   break;
        }
    }
    
    /** Activates the given tab
     * @param view The root view
     * @param showNumpad <tt>true</tt> if the numpad is to be shown, <tt>false</tt> if the variables are to be shown */
    private void showTab(View view, boolean showNumpad)
    {
        // Set the tab buttons to the right state
        ((ToggleButton) view.findViewById(R.id.btn_tab_numpad)).setChecked(showNumpad);
        ((ToggleButton) view.findViewById(R.id.btn_tab_variables)).setChecked(!showNumpad);
        
        // Show the right keyboard
        view.findViewById(R.id.table_keyboard_numpad).setVisibility(showNumpad ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.table_keyboard_variables).setVisibility(showNumpad ? View.GONE : View.VISIBLE);
    }
    
    /** The OnClickListener for the buttons with numbers */
    private class ButtonNumberOnClickListener implements View.OnClickListener 
    {
        @Override
        public void onClick(final View v)
        {
            // Get the number we pressed and add it to the MathSymbolEditor
            final int number = Integer.parseInt(((Button) v).getText().toString());
            mathSymbolEditor.addNumber(number);
        }
    }
    
    /** The OnClickListener for the buttons with symbols */
    private class ButtonSymbolOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Determine which symbol we'll be editing from now on
            switch(v.getId())
            {
                case R.id.keyboardButtonPi:
                    mathSymbolEditor.toggleEditingSymbol(MathSymbolEditor.EditingSymbol.PI);
                break;

                case R.id.keyboardButtonE:
                    mathSymbolEditor.toggleEditingSymbol(MathSymbolEditor.EditingSymbol.E);
                break;

                case R.id.keyboardButtonI:
                    mathSymbolEditor.toggleEditingSymbol(MathSymbolEditor.EditingSymbol.I);
                break;
            }
            
            // Refresh the buttons
            refreshButtonState();
        }
    }

    /** The OnClickListener for the delete button */
    private class ButtonDeleteOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Delete a number
            mathSymbolEditor.deleteNumber();
            
            // The button state might need refreshing
            refreshButtonState();
        }
    }

    /** The OnClickListener for the clear button */
    private class ButtonClearOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Reset the values in the MathSymbolEditor
            mathSymbolEditor.reset();
            refreshButtonState();
        }
    }
    
    /** The OnClickListener for the OK button */
    private class ButtonOkOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            callOnConfirmListener(mathSymbolEditor.getMathSymbol());
            dismiss();
        }
    }

    /** The OnClickListener for the negate button */
    private class ButtonNegateOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            mathSymbolEditor.negate();
        }
    }
    
    /** The OnClickListener for the OK button */
    private class ButtonCancelOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            dismiss();
        }
    }

    /** The OnClickListener for the tabs */
    private class ButtonTabOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Find out which button was pressed and show the right tab
            showTab(getView(), v.getId() == R.id.btn_tab_numpad);
        }
    }
    
    /** The OnClickListener for the variable buttons */
    private class ButtonVarOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            // Get the button that is clicked
            ToggleButton btn = (ToggleButton) v;
            
            // Toggle the editing state
            mathSymbolEditor.toggleEditingSymbol(btn.getText().charAt(0));
            
            // Refresh the buttons
            refreshButtonState();
        }
    }
}

