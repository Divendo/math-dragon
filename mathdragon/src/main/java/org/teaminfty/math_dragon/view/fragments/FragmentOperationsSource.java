package org.teaminfty.math_dragon.view.fragments;

import static org.teaminfty.math_dragon.view.math.Function.FunctionType.*;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.model.AppSettings;
import org.teaminfty.math_dragon.view.math.SourceView;
import org.teaminfty.math_dragon.view.math.source.Expression;
import org.teaminfty.math_dragon.view.math.source.operation.BinaryLinear;
import org.teaminfty.math_dragon.view.math.source.operation.Derivative;
import org.teaminfty.math_dragon.view.math.source.operation.Divide;
import org.teaminfty.math_dragon.view.math.source.operation.Function;
import org.teaminfty.math_dragon.view.math.source.operation.Integral;
import org.teaminfty.math_dragon.view.math.source.operation.Log;
import org.teaminfty.math_dragon.view.math.source.operation.Power;
import org.teaminfty.math_dragon.view.math.source.operation.Root;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class FragmentOperationsSource extends Fragment implements SourceView.DragStartedListener
{
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_operations_source, container, false);
        
        // Set the MathObjects for the MathSourceViews
        // Operators
        setMathSourceObjectFor(layout, R.id.mathSourceAdd, new BinaryLinear(BinaryLinear.OperatorType.ADD));
        setMathSourceObjectFor(layout, R.id.mathSourceSubtract, new BinaryLinear(BinaryLinear.OperatorType.SUBTRACT));
        setMathSourceObjectFor(layout, R.id.mathSourceMultiply, new BinaryLinear(BinaryLinear.OperatorType.MULTIPLY));
        setMathSourceObjectFor(layout, R.id.mathSourceDivide, new Divide());
        setMathSourceObjectFor(layout, R.id.mathSourcePower, new Power());
        setMathSourceObjectFor(layout, R.id.mathSourceRoot, new Root());
        setMathSourceObjectFor(layout, R.id.mathSourceDerivative, new Derivative());
        setMathSourceObjectFor(layout, R.id.mathSourceIntegral, new Integral());
        setMathSourceObjectFor(layout, R.id.mathSourceLog, new Log());
        // Functions
        setMathSourceObjectFor(layout, R.id.mathSourceSin, new Function(SIN));
        setMathSourceObjectFor(layout, R.id.mathSourceArcSin, new Function(ARCSIN));
        setMathSourceObjectFor(layout, R.id.mathSourceCos, new Function(COS));
        setMathSourceObjectFor(layout, R.id.mathSourceArcCos, new Function(ARCCOS));
        setMathSourceObjectFor(layout, R.id.mathSourceTan, new Function(TAN));
        setMathSourceObjectFor(layout, R.id.mathSourceArcTan, new Function(ARCTAN));
        setMathSourceObjectFor(layout, R.id.mathSourceLog, new Log());
        setMathSourceObjectFor(layout, R.id.mathSourceLn, new Function(LN));
        
        // Get the tabs and set their OnClickListener and activate the right one
        ToggleButton tabOperators = (ToggleButton) layout.findViewById(R.id.btn_tab_operators);
        ToggleButton tabFunctions = (ToggleButton) layout.findViewById(R.id.btn_tab_funcs);
        tabOperators.setChecked(true);
        TabOnClickListener tabOnClickListener = new TabOnClickListener();
        tabOperators.setOnClickListener(tabOnClickListener);
        tabFunctions.setOnClickListener(tabOnClickListener);
        
        // Show the right tab
        boolean operatorsVisible = true;
        if(savedInstanceState != null)
            operatorsVisible = savedInstanceState.getBoolean(BUNDLE_OPERATORS_VISIBLE);
        layout.findViewById(R.id.operators_container).setVisibility(operatorsVisible ? SourceView.VISIBLE : SourceView.GONE);
        layout.findViewById(R.id.functions_container).setVisibility(operatorsVisible ? SourceView.GONE : SourceView.VISIBLE);

        // Check or uncheck the "vibration on" checkbox depending on the current settings
        CheckBox checkVibrationOn = (CheckBox) layout.findViewById(R.id.check_vibration_on);
        checkVibrationOn.setChecked(AppSettings.getVibrationOn(getActivity()));
        checkVibrationOn.setOnCheckedChangeListener(new VibrationOnCheckedChangeListener());
        
        // Return the layout
        return layout;
    }
    
    /** A boolean containing which tab is currently shown (<tt>true</tt> means the operators tab is shown, <tt>false</tt> means the functions tab is shown) */
    private static final String BUNDLE_OPERATORS_VISIBLE = "operators_visible";
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Store which tab is shown
        outState.putBoolean(BUNDLE_OPERATORS_VISIBLE, ((ToggleButton) getView().findViewById(R.id.btn_tab_operators)).isChecked());
    }
    
    /** Sets the given {@link Expression} to the {@link SourceView} with the given ID
     * @param layout The layout that contains the {@link SourceView}
     * @param id The ID of the {@link SourceView} where the {@link SourceView} should be set for
     * @param mso The {@link Expression} that should be set */
    protected void setMathSourceObjectFor(android.view.View layout, int id, Expression mso)
    {
        SourceView mathSourceView = (SourceView) layout.findViewById(id);
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
    
    /** The OnClickListener for the tab buttons */
    private class TabOnClickListener implements SourceView.OnClickListener
    {
        @Override
        public void onClick(android.view.View btn)
        {
            // Get the tabs and containers
            ToggleButton tabOperators = (ToggleButton) getView().findViewById(R.id.btn_tab_operators);
            ToggleButton tabFunctions = (ToggleButton) getView().findViewById(R.id.btn_tab_funcs);
            ViewGroup containerOperators = (ViewGroup) getView().findViewById(R.id.operators_container);
            ViewGroup containerFunctions = (ViewGroup) getView().findViewById(R.id.functions_container);
            
            // Determine which container is to be shown
            final boolean showOperators = (btn.getId() == R.id.btn_tab_operators);
            
            // Activate the right tab (and deactivate the other)
            tabOperators.setChecked(showOperators);
            tabFunctions.setChecked(!showOperators);
            
            // Show/hide the containers
            containerOperators.setVisibility(showOperators ? SourceView.VISIBLE : SourceView.INVISIBLE);
            containerFunctions.setVisibility(showOperators ? SourceView.INVISIBLE : SourceView.VISIBLE);
        }
    }

    /** The OnCheckedChangeListener for the "vibration on" checkbox. */
    private class VibrationOnCheckedChangeListener implements CheckBox.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            AppSettings.setVibrationOn(getActivity(), isChecked);
        }
    }
}
