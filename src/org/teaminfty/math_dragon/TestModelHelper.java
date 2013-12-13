package org.teaminfty.math_dragon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestModelHelper
{
	@Test
	public void moAddLong6Longm34() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 6;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = -34;
		MathOperationAdd a = new MathOperationAdd(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong8Longm4() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 8;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = -4;
		MathOperationMultiply m = new MathOperationMultiply(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moDivLongm256Long8() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = -256;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 8;
		MathOperationDivide d = new MathOperationDivide(0, 0);
		d.set(lc, rc);
		// TODO [times, -256, 8^(-1)]
		MathObject result = ModelHelper.toMathObject(d.eval(), 0, 0);
		assertTrue(result instanceof MathOperationDivide);
	}
	
	@Test
	public void moPowLongm2Long12() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = -2;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 12;
		MathOperationPower p = new MathOperationPower(0, 0);
		p.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(p.eval(), 0, 0);
		assertTrue(result instanceof MathOperationPower);
	}
	
	@Test
	public void moAddPiLong21() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 1;
		lc.piPow = 1;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 21;
		MathOperationAdd a = new MathOperationAdd(0, 0);
		a.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong3E() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 3;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 1;
		rc.ePow = 1;
		MathOperationMultiply m = new MathOperationMultiply(0, 0);
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moMulLong4I2() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 4;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 2;
		rc.iPow = 1;
		MathOperationMultiply m = new MathOperationMultiply(0, 0);
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
}
