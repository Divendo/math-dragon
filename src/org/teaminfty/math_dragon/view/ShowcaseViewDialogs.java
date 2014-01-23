package org.teaminfty.math_dragon.view;

import java.util.Collection;
import java.util.LinkedList;

import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.view.fragments.Tutorial;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;

/**
 *
 * @author arian
 * A helper class to easily play multiple  {@link ShowcaseViewDialog}s in order.
 */
public class ShowcaseViewDialogs
{
    
    /** a list of dialogs  */
    private LinkedList<ShowcaseViewDialog> dialogs = new LinkedList<ShowcaseViewDialog>();
    
    /** an event handler */
    private OnShowcaseAcknowledged onShowcaseAcknowledged;
    
    
    private final Tutorial tutorial;
    
    public ShowcaseViewDialogs(Tutorial tutorial)
    { this.tutorial = tutorial; }
    
    /**
     * Adds a new {@link ShowcaseViewDialog} to the queue. 
     * @param dg the dialog to add.
     */
    public void addView(final ShowcaseViewDialog dg)
    {
        dialogs.add(dg);
        
        int index = dialogs.indexOf(dg);
        
        
        if(dialogs.size() > 1)
        {
            dialogs.get(index-1).setOnShowcaseEventListener(new OnShowcaseEventListener()
            {
                
                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView)
                { }
                
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView)
                { dg.show();  tutorial.setCurrentShowcaseDialog(dg);}
                
                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView)
                { }
            });
        }
    }
    
    /** 
     * Utility method to quickly add multiple {@link ShowcaseViewDialog}s
     * @param dgs The dialogs to add.
     */
    public void addViews(final ShowcaseViewDialog[] dgs)
    { for(ShowcaseViewDialog dg : dgs) addView(dg); }
    
    /** 
     * Utility method to quickly add multiple {@link ShowcaseViewDialog}s
     * @param dgs The dialogs to add.
     */    
    public void addViews(final Collection<ShowcaseViewDialog> dgs)
    { for(ShowcaseViewDialog dg : dgs) addView(dg); }
    
    
    /**
     * Shows the {@link ShowcaseViewDialog}s in order that they were added.
     */
    public void show()
    {
        Database db = new Database(tutorial.getActivity());
        if (db.getTutorialState(tutorial.getTutorialId()).tutInProg == false)
        {
            db.close(); return;
        }
        db.close();
        if(!(dialogs.size() > 0)) 
        {
            if(onShowcaseAcknowledged != null) onShowcaseAcknowledged.acknowledge(); 
            return;
        }
        dialogs.getLast().setOnShowcaseEventListener(new OnShowcaseEventListener()
        {
            
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            { }
            
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                if (onShowcaseAcknowledged != null) onShowcaseAcknowledged.acknowledge();    
                
                Database db = new Database(tutorial.getActivity());
                Database.TutorialState state = db.getTutorialState(tutorial.getTutorialId());
                state.tutInProg = false;
                db.saveTutorialState(state);
                db.close();
            }
            
            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            { }
        });
        tutorial.setCurrentShowcaseDialog(dialogs.get(0));
        dialogs.get(0).show();
    }
    
    /**
     * Set the listener
     * @param onShowcaseAcknowledged
     */
    public void setOnShowcaseAcknowledged(OnShowcaseAcknowledged onShowcaseAcknowledged)
    {
        this.onShowcaseAcknowledged = onShowcaseAcknowledged;
    }
    
    
    /**
     */
    public interface OnShowcaseAcknowledged
    {
        void acknowledge();
    }
}
