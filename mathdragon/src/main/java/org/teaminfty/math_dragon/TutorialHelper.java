package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.model.Database;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;

public class TutorialHelper
{

    public static class OnTutorialAcknowledged implements ShowcaseViews.OnShowcaseAcknowledged
    {
        private Database db;
        private Database.TutorialState state;
        public OnTutorialAcknowledged(Database db, Database.TutorialState state)
        {
            this.db = db;
            this.state = state;
        }
        @Override
        public void onShowCaseAcknowledged(ShowcaseView showcaseView)
        {
            state.tutInProg = false;
            db.saveTutorialState(state);
            db.close();
        }
        
    }
}
