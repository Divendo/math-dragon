package org.math_dragon.view.fragments;

import org.math_dragon.view.ShowcaseViewDialog;

import android.app.Activity;

public interface Tutorial
{
    ShowcaseViewDialog getCurrentShowcaseDialog();
    void setCurrentShowcaseDialog(ShowcaseViewDialog dialog);
    
    int getTutorialId();
    
    Activity getActivity();
    
}
