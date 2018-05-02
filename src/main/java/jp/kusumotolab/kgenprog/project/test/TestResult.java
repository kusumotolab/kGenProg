package jp.kusumotolab.kgenprog.project.test;

import java.util.Map;

import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResult {
	private Map<Location, Integer> executionCount;
	private TestCase testCase;
	private Result junitRunnerResult;

	public TestResult(TestCase testCase, Result junitRunnerResult, Map<Location, Integer> executionCount) {
		this.testCase = testCase;
		this.junitRunnerResult = junitRunnerResult;
		this.executionCount = executionCount;
	}

	public boolean wasSuccessful() {
		return junitRunnerResult.wasSuccessful();
	}

	public int getFailureCount() {
		return junitRunnerResult.getFailureCount();
	}

	public int getRunCount() {
		return junitRunnerResult.getRunCount();
	}
}
