package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.Database;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class FragmentTutorialDialog extends DialogFragment
{
    /** The ID of the title resource string */
    private int titleId = 0;
    
    /** The ID of the message resource string */
    private int msgId = 0;
    
    /** The {@link FragmentTutorialDialog#OnConfirmListener OnConfirmListener} */
    private OnConfirmListener onConfirmListener = null;
    
    /** Default constructor */
    public FragmentTutorialDialog() {}
    
    /** Constructor
     * @param title The ID of the title resource string
     * @param msg The ID of the message resource string */
    public FragmentTutorialDialog(int title, int msg)
    {
        titleId = title;
        msgId = msg;
    }

    /** Constructor, constructs the dialog as a question dialog with yes and no buttons
     * @param title The ID of the title resource string
     * @param msg The ID of the message resource string
     * @param listener The {@link FragmentTutorialDialog#OnConfirmListener OnConfirmListener} that listens for confirmation(i.e. the user clicks 'yes') */
    public FragmentTutorialDialog(int title, int msg, OnConfirmListener listener)
    {
        titleId = title;
        msgId = msg;
        onConfirmListener = listener;
    }
    
    /** A string containing the title text */
    private static final String TITLE_TEXT = "title_text_tut";
    
    /** A string containing the message text */
    private static final String MSG_TEXT = "msg_text_tut";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tut_dlg, container, false);
        
        // Set the title
        if(titleId != 0)
            ((TextView) view.findViewById(R.id.text_title)).setText(titleId);
        if(savedInstanceState != null && savedInstanceState.getString(TITLE_TEXT) != null)
            ((TextView) view.findViewById(R.id.text_title)).setText(savedInstanceState.getString(TITLE_TEXT));
        
        // Set the message
        if(msgId != 0)
            ((TextView) view.findViewById(R.id.text_msg)).setText(msgId);
        if(savedInstanceState != null && savedInstanceState.getString(MSG_TEXT) != null)
            ((TextView) view.findViewById(R.id.text_msg)).setText(savedInstanceState.getString(MSG_TEXT));
        
        final FragmentTutorialDialog self = this;
        // Set the listener for the ok and cancel buttons
        view.findViewById(R.id.btn_yes).setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                self.onConfirmListener.confirm();
                dismiss();
                 
            }
        });
        view.findViewById(R.id.btn_no).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(((CheckBox)view.findViewById(R.id.check_dont_show)).isChecked())
                {
                    Database db = new Database(getActivity());
                    Database.TutorialState state = db.getTutorialState(FragmentMainScreen.TUTORIAL_ID);
                    state.showTutDlg = false;
                    db.saveTutorialState(state);
                    db.close();
                }
                dismiss();
                
            }
        });
       
       
        
        // Return the content view
        return view;
    }
   
    public interface OnConfirmListener
    {
        public void confirm();
    }
   
    
    public void setOnConfirmListener(OnConfirmListener listener)
    { onConfirmListener = listener; }
    
    public OnConfirmListener getOnConfirmListener()
    { return onConfirmListener; }
    
    //TODO onconfirmlistener confirm(boolean, showTutorial, boolean dontAskAgain)
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store the title text
        outState.putString(TITLE_TEXT, ((TextView) getView().findViewById(R.id.text_title)).getText().toString());
        
        // Store the message text
        outState.putString(MSG_TEXT, ((TextView) getView().findViewById(R.id.text_msg)).getText().toString());        
    }
    
}
