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

public class TestProcessBuilderTest {

	final static FullyQualifiedName test01 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
	final static FullyQualifiedName test02 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
	final static FullyQualifiedName test03 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
	final static FullyQualifiedName test04 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");
	final static FullyQualifiedName buggyCalculator = new FullyQualifiedName("jp.kusumotolab.BuggyCalculator");

	@Before
	public void before() throws IOException {
		Files.deleteIfExists(TestResults.getSerFilePath());
	}

	@Test
	public void testProcessBuilderBuild01() {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);

		// main
		final TestProcessBuilder builder = new TestProcessBuilder(targetProject);
		builder.start();
		final TestResults r = TestResults.deserialize();

		// テストの結果はこうなるはず
		assertThat(r.getExecutedTestFQNs().size(), is(4));
		assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));
		assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));
		assertThat(r.getTestResult(test01).failed, is(false));
		assertThat(r.getTestResult(test02).failed, is(false));
		assertThat(r.getTestResult(test03).failed, is(true));
		assertThat(r.getTestResult(test04).failed, is(false));

		// BuggyCalculatorTest.test01 実行によるbuggyCalculatorのカバレッジはこうなるはず
		assertThat(r.getTestResult(test01).getCoverages(buggyCalculator).getStatuses(), is(contains( //
				EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED, EMPTY, NOT_COVERED, EMPTY, COVERED)));

		// BuggyCalculatorTest.test04 実行によるカbuggyCalculatorのバレッジはこうなるはず
		assertThat(r.getTestResult(test04).getCoverages(buggyCalculator).getStatuses(), is(contains( //
				EMPTY, EMPTY, COVERED, EMPTY, COVERED, NOT_COVERED, EMPTY, COVERED, EMPTY, COVERED)));
	}

}
