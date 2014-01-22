package org.teaminfty.math_dragon.exceptions;

public class TooBigValueException extends ParseException
{
    private static final long serialVersionUID = 581342122873121790L;
    
    /** Whether the value was too big (<tt>true</tt>) or too small (<tt>false</tt>) */
    public boolean valTooBig = true;
    
    /** Constructor
     * @param valTooBig Whether the value was too big (<tt>true</tt>) or too small (<tt>false</tt>) */
    public TooBigValueException(boolean valTooBig)
    {
        super(valTooBig ? "too big value" : "too small value");
        
        this.valTooBig = valTooBig;
    }
}
