package org.teaminfty.math_dragon;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.exceptions.EmptyChildException;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.model.ModelHelper;
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.fragments.FragmentEvaluation;
import org.teaminfty.math_dragon.view.fragments.FragmentMainScreen;
import org.teaminfty.math_dragon.view.fragments.FragmentOperationsSource;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements
        FragmentOperationsSource.CloseMeListener
{

    /**
     * The ActionBarDrawerToggle that is used to toggle the drawer using the
     * action bar
     */
    ActionBarDrawerToggle actionBarDrawerToggle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Remove the grey overlay
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

        // Set the shadow
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        try
        {
            drawerLayout.closeDrawer(Gravity.LEFT);

            // Set the toggle for the action bar
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.operation_drawer_open, R.string.operation_drawer_close);
            getActionBar().setDisplayHomeAsUpEnabled(true);

            // Listen when to close the operations drawer
            // TODO only register this event when needed
            ((FragmentOperationsSource) getFragmentManager().findFragmentById(R.id.fragmentOperationDrawer)).setOnCloseMeListener(this);
        }
        catch(IllegalArgumentException e)
        {
            // there was no drawer to open
            // Don't have a way to detect if there is a drawer yet so we just listen for this exception..
        }

        // Set the default size in the MathObject class
        MathObject.defaultHeight = getResources().getDimensionPixelSize(R.dimen.math_object_default_size);
        MathObject.lineWidth = getResources().getDimensionPixelSize(R.dimen.math_object_line_width);
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
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it
        // has handled the app icon touch event
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle other action bar items...
        return super.onOptionsItemSelected(item);
    }

    public void evaluate(View view)
    {
        try
        {
            // Calculate the answer
            FragmentMainScreen fragmentMainScreen = (FragmentMainScreen) getFragmentManager().findFragmentById(R.id.fragmentMainScreen);
            long start = System.currentTimeMillis();
            IExpr a = fragmentMainScreen.getMathObject().eval();
            long between = System.currentTimeMillis();
            IExpr result = EvalEngine.eval(a);
            long end = System.currentTimeMillis();
            Log.i("Timings", Long.toString(between - start) + "ms, " + Long.toString(end - between) + "ms");

            // Get the evaluation fragment and show the result
            FragmentEvaluation fragmentEvaluation = (FragmentEvaluation) getFragmentManager().findFragmentById(R.id.fragmentEvaluation);
            fragmentEvaluation.showMathObject(ParenthesesHelper.setParentheses(ModelHelper.toMathObject(result)));

            // Get the DrawerLayout object and open the drawer
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(Gravity.RIGHT | Gravity.BOTTOM);
        }
        catch(EmptyChildException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(MathException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void approximate(View view)
    {
        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        drawerLayout.openDrawer(Gravity.RIGHT | Gravity.BOTTOM);
        // TODO: Approximate the MathObject in the drawing space, and display
        // the resulting constant

        FragmentEvaluation fragmentEvaluation = (FragmentEvaluation) getFragmentManager()
                .findFragmentById(R.id.fragmentEvaluation);

        MathConstant mathConstant = new MathConstant("42");
        fragmentEvaluation.showMathObject(mathConstant);
    }

    public void favourites(View view)
    {

        /*
         * // Get the DrawerLayout object DrawerLayout drawerLayout =
         * (DrawerLayout) findViewById(R.id.drawerLayout);
         * 
         * // Show the favourites drawer
         * drawerLayout.openDrawer(Gravity.CENTER);
         */

        // We use the favourites button to clear the entire screen for the time
        // being
        FragmentMainScreen fragmentMainScreen = (FragmentMainScreen) getFragmentManager()
                .findFragmentById(R.id.fragmentMainScreen);
        fragmentMainScreen.clear();
    }
    
    public void temporary(View view)
    {
    	// Get the DrawerLayout object DrawerLayout drawerLayout =
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
         
        // Show the favourites drawer
        drawerLayout.openDrawer(Gravity.CENTER);
         

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
   
    public void addpi(View view){
  	    EditText edittext = (EditText) findViewById(R.id.editText1);
  	    Editable editable = edittext.getText();
  	    editable.append("pi");
    }
    public void adde(View view){
  	    EditText edittext = (EditText) findViewById(R.id.editText1);
  	    Editable editable = edittext.getText();
  	    editable.append("e");
    }
    public void addPower(View view){
  	    EditText edittext = (EditText) findViewById(R.id.editText1);
  	    Editable editable = edittext.getText();
  	    editable.append("^");
    }
    public void clearText(View view){
    	EditText edittext = (EditText) findViewById(R.id.editText1);
    	Editable editable = edittext.getText();
    	editable.clear();
    }
    
}
