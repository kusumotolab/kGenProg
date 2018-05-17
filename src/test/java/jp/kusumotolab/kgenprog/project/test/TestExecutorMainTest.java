package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorMainTest {

	@Before
	public void before() throws IOException {
		final String outdir = "example/example01/_bin/";
		// ClassPathHacker.addFile(outdir);
		new ProjectBuilder(createTargetProjectFromExample01()).build(outdir);
		new File(TestResults.getSerFilename()).delete();
	}

	@Test
	public void mainTest01() throws Exception {
		TestExecutorMain.main(new String[] { //
				"-s", //
				"jp.kusumotolab.BuggyCalculator", //
				"jp.kusumotolab.BuggyCalculatorTest" });

		assertTrue(new File(TestResults.getSerFilename()).exists());

		final TestResults r = TestResults.deserialize();

		assertEquals(4, r.getTestResults().size());
		assertEquals(new Double(1 / 4), new Double(r.getSuccessRate()));

		assertFalse(r.getTestResults().get(0).wasFailed());
		assertFalse(r.getTestResults().get(1).wasFailed());
		assertTrue(r.getTestResults().get(2).wasFailed());
		assertFalse(r.getTestResults().get(3).wasFailed());

		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test01", r.getTestResults().get(0).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test02", r.getTestResults().get(1).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test03", r.getTestResults().get(2).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test04", r.getTestResults().get(3).getMethodName().value);
	}

	private TargetProject createTargetProjectFromExample01() {
		String project = "example/example01/";
		return new TargetProject( //
				Arrays.asList( //
						new SourceFile(project + "src/jp/kusumotolab/BuggyCalculator.java"), //
						new SourceFile(project + "src/jp/kusumotolab/BuggyCalculatorTest.java")), //
				Arrays.asList( //
						new SourceFile(project + "src/BuggyCalculatorTest.java")), //
				Arrays.asList( //
						new ClassPath("lib/junit4/junit-4.12.jar"), //
						new ClassPath("lib/junit4/hamcrest-core-1.3.jar")));
	}
}
