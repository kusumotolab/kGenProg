package jp.kusumotolab.kgenprog.project.test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestResultsTest {

	final static FullyQualifiedName buggyCalculator = new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");
	final static FullyQualifiedName buggyCalculatorTest = new TestFullyQualifiedName(
			"jp.kusumotolab.BuggyCalculatorTest");

	final static FullyQualifiedName test01 = new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
	final static FullyQualifiedName test02 = new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
	final static FullyQualifiedName test03 = new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
	final static FullyQualifiedName test04 = new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

	private TestResults generateTestResultsForExample01() throws Exception {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(Paths.get(outDir));
		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		return executor.exec(Arrays.asList(buggyCalculator), Arrays.asList(buggyCalculatorTest));
	}

	/**
	 * FLで用いる4メトリクスのテスト
	 */
	@Test
	public void checkFLMetricsInTestResultsForExample01() throws Exception {
		final TestResults r = generateTestResultsForExample01();
		final FullyQualifiedName bc = buggyCalculator; // alias for
														// buggycalculator

		// example01でのbcの6行目（n++;）のテスト結果はこうなるはず
		assertThat(r.getPassedTestFQNsExecutingTheStatement(bc, 6), is(containsInAnyOrder(test01, test02)));
		assertThat(r.getFailedTestFQNsExecutingTheStatement(bc, 6), is(empty()));
		assertThat(r.getPassedTestFQNsNotExecutingTheStatement(bc, 6), is(containsInAnyOrder(test04)));
		assertThat(r.getFailedTestFQNsNotExecutingTheStatement(bc, 6), is(containsInAnyOrder(test03)));

		// example01でのbcの10行目（return n;）のテスト結果はこうなるはず
		assertThat(r.getPassedTestFQNsExecutingTheStatement(bc, 10), is(containsInAnyOrder(test01, test02, test04)));
		assertThat(r.getFailedTestFQNsExecutingTheStatement(bc, 10), is(containsInAnyOrder(test03)));
		assertThat(r.getPassedTestFQNsNotExecutingTheStatement(bc, 10), is(empty()));
		assertThat(r.getFailedTestFQNsNotExecutingTheStatement(bc, 10), is(empty()));
	}

	/**
	 * toString()のテスト．JSON形式の確認
	 */
	@Test
	public void testToString() throws Exception {
		final TestResults r = generateTestResultsForExample01();
		final String expected = "" //
				+ "[\n" //
				+ "  {\n" //
				+ "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test04\",\n" //
				+ "    \"wasFailed\": false,\n" //
				+ "    \"coverages\": [\n" //
				+ "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 1, 0, 2, 0, 2]}\n" //
				+ "    ]\n" //
				+ "  },\n" //
				+ "  {\n" //
				+ "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test03\",\n" //
				+ "    \"wasFailed\": true,\n" //
				+ "    \"coverages\": [\n" //
				+ "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 1, 0, 2, 0, 2]}\n" //
				+ "    ]\n" //
				+ "  },\n" //
				+ "  {\n" //
				+ "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test02\",\n" //
				+ "    \"wasFailed\": false,\n" //
				+ "    \"coverages\": [\n" //
				+ "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 2, 0, 1, 0, 2]}\n" //
				+ "    ]\n" //
				+ "  },\n" //
				+ "  {\n" //
				+ "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test01\",\n" //
				+ "    \"wasFailed\": false,\n" //
				+ "    \"coverages\": [\n" //
				+ "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 2, 0, 1, 0, 2]}\n" //
				+ "    ]\n" //
				+ "  }\n" //
				+ "]\n";

		assertThat(normalizeCrLf(r.toString()), is(normalizeCrLf(expected)));
	}

	/**
	 * 単純なserialize -> deserializeの確認
	 */
	@Test
	public void testSerializeDeserialize01() {
		final TestResults r1 = new TestResults();

		// ダミーな内部要素を追加
		r1.add(new TestResult(test01, false, new HashMap<>()));
		r1.add(new TestResult(test03, false, new HashMap<>()));

		// serializeして
		TestResults.serialize(r1);

		// ファイルが存在するはず
		assertThat(TestResults.getSerFilePath().toFile().exists(), is(true));

		// deserializeして
		final TestResults r2 = TestResults.deserialize();

		// 要素が正しいはず
		assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
	}

	/**
	 * serialize -> 要素書き換え -> deserializeの確認
	 */
	@Test
	public void testSerializeDeserialize02() {
		final TestResults r1 = new TestResults();

		// ダミーな内部要素を追加
		r1.add(new TestResult(test01, false, new HashMap<>()));

		// serializeして
		TestResults.serialize(r1);

		// serialize後に03を追加
		r1.add(new TestResult(test03, false, new HashMap<>()));

		// deserializeして
		final TestResults r2 = TestResults.deserialize();

		// 要素が正しいはず
		assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01)));
	}

	/**
	 * serialize -> serialize -> deserializeの確認
	 */
	@Test
	public void testSerializeDeserialize03() {
		final TestResults r1 = new TestResults();

		// ダミーな内部要素を追加
		r1.add(new TestResult(test01, false, new HashMap<>()));

		// serializeして
		TestResults.serialize(r1);

		// serialize後に03を追加
		r1.add(new TestResult(test03, false, new HashMap<>()));

		// serializeして
		TestResults.serialize(r1);

		// deserializeして
		final TestResults r2 = TestResults.deserialize();

		// 要素が正しいはず
		assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
	}

	/**
	 * 重複要素を持つ要素のserialize -> deserializeの確認
	 */
	@Test
	public void testSerializeDeserialize04() {
		final TestResults r1 = new TestResults();

		// ダミーな内部要素を追加（重複するtest03を追加）
		r1.add(new TestResult(test01, false, new HashMap<>()));
		r1.add(new TestResult(test03, false, new HashMap<>()));
		r1.add(new TestResult(test03, false, new HashMap<>()));
		r1.add(new TestResult(test03, false, new HashMap<>()));

		// serializeして
		TestResults.serialize(r1);

		// ファイルが存在するはず
		assertThat(TestResults.getSerFilePath().toFile().exists(), is(true));

		// deserializeして
		final TestResults r2 = TestResults.deserialize();

		// 要素が正しいはず
		assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
	}

	/**
	 * いきなりdeserializeした際の確認
	 */
	@Test
	public void testSerializeDeserialize05() {
		// runtime exceptionを隠すためにsystem.errを退避して無効化
		final PrintStream ps = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) {
				// 何もしないwriter
			}
		}));

		// serializeファイルを消しておいて
		TestResults.getSerFilePath().toFile().delete();

		// deserializeでnullが返ってくるはず
		assertThat(TestResults.deserialize(), is(nullValue()));

		// system.errを戻しておく
		System.setErr(ps);
	}

	/**
	 * 改行コードのnormalizer
	 * 
	 * @param s
	 * @return
	 */
	private String normalizeCrLf(final String s) {
		return s.replaceAll("\\r|\\n", "\n");
	}
}
