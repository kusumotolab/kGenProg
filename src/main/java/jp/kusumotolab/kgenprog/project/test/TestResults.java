package jp.kusumotolab.kgenprog.project.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResults implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<TestResult> testResults;

	public TestResults() {
		testResults = new ArrayList<>();
	}

	public void add(final TestResult testResult) {
		this.testResults.add(testResult);
	}

	public void addAll(TestResults testResults) {
		this.testResults.addAll(testResults.getTestResults());
	}

	/**
	 * 失敗したテスト結果の一覧を取得．
	 * 
	 * @return 失敗したテスト結果s
	 */
	public List<TestResult> getFailedTestResults() {
		return this.testResults.stream().filter(r -> r.wasFailed()).collect(Collectors.toList());
	}

	// is necessary?
	public List<TestResult> getSuccessedTestResults() {
		return this.testResults.stream().filter(r -> !r.wasFailed()).collect(Collectors.toList());
	}

	public List<TestResult> getTestResults() {
		return testResults;
	}

	public List<FullyQualifiedName> getFailedTestNames() {
		return getFailedTestResults().stream().map(r -> r.getMethodName()).collect(Collectors.toList());
	}

	/**
	 * テストの成功率
	 * 
	 * @return
	 */
	public double getSuccessRate() {
		final int fail = getFailedTestResults().size();
		final int success = getSuccessedTestResults().size();
		return 1.0 * success / (success + fail);
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

	/*
	 * public void add(final Result result) { testResults.add(new TestResult(null,
	 * result, null)); }
	 */

	public static Path getSerFilePath() throws IOException {
		return Paths.get(System.getProperty("java.io.tmpdir") + "/kgenprog-testresults.ser");
	}

	public static void serialize(TestResults testResults) {
		try {
			if (Files.exists(getSerFilePath())) {
				Files.delete(TestResults.getSerFilePath());
			}
			Files.createFile(getSerFilePath());
			final ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(getSerFilePath()));
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
			in = new ObjectInputStream(Files.newInputStream(getSerFilePath()));
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

}
