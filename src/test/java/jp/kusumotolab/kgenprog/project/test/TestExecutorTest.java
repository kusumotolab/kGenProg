package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestExecutorTest {

	@Before
	public void before() throws IOException {
		ClassPathHacker.addFile("example/example02/bin/");
	}

	@Test
	public void exec01() throws Exception {
		TestResults tr = new TestExecutor().exec( //
				Arrays.asList("jp.kusumotolab.BuggyCalculator"), //
				Arrays.asList("jp.kusumotolab.BuggyCalculatorTest"));
		assertEquals(1, tr.getFailedTestResults().size());
		assertEquals(4, tr.getFailedTestResults().get(0).getRunCount());
		assertEquals(1, tr.getFailedTestResults().get(0).getFailureCount());
	}

	@Test
	public void exec02() throws Exception {
		TestResults tr = new TestExecutor().exec( //
				Arrays.asList("jp.kusumotolab.BuggyCalculator", "jp.kusumotolab.Util"),
				Arrays.asList("jp.kusumotolab.BuggyCalculatorTest", "jp.kusumotolab.UtilTest"));
		assertEquals(1, tr.getFailedTestResults().size());
		assertEquals(4, tr.getFailedTestResults().get(0).getRunCount());
		assertEquals(1, tr.getFailedTestResults().get(0).getFailureCount());
		assertEquals(1, tr.getSuccessedTestResults().size());
		assertEquals(5, tr.getSuccessedTestResults().get(0).getRunCount());
	}
}
