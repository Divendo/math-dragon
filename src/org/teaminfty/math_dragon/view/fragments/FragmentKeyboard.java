package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathSymbolEditor;
import org.teaminfty.math_dragon.view.math.MathConstant;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;

public class FragmentKeyboard extends DialogFragment
{
    /** The {@link MathSymbolEditor} in this fragment */
    private MathSymbolEditor mathSymbolEditor = null;
    
    /** A {@link MathConstant} we saved for later to set to {@link FragmentKeyboard#mathSymbolEditor mathSymbolEditor} */
    private MathConstant mathSymbolForLater = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
    	View myFragmentView = inflater.inflate(R.layout.fragment_keyboard, container, false);
    	
    	// Get the MathConstantView
    	mathSymbolEditor = (MathSymbolEditor) myFragmentView.findViewById(R.id.mathSymbolEditor);
        if(mathSymbolForLater != null)
            mathSymbolEditor.fromMathConstant(mathSymbolForLater);
		
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
    	final Button buttonClr = (Button) myFragmentView.findViewById(R.id.keyboardButtonClear);
    	final Button buttonDel = (Button) myFragmentView.findViewById(R.id.keyboardButtonDelete);
    	final Button buttonOK  = (Button) myFragmentView.findViewById(R.id.keyboardButtonConfirm);
        final ToggleButton buttonPi = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonPi);
        final ToggleButton buttonE  = (ToggleButton) myFragmentView.findViewById(R.id.keyboardButtonE);
    	
    	// Create the OnClickListeners we're going to use multiple times
    	final ButtonNumberOnClickListener buttonNumberOnClickListener = new ButtonNumberOnClickListener();
        final ButtonSymbolOnClickListener buttonSymbolOnClickListener = new ButtonSymbolOnClickListener();
    	
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
    	buttonDel.setOnClickListener(new ButtonDeleteOnClickListener());
    	buttonClr.setOnClickListener(new ButtonClearOnClickListener());
    	buttonOK.setOnClickListener(new ButtonOkOnClickListener());
    	
    	// Set the buttons to the right state
        buttonPi.setChecked(false);
        buttonE.setChecked(false);

    	// Return the content view
        return myFragmentView;
    }
    
    @Override
    public void onResume()
    {
        super.onResume();

        // Make sure the dialog takes up all width it can take up
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }
    
    /** Sets the current value from the given {@link MathConstant}
     * @param mathSymbol The {@link MathConstant} to set the current value to (can be <tt>null</tt> in which case the value will be reset) */
    public void setMathSymbol(MathConstant mathSymbol)
    {
        if(mathSymbolEditor == null)
            mathSymbolForLater = mathSymbol;
        else if(mathSymbol == null)
            mathSymbolEditor.reset();
        else
            mathSymbolEditor.fromMathConstant(mathSymbol);
        
        refreshButtonState();
    }
    
    /** A listener that's called when the symbol has been confirmed */
    public interface OnConfirmListener
    {
        /** Called when the symbol has been confirmed
         * @param mathSymbol The input */
        public void confirmed(MathConstant mathSymbol);
    }
    
    /** The current {@link OnConfirmListener} */
    private OnConfirmListener onConfirmListener = null;
    
    /** Set the {@link OnConfirmListener}
     * @param listener The new {@link OnConfirmListener} */
    public void setOnConfirmListener(OnConfirmListener listener)
    { onConfirmListener = listener; }
    
    /** Calls the {@link OnConfirmListener}
     * @param mathConstant The input */
    private void callOnConfirmListener(MathConstant mathConstant)
    {
        if(onConfirmListener != null)
            onConfirmListener.confirmed(mathConstant);
    }
    
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        mathSymbolEditor = null;
    }
    
    /** Refreshes the state of the buttons.
     * That is, the right symbol will be highlighted according to the value of {@link FragmentKeyboard#editingSymbol editingSymbol} */
    private void refreshButtonState()
    {
        // Only execute if we're loaded
        if(mathSymbolEditor == null) return;
        
        // Get the buttons
        final ToggleButton buttonPi = (ToggleButton) getView().findViewById(R.id.keyboardButtonPi);
        final ToggleButton buttonE  = (ToggleButton) getView().findViewById(R.id.keyboardButtonE);
        
        // Uncheck all buttons
        buttonPi.setChecked(false);
        buttonE.setChecked(false);
        
        // Check the right button
        switch(mathSymbolEditor.getEditingSymbol())
        {
            case PI:    buttonPi.setChecked(true);      break;
            case E:     buttonE.setChecked(true);       break;
            default:    /* Just to suppress warnings */ break;
        }
    }
    
    /** The OnClickListener for the buttons with numbers */
    private class ButtonNumberOnClickListener implements View.OnClickListener 
    {
        @Override
        public void onClick(final View v)
        {
            // Get the number we pressed and add it to the MathConstantView
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
            // Reset the values in the MathConstantView
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
            callOnConfirmListener(mathSymbolEditor.getMathConstant());
            dismiss();
        }
    }
}

