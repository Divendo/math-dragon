package org.teaminfty.math_dragon.view.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.ShowcaseViewDialog;
import org.teaminfty.math_dragon.view.ShowcaseViewDialogs;
import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.w3c.dom.Document;

import android.app.Activity;
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

public class FragmentSubstitutionEditor extends DialogFragment implements Tutorial
{
    /** We'll keep a list of all variable buttons */
    private ArrayList<ToggleButton> varButtons = new ArrayList<ToggleButton>();
    
    /** The currently active variable */
    private char varName = 'a';
    
    /** The value that is to be substituted for the variable */
    private Expression value = new Symbol(0);
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitution_editor, container, false);
        
        // Restore the current value and variable name (if necessary)
        if(savedInstanceState != null)
        {
            varName = savedInstanceState.getChar(VAR_NAME);
            try
            {
                value = ExpressionXMLReader.fromXML(savedInstanceState.getByteArray(CURRENT_VALUE));
            }
            catch(ParseException e)
            {
                // TODO Auto-generated catch block (when an error occurs during the conversion from the XML document to a MathObject)
                e.printStackTrace();
            }
        }
        
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
        ((TextView) view.findViewById(R.id.text_substitute_for)).setTypeface(TypefaceHolder.dejavuSans);
        ((TextView) view.findViewById(R.id.text_substitute_for)).setText(valueToString());
        
        // Restore the keyboard confirm listener (if necessary)
        if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
            ((FragmentKeyboard) getFragmentManager().findFragmentByTag(KEYBOARD_TAG)).setOnConfirmListener(new SetSubstitutionValueListener());
        
        // Return the content view
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Set the right size for the dialog
        Configuration resConfig = getResources().getConfiguration();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        if((resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
           (resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            // Set the size of the dialog
            params.width = getResources().getDimensionPixelSize(R.dimen.keyboard_dlg_width);
            params.height = getResources().getDimensionPixelSize(R.dimen.substitution_editor_dlg_height);
        }
        else
        {
            // Make sure the dialog takes up all width and height it can take up
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        getDialog().getWindow().setAttributes(params);
    }

    /** A char containing the name of the variable we're editing */
    private static final String VAR_NAME = "var_name";
    
    /** A byte array containing the current value as XML */
    private static final String CURRENT_VALUE = "curr_val";

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store the name of the variable we're editing
        outState.putChar(VAR_NAME, varName);
        
        // Store the current value
        try
        {
            // Convert the MathObject to a XML document
            Document doc = Expression.createXMLDocument();
            value.writeToXML(doc, doc.getDocumentElement());
            
            // Convert the XML document to a byte array and add it to the ContentValues instance
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
            outState.putByteArray(CURRENT_VALUE, byteStream.toByteArray());
        }
        catch(TransformerConfigurationException e)
        { /* Never thrown, ignore */ }
        catch(TransformerFactoryConfigurationError e)
        { /* Ignore */ }
        catch(TransformerException e)
        { /* Ignore */ }
        catch(ParserConfigurationException e)
        { /* Ignore */ }
    }
    
    /** Gets the value as a string */
    private String valueToString()
    {
        String str = value.toString();
        str = str.replace("(", "");
        str = str.replace(")", "");
        str = str.replace(" ", "");
        return str;
    }
    
    /** Sets the initial variable name (should be called before this dialog is shown
     * @param name The initial variable name */
    public void initVarName(char name)
    { varName = name; }
    
    /** Sets the initial substitution value (should be called before this dialog is shown
     * @param val The initial substitution value */
    public void initValue(Expression val)
    { value = val; }
    
    /** A listener that's called when the substitution has been confirmed */
    public interface OnConfirmListener
    {
        /** Called when the substitution has been confirmed
         * @param varName The variable that should be substituted
         * @param val The value that has to substituted for the variable */
        public void confirmed(char varName, Expression val);
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
     * @param value The input */
    private void callOnConfirmListener()
    {
        if(onConfirmListener != null)
            onConfirmListener.confirmed(varName, value);
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
    private ShowcaseViewDialog currentShowcaseDialog;
    @Override
    public ShowcaseViewDialog getCurrentShowcaseDialog()
    {
        return this.currentShowcaseDialog;
    }

    @Override
    public void setCurrentShowcaseDialog(ShowcaseViewDialog dialog)
    {
        this.currentShowcaseDialog = dialog;
    }

    @Override
    public int getTutorialId()
    {
        return TUTORIAL_ID;
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        tutorial();   
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if (getCurrentShowcaseDialog() != null)
            getCurrentShowcaseDialog().dismiss();
    }
    
    private void tutorial()
    {

        Activity ctx = getActivity();

        
        ShowcaseViewDialogs showcases = new ShowcaseViewDialogs(this);
        
        showcases.addViews(new ShowcaseViewDialog[]
        {
        		new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.text_title), this),
            			R.string.tutorial_subs_title,
                		R.string.tutorial_subs_edit),
            	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.var_buttons_container), this),
            			R.string.tutorial_subs_title,
                		R.string.tutorial_subs_choose),
                new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.btn_edit_substitute_for), this),
                    	R.string.tutorial_subs_title,
                        R.string.tutorial_subs_enter),
                new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.btn_ok), this),
                		R.string.tutorial_subs_title,
                   		R.string.tutorial_subs_confirm),
                new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.btn_ok), this),
                		R.string.tutorial_subs_title,
                   		R.string.tutorial_subs_delete)
        });
        
        showcases.show();

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

    public static final int TUTORIAL_ID = 2;

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
            fragmentKeyboard.setExpression(value);
            fragmentKeyboard.enableVariableButtons(false);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), KEYBOARD_TAG);
        }
    }
    
    /** Sets the substitute for value when the keyboard returns a value */
    private class SetSubstitutionValueListener implements FragmentKeyboard.OnConfirmListener
    {
        @Override
        public void confirmed(Expression input)
        {
            // Store the new value
            value = input;
            
            // Show the new value
            ((TextView) getView().findViewById(R.id.text_substitute_for)).setText(valueToString());
        }
    }
}
