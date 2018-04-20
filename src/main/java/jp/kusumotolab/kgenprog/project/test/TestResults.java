package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.Map;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResults {
	private List<TestResult> testResults;

	//	hitori
	public List<TestResult> getFailedTestResults(){
		return null;
	}

	//	hitori
	public List<TestResult> getSuccessedTestResults(){
		return null;
	}

	//	hitori
	public double getSuccessRate(){
		return Double.NaN;
	}

	//hitori
	public Map<Location, Integer> getExecutedFailedTestCounts() {
		return null;
	}

	//hitori
	public Map<Location, Integer> getNotExecutedFailedTestCounts() {
		return null;
	}

	//hitori
	public Map<Location, Integer> getExecutedPassedTestCounts() {
		return null;
	}

	//hitori
	public Map<Location, Integer> getNotExecutedPassedTestCounts() {
		return null;
	}

}
