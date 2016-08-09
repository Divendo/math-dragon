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
import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.model.Database.Formula;
import org.teaminfty.math_dragon.view.ShowcaseViewDialog;
import org.teaminfty.math_dragon.view.ShowcaseViewDialogs;
import org.teaminfty.math_dragon.view.TouchFeedbackVibrator;
import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentSaveLoad extends DialogFragment implements Tutorial
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
        
        // The return button of the keyboard
        ((TextView) view.findViewById(R.id.edit_name)).setOnEditorActionListener(new BtnReturnKeyListener());

        // Restore the confirmation listener
        if(savedInstanceState != null && getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG) != null)
        {
            // Create the listener
            FragmentWarningDialog.OnConfirmListener listener = null;
            if(savedInstanceState.getBoolean(CONFIRM_DELETE))
                listener = new ConfirmDeleteListener(savedInstanceState.getInt(CONFIRM_ID));
            else
                listener = new ConfirmOverwriteListener(savedInstanceState.getInt(CONFIRM_ID));
            
            // Set the listener
            ((FragmentWarningDialog) getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG)).setOnConfirmListener(listener);
        }
        
        // Restore the current expression
        if(savedInstanceState != null && savedInstanceState.getByteArray(CURRENT_EXPR) != null)
        {
            try
            {
                currExpr = ExpressionXMLReader.fromXML(savedInstanceState.getByteArray(CURRENT_EXPR));
            }
            catch(ParseException e)
            {
                // TODO Auto-generated catch block (when an error occurs during the conversion from the XML document to a MathObject)
                e.printStackTrace();
            }
        }
        // Return the content view
        
       
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        tutorial();
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
    
    /** A boolean containing whether the delete (<tt>true</tt>) or overwrite (<tt>false</tt>) confirm listener is set for the confirmation dialog (if present) */
    private static final String CONFIRM_DELETE = "confirm_delete";
    
    /** An int containing the ID of the confirm listener for the confirmation dialog (if present) */
    private static final String CONFIRM_ID = "confirm_id";
    
    /** A byte array containing the current expression as XML */
    private static final String CURRENT_EXPR = "curr_expr";

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store the confirmation listener
        if(getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG) != null)
        {
            // Get the dialog's listener
            FragmentWarningDialog.OnConfirmListener listener = ((FragmentWarningDialog) getFragmentManager().findFragmentByTag(CONFIRMATION_DLG_TAG)).getOnConfirmListener();
            
            // Store the type of the confirm listener
            outState.putBoolean(CONFIRM_DELETE, listener instanceof ConfirmDeleteListener);
            
            // Store the ID of the confirm listener
            outState.putInt(CONFIRM_ID, listener instanceof ConfirmDeleteListener ? ((ConfirmDeleteListener) listener).id : ((ConfirmOverwriteListener) listener).id);
        }
        
        // Store the current expression
        try
        {
            // Convert the MathObject to a XML document
            Document doc = Expression.createXMLDocument();
            currExpr.writeToXML(doc, doc.getDocumentElement());
            
            // Convert the XML document to a byte array and add it to the ContentValues instance
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
            outState.putByteArray(CURRENT_EXPR, byteStream.toByteArray());
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

    /** Listens for the return key of the keyboard */
    private class BtnReturnKeyListener implements TextView.OnEditorActionListener
    {
        @Override
        public boolean onEditorAction(TextView view, int actionID, KeyEvent event)
        {
            // Save the current expression
            Database db = new Database(getActivity());
            db.saveFormula(Database.INSERT_ID, ((TextView) getView().findViewById(R.id.edit_name)).getText().toString(), currExpr);
            db.close();
            
            // Close the dialog
            dismiss();
            
            // We always consume the event
            return true;
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
        public int id;
        
        /** Constructor
         * @param id The id of the formula */
        public ConfirmOverwriteListener(int id)
        { this.id = id; }
        
        @Override
        public void confirm()
        {
            // Save the current expression
            Database db = new Database(getActivity());
            db.saveFormula(id, null, currExpr);
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

            // Vibrate
            TouchFeedbackVibrator.longPressVibrate(getActivity());
            
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
        public int id;
        
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

    public static final int TUTORIAL_ID = 4;
    
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
            			R.string.tutorial_favs_title,
            			R.string.tutorial_favs_open),
        	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.edit_name), this),
        				R.string.tutorial_favs_title,
                        R.string.tutorial_favs_save_textbox),
            new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.btn_save), this),
                        R.string.tutorial_favs_title,
                        R.string.tutorial_favs_save_btn),
            new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.layout_formula_list), this),
            			R.string.tutorial_favs_title,
            			R.string.tutorial_favs_load),
        	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.layout_formula_list), this),
        				R.string.tutorial_favs_title,
        				R.string.tutorial_favs_overwrite),
        	new ShowcaseViewDialog(ctx, new ShowcaseViewDialog.DialogFragmentTarget(getView().findViewById(R.id.layout_formula_list), this),
            			R.string.tutorial_favs_title,
                		R.string.tutorial_favs_delete)
           

            
        });
        
        showcases.show();

    }
}
