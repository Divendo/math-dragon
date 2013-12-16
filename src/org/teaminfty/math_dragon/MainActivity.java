package org.teaminfty.math_dragon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements FragmentOperationsSource.CloseMeListener
{
    
    /** The ActionBarDrawerToggle that is used to toggle the drawer using the action bar */
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

        // Set the toggle for the action bar
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.operation_drawer_open, R.string.operation_drawer_close);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Listen when to close the operations drawer
        ((FragmentOperationsSource) getFragmentManager().findFragmentById(R.id.fragmentOperationDrawer)).setOnCloseMeListener(this);
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
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        
        // Handle other action bar items...
        return super.onOptionsItemSelected(item);
    }
    public void evaluate(View view)
    {
        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        
        // Show the evaluation drawer
        drawerLayout.openDrawer(Gravity.RIGHT | Gravity.BOTTOM);
       
        // TODO: Evaluate the MathObject in the drawing space, and display the resulting constant
        FragmentEvaluation fragmentEvaluation = (FragmentEvaluation) getFragmentManager().findFragmentById(R.id.fragmentEvaluation);
        
        MathOperationAdd add = new MathOperationAdd(100, 100);
        add.setChild(0, new MathConstant("20", 100, 100));
        add.setChild(1, new MathConstant("5", 100, 100));
        
        MathOperationSubtract subtract = new MathOperationSubtract(100, 100);
        subtract.setChild(0, add);
        subtract.setChild(1, new MathConstant("4", 100, 100));

        MathOperationMultiply multiply = new MathOperationMultiply(100, 100);
        multiply.setChild(0, new MathConstant("2", 100, 100));
        multiply.setChild(1, subtract);
        fragmentEvaluation.showMathObject(multiply);
    }
    
    public void approximate(View view)
    {
        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        
        drawerLayout.openDrawer(Gravity.RIGHT | Gravity.BOTTOM);
        // TODO: Approximate the MathObject in the drawing space, and display the resulting constant
        
        
        FragmentEvaluation fragmentEvaluation = (FragmentEvaluation) getFragmentManager().findFragmentById(R.id.fragmentEvaluation);
        MathConstant mathConstant = new MathConstant("42",100,100);	
        fragmentEvaluation.showMathObject(mathConstant);
    }
    
    public void favourites(View view)
    {
    	// Get the DrawerLayout object
        // DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        
        // Show the favorites drawer
        // drawerLayout.openDrawer(Gravity.CENTER);
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Enter the constant!");
    	alert.setMessage("Enter the value, then press OK!");

    	// Set an EditText view to get user input 	
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	  String value = input.getText().toString();
    	  MathConstant mathconstant = new MathConstant(value,100,100);
    	  }
    	});
    	
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    }
    public void inputMathDialog()
    { 
    	
    }

    @Override
    public void closeMe()
    {
        // Get the DrawerLayout object
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        
        // Close the operations source drawer
        drawerLayout.closeDrawer(Gravity.LEFT);
    }
}
