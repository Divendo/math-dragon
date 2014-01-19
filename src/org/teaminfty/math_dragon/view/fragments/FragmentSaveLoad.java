package org.teaminfty.math_dragon.view.fragments;

import java.util.ArrayList;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.model.Database.Formula;
import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;

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
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentSaveLoad extends DialogFragment
{
    /** The current expression */
    private Expression currExpr = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_load, container, false);
        
        // Load the formulas
        ViewGroup formulasLayout = (ViewGroup) view.findViewById(R.id.layout_formula_list);
        Database db = new Database(getActivity());
        ArrayList<Formula> forms = db.getAllFormulas();
        for(Database.Formula form : forms)
            setFormula(form, formulasLayout);
        
        // The close button
        ((ImageButton) view.findViewById(R.id.btn_close)).setOnClickListener(new OnCloseBtnClickListener());
        
        // The add button
        view.findViewById(R.id.btn_save).setOnClickListener(new BtnSaveClickListener());
        
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
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    /** Set the current {@link Expression}
     * @param expr The current {@link Expression} */
    public void setExpression(Expression expr)
    { currExpr = expr; }

    /** The prefix of the tag for all formula views */
    private static final String FORMULA_TAG_PREFIX = "formula_";
    
    /** Sets (or adds/updates) a formula in the list.
     * @param formula The formula to set in the list
     * @param root The ViewGroup containing all formula views */
    private void setFormula(Database.Formula formula, ViewGroup root)
    {
        // The formula view and its children will be stored in here
        View row = null;
        
        // Check if the substitution view already exists
        if(root.findViewWithTag(FORMULA_TAG_PREFIX + Integer.toString(formula.id)) == null)
        {
            // Inflate a new row for the substitution
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            row = inflater.inflate(R.layout.formula_row, null, false);
            row.setTag(FORMULA_TAG_PREFIX + Integer.toString(formula.id));
            
            // Set the click listeners
            row.findViewById(R.id.btn_overwrite).setOnClickListener(new OnOverwriteClickListener(formula.id));
            RowClickListener rowClickListener = new RowClickListener(formula.id);
            row.setOnClickListener(rowClickListener);
            row.setOnLongClickListener(rowClickListener);
            
            // Add the row to the layout
            root.addView(row);
        }
        else
            row = root.findViewWithTag(FORMULA_TAG_PREFIX + Integer.toString(formula.id));
        
        // Set the formula name
        TextView textName = (TextView) row.findViewById(R.id.text_formula_name);
        textName.setText(formula.name);
        textName.setTypeface(TypefaceHolder.dejavuSans);

        // Set the thumbnail
        ImageView imgThumb = (ImageView) row.findViewById(R.id.img_thumb);
        imgThumb.setImageBitmap(formula.bmp);
    }
    
    /** Refreshes the formula with the given ID
     * @param id The ID of the formula to refresh */
    private void refreshFormula(int id)
    {
        // Get the formula from the database
        Database db = new Database(getActivity());
        Database.Formula formula = db.getFormulaByID(id);
        db.close();
        
        // Update the formula's view
        if(formula != null)
            setFormula(formula, (ViewGroup) getView().findViewById(R.id.layout_formula_list));
    }
    
    /** Removes the formula with the given ID from the View's hierarchy
     * @param id The ID of the formula to remove */
    private void removeFormula(int id)
    {
        // Check if there is a View for the given ID
        if(getView().findViewWithTag(FORMULA_TAG_PREFIX + Integer.toString(id)) != null)
            ((ViewGroup) getView().findViewById(R.id.layout_formula_list)).removeView(getView().findViewWithTag(FORMULA_TAG_PREFIX + Integer.toString(id)));
    }

    /** A listener that can be implemented to listen for formula load events */
    public interface OnFormulaLoadListener
    {
        /** Called when a formula is loaded changed
         * @param expression The loaded formula as an {@link Expression} */
        public void loaded(Expression expression);
    }
    
    /** The current {@link OnFormulaLoadListener} */
    private OnFormulaLoadListener onFormulaLoadListener = null;
    
    /** Set the current {@link OnFormulaLoadListener}
     * @param listener The new {@link OnFormulaLoadListener} */
    public void setFormulaLoadListener(OnFormulaLoadListener listener)
    { onFormulaLoadListener = listener; }
    
    /** Notifies that a formula has been loaded
     * @param formula The loaded formula as an {@link Expression} */
    protected void formulaLoaded(Expression formula)
    {
        if(onFormulaLoadListener != null)
            onFormulaLoadListener.loaded(formula);
    }

    /** Listens for close dialog click events */
    private class OnCloseBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        { dismiss(); }
    }
    
    /** Listens for save click events */
    private class BtnSaveClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // Save the current expression
            Database db = new Database(getActivity());
            db.saveFormula(Database.INSERT_ID, ((TextView) getView().findViewById(R.id.edit_name)).getText().toString(), currExpr);
            db.close();
            
            // Close the dialog
            dismiss();
        }
    }
    
    /** The tag for the confirmation dialog */
    private static final String CONFIRMATION_DLG_TAG = "confirm";
    
    /** Listens for overwrite click events */
    private class OnOverwriteClickListener implements View.OnClickListener
    {
        /** The id of the formula */
        private int id;
        
        /** Constructor
         * @param id The id of the formula */
        public OnOverwriteClickListener(int id)
        { this.id = id; }
        
        @Override
        public void onClick(View btn)
        {
            // If a confirmation dialog is already shown, stop here
            if(getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG) != null)
                return;
            
            // Create and show a warning dialog
            FragmentWarningDialog dlg = new FragmentWarningDialog(R.string.sure_to_overwrite_title, R.string.sure_to_overwrite_msg, new ConfirmOverwriteListener(id));
            dlg.show(getFragmentManager(), CONFIRMATION_DLG_TAG);
        }
    }
    
    /** Listens for a confirmation to overwrite a certain formula */
    private class ConfirmOverwriteListener implements FragmentWarningDialog.OnConfirmListener
    {
        /** The id of the formula */
        private int id;
        
        /** Constructor
         * @param id The id of the formula */
        public ConfirmOverwriteListener(int id)
        { this.id = id; }
        
        @Override
        public void confirm()
        {
            // Save the current expression
            Database db = new Database(getActivity());
            db.saveFormula(id, ((TextView) getView().findViewById(R.id.edit_name)).getText().toString(), currExpr);
            db.close();
            
            // Refresh the formula with given id
            refreshFormula(id);
        }
    }
    
    /** Listens for (long) click events from every row */
    private class RowClickListener implements View.OnClickListener, View.OnLongClickListener
    {
        /** The id of the formula */
        private int id;
        
        /** Constructor
         * @param id The id of the formula */
        public RowClickListener(int id)
        { this.id = id; }
        
        @Override
        public void onClick(View v)
        {
            // Load the formula
            Database db = new Database(getActivity());
            Database.Formula form = db.getFormulaByID(id);
            db.close();
            
            // Notify any listeners of the loaded formula
            formulaLoaded(form.expression);
            
            // Close the dialog
            dismiss();
        }

        @Override
        public boolean onLongClick(View v)
        {
            // If a confirmation dialog is already shown, stop here
            if(getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG) != null)
                return true;
            
            // Create and show a warning dialog
            FragmentWarningDialog dlg = new FragmentWarningDialog(R.string.sure_to_delete_title, R.string.sure_to_delete_msg, new ConfirmDeleteListener(id));
            dlg.show(getFragmentManager(), CONFIRMATION_DLG_TAG);
            
            // We've consumed the event
            return true;
        }
    }
    
    /** Listens for a confirmation to delete a certain formula */
    private class ConfirmDeleteListener implements FragmentWarningDialog.OnConfirmListener
    {
        /** The id of the formula */
        private int id;
        
        /** Constructor
         * @param id The id of the formula */
        public ConfirmDeleteListener(int id)
        { this.id = id; }
        
        @Override
        public void confirm()
        {
            // Save the current expression
            Database db = new Database(getActivity());
            db.deleteFormula(id);
            db.close();
            
            // Remove the formula with given id
            removeFormula(id);
        }
    }
}
