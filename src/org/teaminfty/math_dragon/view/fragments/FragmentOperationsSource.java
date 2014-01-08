package org.teaminfty.math_dragon.view.fragments;

import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.view.MathSourceBinaryOperationLinear;
import org.teaminfty.math_dragon.view.MathSourceObject;
import org.teaminfty.math_dragon.view.MathSourceOperationDerivative;
import org.teaminfty.math_dragon.view.MathSourceOperationDivide;
import org.teaminfty.math_dragon.view.MathSourceOperationIntegral;
import org.teaminfty.math_dragon.view.MathSourceOperationPower;
import org.teaminfty.math_dragon.view.MathSourceOperationRoot;
import org.teaminfty.math_dragon.view.MathSourceOperationFunction;
import org.teaminfty.math_dragon.view.MathSourceView;
import org.teaminfty.math_dragon.view.math.MathOperationFunction;

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
        setMathSourceObjectFor(layout, R.id.mathSourceAdd, new MathSourceBinaryOperationLinear(MathSourceBinaryOperationLinear.OperatorType.ADD));
        setMathSourceObjectFor(layout, R.id.mathSourceSubtract, new MathSourceBinaryOperationLinear(MathSourceBinaryOperationLinear.OperatorType.SUBTRACT));
        setMathSourceObjectFor(layout, R.id.mathSourceMultiply, new MathSourceBinaryOperationLinear(MathSourceBinaryOperationLinear.OperatorType.MULTIPLY));
        setMathSourceObjectFor(layout, R.id.mathSourceDivide, new MathSourceOperationDivide());
        setMathSourceObjectFor(layout, R.id.mathSourcePower, new MathSourceOperationPower());
        setMathSourceObjectFor(layout, R.id.mathSourceRoot, new MathSourceOperationRoot());
        setMathSourceObjectFor(layout, R.id.mathSourceDerivative, new MathSourceOperationDerivative());
        setMathSourceObjectFor(layout, R.id.mathSourceIntegral, new MathSourceOperationIntegral());
        setMathSourceObjectFor(layout, R.id.mathSourceSin, new MathSourceOperationFunction(MathOperationFunction.FunctionType.SIN));
        setMathSourceObjectFor(layout, R.id.mathSourceArcSin, new MathSourceOperationFunction(MathOperationFunction.FunctionType.ARCSIN));
        setMathSourceObjectFor(layout, R.id.mathSourceSinh, new MathSourceOperationFunction(MathOperationFunction.FunctionType.SINH));
        setMathSourceObjectFor(layout, R.id.mathSourceCos, new MathSourceOperationFunction(MathOperationFunction.FunctionType.COS));
        
        // Return the layout
        return layout;
    }
    
    /** Sets the given {@link MathSourceObject} to the {@link MathSourceView} with the given ID
     * @param layout The layout that contains the {@link MathSourceView}
     * @param id The ID of the {@link MathSourceView} where the {@link MathSourceView} should be set for
     * @param mo The {@link MathSourceObject} that should be set */
    protected void setMathSourceObjectFor(View layout, int id, MathSourceObject mso)
    {
        MathSourceView mathSourceView = (MathSourceView) layout.findViewById(id);
        mathSourceView.setSource(mso);
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
