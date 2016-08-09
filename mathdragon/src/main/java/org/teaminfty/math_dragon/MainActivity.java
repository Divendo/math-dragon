package org.teaminfty.math_dragon;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.teaminfty.math_dragon.model.Database;
import org.teaminfty.math_dragon.model.EvalHelper;
import org.teaminfty.math_dragon.view.TypefaceHolder;
import org.teaminfty.math_dragon.view.fragments.FragmentEvaluation;
import org.teaminfty.math_dragon.view.fragments.FragmentMainScreen;
import org.teaminfty.math_dragon.view.fragments.FragmentOperationsSource;
import org.teaminfty.math_dragon.view.fragments.FragmentSubstitute;
import org.teaminfty.math_dragon.view.math.Expression;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity implements FragmentOperationsSource.CloseMeListener
{
	public static final int TUTORIAL_ID = 0;

    /** The ActionBarDrawerToggle that is used to toggle the drawer using the action bar */
    ActionBarDrawerToggle actionBarDrawerToggle = null;

    /** Class that loads the Symja library in a separate thread */
    private class SymjaLoader extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... args)
        {
            // Simply do a simple (yet beautiful :D) calculation to make the system load Symja
            EvalEngine.eval(F.Plus(F.ZZ(1), F.Power(F.E, F.Times(F.Pi, F.I))));
            
            // Solve an integral to load that module
            EvalEngine.eval(F.Integrate(F.x, F.x));
            
            // Return null (return value won't be used)
            return null;
        }
    }


	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Load the typefaces
        TypefaceHolder.loadFromAssets(getAssets());

        // Set the default size in the Expression class
        Expression.lineWidth = getResources().getDimensionPixelSize(R.dimen.math_object_line_width);
        
        // Set the default size for formula thumbnails in the save load dialog
        Database.TABLE_FORMULAS.IMAGE_SIZE = getResources().getDimensionPixelSize(R.dimen.formula_thumb_size);
        
        // Load the layout
        setContentView(R.layout.main);
        
        // Load Symja
        new SymjaLoader().execute();

        DrawerLayout drawerLayout;
        // DrawLayout specific code
        if((drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout)) != null)
        {

            // Remove the grey overlay
            drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
    
            // Set the shadow
            //drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
    
            try
            {
                // Make sure the source drawer is closed
                drawerLayout.closeDrawer(Gravity.LEFT);
    
                // Set the toggle for the action bar
                actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.operation_drawer_open, R.string.operation_drawer_close);
                getActionBar().setDisplayHomeAsUpEnabled(true);
    
                // Listen when to close the operations drawer
                ((FragmentOperationsSource) getFragmentManager().findFragmentById(R.id.fragmentOperationDrawer)).setOnCloseMeListener(this);
            }
            catch(IllegalArgumentException e)
            {
                // There was no drawer to close
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred
        if(actionBarDrawerToggle != null)
            actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        // Notify actionBarDrawerToggle of any configuration changes
        if(actionBarDrawerToggle != null)
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if(actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle other action bar items...
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    /** Tag of the evaluation dialog */
    private static final String EVALUATION_TAG = "evaluation";
    
    public void evaluate(View view)
    {
        // If the evaluation dialog is already shown, stop here
        if(getFragmentManager().findFragmentByTag(EVALUATION_TAG) != null)
            return;
        
        // Get the expression
        FragmentMainScreen fragmentMainScreen = (FragmentMainScreen) getFragmentManager().findFragmentById(R.id.fragmentMainScreen);
        Expression expr = fragmentMainScreen.getExpression();
        
        // If the expression isn't completed yet, we stop here
        if(!expr.isCompleted()) return;
        
        // Load the substitutions
        Database db = new Database(this);
        EvalHelper.substitutions = db.getAllSubstitutions();
        db.close();

        // Create an evaluation fragment and show the result
        FragmentEvaluation fragmentEvaluation = new FragmentEvaluation();
        fragmentEvaluation.setEvalType(true);
        fragmentEvaluation.evaluate(expr);
        fragmentEvaluation.show(getFragmentManager(), EVALUATION_TAG);
    }

    public void approximate(View view)
    {
        // If the evaluation dialog is already shown, stop here
        if(getFragmentManager().findFragmentByTag(EVALUATION_TAG) != null)
            return;
        
        // Get the expression
        FragmentMainScreen fragmentMainScreen = (FragmentMainScreen) getFragmentManager().findFragmentById(R.id.fragmentMainScreen);
        Expression expr = fragmentMainScreen.getExpression();
        
        // If the expression isn't completed yet, we stop here
        if(!expr.isCompleted()) return;
        
        // Load the substitutions
        Database db = new Database(this);
        EvalHelper.substitutions = db.getAllSubstitutions();
        db.close();

        // Create an evaluation fragment and show the result
        FragmentEvaluation fragmentEvaluation = new FragmentEvaluation();
        fragmentEvaluation.setEvalType(false);
        fragmentEvaluation.evaluate(expr);
        fragmentEvaluation.show(getFragmentManager(), EVALUATION_TAG);
    }

    public void clear(View view)
    {
        // Simply clear the current formula
        FragmentMainScreen fragmentMainScreen = (FragmentMainScreen) getFragmentManager().findFragmentById(R.id.fragmentMainScreen);
        fragmentMainScreen.clear();
    }
    
    /** The tag for the substitute dialog */
    private static final String SUBSTITUTE_TAG = "substitute";
    
    public void substitute(View view)
    {
        // If a substitute dialog is already shown, stop here
        if(getFragmentManager().findFragmentByTag(SUBSTITUTE_TAG) != null)
            return;
        
        // Create and show the substitute dialog
        FragmentSubstitute fragmentSubstitute = new FragmentSubstitute();
        fragmentSubstitute.show(getFragmentManager(), SUBSTITUTE_TAG);
    }

    @Override
    public void closeMe()
    {
        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        try
        {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        catch(IllegalArgumentException e)
        {
            // there was no drawer to open.
            // Don't have a way to detect if there is a drawer yet so we just listen for this exception..
        }
    }
}
