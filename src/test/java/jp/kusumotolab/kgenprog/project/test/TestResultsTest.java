package jp.kusumotolab.kgenprog.project.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestResultsTest {

	final static FullyQualifiedName buggyCalculator = new FullyQualifiedName("jp.kusumotolab.BuggyCalculator");
	final static FullyQualifiedName buggyCalculatorTest = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");

	final static FullyQualifiedName test01 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
	final static FullyQualifiedName test02 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
	final static FullyQualifiedName test03 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
	final static FullyQualifiedName test04 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

	private TestResults generateTestResultsForExample01() throws Exception {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);
		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		return executor.exec(Arrays.asList(buggyCalculator), Arrays.asList(buggyCalculatorTest));
	}

	@Test
	public void checkFLMetricsInTestResultsForExample01() throws Exception {
		final TestResults r = generateTestResultsForExample01();
		final FullyQualifiedName bc = buggyCalculator; // just an alias

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
	 * toString()のテスト
	 * @throws Exception
	 */
	@Test
	public void xx() throws Exception {
		final TestResults r = generateTestResultsForExample01();
		final String expected = "" //
				+ "[" //
				+ "  {" //
				+ "    \"executedTestFqn\": \"jp.kusumotolab.BuggyCalculatorTest.test04\"," //
				+ "    \"wasFailed\": false," //
				+ "    \"coverages\": [" //
				+ "      {\"executedTargetFqn\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 1, 0, 2, 0, 2]}" //
				+ "    ]" //
				+ "  }," //
				+ "  {" //
				+ "    \"executedTestFqn\": \"jp.kusumotolab.BuggyCalculatorTest.test03\"," //
				+ "    \"wasFailed\": true," //
				+ "    \"coverages\": [" //
				+ "      {\"executedTargetFqn\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 1, 0, 2, 0, 2]}" //
				+ "    ]" //
				+ "  }," //
				+ "  {" //
				+ "    \"executedTestFqn\": \"jp.kusumotolab.BuggyCalculatorTest.test02\"," //
				+ "    \"wasFailed\": false," //
				+ "    \"coverages\": [" //
				+ "      {\"executedTargetFqn\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 2, 0, 1, 0, 2]}" //
				+ "    ]" //
				+ "  }," //
				+ "  {" //
				+ "    \"executedTestFqn\": \"jp.kusumotolab.BuggyCalculatorTest.test01\"," //
				+ "    \"wasFailed\": false," //
				+ "    \"coverages\": [" //
				+ "      {\"executedTargetFqn\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 0, 2, 0, 2, 2, 0, 1, 0, 2]}" //
				+ "    ]" //
				+ "  }" //
				+ "]";

		assertThat(normalize(r.toString()), is(normalize(expected)));
	}

	private String normalize(final String s) {
		return s.replaceAll("\\r|\\n", "");
	}
}
