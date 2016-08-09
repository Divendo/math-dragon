package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.view.ShowcaseViewDialog;
import org.teaminfty.math_dragon.view.ShowcaseViewDialogs;
import org.teaminfty.math_dragon.view.TouchFeedbackVibrator;
import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;

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
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentSubstitute extends DialogFragment implements Tutorial
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitute, container, false);
        
        // Load the substitutions
        ViewGroup subsLayout = (ViewGroup) view.findViewById(R.id.layout_substitute_list);
        Database db = new Database(getActivity());
        Database.Substitution[] substitutions = db.getAllSubstitutions();
        for(Database.Substitution sub : substitutions)
            setSubstitution(sub.name, sub.value, subsLayout);
        
        // The close button
        ((ImageButton) view.findViewById(R.id.btn_close)).setOnClickListener(new OnCloseBtnClickListener());
        
        // The add button
        view.findViewById(R.id.btn_add).setOnClickListener(new BtnAddClickListener());

        // Restore the confirmation listener
        if(savedInstanceState != null && getFragmentManager().findFragmentByTag(WARNING_TAG) != null)
        {
            // Create the listener
            DeleteSubstituteListener listener = new DeleteSubstituteListener(savedInstanceState.getChar(CONFIRM_VAR_NAME));
            
            // Set the listener
            ((FragmentWarningDialog) getFragmentManager().findFragmentByTag(WARNING_TAG)).setOnConfirmListener(listener);
        }
        
        // Restore the editor listener (if necessary)
        if(getFragmentManager().findFragmentByTag(EDITOR_TAG) != null)
            ((FragmentSubstitutionEditor) getFragmentManager().findFragmentByTag(EDITOR_TAG)).setOnConfirmListener(new SetSubstitutionListener());
        
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
            params.width = getResources().getDimensionPixelSize(R.dimen.evaluation_dlg_width);
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

    /** A char containing the variable name of the confirm listener for the confirmation dialog (if present) */
    private static final String CONFIRM_VAR_NAME = "var_name";

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store the confirmation listener
        if(getFragmentManager().findFragmentByTag(WARNING_TAG) != null)
        {
            // Get the dialog's listener
            DeleteSubstituteListener listener = (DeleteSubstituteListener) ((FragmentWarningDialog) getFragmentManager().findFragmentByTag(WARNING_TAG)).getOnConfirmListener();
            
            // Store the variable name of the confirm listener
            outState.putChar(CONFIRM_VAR_NAME, listener.varName);
        }
    }
    
    /** Gets the value as a string */
    private String valueToString(Expression value)
    {
        String str = value.toString();
        str = str.replace("(", "");
        str = str.replace(")", "");
        str = str.replace(" ", "");
        return str;
    }
    
    /** The prefix of the tag for all substitution views */
    private static final String SUBSTITUTION_TAG_PREFIX = "substitution_";
    
    /** Sets (or adds/removes) a substitution for the given variable.
     * @param var The variable name to set the substitution for
     * @param expr The new value of the variable (or <tt>null</tt> to remove the variable
     * @param root The View containing all substitution views */
    private void setSubstitution(char var, Expression expr, ViewGroup root)
    {
        // Check whether or not we're deleting the substitution view
        if(expr == null)
        {
            if(root.findViewWithTag(SUBSTITUTION_TAG_PREFIX + var) != null)
                root.removeView(root.findViewWithTag(SUBSTITUTION_TAG_PREFIX + var));
            return;
        }
        
        // The substitution view and its children will be stored in here
        View row = null;
        
        // Check if the substitution view already exists
        if(root.findViewWithTag(SUBSTITUTION_TAG_PREFIX + var) == null)
        {
            // Inflate a new row for the substitution
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            row = inflater.inflate(R.layout.substitute_row, null, false);
            row.setTag(SUBSTITUTION_TAG_PREFIX + var);
            
            // Set the click listeners
            RowClickListener rowClickListener = new RowClickListener(var);
            row.setOnClickListener(rowClickListener);
            row.setOnLongClickListener(rowClickListener);
            row.findViewById(R.id.btn_delete_substitution).setOnClickListener(rowClickListener);
            
            // Insert the row into the layout
            boolean inserted = false;
            for(int i = 0; i < root.getChildCount(); ++i)
            {
                final char currVar = ((String) root.getChildAt(i).getTag()).substring(SUBSTITUTION_TAG_PREFIX.length()).charAt(0);
                if(var < currVar)
                {
                    root.addView(row, i);
                    inserted = true;
                    break;
                }
            }
            if(!inserted)
                root.addView(row);
        }
        else
            row = root.findViewWithTag(SUBSTITUTION_TAG_PREFIX + var);
        
        // Get the TextViews
        TextView varName = (TextView) row.findViewById(R.id.text_var_name);
        TextView varVal = (TextView) row.findViewById(R.id.text_var_val);
        
        // Set the typeface for the TextViews
        varName.setTypeface(TypefaceHolder.dejavuSans);
        varVal.setTypeface(TypefaceHolder.dejavuSans);
        
        // Set the typeface for the TextViews
        varName.setTypeface(TypefaceHolder.dejavuSans);
        varVal.setTypeface(TypefaceHolder.dejavuSans);
        
        // Set the text for the TextViews
        varName.setText(Character.toString(var));
        varVal.setText(valueToString(expr));
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }

    /** The tag for the substitution editor fragment */
    private static final String EDITOR_TAG = "substitution_editor";

    /** The tag for the warning dialog fragment */
    private static final String WARNING_TAG = "warning_dlg";

    public static final int TUTORIAL_ID = 1;
    
    private class OnCloseBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        { dismiss(); }
    }
    
    private class BtnAddClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        {
            // If an editor is already shown, stop here
            if(getFragmentManager().findFragmentByTag(EDITOR_TAG) != null)
                return;
            
            // Create an editor
            FragmentSubstitutionEditor editor = new FragmentSubstitutionEditor();
            
            // Set the listener
            editor.setOnConfirmListener(new SetSubstitutionListener());
            
            // Show the editor
            editor.show(getFragmentManager(), EDITOR_TAG);
        }
    }
    
    private class SetSubstitutionListener implements FragmentSubstitutionEditor.OnConfirmListener
    {
        @Override
        public void confirmed(char varName, Expression expr)
        {
            // Save the substitution to the database
            Database db = new Database(getActivity());
            db.saveSubstitution(new Database.Substitution(varName, expr));
            db.close();
            
            // Update the interface
            setSubstitution(varName, expr, (ViewGroup) getView().findViewById(R.id.layout_substitute_list));
        }
    }
    
    private class DeleteSubstituteListener implements FragmentWarningDialog.OnConfirmListener
    {
        /** The variable that is to be deleted */
        public char varName = 'a';
        
        /** Constructor
         * @param name The variable that is to be deleted */
        public DeleteSubstituteListener(char name)
        { varName = name; }

        @Override
        public void confirm()
        {
            // Delete the substitution from the database
            Database db = new Database(getActivity());
            db.saveSubstitution(new Database.Substitution(varName));
            db.close();
            
            // Update the interface
            setSubstitution(varName, null, (ViewGroup) getView().findViewById(R.id.layout_substitute_list));
        }
        
    }
    
    private class RowClickListener implements View.OnClickListener, View.OnLongClickListener
    {
        /** The name of the variable that is to be substituted */
        private char varName;
        
        /** Constructor
         * @param name The name of the variable that is to be substituted */
        public RowClickListener(char name)
        { varName = name; }
        
        @Override
        public void onClick(View view)
        {
            // This onclick listener can handle the onclick event of both the row and the delete button
            // In case the delete button is clicked we simply display the confirmation dialog, otherwise we start editing the substitution
            if(view.getId() == R.id.btn_delete_substitution)
            {
                // Delete the substitution (if the user confirms to do so)
                confirmSubstitutionDeletion();

                // We're done
                return;
            }

            // If an editor is already shown, stop here
            if(getFragmentManager().findFragmentByTag(EDITOR_TAG) != null)
                return;
            
            // Create an editor
            FragmentSubstitutionEditor editor = new FragmentSubstitutionEditor();
            
            // Set the listener
            editor.setOnConfirmListener(new SetSubstitutionListener());
            
            // Set the initial value
            Database db = new Database(getActivity());
            editor.initVarName(varName);
            if(db.substitutionExists(varName))
                editor.initValue(db.getSubstitution(varName).value);
            db.close();
            
            // Show the editor
            editor.show(getFragmentManager(), EDITOR_TAG);
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Vibrate
            TouchFeedbackVibrator.longPressVibrate(getActivity());

            // Delete the substitution (if the user confirms to do so)
            confirmSubstitutionDeletion();
            
            // We've consumed the event
            return true;
        }

        /** Displays a warning dialog that deletes the substitution if the user confirms it. */
        private void confirmSubstitutionDeletion()
        {
            // Only display a warning dialog if none is shown
            if(getFragmentManager().findFragmentByTag(WARNING_TAG) == null)
            {
                FragmentWarningDialog dlg = new FragmentWarningDialog(R.string.delete_substitution, R.string.sure_to_delete_substitution, new DeleteSubstituteListener(varName));
                dlg.show(getFragmentManager(), WARNING_TAG);
            }
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
            	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.titleRow), this),
            			R.string.tutorial_subs_title,
                		R.string.tutorial_subs_open),
            	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.btn_add), this),
            			R.string.tutorial_subs_title,
                		R.string.tutorial_subs_add)
        });
        
        showcases.show();

    }
}
