package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.view.ShowcaseViewDialog;

import android.app.Activity;

public interface Tutorial
{
    ShowcaseViewDialog getCurrentShowcaseDialog();
    void setCurrentShowcaseDialog(ShowcaseViewDialog dialog);
    
    int getTutorialId();
    
    Activity getActivity();
    
}
