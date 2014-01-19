package org.teaminfty.math_dragon.view.fragments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
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
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard.OnConfirmListener;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.ShowcaseViews.OnShowcaseAcknowledged;
import com.espian.showcaseview.targets.ActionViewTarget;
import com.espian.showcaseview.targets.PointTarget;

public class FragmentMainScreen extends Fragment
{
    /** The {@link MathView} in this fragment */
    private MathView mathView = null;
    
    /** The undo/redo history, each state is stored as a XML document */
    private ArrayList<Document> history = new ArrayList<Document>();
    
    /** The current position in the history */
    private int historyPos = 0;
    

    private boolean isShowingDialog = false;
    
    
    public static final int TUTORIAL_ID = 0;
    private void tutorial()
    {
        //TODO werken met savedInstanceState
        final Database db = new Database(getActivity());
        try
        {
            Database.TutorialState state = db.getTutorialState(TUTORIAL_ID);
            
            System.out.println("SHOW_TUT_DG:"+ state.showTutDlg);
   
            
            
            if(!state.tutInProg && state.showTutDlg && !isShowingDialog)
            {
                
                FragmentTutorialDialog dg = new FragmentTutorialDialog(
                        R.string.app_name, R.string.app_name,
                        new FragmentTutorialDialog.OnConfirmListener()
                        {

                            @Override
                            public void confirm()
                            {
                                
                                System.out.println("IK BEN TOCH ECHT GECONFIRMEERD");
                                
                                
                                //TODO andere tutInProgs inzetten
                               // savedInstanceState.putBoolean("isShowingDialog", false);
                                continueTutorial(db);
                            }
                        });
                dg.show(getFragmentManager(), "tut_dlg");
                //savedInstanceState.putBoolean("isShowingDialog", true);
                isShowingDialog = true;
            }

        }
        // make sure the database is closed.
        finally
        {
            db.close();
        }

    }
    
    private void continueTutorial(final Database db)
    {

        final ShowcaseView actionBar, swipeToOpen, editExpr, ops, evaluate;

        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();

        co.shotType = ShowcaseView.TYPE_ONE_SHOT;

        final DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);

        actionBar = ShowcaseView.insertShowcaseView(new ActionViewTarget(getActivity(),
                ActionViewTarget.Type.HOME), getActivity(),
                R.string.tutorial_fun_op_title, R.string.tutorial_fun_op_msg,
                co);
        swipeToOpen = ShowcaseView.insertShowcaseView(new PointTarget(0, 300),
                getActivity(), R.string.tutorial_fun_op_title,
                R.string.tutorial_fun_op_drag_msg);

        swipeToOpen.hide();

        actionBar.setOnShowcaseEventListener(new OnShowcaseEventListener()
        {

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                swipeToOpen.show();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            {}

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            {}

        });

        final ShowcaseViews views = new ShowcaseViews(getActivity(),
                new OnShowcaseAcknowledged()
                {

                    @Override
                    public void onShowCaseAcknowledged(ShowcaseView showcaseView)
                    {
                        Database.TutorialState state = new Database.TutorialState(
                                TUTORIAL_ID, false, false);
                        db.saveTutorialState(state);
                        db.close();
                    }
                });

        views.addView(new ShowcaseViews.ItemViewProperties(R.id.btn_undo,
                R.string.tutorial_undo_redo_title,
                R.string.tutorial_undo_redo_msg));

        views.addView(new ShowcaseViews.ItemViewProperties(R.id.btn_evaluate,
                R.string.tutorial_eval_title, R.string.tutorial_eval_msg));

        views.addView(new ShowcaseViews.ItemViewProperties(R.id.btn_substitute,
                R.string.tutorial_subst_title, R.string.tutorial_subst_msg1));
        
