package org.teaminfty.math_dragon.engine;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class that performs unit tests upon <tt>MathOperationAdd</tt>. All test
 * cases are evaluated with <tt>JUnit4</tt>.
 * 
 * @author Folkert van Verseveld
 * 
 */
public class TestMathOperationAdd
{
	MathOperationAdd add;

	public TestMathOperationAdd()
	{
		add = new MathOperationAdd();
	}

	@Test(expected = NotConstantException.class)
	public void approximate() throws MathException
	{
		add.approximate();
	}

	@Test(expected = NotConstantException.class)
	public void approximateLeft() throws MathException
	{
		add.set(new MathConstantNumber<Integer>(4), null);
		add.approximate();
	}

	@Test(expected = NotConstantException.class)
	public void approximateRight() throws MathException
	{
		add.set(null, new MathConstantNumber<Integer>(9));
		add.approximate();
	}

	@Test
	public void approximate1Add1() throws MathException
	{
		MathConstantNumber<Integer> lvalue = new MathConstantNumber<Integer>(1), rvalue = new MathConstantNumber<Integer>(
				1);
		add.set(lvalue, rvalue);
		assertTrue(lvalue.getValue() + rvalue.getValue() == add.approximate());
		add.set(rvalue, lvalue);
		assertTrue(lvalue.getValue() + rvalue.getValue() == add.approximate());
	}

	@Test
	public void approximate2Add5() throws MathException
	{
		MathConstantNumber<Integer> lvalue = new MathConstantNumber<Integer>(2), rvalue = new MathConstantNumber<Integer>(
				5);
		add.set(lvalue, rvalue);
		assertTrue(7 == add.approximate());
		add.set(rvalue, lvalue);
		assertTrue(7 == add.approximate());
	}
}
