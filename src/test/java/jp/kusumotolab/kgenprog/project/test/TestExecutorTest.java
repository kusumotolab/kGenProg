package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorTest {

	@Before
	public void before() throws IOException {
	}

	@Test
	public void exec01() throws Exception {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";

		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);

		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		final TestResults r = executor.exec( //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculator")), //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest")));

		assertThat(r.getTestResults().size(), is(4));
		assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));

		assertThat(r.getTestResults().get(0).wasFailed(), is(false));
		assertThat(r.getTestResults().get(1).wasFailed(), is(false));
		assertThat(r.getTestResults().get(2).wasFailed(), is(true));
		assertThat(r.getTestResults().get(3).wasFailed(), is(false));

		assertThat(r.getTestResults().get(0).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test01"));
		assertThat(r.getTestResults().get(1).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test02"));
		assertThat(r.getTestResults().get(2).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test03"));
		assertThat(r.getTestResults().get(3).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test04"));

		// BuggyCalculatorTest.test01 実行によるカバレッジはこうなるはず
		assertThat(r.getTestResults().get(0).getCoverages().get(0).getStatuses(), is(contains( //
				EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED, EMPTY, NOT_COVERED, EMPTY, COVERED)));

		// BuggyCalculatorTest.test04 によるカバレッジはこうなるはず
		assertThat(r.getTestResults().get(3).getCoverages().get(0).getStatuses(), is(contains( //
				EMPTY, EMPTY, COVERED, EMPTY, COVERED, NOT_COVERED, EMPTY, COVERED, EMPTY, COVERED)));
	}

	@Test
	public void exec02() throws Exception {
		final String rootDir = "example/example02";
		final String outDir = rootDir + "/_bin/";

		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);

		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		final TestResults r = executor.exec( //
				Arrays.asList( //
						new FullyQualifiedName("jp.kusumotolab.BuggyCalculator"), //
						new FullyQualifiedName("jp.kusumotolab.Util")), //
				Arrays.asList( //
						new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest"),
						new FullyQualifiedName("jp.kusumotolab.UtilTest")));

		assertThat(r.getTestResults().size(), is(10));
		assertThat(r.getSuccessRate(), is(1.0 * 9 / 10));

		assertThat(r.getTestResults().get(0).wasFailed(), is(false));
		assertThat(r.getTestResults().get(1).wasFailed(), is(false));
		assertThat(r.getTestResults().get(2).wasFailed(), is(true));
		assertThat(r.getTestResults().get(3).wasFailed(), is(false));

		assertThat(r.getTestResults().get(0).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test01"));
		assertThat(r.getTestResults().get(1).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test02"));
		assertThat(r.getTestResults().get(2).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test03"));
		assertThat(r.getTestResults().get(3).getMethodName().value, is("jp.kusumotolab.BuggyCalculatorTest.test04"));

		assertThat(r.getTestResults().get(8).getMethodName().value, is("jp.kusumotolab.UtilTest.plusTest01"));
		assertThat(r.getTestResults().get(9).getMethodName().value, is("jp.kusumotolab.UtilTest.plusTest02"));

		Coverage c = r.getTestResults().get(0).getCoverages().get(0);
		// assertEquals(c.)
	}

}
