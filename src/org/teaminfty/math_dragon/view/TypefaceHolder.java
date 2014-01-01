package org.teaminfty.math_dragon.view;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public final class TypefaceHolder
{
    /** Initialises the fonts from the given {@link AssetManager}
     * @param assets The {@link AssetManager} */
    public static void loadFromAssets(AssetManager assets)
    {
        dejavuSans = Typeface.createFromAsset(assets, "DejaVuSans.ttf");
    }
    
    /** The DejaVu sans font */
    public static Typeface dejavuSans = null;
}
