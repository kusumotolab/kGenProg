package jp.kusumotolab.kgenprog.project.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResults {

	private final List<TestResult> testResults;

	public TestResults() {
		testResults = new ArrayList<>();
	}

	/**
	 * 失敗したテスト結果の一覧を取得．
	 * 
	 * @return 失敗したテスト結果s
	 */
	public List<TestResult> getFailedTestResults() {
		final List<TestResult> testResults = new ArrayList<>();
		for (final TestResult testResult : this.testResults) {
			if (testResult.wasSuccessful()) {
				continue;
			}
			testResults.add(testResult);
		}
		return testResults;
	}

	// necessary?
	public List<TestResult> getSuccessedTestResults() {
		final List<TestResult> testResults = new ArrayList<>();
		for (final TestResult testResult : this.testResults) {
			if (! testResult.wasSuccessful()) {
				continue;
			}
			testResults.add(testResult);
		}
		return testResults;
	}

	/**
	 * テストの成功率
	 * @return
	 */
	public double getSuccessRate() {
		final int fail = getFailedTestResults().size();
		final int success = getSuccessedTestResults().size();
		return success / (success + fail);
	}

	public Map<Location, Integer> getExecutedFailedTestCounts() {
		return null;
	}

	public Map<Location, Integer> getNotExecutedFailedTestCounts() {
		return null;
	}

	public Map<Location, Integer> getExecutedPassedTestCounts() {
		return null;
	}

	public Map<Location, Integer> getNotExecutedPassedTestCounts() {
		return null;
	}

	public void add(final String testClass, final Result result) {
		final TestCase testCase = getTestCase(testClass); // stub
		testResults.add(new TestResult(testCase, result, null));
	}

	// stub for generating TestCase
	private TestCase getTestCase(final String testClass) {
		return new TestCase(null, testClass, -1);
	}

}
