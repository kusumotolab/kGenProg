package jp.kusumotolab.kgenprog.project.test;

import java.util.Map;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResult {
	private boolean isSuccess;
	private Map<Location,Integer> executionCount;
	private TestCase testCase;
	private String message;

	public TestResult(boolean isSuccess, Map<Location, Integer> executionCount, TestCase testCase, String message) {
		this.isSuccess = isSuccess;
		this.executionCount = executionCount;
		this.testCase = testCase;
		this.message = message;
	}
}
