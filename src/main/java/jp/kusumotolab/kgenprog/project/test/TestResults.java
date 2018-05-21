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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jp.kusumotolab.kgenprog.project.Location;

public class TestResults implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<FullyQualifiedName, TestResult> value;

	public TestResults() {
		value = new HashMap<>();
	}

	public void add(final TestResult testResult) {
		this.value.put(testResult.executedTestFQN, testResult);
	}

	public void addAll(final TestResults testResults) {
		testResults.value.forEach(this.value::putIfAbsent);
	}

	/**
	 * 失敗したテストのFQN一覧を取得．
	 * @return 失敗したテスト結果s
	 */
	public List<TestResult> getFailedTestResults() {
		return this.value.values().stream().filter(r -> r.failed).collect(Collectors.toList());
	}

	/**
	 * 成功したテストのFQN一覧を取得．
	 * @return 成功したテスト結果s
	 */
	public List<TestResult> getSuccessedTestResults() {
		return this.value.values().stream().filter(r -> !r.failed).collect(Collectors.toList());
	}

	/**
	 * obsoleted
	 * @return
	 */
	@Deprecated
	public List<TestResult> getTestResults() {
		return value.values().stream().collect(Collectors.toList());
	}

	/**
	 * 実行されたテストメソッドのFQN一覧を返す．
	 * @return
	 */
	public Set<FullyQualifiedName> getExecutedTestFQNs() {
		return this.value.keySet();
	}

	/**
	 * 実行された単一テストメソッドの結果を返す．
	 * @param fqn 対象のテストメソッドFQN
	 * @return
	 */
	public TestResult getTestResult(final FullyQualifiedName fqn) {
		//TODO if null 
		return this.value.get(fqn);
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

	/**
	 * FLで用いる4メトリクスのParameterized-Method
	 * 
	 * @param targetFqn 計算対象クラスのFQN
	 * @param lineNumber 計算対象クラスの行番号
	 * @param status 実行されたか否か
	 * @param failed テストの成否
	 * @return
	 */
	private List<FullyQualifiedName> getTestFQNs(final FullyQualifiedName targetFqn, final int lineNumber,
			final Coverage.Status status, final boolean failed) {
		final List<FullyQualifiedName> result = new ArrayList<>();
		for (final TestResult testResult : this.value.values()) {
			final Coverage coverage = testResult.getCoverages(targetFqn);
			final Coverage.Status _status = coverage.getStatuses().get(lineNumber - 1);
			if (status == _status && failed == testResult.failed) {
				result.add(testResult.executedTestFQN);
			}
		}
		return result;
	}

	/**
	 * a_ef
	 * @param targetFqn ターゲットクラスのFQN
	 * @param lineNumber ターゲットクラスの行番号
	 * @return a_ef
	 */
	public List<FullyQualifiedName> getFailedTestFQNsExecutingTheStatement(final FullyQualifiedName targetFqn,
			final int lineNumber) {
		return getTestFQNs(targetFqn, lineNumber, Coverage.Status.COVERED, true);
	}

	/**
	 * a_ep
	 * @param targetFqn ターゲットクラスのFQN
	 * @param lineNumber ターゲットクラスの行番号
	 * @return
	 */
	public List<FullyQualifiedName> getPassedTestFQNsExecutingTheStatement(final FullyQualifiedName targetFqn,
			final int lineNumber) {
		return getTestFQNs(targetFqn, lineNumber, Coverage.Status.COVERED, false);
	}

	/**
	 * a_nf
	 * @param targetFqn ターゲットクラスのFQN
	 * @param lineNumber ターゲットクラスの行番号
	 * @return
	 */
	public List<FullyQualifiedName> getFailedTestFQNsNotExecutingTheStatement(final FullyQualifiedName targetFqn,
			final int lineNumber) {
		return getTestFQNs(targetFqn, lineNumber, Coverage.Status.NOT_COVERED, true);
	}

	/**
	 * a_np
	 * @param targetFqn ターゲットクラスのFQN
	 * @param lineNumber ターゲットクラスの行番号
	 * @return
	 */
	public List<FullyQualifiedName> getPassedTestFQNsNotExecutingTheStatement(final FullyQualifiedName targetFqn,
			final int lineNumber) {
		return getTestFQNs(targetFqn, lineNumber, Coverage.Status.NOT_COVERED, false);
	}

	@Deprecated
	public Map<Location, Integer> getExecutedFailedTestCounts() {
		return null;
	}

	@Deprecated
	public Map<Location, Integer> getNotExecutedFailedTestCounts() {
		return null;
	}

	@Deprecated
	public Map<Location, Integer> getExecutedPassedTestCounts() {
		return null;
	}

	@Deprecated
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
			Files.deleteIfExists(TestResults.getSerFilePath());
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
		try {
			ObjectInputStream in = new ObjectInputStream(Files.newInputStream(getSerFilePath()));
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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		sb.append(String.join(",\n", //
				this.value.values().stream().map(v -> v.toString(2)).collect(Collectors.toList())));
		sb.append("\n");
		sb.append("]\n");
		return sb.toString();
	}
}
