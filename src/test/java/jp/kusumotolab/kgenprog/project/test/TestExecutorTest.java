package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestExecutorTest {

	@Before
	public void before() {
		// 対象プロジェクトをビルドすべき
	}

	@Test
	public void exec01() {
		TestExecutor executor = new TestExecutor(null);
		TestResults results = executor.exec("example/example01/bin/", Arrays.asList("BuggyCalculatorTest"));
		assertEquals(4, results.getFailedTestResults().get(0).getRunCount());
		assertEquals(1, results.getFailedTestResults().get(0).getFailureCount());
	}
}
