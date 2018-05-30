package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ProjectBuilderTest {

	@Test
	public void testBuildStringForExample01() {
		final String separator = File.separator;
		final TargetProject targetProject = TargetProject.generate("example/example01");
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example01" + separator + "bin";

		final BuildResults buildResults = projectBuilder.build(outDirPath);

		assertFalse(buildResults.isBuildFailed); // TODO assertThat に置き換える

		for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
			if (sourceFile.path.endsWith("BuggyCalculator.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("BuggyCalculator.class"), is(true));
				assertThat(correspondingSource.endsWith("BuggyCalculator.java"), is(true));
			}
			// TODO テストファイルがTargetProject#sourceFilesに含まれている前提のテスト．これでよいか？
			if (sourceFile.path.endsWith("BuggyCalculatorTest.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("BuggyCalculatorTest.class"), is(true));
				assertThat(correspondingSource.endsWith("BuggyCalculatorTest.java"), is(true));
			}
		}
	}

	@Test
	public void testBuildStringForExample02() {
		final String separator = File.separator;
		final TargetProject targetProject = TargetProject.generate("example/example02");
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example02" + separator + "bin";

		final BuildResults buildResults = projectBuilder.build(outDirPath);

		assertFalse(buildResults.isBuildFailed); // TODO assertThat に置き換える
		
		for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
			if (sourceFile.path.endsWith("BuggyCalculator.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("BuggyCalculator.class"), is(true));
				assertThat(correspondingSource.endsWith("BuggyCalculator.java"), is(true));
			}
			// TODO テストファイルがTargetProject#sourceFilesに含まれている前提のテスト．これでよいか？
			if (sourceFile.path.endsWith("BuggyCalculatorTest.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("BuggyCalculatorTest.class"), is(true));
				assertThat(correspondingSource.endsWith("BuggyCalculatorTest.java"), is(true));
			}
			if (sourceFile.path.endsWith("Util.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("Util.class"), is(true));
				assertThat(correspondingSource.endsWith("Util.java"), is(true));
			}
			// TODO テストファイルがTargetProject#sourceFilesに含まれている前提のテスト．これでよいか？
			if (sourceFile.path.endsWith("UtilTest.java")) {
				final Path pathToClass = buildResults.getPathToClasses(Paths.get(sourceFile.path)).iterator().next();
				final Path correspondingSource = buildResults.getPathToSource(pathToClass);
				assertThat(pathToClass.endsWith("UtilTest.class"), is(true));
				assertThat(correspondingSource.endsWith("UtilTest.java"), is(true));
			}
		}
	}
}
