package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.R.dimen;
import org.teaminfty.math_dragon.R.id;
import org.teaminfty.math_dragon.R.layout;
import org.teaminfty.math_dragon.view.MathSourceView;
import org.teaminfty.math_dragon.view.MathSourceView.DragStartedListener;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;
import org.teaminfty.math_dragon.view.math.MathOperationRoot;
import org.teaminfty.math_dragon.view.math.MathOperationSubtract;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOperationsSource extends Fragment implements MathSourceView.DragStartedListener
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_operations_source, container, false);
        
        // Get the default size for the MathObjects in this drawer
        final int defSize = getResources().getDimensionPixelSize(R.dimen.math_source_height);
        
        // Set the MathObjects for the MathSourceViews
        setMathObjectFor(layout, R.id.mathSourceAdd, new MathOperationAdd(defSize, defSize));
        setMathObjectFor(layout, R.id.mathSourceSubtract, new MathOperationSubtract(defSize, defSize));
        setMathObjectFor(layout, R.id.mathSourceMultiply, new MathOperationMultiply(defSize, defSize));
        setMathObjectFor(layout, R.id.mathSourceDivide, new MathOperationDivide(defSize, defSize));
        setMathObjectFor(layout, R.id.mathSourcePower, new MathOperationPower(defSize, defSize));
        setMathObjectFor(layout, R.id.mathSourceRoot, new MathOperationRoot(defSize, defSize));
        
        // Return the layout
        return layout;
    }
    
    /** Sets the given {@link MathObject} to the {@link MathSourceView} with the given ID
     * @param layout The layout that contains the {@link MathSourceView}
     * @param id The ID of the {@link MathSourceView} where the {@link MathObject} should be set for
     * @param mo The {@link MathObject} that should be set */
    protected void setMathObjectFor(View layout, int id, MathObject mo)
    {
        MathSourceView mathSourceView = (MathSourceView) layout.findViewById(id);
        mathSourceView.setMathObject(mo);
        mathSourceView.setOnDragStarted(this);
    }
    
    /** Interface definition for a callback to be invoked when this fragment should be closed (if it's a drawer) */
    public interface CloseMeListener
    { public void closeMe(); }

    /** The close event listener */
    private CloseMeListener onCloseMe = null;
    
    /** Set the close event listener */
    public void setOnCloseMeListener(CloseMeListener listener)
    { onCloseMe = listener; }

    @Override
    public void dragStarted()
    {
        if(onCloseMe != null)
            onCloseMe.closeMe();
    }

}
