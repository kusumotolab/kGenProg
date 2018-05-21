package jp.kusumotolab;

import static org.junit.Assert.*;

import org.junit.Test;

public class BuggyCalculatorTest {
	@Test
	public void test01() {
		assertEquals(9, new BuggyCalculator().close_to_zero(10));
	}

	@Test
	public void test02() {
		assertEquals(99, new BuggyCalculator().close_to_zero(100));
	}

	@Test
	public void test03() {
		assertEquals(0, new BuggyCalculator().close_to_zero(0)); // fail: 0 should be 0
	}

	@Test
	public void test04() {
		assertEquals(-9, new BuggyCalculator().close_to_zero(-10));
	}
}
