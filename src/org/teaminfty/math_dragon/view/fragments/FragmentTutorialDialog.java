package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.MainActivity;
import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.Database;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.ShowcaseViews.OnShowcaseAcknowledged;
import com.espian.showcaseview.targets.ActionViewTarget;
import com.espian.showcaseview.targets.PointTarget;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tut_dlg, container, false);
        
        // Set the title
        if(titleId != 0)
            ((TextView) view.findViewById(R.id.text_title)).setText(titleId);
        
        // Set the message
        if(msgId != 0)
            ((TextView) view.findViewById(R.id.text_msg)).setText(msgId);
        
        // Set the listener for the ok and cancel buttons
        view.findViewById(R.id.btn_yes).setOnClickListener(new ButtonYesOnClickListener());
        view.findViewById(R.id.btn_no).setOnClickListener(new ButtonNoOnClickListener());
       
        CheckBox b = ((CheckBox)view.findViewById(R.id.check_dont_show));
        
        Database db = new Database(getActivity());
        b.setOnCheckedChangeListener(new DontShowChangedListener(db));
        // Check if we're a confirmation dialog
        if(onConfirmListener != null)
        {
            ((Button) view.findViewById(R.id.btn_yes)).setText(R.string.yes);
            view.findViewById(R.id.btn_no).setVisibility(View.VISIBLE);
        }

        // Return the content view
        return view;
    }
 
    /** An interface that can be implemented to listen for confirms */
    public interface OnConfirmListener
    {
        public void confirm();
    }
    
    /** The listener for the yes button */
    private class ButtonYesOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(onConfirmListener != null)
                onConfirmListener.confirm();
            dismiss();
        }
    }
    

    /** The listener for the no button */
    private class ButtonNoOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        { dismiss(); }
    }
    
    /** the listener for the dont-show button */
    private class DontShowChangedListener implements CompoundButton.OnCheckedChangeListener
    {
    	private Database db;
    	public DontShowChangedListener(Database db)
    	{ this.db = db; }
    	

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			if (isChecked)
			{
			    System.out.println("I HAVE BEEN CHECKED");
				Database.TutorialState state = db.getTutorialState(MainActivity.TUTORIAL_ID);
				
				state.showTutDlg = false;
				
				db.saveTutorialState(state);
				db.close();
			}
		}
    	
    }
    
}
