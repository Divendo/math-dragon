package org.teaminfty.math_dragon;

import org.teaminfty.math_dragon.R;

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
        
        // Set the MathObjects for the MathSourceViews
        // Note: default size isn't necessary since we'll always have a maximum size
        setMathObjectFor(layout, R.id.mathSourceAdd, new MathOperationAdd(0, 0));
        setMathObjectFor(layout, R.id.mathSourceSubtract, new MathOperationSubtract(0, 0));
        setMathObjectFor(layout, R.id.mathSourceMultiply, new MathOperationMultiply(0, 0));
        setMathObjectFor(layout, R.id.mathSourceDivide, new MathOperationDivide(0, 0));
        
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
