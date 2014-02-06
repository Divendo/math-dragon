package org.teaminfty.math_dragon.view.math;

import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.w3c.dom.Document;

public final class ExpressionDuplicator
{
    /** Creates a deep copy of the given {@link Expression}
     * @param src The {@link Expression} to copy
     * @return The copy of the given {@link Expression} (returns <tt>null</tt> if the function somehow fails) */
    public static Expression deepCopy(Expression src)
    {
        try
        {
            // Create a XML document of the source object
            Document doc = Expression.createXMLDocument();
            src.writeToXML(doc, doc.getDocumentElement());
            
            // Create a new object from the XML and return that object
            return ExpressionXMLReader.fromXML(doc);
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
