package org.teaminfty.math_dragon.view.math;

import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.w3c.dom.Document;

public final class MathObjectDuplicator
{
    /** Creates a deep copy of the given {@link MathObject}
     * @param src The {@link MathObject} to copy
     * @return The copy of the given {@link MathObject} (returns <tt>null</tt> if the function somehow fails) */
    public static MathObject deepCopy(MathObject src)
    {
        try
        {
            // Create a XML document of the source object
            Document doc = MathObject.createXMLDocument();
            src.writeToXML(doc, doc.getDocumentElement());
            
            // Create a new object from the XML and return that object
            return MathFactory.fromXML(doc);
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
            return null;
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
