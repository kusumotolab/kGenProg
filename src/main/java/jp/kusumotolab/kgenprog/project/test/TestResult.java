package jp.kusumotolab.kgenprog.project.test;

import java.io.Serializable;
import java.util.Map;

import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ExecutionStatus {
		/**
		 * Status flag for no items (value is 0x00).
		 */
		EMPTY,
		/**
		 * Status flag when all items are not covered (value is 0x01).
		 */
		NotCovered,
		/**
		 * Status flag when all items are covered (value is 0x02).
		 */
		Covered,
		/**
		 * Status flag when items are partly covered (value is 0x03). どういう時に起きるか不明．
		 */
		PARTLY_COVERED
	}

	private Map<Location, ExecutionStatus> executionCount;
	private TestCase testCase;
	private Result junitRunnerResult;

	public TestResult(TestCase testCase, Result junitRunnerResult, Map<Location, ExecutionStatus> executionCount) {
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
