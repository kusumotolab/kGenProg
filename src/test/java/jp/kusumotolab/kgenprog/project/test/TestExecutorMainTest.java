package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorMainTest {

	final static FullyQualifiedName buggyCalculator = new FullyQualifiedName("jp.kusumotolab.BuggyCalculator");
	final static FullyQualifiedName buggyCalculatorTest = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");

	final static FullyQualifiedName test01 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
	final static FullyQualifiedName test02 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
	final static FullyQualifiedName test03 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
	final static FullyQualifiedName test04 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

	@Before
	public void before() throws IOException {
		Files.delete(TestResults.getSerFilePath());
	}

	@Test
	public void mainTest01() throws Exception {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);

		TestExecutorMain.main(new String[] { //
				"-s", //
				buggyCalculator.toString(), //
				buggyCalculatorTest.toString() });

		// serialize対象のファイルがあるはず
		assertThat(Files.exists(TestResults.getSerFilePath()), is(true));

		final TestResults r = TestResults.deserialize();

		// example01で実行されたテストは4つのはず
		assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));
		assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));

		// テストの成否はこうなるはず
		assertThat(r.getTestResult(test01).failed, is(false));
		assertThat(r.getTestResult(test02).failed, is(false));
		assertThat(r.getTestResult(test03).failed, is(true));
		assertThat(r.getTestResult(test04).failed, is(false));

		// test01()ではBuggyCalculatorのみが実行されたはず
		assertThat(r.getTestResult(test01).getExecutedTargetFQNs(), is(containsInAnyOrder(buggyCalculator)));

		// test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
		assertThat(r.getTestResult(test01).getCoverages(buggyCalculator).getStatuses(), //
				is(contains(EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED, EMPTY, NOT_COVERED, EMPTY, COVERED)));

	}

}
