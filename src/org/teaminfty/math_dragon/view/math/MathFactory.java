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
            if (type.equals(MathOperationAdd.TYPE)) {
                return new MathOperationAdd(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals(MathOperationMultiply.TYPE)) {
                return new MathOperationMultiply(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals(MathOperationDivide.TYPE)) {
                return new MathOperationDivide(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals(MathOperationSubtract.TYPE)) {
                return new MathOperationSubtract(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals(MathOperationPower.TYPE)) {
                return new MathOperationPower(
                        toMath((Element) e.getFirstChild()),
                        toMath((Element) e.getLastChild())
                        );
            } else if (type.equals(MathOperationRoot.TYPE)) {
                return new MathOperationRoot(
                        toMath((Element) e.getLastChild()),
                        toMath((Element) e.getFirstChild())
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
            } else if (tag.equals(MathBinaryOperation.NAME)) {
                if (Integer.parseInt(e.getAttribute(MathBinaryOperation.ATTR_OPERANDS)) == 2) {
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
        Element root = doc.getDocumentElement();
        return toMath((Element) root.getFirstChild());
    }
}
