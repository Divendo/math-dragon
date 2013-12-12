package org.teaminfty.math_dragon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestModelHelper {
	@Test
	public void moAddLong6Longm34() throws MathException {
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 6;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = -34;
		MathOperationAdd a = new MathOperationAdd(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong8Longm4() throws MathException {
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = 8;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = -4;
		MathOperationMultiply m = new MathOperationMultiply(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
//	@Test
	public void moMulLongm256Long8() throws MathException {
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
	public void moPowLongm2Long12() throws MathException {
		MathConstant lc = new MathConstant(0, 0);
		lc.factor = -2;
		MathConstant rc = new MathConstant(0, 0);
		rc.factor = 12;
		MathOperationPower p = new MathOperationPower(0, 0);
		p.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(p.eval(), 0, 0);
		assertTrue(result instanceof MathOperationPower);
	}
}
