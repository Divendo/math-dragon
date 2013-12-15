package org.teaminfty.math_dragon.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
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
		MathOperationAdd add = (MathOperationAdd) a;
		MathObject tmp = add.getLeft();
		assertTrue(tmp instanceof MathConstant);
		MathConstant left = (MathConstant) tmp;
		tmp = add.getRight();
		assertTrue(tmp instanceof MathConstant);
		MathConstant right = (MathConstant) tmp;
		assertTrue(lc.equals(left));
		assertTrue(rc.equals(right));
		result = ModelHelper.toMathObject(EvalEngine.eval(a.eval()), 0, 0);
		assertTrue(result instanceof MathConstant);
		MathConstant c = new MathConstant(-28, 0, 0, 0, 0, 0);
		assertTrue(c.equals((MathConstant) result));
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
		rc.setEPow(1);
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
		rc.setIPow(1);
		MathOperationMultiply m = new MathOperationMultiply(0, 0);
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(m.eval(), 0, 0);
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moAddLong5AddEPi1() throws MathException
	{
		MathConstant lc = new MathConstant(0, 0);
		lc.setFactor(5);
		MathConstant mc = new MathConstant(1, 1, 0, 0, 0, 0);
		MathConstant rc = new MathConstant(1, 0, 1, 0, 0, 0);
		MathOperationAdd ra = new MathOperationAdd(mc, rc, 0, 0);
		MathOperationAdd a = new MathOperationAdd(lc, ra, 0, 0);
		MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
		MathOperationAdd add = (MathOperationAdd) result;
		MathOperationAdd radd = (MathOperationAdd) add.getRight();
		MathConstant left = (MathConstant) add.getLeft();
		MathConstant middle = (MathConstant) radd.getLeft();
		MathConstant right = (MathConstant) radd.getRight();
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
		result = ModelHelper.toMathObject(EvalEngine.eval(a.eval()), 0, 0);
		assertTrue(result instanceof MathOperationAdd);
		add = (MathOperationAdd) result;
		radd = (MathOperationAdd) add.getRight();
		MathObject tmp = add.getLeft();
		assertTrue(tmp instanceof MathConstant);
		left = (MathConstant) tmp;
		tmp = radd.getLeft();
		assertTrue(tmp instanceof MathConstant);
		middle = (MathConstant) tmp;
		tmp = radd.getRight();
		assertTrue(tmp instanceof MathConstant);
		right = (MathConstant) tmp;
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
	}
	
	@Test
	public void moAddPi1DivLong2Pi1() throws MathException {
	    // pi + 2/pi
	    MathConstant cpi = new MathConstant(1, 0, 1, 0, 0, 0);
	    MathConstant rc = new MathConstant(2, 0, 0, 0, 0, 0);
	    MathOperationDivide d = new MathOperationDivide(rc, cpi, 0, 0);
	    MathOperationAdd a = new MathOperationAdd(cpi, d, 0, 0);
	    MathObject result = ModelHelper.toMathObject(a.eval(), 0, 0);
	    assertTrue(result instanceof MathOperationAdd);
	    MathOperationAdd add = (MathOperationAdd) result;
	    MathObject tmp = add.getRight();
	    assertTrue(tmp instanceof MathOperationDivide);
	    MathOperationDivide div = (MathOperationDivide) tmp;
	    tmp = add.getLeft();
	    assertTrue(tmp instanceof MathConstant);
	    MathConstant c1 = (MathConstant) tmp;
	    tmp = div.getLeft();
	    assertTrue(tmp instanceof MathConstant);
	    MathConstant c2 = (MathConstant) tmp;
	    tmp = div.getRight();
	    assertTrue(tmp instanceof MathConstant);
	    MathConstant c3 = (MathConstant) tmp;
	    assertTrue(cpi.equals(c1));
	    assertTrue(rc.equals(c2));
	    assertTrue(cpi.equals(c3));
	    result = ModelHelper.toMathObject(EvalEngine.eval(a.eval()), 0, 0);
	    // TODO test result
	}
}
