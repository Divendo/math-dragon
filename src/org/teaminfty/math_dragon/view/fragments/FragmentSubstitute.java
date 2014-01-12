package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.view.TypefaceHolder;
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
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentSubstitute extends DialogFragment
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
    
    /** The prefix of the tag for all substitution views */
    private static final String SUBSTITUTION_TAG_PREFIX = "substitution_";
    
    /** Sets (or adds/removes) a substitution for the given variable.
     * @param var The variable name to set the substitution for
     * @param symbol The new value of the variable (or <tt>null</tt> to remove the variable
     * @param root The View containing all substitution views */
    private void setSubstitution(char var, Symbol symbol, ViewGroup root)
    {
        // Check whether or not we're deleting the substitution view
        if(symbol == null)
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
        
        // Set the text for the TextViews
        final String symbolStr = symbol.toString();
        varName.setText(Character.toString(var));
        varVal.setText(symbolStr.substring(1, symbolStr.length() - 1));
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        //mathView = null;
    }
    
    private class OnCloseBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        { dismiss(); }
    }
}
