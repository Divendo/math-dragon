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
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.ShowcaseViewDialog;
import org.teaminfty.math_dragon.view.ShowcaseViewDialogs;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard.OnConfirmListener;
import org.teaminfty.math_dragon.view.math.Empty;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.operation.Integral;
import org.teaminfty.math_dragon.view.math.operation.binary.Derivative;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ActionViewTarget;
import com.espian.showcaseview.targets.PointTarget;
import com.espian.showcaseview.targets.ViewTarget;

public class FragmentMainScreen extends Fragment implements Tutorial
{
    /** The {@link MathView} in this fragment */
    private MathView mathView = null;

    /** The undo/redo history, each state is stored as a XML document */
    private ArrayList<Document> history = new ArrayList<Document>();

    /** The current position in the history */
    private int historyPos = 0;

    private boolean isShowingDialog = false;
    
    private ShowcaseViewDialog currentDialog;

    public static final int TUTORIAL_ID = 0;

    public static final String TUTORIAL_TAG = "tut_dlg";

    private void tutorial()
    {
        // TODO werken met savedInstanceState
        final Database db = new Database(getActivity());
        Database.TutorialState state = db.getTutorialState(FragmentMainScreen.TUTORIAL_ID);
        
        System.out.println("tutInProg:"+state.tutInProg);
        System.out.println("showTutDlg:"+state.showTutDlg);
        if(!state.tutInProg && state.showTutDlg && !isShowingDialog)
        {
            FragmentTutorialDialog dg = new FragmentTutorialDialog(R.string.tutorial_dialog_title, R.string.tutorial_dialog_msg);

            dg.setOnConfirmListener(new OnTutorialConfirmListener());
            dg.show(getFragmentManager(), TUTORIAL_TAG);

            isShowingDialog = true;
        }
        else if(state.tutInProg)
        {
            continueTutorial();
        }
        db.close();
    }

    final class OnTutorialConfirmListener implements FragmentTutorialDialog.OnConfirmListener
    {
        @Override
        public void confirm()
        {
            continueTutorial();
        }
    }

    private void continueTutorial()
    {
        final Database db = new Database(getActivity());

        Database.TutorialState state = db.getTutorialState(TUTORIAL_ID);
        state.tutInProg = true;
        db.saveTutorialState(state);

        
        // set the correct states for all the other tutorials
        Database.TutorialState[] states = new Database.TutorialState[] {
                db.getTutorialState(FragmentSubstitute.TUTORIAL_ID),
                db.getTutorialState(FragmentSubstitutionEditor.TUTORIAL_ID),
                db.getTutorialState(FragmentKeyboard.TUTORIAL_ID),
                db.getTutorialState(FragmentSaveLoad.TUTORIAL_ID)};

        for(Database.TutorialState s : states)
        {
            s.tutInProg = true;
            db.saveTutorialState(s);
        }
        
        
        // we programatically open and close the drawer for demonstration, so we're going to need it here.
        final DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        
        ShowcaseViewDialog actionBar = new ShowcaseViewDialog(
                getActivity(),
                new ActionViewTarget(getActivity(), ActionViewTarget.Type.HOME),
                R.string.tutorial_main_title, R.string.tutorial_main_drawer);
        
        setCurrentShowcaseDialog(actionBar);
        actionBar.show();
        final ShowcaseViewDialog swipeToOpen = new ShowcaseViewDialog(getActivity(),
                new PointTarget(0, 200), R.string.tutorial_main_title,
                R.string.tutorial_main_drawer_slide, new ShowcaseViewDialog.Gesture(0, 0, 200, 0));
        
        final ShowcaseViewDialogs showcases = new ShowcaseViewDialogs(this);
        
        
        
        // make sure the drawer is closed before we explain the swipe to open gesture.
        actionBar.setOnShowcaseEventListener(new OnShowcaseEventListener()
        {
            
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            { }
            
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                if(drawerLayout != null)
                {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    swipeToOpen.show();
                    setCurrentShowcaseDialog(swipeToOpen);
                }
                else
                    showcases.show();
            }
            
            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            { }
        });
        swipeToOpen.setOnShowcaseEventListener(new OnShowcaseEventListener()
        {
            
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            { }

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                drawerLayout.closeDrawer(Gravity.LEFT);
                showcases.show();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            {}
        });
        
