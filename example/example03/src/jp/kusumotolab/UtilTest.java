package jp.kusumotolab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilTest {
	@Test
	public void plusTest01() {
		assertEquals(11, Util.plus(10));
	}

	@Test
	public void plusTest02() {
		assertEquals(101, Util.plus(100));
	}

	@Test
	public void minusTest01() {
		assertEquals(9, Util.minus(10));
	}

	@Test
	public void minusTest02() {
		assertEquals(-1, Util.minus(0));
	}

	@Test
	public void dummyTest01() {
		Util.dummy();
		assertTrue(true);
	}
}
