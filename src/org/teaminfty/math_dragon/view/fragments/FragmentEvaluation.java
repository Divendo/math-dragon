package org.teaminfty.math_dragon.view.fragments;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.teaminfty.math_dragon.R;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.model.EvalHelper;
import org.teaminfty.math_dragon.model.ExpressionBeautifier;
import org.teaminfty.math_dragon.model.ModelHelper;
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.teaminfty.math_dragon.view.math.Expression;
import org.w3c.dom.Document;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentEvaluation extends DialogFragment
{
    /** The {@link MathView} in this fragment */
    private MathView mathView = null;
    
    /** The {@link Expression} to show when the {@link MathView} is created */
    private Expression showExpression = null;
    
    /** The evaluation type, <tt>true</tt> if an exact evaluation is shown, <tt>false</tt> for an approximation */
    private boolean exactEvaluation = true;
    
    /** The current evaluator (or <tt>null</tt> if there is none) */
    private Evaluator evaluator = null;
    
    /** Whether or not we were unable to evaluate the expression */
    private boolean unableToEval = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Some dialog settings
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        
        // Disable the the MathView and set its contents
        mathView = (MathView) view.findViewById(R.id.mathView);
        mathView.setEnabled(false);
        mathView.setDefaultHeight(getResources().getDimensionPixelSize(R.dimen.math_object_eval_default_size));
        if(showExpression != null)
        {
            mathView.setExpression(showExpression);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.mathView).setVisibility(View.VISIBLE);
        }
        else if(savedInstanceState != null && savedInstanceState.getString(BUNDLE_MATH_EXPRESSION) != null)
        {
            try
            {
                mathView.setExpression(ExpressionXMLReader.fromXML(savedInstanceState.getString(BUNDLE_MATH_EXPRESSION)));
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                view.findViewById(R.id.mathView).setVisibility(View.VISIBLE);
            }
            catch(ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // The close button
        ((ImageButton) view.findViewById(R.id.btn_close)).setOnClickListener(new OnCloseBtnClickListener());
        
        // The title
        ((TextView) view.findViewById(R.id.textViewEvalType)).setText(exactEvaluation ? R.string.evaluate_exact : R.string.evaluate_approximate);
        if(savedInstanceState != null && savedInstanceState.getString(BUNDLE_TITLE) != null)
            ((TextView) view.findViewById(R.id.textViewEvalType)).setText(savedInstanceState.getString(BUNDLE_TITLE));
        
        // If an error occurred, show the error
        if(unableToEval)
        {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.unableToEvalLayout).setVisibility(View.VISIBLE);
        }
        
        // Wolfram|Alpha button click listener
        view.findViewById(R.id.btn_wolfram).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(onWolframListener != null)
                    onWolframListener.wolfram();
            }
        });
        
        // Return the content view
        return view;
    }

    /** A string containing the title of the dialog */
    private static final String BUNDLE_TITLE = "title";

    /** A XML string containing the current the expressions that is to be shown */
    private static final String BUNDLE_MATH_EXPRESSION = "math_expr";
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Save the title
        outState.putString(BUNDLE_TITLE, ((TextView) getView().findViewById(R.id.textViewEvalType)).getText().toString());
        
        // Save the current math expression
        if(mathView != null)
        {
            try
            {
                // Convert the MathObject to a XML document
                Document doc = Expression.createXMLDocument();
                mathView.getExpression().writeToXML(doc, doc.getDocumentElement());
                
                // Convert the XML document to a string and add it to the list
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                transformer.transform(new DOMSource(doc), new StreamResult(byteStream));
                outState.putString(BUNDLE_MATH_EXPRESSION, byteStream.toString());
            }
            catch(TransformerConfigurationException e)
            { /* Never thrown, ignore */ }
            catch(TransformerFactoryConfigurationError e)
            { /* Ignore */ }
            catch(TransformerException e)
            { /* Ignore */ }
            catch(ParserConfigurationException e)
            { /* Ignore */ }
        }
    }
    
    /** Sets the {@link Expression} that is to be shown
     * @param expr The {@link Expression} that is to be shown */
    public void showExpression(Expression expr)
    {
    	if(mathView == null)
    	    showExpression = expr;
    	else
    	{
    	    mathView.setExpression(expr);
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
    	    getView().findViewById(R.id.mathView).setVisibility(View.VISIBLE);
    	}
    }

    /** Sets whether an approximation or exact evaluation is shown.
     * Should be called before {@link FragmentEvaluation#show(android.app.FragmentManager, String) show()} is called to have effect.
     * @param exact Set to <tt>true</tt> if an exact evaluation is shown, set to <tt>false</tt> for an approximation */
    public void setEvalType(boolean exact)
    { exactEvaluation = exact; }
    
    /** Evaluates the given expression
     * @param expr The expression to evaluate */
    public void evaluate(Expression expr)
    {
        // Start the evaluator
        evaluator = new Evaluator();
        evaluator.execute(expr);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Set the right size for the keyboard dialog
        Configuration resConfig = getResources().getConfiguration();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        if((resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
           (resConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            // Set the size of the dialog
            params.width = getResources().getDimensionPixelSize(R.dimen.evaluation_dlg_width);
            params.height = getResources().getDimensionPixelSize(R.dimen.evaluation_dlg_height);
        }
        else
        {
            // Make sure the dialog takes up all width and height it can take up
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        getDialog().getWindow().setAttributes(params);
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    { dismiss(); }
    
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        mathView = null;
        if(evaluator != null)
        {
            synchronized(evaluator)
            {
                evaluator.cancel(true);
            }
        }
    }
    
    private class OnCloseBtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View btn)
        { dismiss(); }
    }

    /** Class that evaluates the expression in a separate thread */
    private class Evaluator extends AsyncTask<Expression, Void, Expression>
    {        
        @Override
        protected Expression doInBackground(Expression... args)
        {
            synchronized(evaluator)
            {
                if(!isCancelled())
                {
                    try
                    {
                        // Calculate the answer
                        // Note that we use a two-step evaluation as for some reason integrals like integrate(x*sin(x), {x, 0, pi})
                        // wouldn't work in approximation mode otherwise
                        IExpr result = null;
                        result = EvalEngine.eval(EvalHelper.eval(args[0]));
                        if(!exactEvaluation)
                            result = F.evaln(result);
                        
                        // Parse the expression, beautify it and place parentheses
                        Expression resultExpr = ParenthesesHelper.setParentheses(ExpressionBeautifier.parse(ModelHelper.toExpression(result)));
                        
                        // Return the result
                        return resultExpr;
                    }
                    catch(MathException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch(RuntimeException e)
                    {
                        // This occurs for impossible to solve expression (e.g. integrate(x^x, x))
                        unableToEval = true;
                    }
                }
            }
            
            // Something went wrong, return null
            return null;
        }
        
        @Override
        protected void onPostExecute(Expression result)
        {
            if(result != null)
                showExpression(result);
            else if(getView() != null)
            {
                if(unableToEval)
                {
                    getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
                    getView().findViewById(R.id.unableToEvalLayout).setVisibility(View.VISIBLE);
                }
            }
        }
    }
    
    /** Interface that can be implemented to listen when the expression should be evaluated using Wolfram|Alpha */
    public interface OnWolframListener
    {
        /** Called when the expression should be evaluated using Wolfram|Alpha */
        public void wolfram();
    }
    
    /** The current {@link FragmentEvaluation#OnWolframListener OnWolframListener} */
    private OnWolframListener onWolframListener = null;
    
    /** Set the {@link FragmentEvaluation#OnWolframListener OnWolframListener}
     * @param listener The new {@link FragmentEvaluation#OnWolframListener OnWolframListener} */
    public void setOnWolframListener(OnWolframListener listener)
    { onWolframListener = listener; }
}