        showcases.addViews(new ShowcaseViewDialog[]
        {
                new ShowcaseViewDialog(getActivity(), new ViewTarget(getActivity().findViewById(R.id.btn_derivative)),
                        R.string.tutorial_main_title, R.string.tutorial_main_diff),
                new ShowcaseViewDialog(getActivity(), new ViewTarget(getActivity().findViewById(R.id.btn_substitute)),
                        R.string.tutorial_main_title, R.string.tutorial_main_subs),
                new ShowcaseViewDialog(getActivity(), new ViewTarget(getActivity().findViewById(R.id.btn_evaluate)),
                        R.string.tutorial_main_title, R.string.tutorial_main_eval),
                
        });
        showcases.setOnShowcaseAcknowledged(new ShowcaseViewDialogs.OnShowcaseAcknowledged()
        {
            @Override
            public void acknowledge()
            {
                Database.TutorialState state = db.getTutorialState(getTutorialId());
                state.tutInProg = false;
                state.showTutDlg = false;
                db.saveTutorialState(state);

                db.close();
            }

        });
        actionBar.show();
        
        
    }

    public static final String ABOUT_TAG = "about";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container,
                false);

        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setEventListener(new MathViewEventListener());

        if(savedInstanceState != null)
            mathView.setDefaultHeight(savedInstanceState.getInt(BUNDLE_MATH_VIEW_DEFAULT_HEIGHT));

        // Disable the evaluate buttons by default
        enableDisableEvalButtons(view, mathView.getExpression());

        if(savedInstanceState != null)
        {

            // Load the history from the bundle
            ArrayList<String> historyStrings = savedInstanceState
                    .getStringArrayList(BUNDLE_HISTORY);
            for(String xml : historyStrings)
            {
                try
                {
                    InputStream in = new ByteArrayInputStream(xml.getBytes());
                    Document doc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder().parse(in);
                    history.add(doc);
                }
                catch(SAXException e)
                { /* Ignore */}
                catch(IOException e)
                { /* Ignore */}
                catch(ParserConfigurationException e)
                { /* Ignore */}
            }

            // Set the history position (and load the MathObject)
            try
            {
                historyPos = Math.min(history.size() - 1,
                        savedInstanceState.getInt(BUNDLE_HISTORY_POS));
                mathView.setExpressionSilent(ExpressionXMLReader
                        .fromXML(history.get(historyPos)));

                // Enable the evaluate buttons (if necessary)
                enableDisableEvalButtons(view, mathView.getExpression());
            }
            catch(ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Set the keyboard listener
            if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null
                    && savedInstanceState.getBundle(BUNDLE_KEYBOARD_LISTENER) != null)
            {
                FragmentKeyboard.OnConfirmListener listener = mathView
                        .keyboardListenerFromBundle(savedInstanceState
                                .getBundle(BUNDLE_KEYBOARD_LISTENER));
                ((FragmentKeyboard) getFragmentManager().findFragmentByTag(
                        KEYBOARD_TAG)).setOnConfirmListener(listener);
            }

            if(getFragmentManager().findFragmentByTag(TUTORIAL_TAG) != null)
            {

            }
        }
        else
        {
            // Set the first history entry (i.e. an empty element)
            try
            {
                Document doc = Expression.createXMLDocument();
                mathView.getExpression().writeToXML(doc,
                        doc.getDocumentElement());
                history.add(doc);
                historyPos = history.size() - 1;
            }
            catch(ParserConfigurationException e)
            { /* Ignore */}
        }

        // Disable the undo and redo buttons and set their click listeners
        ImageButton btnUndo = (ImageButton) view.findViewById(R.id.btn_undo);
        ImageButton btnRedo = (ImageButton) view.findViewById(R.id.btn_redo);
        view.findViewById(R.id.btn_undo).setEnabled(historyPos > 0);
        view.findViewById(R.id.btn_redo).setEnabled(
                historyPos < history.size() - 1);
        btnUndo.setOnClickListener(new UndoRedoClickListener());
        btnRedo.setOnClickListener(new UndoRedoClickListener());

        view.findViewById(R.id.btn_help).setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                // If a about dialog is already shown, stop here
                if(getFragmentManager().findFragmentByTag(ABOUT_TAG) != null)
                    return;
                
                // Create and show the about dialog
                final FragmentAbout fragmentAbout = new FragmentAbout();
                fragmentAbout.setListener(new View.OnClickListener()
                {
                    
                    @Override
                    public void onClick(View v)
                    {
                        Database db = new Database(getActivity());
                        Database.TutorialState state = db.getTutorialState(getTutorialId());
                        state.showTutDlg = true;
                        state.tutInProg = false;
                        db.saveTutorialState(state);
                        db.close();
                        fragmentAbout.dismiss();
                        continueTutorial();
                    }
                });
                fragmentAbout.show(getFragmentManager(), ABOUT_TAG);    
            }
        });

        if(savedInstanceState != null)
        {
            isShowingDialog = savedInstanceState.getBoolean("isShowingDialog");

            FragmentTutorialDialog dg;
            if((dg = (FragmentTutorialDialog) getFragmentManager().findFragmentByTag(TUTORIAL_TAG)) != null)
                dg.setOnConfirmListener(new OnTutorialConfirmListener());
        }

        // The click listener for the favourites button
        view.findViewById(R.id.btn_favourites).setOnClickListener(
                new FavouritesClickListener());

        // Set the favourites listener (if necessary)
        if(getFragmentManager().findFragmentByTag(FAVOURITES_TAG) != null)
            ((FragmentSaveLoad) getFragmentManager().findFragmentByTag(
                    FAVOURITES_TAG))
                    .setFormulaLoadListener(new FavouriteLoadedListener());

        // Set the click listener for the derivative and integrate buttons
        DerivativeIntegrateButtonListener derivativeIntegrateButtonListener = new DerivativeIntegrateButtonListener();
        view.findViewById(R.id.btn_derivative).setOnClickListener(
                derivativeIntegrateButtonListener);
        view.findViewById(R.id.btn_integrate).setOnClickListener(
                derivativeIntegrateButtonListener);
        // Listen for events from the MathView

        // Return the view
        return view;
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
        System.out.println("onStop");
        super.onStop();
        if (getCurrentShowcaseDialog() != null)
        {
            getCurrentShowcaseDialog().dismiss();
        }
    }

    /**
     * An integer in the state bundle that indicates the current history
     * position
     */
    private static final String BUNDLE_HISTORY_POS = "history_pos";

    /** An ArrayList of strings containing all expressions in the history */
    private static final String BUNDLE_HISTORY = "history";

    /**
     * A bundle containing the current keyboard listener (if the keyboard is
     * active)
     */
    private static final String BUNDLE_KEYBOARD_LISTENER = "keyboard_listener";

    /** An integer containing the default height of the MathView */
    private static final String BUNDLE_MATH_VIEW_DEFAULT_HEIGHT = "math_view_default_height";
    
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
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                transformer.transform(new DOMSource(doc), new StreamResult(
                        byteStream));
                historyStrings.add(byteStream.toString());
            }
            catch(TransformerConfigurationException e)
            { /* Never thrown, ignore */}
            catch(TransformerFactoryConfigurationError e)
            { /* Ignore */}
            catch(TransformerException e)
            { /* Ignore */}
        }
        outState.putStringArrayList(BUNDLE_HISTORY, historyStrings);

        // Save the history position
        outState.putInt(BUNDLE_HISTORY_POS, historyPos);

        // Save the keyboard listener
        if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
        {
            FragmentKeyboard.OnConfirmListener listener = ((FragmentKeyboard) getFragmentManager()
                    .findFragmentByTag(KEYBOARD_TAG)).getOnConfirmListener();
            outState.putBundle(BUNDLE_KEYBOARD_LISTENER,
                    mathView.keyboardListenerToBundle(listener));
        }
        
        // Save the default height
        outState.putInt(BUNDLE_MATH_VIEW_DEFAULT_HEIGHT, mathView.getDefaultHeight());
    }

    /** Clears the current formula */
    public void clear()
    {
        if(mathView.getExpression() instanceof Empty)
            mathView.resetScroll();
        else
            mathView.setExpression(null);
        mathView.resetDefaultHeight();
        mathView.invalidate();
    }

    /**
     * Returns the current {@link Expression}
     * 
     * @return The current {@link Expression}
     */
    public Expression getExpression()
    {
        return mathView.getExpression();
    }

    /**
     * Enables or disables the undo/redo buttons according to the current
     * position in the history
     */
    private void refreshUndoRedoButtons()
    {
        getView().findViewById(R.id.btn_undo).setEnabled(historyPos > 0);
        getView().findViewById(R.id.btn_redo).setEnabled(
                historyPos < history.size() - 1);
    }

    /**
     * Goes to the given position in the history (if possible)
     * 
     * @param pos
     *        The position to go to
     */
    private void goToHistoryPos(int pos)
    {
        // Check if the position is valid
        if(pos < 0 || pos >= history.size())
            return;

        // Get the MathObject at the given history position
        try
        {
            mathView.setExpressionSilent(ExpressionXMLReader.fromXML(history
                    .get(pos)));
            historyPos = pos;

            // Enable / disable the evaluate buttons
            enableDisableEvalButtons(getView(), mathView.getExpression());
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        // Refresh the state of the undo/redo buttons
        refreshUndoRedoButtons();
    }

    /** Undo the last change (if possible) */
    public void undo()
    {
        goToHistoryPos(historyPos - 1);
    }

    /** Redo the last change (if possible) */
    public void redo()
    {
        goToHistoryPos(historyPos + 1);
    }

    /**
     * Enables / disables the evaluate and approximate buttons
     * according to the current {@link Expression}
     * 
     * @param view
     *        The root view containing the buttons
     * @param expr
     *        The {@link Expression} that determines whether the buttons should
     *        be enabled or disabled
     */
    private void enableDisableEvalButtons(View view, Expression expr)
    {
        // Whether or not the expression is completed
        final boolean isCompleted = expr.isCompleted();

        // Enable / disable the buttons
        view.findViewById(R.id.btn_approximate).setEnabled(isCompleted);
        view.findViewById(R.id.btn_evaluate).setEnabled(isCompleted);
    }

    public ShowcaseViewDialog getCurrentShowcaseDialog()
    {
        return currentDialog;
    }

    public void setCurrentShowcaseDialog(ShowcaseViewDialog currentDialog)
    {
        this.currentDialog = currentDialog;
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

    /** The tag for the favourites dialog */
    private static final String FAVOURITES_TAG = "favourites";

    /** The click listener that handles clicks from the favourites button */
    private class FavouritesClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // If a favourites dialog is already shown, stop here
            if(getFragmentManager().findFragmentByTag(FAVOURITES_TAG) != null)
                return;

            // Create and show the favourites dialog
            FragmentSaveLoad fragmentSaveLoad = new FragmentSaveLoad();
            fragmentSaveLoad
                    .setFormulaLoadListener(new FavouriteLoadedListener());
            fragmentSaveLoad.setExpression(mathView.getExpression());
            fragmentSaveLoad.show(getFragmentManager(), FAVOURITES_TAG);
        }
    }

    /** Listener that handles load events from the favourites dialog */
    private class FavouriteLoadedListener implements
            FragmentSaveLoad.OnFormulaLoadListener
    {
        @Override
        public void loaded(Expression expression)
        {
            // Simply set the new expression
            mathView.setExpression(expression);
        }
    }

    /** Listener that handles derive / integrate button clicks */
    private class DerivativeIntegrateButtonListener implements
            View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // Determine the new expression
            Expression newExpr = null;
            if(v.getId() == R.id.btn_integrate)
                newExpr = new Integral(mathView.getExpression(),
                        Symbol.createVarSymbol('x'));
            else
                newExpr = new Derivative(mathView.getExpression(), Symbol.createVarSymbol('x'));
            
            // Set the parentheses
            newExpr = ParenthesesHelper.setParentheses(newExpr);
            
            // Set the new expression
            mathView.setExpression(newExpr);

            // Evaluate (by simulating an evaluate button press)
            getView().findViewById(R.id.btn_evaluate).performClick();
        }
    }


    @Override
    public int getTutorialId()
    {
        return TUTORIAL_ID;
    }
}
