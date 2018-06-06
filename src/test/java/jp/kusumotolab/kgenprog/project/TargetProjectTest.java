package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

public class TargetProjectTest {
	@Test
	public void testGenerate01() throws IOException {
		final String basePath = "example/example01/";
		final TargetProject project = TargetProject.generate(basePath);

		assertThat(project.getSourceFiles(),
				is(containsInAnyOrder( //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculator.java")), //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"))//
				)));

		assertThat(project.getTestFiles(), is(containsInAnyOrder( //
				new TestSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"))//
		)));
	}

	@Test
	public void testGenerate02() throws IOException {
		final String basePath = "example/example02/";
		final TargetProject project = TargetProject.generate(basePath);

		assertThat(project.getSourceFiles(),
				is(containsInAnyOrder( //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculator.java")), //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java")), //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/Util.java")), //
						new TargetSourceFile(Paths.get(basePath + "src/jp/kusumotolab/UtilTest.java"))//
				)));

		assertThat(project.getTestFiles(),
				is(containsInAnyOrder( //
						new TestSourceFile(Paths.get(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java")), //
						new TestSourceFile(Paths.get(basePath + "src/jp/kusumotolab/UtilTest.java"))//
				)));

	}
}
