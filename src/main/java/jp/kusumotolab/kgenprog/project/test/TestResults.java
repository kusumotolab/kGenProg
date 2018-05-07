package jp.kusumotolab.kgenprog.project.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResults implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<TestResult> testResults;

	public TestResults() {
		testResults = new ArrayList<>();
	}

	public static String getSerFilename() {
		return "tmp/__testresults.ser";
	}

	public static void serialize(TestResults testResults) {
		try {
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getSerFilename()));
			out.writeObject(testResults);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static TestResults deserialize() {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(getSerFilename()));
			final TestResults testResults = (TestResults) in.readObject();
			in.close();
			return testResults;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 失敗したテスト結果の一覧を取得．
	 * 
	 * @return 失敗したテスト結果s
	 */
	public List<TestResult> getFailedTestResults() {
		return this.testResults.stream().filter(r -> !r.wasSuccessful()).collect(Collectors.toList());
	}

	// necessary?
	public List<TestResult> getSuccessedTestResults() {
		return this.testResults.stream().filter(r -> r.wasSuccessful()).collect(Collectors.toList());
	}

	/**
	 * テストの成功率
	 * 
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

	public void add(final Result result) {
		testResults.add(new TestResult(null, result, null));
	}

}
