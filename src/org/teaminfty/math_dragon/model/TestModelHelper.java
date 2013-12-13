package org.teaminfty.math_dragon.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.view.math.MathConstant;
import org.teaminfty.math_dragon.view.math.MathObject;
import org.teaminfty.math_dragon.view.math.MathOperationAdd;
import org.teaminfty.math_dragon.view.math.MathOperationDivide;
import org.teaminfty.math_dragon.view.math.MathOperationMultiply;
import org.teaminfty.math_dragon.view.math.MathOperationPower;

public class TestModelHelper
{
	@Test
	public void moAddLong6Longm34() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(6);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(-34);
		MathOperationAdd a = new MathOperationAdd(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong8Longm4() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(8);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(-4);
		MathOperationMultiply m = new MathOperationMultiply(lc, rc, 0, 0);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moDivLongm256Long8() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(-256);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(8);
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
		lc.setFactor(-2);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(12);
		MathOperationPower p = new MathOperationPower(0, 0);
		p.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(p.eval(), 0, 0);
		assertTrue(result instanceof MathOperationPower);
	}
	
	@Test
	public void moAddPiLong21() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(1);
		lc.setPiPow(1);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(21);
		MathOperationAdd a = new MathOperationAdd(0, 0);
		a.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong3E() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(3);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(1);
		rc.setePow(1);
		MathOperationMultiply m = new MathOperationMultiply(0, 0);
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moMulLong4I2() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(4);
		MathConstant rc = new MathConstant(0, 0);
		rc.setFactor(2);
		rc.setiPow(1);
		MathOperationMultiply m = new MathOperationMultiply(0, 0);
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
}
