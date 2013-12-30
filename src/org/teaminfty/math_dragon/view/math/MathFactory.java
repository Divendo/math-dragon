package org.teaminfty.math_dragon.view.math;

import org.teaminfty.math_dragon.exceptions.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Factory for creating {@link MathObject}s from XML documents.
 * @author Folkert van Verseveld
 * @see #fromXML(Document)
 */
public final class MathFactory
{
    private MathFactory() {}
    
    static MathBinaryOperation toOpBin(Element e) throws ParseException {
        final String type = e.getAttribute("type");
        try {
            if (type.equals("add")) {
                return new MathOperationAdd(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals("multiply")) {
                return new MathOperationMultiply(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals("divide")) {
                return new MathOperationDivide(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals("subtract")) {
                return new MathOperationSubtract(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals("power")) {
                return new MathOperationPower(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals("root")) {
                return new MathOperationRoot(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            }
        } catch (RuntimeException ex) {}
        throw new ParseException(e.getTagName() + "." + type);
    }
    
    static MathObject toMath(Element e) throws ParseException {
        String tag = e.getTagName();
        try {
            if (tag.equals(MathConstant.NAME)) {
                return new MathConstant(
                        Long.parseLong(e.getAttribute(MathConstant.ATTR_FACTOR)),
                        Long.parseLong(e.getAttribute(MathConstant.ATTR_E)),
                        Long.parseLong(e.getAttribute(MathConstant.ATTR_PI)),
                        Long.parseLong(e.getAttribute(MathConstant.ATTR_I))
                );
            } else if (tag.equals(MathVariable.NAME)) {
                return new MathVariable(e.getAttribute(MathVariable.ATTR_NAME));
            } else if (tag.equals("operation")) {
                if (Integer.parseInt(e.getAttribute("operands")) == 2) {
                    return toOpBin(e);
                }
            } else if (tag.equals(MathObjectEmpty.NAME)) {
                return new MathObjectEmpty();
            }
        } catch (RuntimeException ex) {}
        throw new ParseException(tag);
    }
    
    /**
     * Construct {@link MathObject} from an XML document. If anything fails
     * while parsing the document, a {@link ParseException} is thrown.
     * @param doc The XML document.
     * @return The constructed mathematical object. Never returns <tt>null</tt>
     * @throws ParseException Thrown if anything couldn't be parsed.
     */
    public static MathObject fromXML(Document doc) throws ParseException {
        return toMath(doc.getDocumentElement());
    }
}
