package org.teaminfty.math_dragon.view;

import android.content.Context;
import android.os.Vibrator;
import org.teaminfty.math_dragon.model.AppSettings;

/** Class that handles the vibrating for the touch feedback. */
public abstract class TouchFeedbackVibrator
{
    /**
     * Vibrates on a long press.
     * @param context The context that is used to access the vibrator.
     */
    public static void longPressVibrate(Context context)
    {
        if(AppSettings.getVibrationOn(context))
        {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }
    }
}
