package org.teaminfty.math_dragon.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/** Class that represents the settings of the application. */
public abstract class AppSettings
{
    /** The name of the shared preferences that contains the app settings. */
    public static final String SHARED_PREFS_NAME = "app_settings";

    /** A boolean settings that indicates whether or not to vibrate on certain input events (true by default). */
    public static final String VIBRATION_ON = "vibration_on";

    /**
     * Returns whether or not to vibrate on certain input events.
     * @param context The context that is used to access the settings.
     * @return True if vibration is on (default), false otherwise.
     */
    public static boolean getVibrationOn(Context context)
    {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE).getBoolean(VIBRATION_ON, true);
    }

    /**
     * Sets whether or not to vibrate on certain input events.
     * @param context The context that is used to access the settings.
     * @param vibrationOn True if vibration should be on, false otherwise.
     */
    public static void setVibrationOn(Context context, boolean vibrationOn)
    {
        // Get an editor for the settings
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();

        // Set the new vibration setting
        settingsEditor.putBoolean(VIBRATION_ON, vibrationOn);

        // Commit the changes
        settingsEditor.commit();
    }
}
