package org.teaminfty.math_dragon.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.teaminfty.math_dragon.exceptions.MathException;
import org.teaminfty.math_dragon.view.math.Symbol;
import org.teaminfty.math_dragon.view.math.Expression;
import org.teaminfty.math_dragon.view.math.operation.binary.Add;
import org.teaminfty.math_dragon.view.math.operation.binary.Divide;
import org.teaminfty.math_dragon.view.math.operation.binary.Multiply;
import org.teaminfty.math_dragon.view.math.operation.binary.Power;

public class TestModelHelper
{
	@Test
	public void moAddLong6Longm34() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(6);
		Symbol rc = new Symbol();
		rc.setFactor(-34);
		Add a = new Add(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(a));
		assertTrue(result instanceof Add);
		Add add = (Add) a;
		Expression tmp = add.getLeft();
		assertTrue(tmp instanceof Symbol);
		Symbol left = (Symbol) tmp;
		tmp = add.getRight();
		assertTrue(tmp instanceof Symbol);
		Symbol right = (Symbol) tmp;
		assertTrue(lc.equals(left));
		assertTrue(rc.equals(right));
		result = ModelHelper.toExpression(EvalEngine.eval(EvalHelper.eval(a)));
		assertTrue(result instanceof Symbol);
		Symbol c = new Symbol(-28, 0, 0, 0, null);
		assertTrue(c.equals((Symbol) result));
	}
	
	@Test
	public void moMulLong8Longm4() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(8);
		Symbol rc = new Symbol();
		rc.setFactor(-4);
		Multiply m = new Multiply(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(m));
		assertTrue(result instanceof Multiply);
	}
	
	@Test
	public void moDivLongm256Long8() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(-256);
		Symbol rc = new Symbol();
		rc.setFactor(8);
		Divide d = new Divide();
		d.set(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(d));
		assertTrue(result instanceof Divide);
	}
	
	@Test
	public void moPowLongm2Long12() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(-2);
		Symbol rc = new Symbol();
		rc.setFactor(12);
		Power p = new Power();
		p.set(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(p));
		assertTrue(result instanceof Power);
	}
	
	@Test
	public void moAddPiLong21() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(1);
		lc.setPiPow(1);
		Symbol rc = new Symbol();
		rc.setFactor(21);
		Add a = new Add();
		a.set(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(a));
		assertTrue(result instanceof Add);
	}
	
	@Test
	public void moMulLong3E() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(3);
		Symbol rc = new Symbol();
		rc.setFactor(1);
		rc.setEPow(1);
		Multiply m = new Multiply();
		m.set(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(m));
		assertTrue(result instanceof Multiply);
	}
	
	@Test
	public void moMulLong4I2() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(4);
		Symbol rc = new Symbol();
		rc.setFactor(2);
		rc.setIPow(1);
		Multiply m = new Multiply();
		m.set(lc, rc);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(m));
		assertTrue(result instanceof Multiply);
	}
	
	@Test
	public void moAddLong5AddEPi1() throws MathException
	{
		Symbol lc = new Symbol();
		lc.setFactor(5);
		Symbol mc = new Symbol(1, 1, 0, 0, null);
		Symbol rc = new Symbol(1, 0, 1, 0, null);
		Add ra = new Add(mc, rc);
		Add a = new Add(lc, ra);
		Expression result = ModelHelper.toExpression(EvalHelper.eval(a));
		assertTrue(result instanceof Add);
		Add add = (Add) result;
		Add radd = (Add) add.getRight();
		Symbol left = (Symbol) add.getLeft();
		Symbol middle = (Symbol) radd.getLeft();
		Symbol right = (Symbol) radd.getRight();
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
		result = ModelHelper.toExpression(EvalEngine.eval(EvalHelper.eval(a)));
		assertTrue(result instanceof Add);
		add = (Add) result;
		radd = (Add) add.getRight();
		Expression tmp = add.getLeft();
		assertTrue(tmp instanceof Symbol);
		left = (Symbol) tmp;
		tmp = radd.getLeft();
		assertTrue(tmp instanceof Symbol);
		middle = (Symbol) tmp;
		tmp = radd.getRight();
		assertTrue(tmp instanceof Symbol);
		right = (Symbol) tmp;
		assertTrue(lc.equals(left));
		assertTrue(mc.equals(middle));
		assertTrue(rc.equals(right));
	}
	
	@Test
	public void moAddPi1DivLong2Pi1() throws MathException {
	    // pi + 2/pi
	    Symbol cpi = new Symbol(1, 0, 1, 0, null);
	    Symbol rc = new Symbol(2, 0, 0, 0, null);
	    Divide d = new Divide(rc, cpi);
	    Add a = new Add(cpi, d);
	    Expression result = ModelHelper.toExpression(EvalHelper.eval(a));
	    assertTrue(result instanceof Add);
	    Add add = (Add) result;
	    Expression tmp = add.getRight();
	    assertTrue(tmp instanceof Divide);
	    Divide div = (Divide) tmp;
	    tmp = add.getLeft();
	    assertTrue(tmp instanceof Symbol);
	    Symbol c1 = (Symbol) tmp;
	    tmp = div.getLeft();
	    assertTrue(tmp instanceof Symbol);
	    Symbol c2 = (Symbol) tmp;
	    tmp = div.getRight();
	    assertTrue(tmp instanceof Symbol);
	    Symbol c3 = (Symbol) tmp;
	    assertTrue(cpi.equals(c1));
	    assertTrue(rc.equals(c2));
	    assertTrue(cpi.equals(c3));
	    result = ModelHelper.toExpression(EvalEngine.eval(EvalHelper.eval(a)));
	    // TODO test result
	}
}
