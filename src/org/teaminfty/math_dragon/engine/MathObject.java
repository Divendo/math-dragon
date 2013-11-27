package org.teaminfty.math_dragon.engine;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents a mathematical object
 * 
 * @author Mark Kamsma
 * @author Folkert van Verseveld
 */
public abstract class MathObject
{
	/**
	 * Returns the unique name of this math object. This guarantees for
	 * mathematical objects <tt>a</tt> and <tt>b</tt> that if
	 * <tt>a.getName() == b.getName()</tt> then both are of the same class.
	 */
	public abstract String getName();

	/**
	 * Solves this math object symbolically. If it is not possible to reduce the
	 * current mathematical object, <tt>this</tt> is returned. <tt>null</tt>
	 * should <b>never</b> be returned.
	 * 
	 * @return The symbolic solution of this math object. Returns <tt>this</tt>
	 *         when it could not be reduced/simplified.
	 */
	public abstract MathObject solveSymb();

	/**
	 * Approximates the value of this math object, this is only possible if this
	 * math object evaluates into a constant value
	 * 
	 * @return The approximation of this math object
	 * @throws MathException
	 * 
	 * @throws NotConstantException
	 *             If this math object doesn't evaluate to a constant value
	 */
	public abstract double approximate() throws MathException;

	/**
	 * Returns whether or not this math object will evaluate into a constant
	 * value
	 * 
	 * @return Whether or not this math object will evaluate into a constant
	 *         value
	 */
	public abstract boolean isConstant();

	/**
	 * Serialize current mathematical object configurations into <tt>root</tt>
	 * in XML file format. Subclasses with different behavior (i.e. have at
	 * least one specific member). <b>must</b> override this method.
	 * 
	 * @param doc
	 *            The XML document this instance will be written in.
	 * @param root
	 *            The document section where this mathematical object will be
	 *            written in.
	 * @throws DOMException
	 *             Thrown when invalid elements are added into <tt>root</tt>.
	 *             This should not happen under (any?) normal circumstances.
	 */
	public void write(Document doc, Element root) throws DOMException
	{
		root.appendChild(doc.createAttribute(getName()));
	}
	// TODO Add a method that reads the object from an XML element (could be a
	// static method that creates an object from the XML element)
}
