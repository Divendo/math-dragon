package org.teaminfty.math_dragon.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.view.math.MathSymbol;
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
		MathSymbol lc = new MathSymbol();
		lc.setFactor(6);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(-34);
		MathOperationAdd a = new MathOperationAdd(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(a));
		assertTrue(result instanceof MathOperationAdd);
		MathOperationAdd add = (MathOperationAdd) a;
		MathObject tmp = add.getLeft();
		assertTrue(tmp instanceof MathSymbol);
		MathSymbol left = (MathSymbol) tmp;
		tmp = add.getRight();
		assertTrue(tmp instanceof MathSymbol);
		MathSymbol right = (MathSymbol) tmp;
		assertTrue(lc.equals(left));
		assertTrue(rc.equals(right));
		result = ModelHelper.toMathObject(EvalEngine.eval(EvalHelper.eval(a)));
		assertTrue(result instanceof MathSymbol);
		MathSymbol c = new MathSymbol(-28, 0, 0, 0, null);
		assertTrue(c.equals((MathSymbol) result));
	}
	
	@Test
	public void moMulLong8Longm4() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(8);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(-4);
		MathOperationMultiply m = new MathOperationMultiply(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(m));
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moDivLongm256Long8() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(-256);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(8);
		MathOperationDivide d = new MathOperationDivide();
		d.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(d));
		assertTrue(result instanceof MathOperationDivide);
	}
	
	@Test
	public void moPowLongm2Long12() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(-2);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(12);
		MathOperationPower p = new MathOperationPower();
		p.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(p));
		assertTrue(result instanceof MathOperationPower);
	}
	
	@Test
	public void moAddPiLong21() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(1);
		lc.setPiPow(1);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(21);
		MathOperationAdd a = new MathOperationAdd();
		a.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(a));
		assertTrue(result instanceof MathOperationAdd);
	}
	
	@Test
	public void moMulLong3E() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(3);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(1);
		rc.setEPow(1);
		MathOperationMultiply m = new MathOperationMultiply();
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(m));
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moMulLong4I2() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(4);
		MathSymbol rc = new MathSymbol();
		rc.setFactor(2);
		rc.setIPow(1);
		MathOperationMultiply m = new MathOperationMultiply();
		m.set(lc, rc);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(m));
		assertTrue(result instanceof MathOperationMultiply);
	}
	
	@Test
	public void moAddLong5AddEPi1() throws MathException
	{
		MathSymbol lc = new MathSymbol();
		lc.setFactor(5);
		MathSymbol mc = new MathSymbol(1, 1, 0, 0, null);
		MathSymbol rc = new MathSymbol(1, 0, 1, 0, null);
		MathOperationAdd ra = new MathOperationAdd(mc, rc);
		MathOperationAdd a = new MathOperationAdd(lc, ra);
		MathObject result = ModelHelper.toMathObject(EvalHelper.eval(a));
		assertTrue(result instanceof MathOperationAdd);
		MathOperationAdd add = (MathOperationAdd) result;
		MathOperationAdd radd = (MathOperationAdd) add.getRight();
		MathSymbol left = (MathSymbol) add.getLeft();
		MathSymbol middle = (MathSymbol) radd.getLeft();
		MathSymbol right = (MathSymbol) radd.getRight();
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
		result = ModelHelper.toMathObject(EvalEngine.eval(EvalHelper.eval(a)));
		assertTrue(result instanceof MathOperationAdd);
		add = (MathOperationAdd) result;
		radd = (MathOperationAdd) add.getRight();
		MathObject tmp = add.getLeft();
		assertTrue(tmp instanceof MathSymbol);
		left = (MathSymbol) tmp;
		tmp = radd.getLeft();
		assertTrue(tmp instanceof MathSymbol);
		middle = (MathSymbol) tmp;
		tmp = radd.getRight();
		assertTrue(tmp instanceof MathSymbol);
		right = (MathSymbol) tmp;
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
	}
	
	@Test
	public void moAddPi1DivLong2Pi1() throws MathException {
	    // pi + 2/pi
	    MathSymbol cpi = new MathSymbol(1, 0, 1, 0, null);
	    MathSymbol rc = new MathSymbol(2, 0, 0, 0, null);
	    MathOperationDivide d = new MathOperationDivide(rc, cpi);
	    MathOperationAdd a = new MathOperationAdd(cpi, d);
	    MathObject result = ModelHelper.toMathObject(EvalHelper.eval(a));
	    assertTrue(result instanceof MathOperationAdd);
	    MathOperationAdd add = (MathOperationAdd) result;
	    MathObject tmp = add.getRight();
	    assertTrue(tmp instanceof MathOperationDivide);
	    MathOperationDivide div = (MathOperationDivide) tmp;
	    tmp = add.getLeft();
	    assertTrue(tmp instanceof MathSymbol);
	    MathSymbol c1 = (MathSymbol) tmp;
	    tmp = div.getLeft();
	    assertTrue(tmp instanceof MathSymbol);
	    MathSymbol c2 = (MathSymbol) tmp;
	    tmp = div.getRight();
	    assertTrue(tmp instanceof MathSymbol);
	    MathSymbol c3 = (MathSymbol) tmp;
	    assertTrue(cpi.equals(c1));
	    assertTrue(rc.equals(c2));
	    assertTrue(cpi.equals(c3));
	    result = ModelHelper.toMathObject(EvalEngine.eval(EvalHelper.eval(a)));
	    // TODO test result
	}
}
