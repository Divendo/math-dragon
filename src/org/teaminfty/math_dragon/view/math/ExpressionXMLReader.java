package org.teaminfty.math_dragon.view.math;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.teaminfty.math_dragon.view.math.operation.Derivative;
import org.teaminfty.math_dragon.view.math.operation.Function;
import org.teaminfty.math_dragon.view.math.operation.Integral;
import org.teaminfty.math_dragon.view.math.operation.Binary;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;
import org.teaminfty.math_dragon.view.math.operation.binary.Root;
import org.teaminfty.math_dragon.view.math.operation.binary.Subtract;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Factory for creating {@link Expression}s from XML documents.
 * 
 * @author Folkert van Verseveld
 * @see #fromXML(Document)
 */
public final class ExpressionXMLReader
{
    private ExpressionXMLReader()
    {}

    static Binary toOpBin(Element e) throws ParseException
    {
        final String type = e.getAttribute("type");
        try
        {
            if(type.equals(Add.TYPE))
            {
                return new Add(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(Multiply.TYPE))
            {
                return new Multiply(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(Divide.TYPE))
            {
                return new Divide(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(Subtract.TYPE))
            {
                return new Subtract(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(Power.TYPE))
            {
                return new Power(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(Root.TYPE))
            {
                return new Root(toMath((Element) e.getLastChild()), toMath((Element) e.getFirstChild()));
            }
            else if(type.equals(Derivative.TYPE))
            {
                return new Derivative(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
        }
        catch(RuntimeException ex)
        {}
        throw new ParseException(e.getTagName() + "." + type);
    }

    static Expression toMath(Element e) throws ParseException
    {
        String tag = e.getTagName();
        try
        {
            if(tag.equals(Symbol.NAME))
            {
                // The values of the powers
                double factor = 0;
                long ePow = 0;
                long piPow = 0;
                long iPow = 0;
                long[] varPows = new long[26];
                
                // Loop through all attributes
                NamedNodeMap attrMap = e.getAttributes();
                for(int i = 0; i < attrMap.getLength(); ++i)
                {
                    final String name = attrMap.item(i).getNodeName();
                    if(name.equals(Symbol.ATTR_FACTOR))
                        factor = Double.parseDouble(attrMap.item(i).getNodeValue());
                    else if(name.equals(Symbol.ATTR_E))
                        ePow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.equals(Symbol.ATTR_PI))
                        piPow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.equals(Symbol.ATTR_I))
                        iPow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.startsWith(Symbol.ATTR_VAR))
                        varPows[name.charAt(Symbol.ATTR_VAR.length()) - 'a'] = Long.parseLong(attrMap.item(i).getNodeValue());
                }
                
                // Create and return the symbol
                return new Symbol(factor, ePow, piPow, iPow, varPows);
            }
            else if(tag.equals(Operation.NAME))
            {
                switch(Integer.parseInt(e.getAttribute(Operation.ATTR_OPERANDS)))
                {
                    case 2: return toOpBin(e);
                    case 4:
                        if(e.getAttribute("type").equals(Integral.TYPE))
                        {
                            Integral integral = new Integral();
                            NodeList childNodes = e.getChildNodes();
                            for(int i = 0; i < childNodes.getLength(); ++i)
                                integral.setChild(i, toMath((Element) childNodes.item(i)));
                            return integral;
                        }
                    break;
                }
            }
            else if(tag.equals(Function.NAME))
            {
                Function.FunctionType type = Function.FunctionType.getByXmlName(e.getAttribute(Function.ATTR_TYPE));
                Function f = new Function(type);
                f.setChild(0, toMath((Element) e.getFirstChild()));
                return f;
            }
            else if(tag.equals(Parentheses.NAME))
                return new Parentheses(toMath((Element) e.getFirstChild()));
            else if(tag.equals(Empty.NAME))
                return new Empty();
        }
        catch(RuntimeException ex)
        {}
        throw new ParseException(tag);
    }

    /**
     * Construct {@link Expression} from an XML document. If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param doc
     *        The XML document.
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static Expression fromXML(Document doc) throws ParseException
    {
        Element root = doc.getDocumentElement();
        return toMath((Element) root.getFirstChild());
    }
    
    /**
     * Construct {@link Expression} from an XML string (as a byte array). If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param xml The XML byte array
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static Expression fromXML(byte[] xml) throws ParseException
    {
        try
        {
            InputStream in = new ByteArrayInputStream(xml);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            return fromXML(doc);
        }
        catch(SAXException e)
        { throw new ParseException(e); }
        catch(IOException e)
        { throw new ParseException(e); }
        catch(ParserConfigurationException e)
        { throw new ParseException(e); }
    }

    /**
     * Construct {@link Expression} from an XML string. If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param xml The XML string
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static Expression fromXML(String xml) throws ParseException
    { return fromXML(xml.getBytes()); }
}
