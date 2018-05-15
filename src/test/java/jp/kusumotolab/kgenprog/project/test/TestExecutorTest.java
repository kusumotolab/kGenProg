package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorTest {

	@Before
	public void before() throws IOException {
		final String outdir = "example/example01/_bin/";
		ClassPathHacker.addFile(outdir);
		new ProjectBuilder(createTargetProjectFromExample01()).build(outdir);
	}

	@Test
	public void exec01() throws Exception {
		TestResults r = new TestExecutor().exec( //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculator")), //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest")));

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

		Coverage c = r.getTestResults().get(0).getCoverages().get(0);
		// assertEquals(c.)
	}

	private TargetProject createTargetProjectFromExample01() {
		String project = "example/example01/";
		return new TargetProject( //
				Arrays.asList( //
						new SourceFile(project + "src/jp/kusumotolab/BuggyCalculator.java"), //
						new SourceFile(project + "src/jp/kusumotolab/BuggyCalculatorTest.java")),
				Arrays.asList( //
						new SourceFile(project + "src/BuggyCalculatorTest.java")),
				Arrays.asList( //
						new ClassPath("lib/junit4/junit-4.12.jar"),
						new ClassPath("lib/junit4/hamcrest-core-1.3.jar")));
	}
}
