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
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard.OnConfirmListener;
import org.teaminfty.math_dragon.view.math.MathFactory;
import org.teaminfty.math_dragon.view.math.MathSymbol;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentMainScreen extends Fragment
{
    /** The {@link MathView} in this fragment */
    private MathView mathView = null;
    
    /** The undo/redo history, each state is stored as a XML document */
    private ArrayList<Document> history = new ArrayList<Document>();
    
    /** The current position in the history */
    private int historyPos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        
        // Listen for events from the MathView
        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setOnShowKeyboardListener(new ShowKeyboardListener());
        mathView.setOnMathObjectChangeListener(new MathObjectChangeListener());
        
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
                mathView.setMathObjectSilent(MathFactory.fromXML(history.get(historyPos)));
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
                Document doc = MathObject.createXMLDocument();
                mathView.getMathObject().writeToXML(doc, doc.getDocumentElement());
                history.add(doc);
                historyPos = history.size() - 1; 
            }
            catch(ParserConfigurationException e)
            { /* Ignore */ }
        }
        
        // Disable the undo and redo buttons and set their click listeners
        ImageButton btnUndo = (ImageButton) view.findViewById(R.id.btn_undo);
        ImageButton btnRedo = (ImageButton) view.findViewById(R.id.btn_redo);
        view.findViewById(R.id.btn_undo).setEnabled(historyPos > 0);
        view.findViewById(R.id.btn_redo).setEnabled(historyPos < history.size() - 1);
        btnUndo.setOnClickListener(new UndoRedoClickListener());
        btnRedo.setOnClickListener(new UndoRedoClickListener());
        
        // Return the view
        return view;
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
        if(mathView.getMathObject() instanceof MathObjectEmpty)
            mathView.resetScroll();
        else
        {
            mathView.setMathObject(null);
            mathView.invalidate();
        }
    }
    
    /** Returns the current {@link MathObject}
     * @return The current {@link MathObject} */
    public MathObject getMathObject()
    {
        return mathView.getMathObject();
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
            mathView.setMathObjectSilent(MathFactory.fromXML(history.get(pos)));
            historyPos = pos;
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
    
    /** The tag of the keyboard fragment */
    private static final String KEYBOARD_TAG = "keyboard";
    
    /** We'll want to listen for keyboard show requests from the {@link MathView} */
    private class ShowKeyboardListener implements MathView.OnShowKeyboardListener
    {
        @Override
        public void showKeyboard(MathSymbol mathConstant, OnConfirmListener listener)
        {
            // If a keyboard is already shown, stop here
            if(getFragmentManager().findFragmentByTag(KEYBOARD_TAG) != null)
                return;
            
            // Create a keyboard
            FragmentKeyboard fragmentKeyboard = new FragmentKeyboard();
            
            // Set the listener and the math symbol
            fragmentKeyboard.setOnConfirmListener(listener);
            fragmentKeyboard.setMathSymbol(mathConstant);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), KEYBOARD_TAG);
        }
    }
    
    /** We'll want to listen for MathObject change events */
    private class MathObjectChangeListener implements MathView.OnMathObjectChangeListener
    {
        @Override
        public void changed(MathObject mathObject)
        {
            // Remove the history from the current position
            if(historyPos + 1 < history.size())
                history.subList(historyPos + 1, history.size()).clear();

            // Add the current MathObject to the history
            try
            {
                Document doc = MathObject.createXMLDocument();
                mathObject.writeToXML(doc, doc.getDocumentElement());
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
