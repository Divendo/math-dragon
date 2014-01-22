package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.Target;
import com.espian.showcaseview.targets.ViewTarget;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class ShowcaseViewDialog extends Dialog
{
    public ShowcaseViewDialog(Activity ctx, Target target, String title, String msg)
    {
        // Construct an invisible dialog
        super(ctx, R.style.ShowcaseViewDialogTheme);
        
        // Create the ShowcaseView and directly remove is from its parent
        ShowcaseView sv = ShowcaseView.insertShowcaseView(target, ctx, title, msg);
        ((ViewGroup) sv.getParent()).removeView(sv);
        sv.setOnShowcaseEventListener(new ShowcaseEventListener());
        
        // Set the ShowcaseView as content
        setContentView(sv);
    }

    public ShowcaseViewDialog(Activity ctx, Target target, int titleId, int msgId)
    {
        // Simply call the other constructor
        this(ctx, target, ctx.getResources().getString(titleId), ctx.getResources().getString(msgId));
    }
    
    /** A target for the ShowcaseView that can target a certain view in a DialogFragment */
    public static class DialogFragmentTarget extends ViewTarget
    {
        /** The DialogFragment */
        private DialogFragment dlgFrag;

        public DialogFragmentTarget(View view, DialogFragment dlgFrag)
        {
            // Call the super constructor
            super(view);
            
            // Remember the DialogFragment
            this.dlgFrag = dlgFrag;
        }
        
        @Override
        public Point getPoint()
        {
            // Get the position of the view inside the window
            Point viewPos = super.getPoint();
            
            // Get the layout parameters of the dialog and get the content view and the action bar
            WindowManager.LayoutParams params = dlgFrag.getDialog().getWindow().getAttributes();
            View contentView = dlgFrag.getActivity().findViewById(android.R.id.content);
            ActionBar actionBar = dlgFrag.getActivity().getActionBar();

            // Calculate the coordinates of the dialog
            int x = (int) params.horizontalMargin;
            if(params.width != LayoutParams.MATCH_PARENT)
                x += (contentView.getWidth() - params.width) / 2;

            int y = (int) params.verticalMargin;
            if(params.height != LayoutParams.MATCH_PARENT)
                y += (contentView.getHeight() + actionBar.getHeight() - params.height) / 2;
            
            // Offset the point by the coordinates of the window
            viewPos.offset(x, y);
            
            // Return the new point
            return viewPos;
        }
    }
    
    /** The current OnShowcaseEventListener */
    private OnShowcaseEventListener onShowcaseEventListener = null;
    
    /** Sets the current OnShowcaseEventListener
     * @param listener The new listener */
    public void setOnShowcaseEventListener(OnShowcaseEventListener listener)
    { onShowcaseEventListener = listener; }
    
    /** Listens for events from the ShowcaseView */
    private class ShowcaseEventListener implements OnShowcaseEventListener
    {

        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView)
        {
            // Call the OnShowcaseEventListener
            if(onShowcaseEventListener != null)
                onShowcaseEventListener.onShowcaseViewHide(showcaseView);
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView)
        {
            // Simply dismiss the dialog
            dismiss();

            // Call the OnShowcaseEventListener
            if(onShowcaseEventListener != null)
                onShowcaseEventListener.onShowcaseViewDidHide(showcaseView);
        }

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView)
        {
            // Call the OnShowcaseEventListener
            if(onShowcaseEventListener != null)
                onShowcaseEventListener.onShowcaseViewShow(showcaseView);
            }
        
    }
}
