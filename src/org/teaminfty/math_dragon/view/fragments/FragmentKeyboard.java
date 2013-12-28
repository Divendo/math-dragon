package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathSymbolEditor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

public class FragmentKeyboard extends Fragment
{
    /** The {@link MathSymbolEditor} in this fragment */
    private MathSymbolEditor mathSymbolEditor = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
    	View myFragmentView = inflater.inflate(R.layout.fragment_keyboard, container, false);
    	
    	// Get the MathConstantView
    	mathSymbolEditor = (MathSymbolEditor) myFragmentView.findViewById(R.id.mathSymbolEditor);
		
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
    	final Button buttonPi = (Button) myFragmentView.findViewById(R.id.keyboardButtonPi);
    	final Button buttonE  = (Button) myFragmentView.findViewById(R.id.keyboardButtonE);
    	final Button buttonClr = (Button) myFragmentView.findViewById(R.id.keyboardButtonClear);
    	final Button buttonDel = (Button) myFragmentView.findViewById(R.id.keyboardButtonDelete);
    	
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

    	// Return the content view
        return myFragmentView;
    }
    
    /** Refreshes the state of the buttons.
     * That is, the right symbol will be highlighted acording to the value of {@link FragmentKeyboard#editingSymbol editingSymbol} */
    private void refreshButtonState()
    {
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
}