        swipeToOpen.setOnShowcaseEventListener(new OnShowcaseEventListener()
        {
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            {
                swipeToOpen.animateGesture(-40, 0, 200, 0);
            }

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                drawerLayout.closeDrawer(Gravity.LEFT);
                views.show();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            {}

        });

    }	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
    
		// Listen for events from the MathView
        
        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setEventListener(new MathViewEventListener());
        
        // Disable the evaluate buttons by default
        enableDisableEvalButtons(view, mathView.getExpression());
        
        if(savedInstanceState != null)
        {
            // Load the history from the bundle
            ArrayList<String> historyStrings = savedInstanceState.getStringArrayList(BUNDLE_HISTORY);
            for(String xml : historyStrings)
            {
                try
                {
                    InputStream in = new ByteArrayInputStream(xml.getBytes());
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                    history.add(doc);
                }
                catch(SAXException e)
                { /* Ignore */ }
                catch(IOException e)
                { /* Ignore */ }
                catch(ParserConfigurationException e)
                { /* Ignore */ }
            }
            
            // Set the history position (and load the MathObject)
            try
            {
                historyPos = Math.min(history.size() - 1, savedInstanceState.getInt(BUNDLE_HISTORY_POS));
                mathView.setExpressionSilent(ExpressionXMLReader.fromXML(history.get(historyPos)));
                
                // Enable the evaluate buttons (if necessary)
                enableDisableEvalButtons(view, mathView.getExpression());
            }
            catch(ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // Set the keyboard listener
            if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null && savedInstanceState.getBundle(BUNDLE_KEYBOARD_LISTENER) != null)
            {
                FragmentKeyboard.OnConfirmListener listener = mathView.keyboardListenerFromBundle(savedInstanceState.getBundle(BUNDLE_KEYBOARD_LISTENER));
                ((FragmentKeyboard) getFragmentManager().findFragmentByTag(KEYBOARD_TAG)).setOnConfirmListener(listener);
            }
        }
        else
        {
            // Set the first history entry (i.e. an empty element)
            try
            {
                Document doc = Expression.createXMLDocument();
                mathView.getExpression().writeToXML(doc, doc.getDocumentElement());
                history.add(doc);
                historyPos = history.size() - 1; 
            }
            catch(ParserConfigurationException e)
            { /* Ignore */ }
        }
        
        // Disable the undo and redo buttons and set their click listeners
        ImageButton btnUndo = (ImageButton) view.findViewById(R.id.btn_undo);
        ImageButton btnRedo = (ImageButton) view.findViewById(R.id.btn_redo);
        
        ImageButton btnHep = (ImageButton) view.findViewById(R.id.btn_help);
        view.findViewById(R.id.btn_undo).setEnabled(historyPos > 0);
        view.findViewById(R.id.btn_redo).setEnabled(historyPos < history.size() - 1);
        btnUndo.setOnClickListener(new UndoRedoClickListener());
        btnRedo.setOnClickListener(new UndoRedoClickListener());

        
        
    
        
        
        if (savedInstanceState != null)
        {
            isShowingDialog = savedInstanceState.getBoolean("isShowingDialog");
        }
        
        // Return the view
        return view;
    }
    
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        tutorial();
    }


    /** An integer in the state bundle that indicates the current history position */
    private static final String BUNDLE_HISTORY_POS = "history_pos";
    
    /** An ArrayList of strings containing all expressions in the history */
    private static final String BUNDLE_HISTORY = "history";
    
    /** A bundle containing the current keyboard listener (if the keyboard is active) */
    private static final String BUNDLE_KEYBOARD_LISTENER = "keyboard_listener";
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        
        
        outState.putBoolean("isShowingDialog", isShowingDialog);
        // Save the history
        ArrayList<String> historyStrings = new ArrayList<String>();
        for(Document doc : history)
        {
            try
            {
                // Convert the XML document to a string and add it to the list
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
                historyStrings.add(byteStream.toString());
            }
            catch(TransformerConfigurationException e)
            { /* Never thrown, ignore */ }
            catch(TransformerFactoryConfigurationError e)
            { /* Ignore */ }
            catch(TransformerException e)
            { /* Ignore */ }
        }
        outState.putStringArrayList(BUNDLE_HISTORY, historyStrings);
        
        // Save the history position
        outState.putInt(BUNDLE_HISTORY_POS, historyPos);
        
        // Save the keyboard listener
        if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
        {
            FragmentKeyboard.OnConfirmListener listener = ((FragmentKeyboard) getFragmentManager().findFragmentByTag(KEYBOARD_TAG)).getOnConfirmListener();
            outState.putBundle(BUNDLE_KEYBOARD_LISTENER, mathView.keyboardListenerToBundle(listener));
        }
    }

    /** Clears the current formula */
    public void clear()
    {
        if(mathView.getExpression() instanceof Empty)
            mathView.resetScroll();
        else
        {
            mathView.setExpression(null);
            mathView.invalidate();
        }
    }
    
    /** Returns the current {@link Expression}
     * @return The current {@link Expression} */
    public Expression getMathObject()
    {
        return mathView.getExpression();
    }
    
    /** Enables or disables the undo/redo buttons according to the current position in the history */
    private void refreshUndoRedoButtons()
    {
        getView().findViewById(R.id.btn_undo).setEnabled(historyPos > 0);
        getView().findViewById(R.id.btn_redo).setEnabled(historyPos < history.size() - 1);
    }
    
    /** Goes to the given position in the history (if possible)
     * @param pos The position to go to */
    private void goToHistoryPos(int pos)
    {
        // Check if the position is valid
        if(pos < 0 || pos >= history.size())
            return;
        
        // Get the MathObject at the given history position
        try
        {
            mathView.setExpressionSilent(ExpressionXMLReader.fromXML(history.get(pos)));
            historyPos = pos;

            // Enable / disable the evaluate buttons
            enableDisableEvalButtons(getView(), mathView.getExpression());
        }
        catch(ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Refresh the state of the undo/redo buttons
        refreshUndoRedoButtons();
    }
    
    /** Undo the last change (if possible) */
    public void undo()
    { goToHistoryPos(historyPos - 1); }

    /** Redo the last change (if possible) */
    public void redo()
    { goToHistoryPos(historyPos + 1); }
    
    /** Enables / disables the evaluate, approximate and Wolfram|Alpha buttons according to the current {@link Expression}
     * @param view The root view containing the buttons
     * @param expr The {@link Expression} that determines whether the buttons should be enabled or disabled */
    private void enableDisableEvalButtons(View view, Expression expr)
    {
        // Whether or not the expression is completed
        final boolean isCompleted = expr.isCompleted();
        
        // Enable / disable the buttons
        view.findViewById(R.id.btn_wolfram).setEnabled(isCompleted);
        view.findViewById(R.id.btn_approximate).setEnabled(isCompleted);
        view.findViewById(R.id.btn_evaluate).setEnabled(isCompleted);
    }
    
    /** The tag for the keyboard fragment */
    private static final String KEYBOARD_TAG = "keyboard";
    
    /** The tag for the warning dialog fragment */
    private static final String WARNING_DLG_TAG = "warning_dlg";
    
    /** We'll want to listen for events from the {@link MathView} */
    private class MathViewEventListener implements MathView.OnEventListener
    {
        @Override
        public void changed(Expression expression)
        {
            // Enable / disable the evaluate buttons
            enableDisableEvalButtons(getView(), expression);
            
            // Remove the history from the current position
            if(historyPos + 1 < history.size())
                history.subList(historyPos + 1, history.size()).clear();

            // Add the current MathObject to the history
            try
            {
                Document doc = Expression.createXMLDocument();
                expression.writeToXML(doc, doc.getDocumentElement());
                history.add(doc);
                historyPos = history.size() - 1; 
            }
            catch(ParserConfigurationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // Refresh the state of the undo/redo buttons
            refreshUndoRedoButtons();
        }
        
        @Override
        public void showKeyboard(Expression expr, OnConfirmListener listener)
        {
            // If a keyboard is already shown, stop here
            if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
                return;
            
            // Create a keyboard
            FragmentKeyboard fragmentKeyboard = new FragmentKeyboard();
            
            // Set the listener and the math symbol
            fragmentKeyboard.setOnConfirmListener(listener);
            fragmentKeyboard.setExpression(expr);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), KEYBOARD_TAG);
        }

        @Override
        public void showWarning(int title, int msg)
        {
            FragmentWarningDialog warningDlg = new FragmentWarningDialog(title, msg);
            warningDlg.show(getFragmentManager(), WARNING_DLG_TAG);
        }
    }
    
    /** The click listener that handles clicks from the undo/redo buttons */
    private class UndoRedoClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.btn_undo)
                undo();
            else
                redo();
        }
    }
}
