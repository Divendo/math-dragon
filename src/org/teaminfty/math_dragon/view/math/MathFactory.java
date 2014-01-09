package org.teaminfty.math_dragon.view.math;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Factory for creating {@link MathObject}s from XML documents.
 * 
 * @author Folkert van Verseveld
 * @see #fromXML(Document)
 */
public final class MathFactory
{
    private MathFactory()
    {}

    static MathBinaryOperation toOpBin(Element e) throws ParseException
    {
        final String type = e.getAttribute("type");
        try
        {
            if(type.equals(MathOperationAdd.TYPE))
            {
                return new MathOperationAdd(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(MathOperationMultiply.TYPE))
            {
                return new MathOperationMultiply(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(MathOperationDivide.TYPE))
            {
                return new MathOperationDivide(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(MathOperationSubtract.TYPE))
            {
                return new MathOperationSubtract(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(MathOperationPower.TYPE))
            {
                return new MathOperationPower(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
            else if(type.equals(MathOperationRoot.TYPE))
            {
                return new MathOperationRoot(toMath((Element) e.getLastChild()), toMath((Element) e.getFirstChild()));
            }
            else if(type.equals(MathOperationDerivative.TYPE))
            {
                return new MathOperationDerivative(toMath((Element) e.getFirstChild()), toMath((Element) e.getLastChild()));
            }
        }
        catch(RuntimeException ex)
        {}
        throw new ParseException(e.getTagName() + "." + type);
    }

    static MathObject toMath(Element e) throws ParseException
    {
        String tag = e.getTagName();
        try
        {
            if(tag.equals(MathSymbol.NAME))
            {
                // The values of the powers
                long factor = 0;
                long ePow = 0;
                long piPow = 0;
                long iPow = 0;
                long[] varPows = new long[26];
                
                // Loop through all attributes
                NamedNodeMap attrMap = e.getAttributes();
                for(int i = 0; i < attrMap.getLength(); ++i)
                {
                    final String name = attrMap.item(i).getNodeName();
                    if(name.equals(MathSymbol.ATTR_FACTOR))
                        factor = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.equals(MathSymbol.ATTR_E))
                        ePow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.equals(MathSymbol.ATTR_PI))
                        piPow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.equals(MathSymbol.ATTR_I))
                        iPow = Long.parseLong(attrMap.item(i).getNodeValue());
                    else if(name.startsWith(MathSymbol.ATTR_VAR))
                        varPows[name.charAt(MathSymbol.ATTR_VAR.length()) - 'a'] = Long.parseLong(attrMap.item(i).getNodeValue());
                }
                
                // Create and return the symbol
                return new MathSymbol(factor, ePow, piPow, iPow, varPows);
            }
            else if(tag.equals(MathOperation.NAME))
            {
                switch(Integer.parseInt(e.getAttribute(MathOperation.ATTR_OPERANDS)))
                {
                    case 2: return toOpBin(e);
                    case 4:
                        if(e.getAttribute("type").equals(MathOperationIntegral.TYPE))
                        {
                            MathOperationIntegral integral = new MathOperationIntegral();
                            NodeList childNodes = e.getChildNodes();
                            for(int i = 0; i < childNodes.getLength(); ++i)
                                integral.setChild(i, toMath((Element) childNodes.item(i)));
                            return integral;
                        }
                    break;
                }
            }
            else if(tag.equals(MathOperationFunction.NAME))
            {
                MathOperationFunction.FunctionType type = MathOperationFunction.FunctionType.getByXmlName(e.getAttribute(MathOperationFunction.ATTR_TYPE));
                MathOperationFunction f = new MathOperationFunction(type);
                f.setChild(0, toMath((Element) e.getFirstChild()));
                return f;
            }
            else if(tag.equals(MathParentheses.NAME))
                return new MathParentheses(toMath((Element) e.getFirstChild()));
            else if(tag.equals(MathObjectEmpty.NAME))
                return new MathObjectEmpty();
        }
        catch(RuntimeException ex)
        {}
        throw new ParseException(tag);
    }

    /**
     * Construct {@link MathObject} from an XML document. If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param doc
     *        The XML document.
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static MathObject fromXML(Document doc) throws ParseException
    {
        Element root = doc.getDocumentElement();
        return toMath((Element) root.getFirstChild());
    }
    
    /**
     * Construct {@link MathObject} from an XML string (as a byte array). If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param xml The XML byte array
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static MathObject fromXML(byte[] xml) throws ParseException
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
     * Construct {@link MathObject} from an XML string. If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * 
     * @param xml The XML string
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException
     *         Thrown if anything couldn't be parsed.
     */
    public static MathObject fromXML(String xml) throws ParseException
    { return fromXML(xml.getBytes()); }
}
