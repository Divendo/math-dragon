package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.TextView;

public class MathExpressionDeleteConfirmer extends TextView
{
    /** Whether a deletion should be confirmed (<tt>true</tt>) or cancelled (<tt>false</tt>) by this instance */
    private boolean confirmDeletion = true;
    
    /** Whether or not the view should currently be visible */
    private boolean visible = false;

    public MathExpressionDeleteConfirmer(Context context)
    {
        super(context);
    }

    public MathExpressionDeleteConfirmer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        initFromAttrs(context.getTheme().obtainStyledAttributes(attrs, R.styleable.MathExpressionDeleteConfirmer, 0, 0));
    }

    public MathExpressionDeleteConfirmer(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initFromAttrs(context.getTheme().obtainStyledAttributes(attrs, R.styleable.MathExpressionDeleteConfirmer, defStyleAttr, 0));
    }

    /** Initialise from the given attributes
     * @param attrs The attributes */
    private void initFromAttrs(TypedArray attrs)
    {
        confirmDeletion = attrs.getBoolean(R.styleable.MathExpressionDeleteConfirmer_delete, true);
    }
    

    @Override
    public boolean onDragEvent(DragEvent event)
    {
        // If we're not dragging a MathDeleteShadow, we're not interested
        if(!(event.getLocalState() instanceof MathDeleteShadow))
            return false;
        
        switch(event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:
                setBackgroundResource(R.color.lightgray);
                visible = true;
                invalidate();
            return true;
            
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
                setBackgroundResource(R.color.blue);
                invalidate();
            return true;
            
            case DragEvent.ACTION_DRAG_EXITED:
                setBackgroundResource(R.color.lightgray);
                invalidate();
            return true;
            
            case DragEvent.ACTION_DROP:
                if(confirmDeletion)
                    ((MathDeleteShadow) event.getLocalState()).confirm();
            return true;

            case DragEvent.ACTION_DRAG_ENDED:
                setBackgroundResource(android.R.color.transparent);
                visible = false;
                invalidate();
            return true;
        }
        return false;
    }
    
    @Override
    public void onDraw(Canvas canvas)
    {
        if(visible)
            super.onDraw(canvas);
    }
}
