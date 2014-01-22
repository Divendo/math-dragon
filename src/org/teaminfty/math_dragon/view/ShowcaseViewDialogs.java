package org.teaminfty.math_dragon.view;

import java.util.Collection;
import java.util.LinkedList;

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
                { dg.show(); }
                
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
        dialogs.getLast().setOnShowcaseEventListener(new OnShowcaseEventListener()
        {
            
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView)
            { }
            
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView)
            {
                if (onShowcaseAcknowledged != null) onShowcaseAcknowledged.acknowledge();              
            }
            
            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView)
            { }
        });
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
