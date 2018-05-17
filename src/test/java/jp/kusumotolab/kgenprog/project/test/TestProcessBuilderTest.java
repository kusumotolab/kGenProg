package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestProcessBuilderTest {

	@Before
	public void before() throws IOException {
		new File(TestResults.getSerFilename()).delete();
	}

	@Test
	public void exec01() {
		final TargetProject targetProject = TargetProject.generate("example/example01");
		final TestProcessBuilder builder = new TestProcessBuilder(targetProject);
		builder.start();

		TestResults r = TestResults.deserialize();

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

}
