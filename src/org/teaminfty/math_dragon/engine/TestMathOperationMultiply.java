package org.teaminfty.math_dragon.engine;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class that performs unit tests upon <tt>MathOperationAdd</tt>. All test
 * cases are evaluated with <tt>JUnit4</tt>.
 * @author Folkert van Verseveld
 *
 */
public class TestMathOperationMultiply
{
	MathOperationMultiply mul;
	
	public TestMathOperationMultiply()
	{
		mul = new MathOperationMultiply();
	}
	
	@BeforeClass
	public static void init()
	{
		new TestMathOperationMultiply();
	}
	
	@Test(expected = NotConstantException.class)
	public void approximate() throws MathException
	{
		mul.approximate();
	}
	
	@Test(expected = NotConstantException.class)
	public void approximateLeft() throws MathException
	{
		mul.set(new MathConstantNumber(4), null);
		mul.approximate();
	}
	
	@Test(expected = NotConstantException.class)
	public void approximateRight() throws MathException
	{
		mul.set(null, new MathConstantNumber(9));
		mul.approximate();
	}
	
	@Test
    public void t1Mul0() throws MathException
    {
    	assertTrue(0 == (new MathOperationMultiply(1, 0)).approximate());
    	assertTrue(0 == (new MathOperationMultiply(0, 1)).approximate());
    }
	
	@Test
	public void set()
	{
		mul.set(423, 318);
		assertTrue(423 == ((MathConstantNumber)mul.getLeft()).getValue());
		assertTrue(318 == ((MathConstantNumber)mul.getRight()).getValue());
	}
	
	@Test
	public void swap()
	{
		MathConstantNumber value = new MathConstantNumber(4);
		mul.set(null, value);
		mul.swap();
		assertEquals(value, mul.getLeft());
		mul.set(value, null);
		mul.swap();
		assertEquals(value, mul.getRight());
	}
    
    @Test
    public void solve14Mul36() throws MathException
    {
    	MathOperationMultiply mul = new MathOperationMultiply(14, 36);
    	assertTrue(504 == mul.approximate());
    	MathObject o = mul.solveSymb();
    	assertTrue(o instanceof MathConstantNumber);
    	assertTrue(504 == o.approximate());
    	// swap
    	mul = new MathOperationMultiply(36, 14);
    	assertTrue(504 == mul.approximate());
    	o = mul.solveSymb();
    	assertTrue(o instanceof MathConstantNumber);
    	assertTrue(504 == o.approximate());
    }
}
