package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestProcessBuilderTest {

	private static TargetProject targetProject;

	@Before
	public void before() throws IOException {
		new File(TestResults.getSerFilename()).delete();
	}

	@Test
	public void exec01() {
		final TestProcessBuilder builder = new TestProcessBuilder(createTargetProjectFromExample01());
		builder.start();
		/*
		builder.start( //
				Arrays.asList("jp.kusumotolab.BuggyCalculator"), //
				Arrays.asList("jp.kusumotolab.BuggyCalculatorTest"), //
				"example/example01/bin/");
		*/
		TestResults tr = TestResults.deserialize();
		assertEquals(4, tr.getTestResults().size());
		assertEquals(1, tr.getFailedTestResults().size());
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
