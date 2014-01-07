package org.teaminfty.math_dragon.view.math;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class draws operations and provides simply functions to modify both
 * mathematical expressions.
 * 
 * @author Folkert van Verseveld
 * @see MathBinaryOperation
 * @see MathBinaryOperationLinear
 */
public abstract class MathOperation extends MathObject
{
    public static final String NAME = "operation";
    public static final String ATTR_OPERANDS = "operands";
    public static final String ATTR_TYPE = "type";

    /**
     * Construct unary mathematical operation (i.e. only one operand).
     * 
     * @see #MathOperation(int)
     */
    protected MathOperation()
    {
        this(1);
    }

    /**
     * Construct mathematical operation with specified number of operands. The
     * number of operands should be 1 or more. <b>Note:</b> the number of
     * operands is not guaranteed to be immutable. The implementation indicates
     * whether it is mutable.
     * 
     * @param operands
     *        The number of operands.
     */
    protected MathOperation(int operands)
    {
        if(operands < 1)
        {
            throw new IllegalArgumentException(operands
                    + ": invalid number of operands, 1 or more expected ");
        }
        this.children.ensureCapacity(operands);
    }

    public MathOperation(ArrayList<MathObject> list)
    {
        this(list, true);
    }

    protected MathOperation(ArrayList<MathObject> list, boolean replaceNullElements)
    {
        if (list == null)
            throw new NullPointerException("list");

        children = list;
        if(replaceNullElements)
        {
            for(int i = 0; i < list.size(); ++i)
            {
                if(children.get(i) == null)
                    children.set(i, new MathObjectEmpty());
            }
        }
    }

    /**
     * Assign list to current children using deep-copy.
     * 
     * @param list
     *        The source collection.
     */
    public void set(ArrayList<MathObject> list)
    {
        set(list, true);
    }

    /**
     * Assign list to current children with or without a deep-copy.
     * 
     * @param list
     *        The source collection.
     * @param deepcopy
     *        Whether to perform a deep-copy.
     */
    protected void set(ArrayList<MathObject> list, boolean deepcopy)
    {
        if(list == null)
            throw new NullPointerException("list");
        if(deepcopy)
            this.children = new ArrayList<MathObject>(list);
        else
            this.children = list;
    }
    
    protected abstract String getType();
    
    protected abstract void writeChildrenToXML(Document doc, Element el);
    
    public final void writeToXML(Document doc, Element el)
    {
        Element e = doc.createElement(NAME);
        e.setAttribute(ATTR_OPERANDS, String.valueOf(children.size()));
        e.setAttribute(ATTR_TYPE, getType());
        writeChildrenToXML(doc, e);
        el.appendChild(e);
    }
}
