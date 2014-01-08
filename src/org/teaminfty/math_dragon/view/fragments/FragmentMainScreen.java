package org.teaminfty.math_dragon.view.fragments;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.fragments.FragmentKeyboard.OnConfirmListener;
import org.teaminfty.math_dragon.view.math.MathFactory;
import org.teaminfty.math_dragon.view.math.MathSymbol;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathObjectEmpty;
import org.w3c.dom.Document;
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
        
        // Disable the undo and redo buttons and set their click listeners
        ImageButton btnUndo = (ImageButton) view.findViewById(R.id.btn_undo);
        ImageButton btnRedo = (ImageButton) view.findViewById(R.id.btn_redo);
        btnUndo.setEnabled(false);
        btnRedo.setEnabled(false);
        btnUndo.setOnClickListener(new UndoRedoClickListener());
        btnRedo.setOnClickListener(new UndoRedoClickListener());
        
        // Listen for events from the MathView
        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setOnShowKeyboardListener(new ShowKeyboardListener());
        mathView.setOnMathObjectChangeListener(new MathObjectChangeListener());
        
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
        
        // Return the view
        return view;
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
    
    /** We'll want to listen for keyboard show requests from the {@link MathView} */
    private class ShowKeyboardListener implements MathView.OnShowKeyboardListener
    {
        @Override
        public void showKeyboard(MathSymbol mathConstant, OnConfirmListener listener)
        {
            // Create a keyboard
            FragmentKeyboard fragmentKeyboard = new FragmentKeyboard();
            
            // Set the listener and the math symbol
            fragmentKeyboard.setOnConfirmListener(listener);
            fragmentKeyboard.setMathSymbol(mathConstant);
            
            // Show the keyboard
            fragmentKeyboard.show(getFragmentManager(), "keyboard");
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
