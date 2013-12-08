package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

public class MainActivity extends Activity
{
    
    /** The ActionBarDrawerToggle that is used to toggle the drawer using the action bar */
    ActionBarDrawerToggle actionBarDrawerToggle = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // If we're using a drawer layout, we'll want to change a few settings
        if(findViewById(R.id.drawerLayout) != null)
        {
            // Get the DrawerLayout object
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            
            // Remove the grey overlay
            drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
            
            // Set the shadow
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

            
            // Set the toggle for the action bar
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.operation_drawer_open, R.string.operation_drawer_close);
            getActionBar().setDisplayHomeAsUpEnabled(true);
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
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        
        // Handle other action bar items...
        return super.onOptionsItemSelected(item);
    }
    
}
