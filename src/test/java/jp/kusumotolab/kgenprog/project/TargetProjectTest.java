package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.junit.Test;

public class TargetProjectTest {
	@Test
	public void testGenerate01() throws IOException {
		final String basePath = "example/example01/";
		final TargetProject project = TargetProject.generate(basePath);

		assertThat(project.getSourceFiles(), is(containsInAnyOrder( //
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculator.java"),
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"))));

		assertThat(project.getTestFiles(), is(containsInAnyOrder( //
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"))));
	}

	@Test
	public void testGenerate02() throws IOException {
		final String basePath = "example/example02/";
		final TargetProject project = TargetProject.generate(basePath);

		assertThat(project.getSourceFiles(), is(containsInAnyOrder( //
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculator.java"),
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"),
				new SourceFile(basePath + "src/jp/kusumotolab/Util.java"),
				new SourceFile(basePath + "src/jp/kusumotolab/UtilTest.java"))));

		assertThat(project.getTestFiles(), is(containsInAnyOrder( //
				new SourceFile(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"),
				new SourceFile(basePath + "src/jp/kusumotolab/UtilTest.java"))));

	}
}
