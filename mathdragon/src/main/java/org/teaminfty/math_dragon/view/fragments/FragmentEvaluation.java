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
import org.teaminfty.math_dragon.exceptions.TooBigValueException;
import org.teaminfty.math_dragon.model.EvalHelper;
import org.teaminfty.math_dragon.model.ExpressionBeautifier;
import org.teaminfty.math_dragon.model.ModelHelper;
import org.teaminfty.math_dragon.model.ParenthesesHelper;
import org.teaminfty.math_dragon.view.MathView;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.ExpressionXMLReader;
import org.w3c.dom.Document;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;


//TODO oops wrong fragment
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
    
    /** The timer to timeout the evaluator (or <tt>null</tt> if there is none) */
    private Handler timerHandler = null;
    
    /** Whether or not the timer is cancelled */
    private boolean timerCancelled = false;
    
    /** Whether or not we were unable to evaluate the expression */
    private boolean unableToEval = false;
    
    /** The ID of the message to the user when we're unable to evaluate an expression */
    private int unableToEvalMsgId = R.string.unable_to_eval;
    
    /** Whether or not variables have been substituted */
    private boolean varsSubstituted = false;
    
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
        if(savedInstanceState != null)
            mathView.setDefaultHeight(savedInstanceState.getInt(BUNDLE_MATH_VIEW_DEFAULT_HEIGHT));
        else
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
            ((TextView) view.findViewById(R.id.text_unable_to_eval)).setText(unableToEvalMsgId);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.findViewById(R.id.unableToEvalLayout).setVisibility(View.VISIBLE);
        }
        
        // Show a warning iff variables have been substituted
        view.findViewById(R.id.text_warning_substitutions_used).setVisibility(varsSubstituted ? View.VISIBLE : View.GONE);
        
        // Return the content view
        return view;
    }


    /** A string containing the title of the dialog */
    private static final String BUNDLE_TITLE = "title";

    /** A XML string containing the current the expressions that is to be shown */
    private static final String BUNDLE_MATH_EXPRESSION = "math_expr";
    
    /** An integer containing the default height of the MathView */
    private static final String BUNDLE_MATH_VIEW_DEFAULT_HEIGHT = "math_view_default_height";
    
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
            
            // Save the default height
            outState.putInt(BUNDLE_MATH_VIEW_DEFAULT_HEIGHT, mathView.getDefaultHeight());
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
        
        // Set and start the timer
        timerCancelled = false;
        timerHandler = new Handler();
        timerHandler.postDelayed(new EvaluatorTimeout(), 5000);
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
            timerCancelled = true;
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
    
    /** Runnable that is executed when the evaluation times out */
    private class EvaluatorTimeout implements Runnable
    {
        @Override
        public void run()
        {
            // Check before synchronising if the timer was cancelled
            // because we may have cancelled it from our ui thread
            if(timerCancelled) return;
            
            synchronized(timerHandler)
            {
                // Check after synchronising if the timer was cancelled
                // because we may have cancelled it from the evaluation thread
                if(timerCancelled) return;
                
                // Cancel the evaluator (interrupt)
                evaluator.cancel(true);
                
                // Show that we were unable to evaluate the expression
                unableToEval = true;
                unableToEvalMsgId = R.string.unable_to_eval_timeout;
                if(getView() != null)
                {
                    ((TextView) getView().findViewById(R.id.text_unable_to_eval)).setText(unableToEvalMsgId);
                    getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
                    getView().findViewById(R.id.unableToEvalLayout).setVisibility(View.VISIBLE);
                }
            }
        }
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
                        // We always evaluate the answer exactly first (without substitutions)
                        EvalHelper.substitute = false;
                        EvalHelper.substitutionsMade = false;
                        IExpr result = EvalEngine.eval(EvalHelper.eval(args[0]));
                        
                        // Now we calculate the answer (with substitutions, if there are any)
                        if(EvalHelper.substitutions != null && EvalHelper.substitutions.length != 0)
                        {
                            Expression resultExpr = ModelHelper.toExpression(result);
                            EvalHelper.substitute = true;
                            if(exactEvaluation)
                                result = EvalEngine.eval(EvalHelper.eval(resultExpr));
                            else
                                result = F.evaln(EvalHelper.eval(resultExpr));
                                
                        }
                        // We still might need to approximate the answer
                        else if(!exactEvaluation)
                            result = F.evaln(result);
                        
                        // Note that we always approximate after exact evaluation because for some reason
                        // integrals like integrate(x*sin(x), {x, 0, pi}) wouldn't work in approximation mode otherwise
                        
                        // Parse the expression, beautify it and place parentheses
                        Expression resultExpr = ParenthesesHelper.setParentheses(ExpressionBeautifier.parse(ModelHelper.toExpression(result)));
                        
                        // Cancel the timer
                        synchronized(timerHandler)
                        {
                            timerCancelled = true;
                        }
                        
                        // Remember whether or not substitutions have been made
                        varsSubstituted = EvalHelper.substitutionsMade;
                        
                        // Return the result
                        return resultExpr;
                    }
                    catch(TooBigValueException e)
                    {
                        unableToEval = true;
                        unableToEvalMsgId = e.valTooBig ? R.string.unable_to_eval_too_big : R.string.unable_to_eval_too_small;
                    }
                    catch(MathException e)
                    {
                        // Apparently we were unable to correctly parse the calculation
                        unableToEval = true;
                        unableToEvalMsgId = R.string.unable_to_eval;
                        e.printStackTrace();
                    }
                    catch(RuntimeException e)
                    {
                        // This occurs for impossible to solve expression (e.g. integrate(x^x, x))
                        unableToEval = true;
                        unableToEvalMsgId = R.string.unable_to_eval;
                    }
                    catch(StackOverflowError e)
                    {
                        // This occurs for impossible to solve expression (e.g. integrate(x*(x*x)^x, x))
                        unableToEval = true;
                        unableToEvalMsgId = R.string.unable_to_eval;
                    }
                }
            }
            
            // Cancel the timer
            synchronized(timerHandler)
            {
                timerCancelled = true;
            }
            
            // Something went wrong, return null
            return null;
        }
        
        @Override
        protected void onPostExecute(Expression result)
        {
            if(result != null)
                showExpression(result);
            if(getView() != null)
            {
                // We'll want to hide the progressbar anyway
                getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
                
                // Show an error if we were unable to evaluate
                if(unableToEval)
                {
                    ((TextView) getView().findViewById(R.id.text_unable_to_eval)).setText(unableToEvalMsgId);
                    getView().findViewById(R.id.unableToEvalLayout).setVisibility(View.VISIBLE);
                }
                
                // Show a warning iff variables have been substituted
                getView().findViewById(R.id.text_warning_substitutions_used).setVisibility(varsSubstituted ? View.VISIBLE : View.GONE);
            }
        }
    }
   
}

