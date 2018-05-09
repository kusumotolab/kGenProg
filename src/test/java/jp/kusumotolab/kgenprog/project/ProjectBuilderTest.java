package jp.kusumotolab.kgenprog.project;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ProjectBuilderTest {

	@Test
	public void testBuildStringForExample01() {

		final String separator = File.separator;
		final String sourceFilePath = "example" + separator + "example01" + separator + "src" + separator
				+ "BuggyCalculator.java";
		final List<SourceFile> sourceFiles = new ArrayList<>();
		sourceFiles.add(new SourceFile(sourceFilePath));
		final String testFilePath = "example" + separator + "example01" + separator + "src" + separator
				+ "BuggyCalculatorTest.java";
		final List<SourceFile> testFiles = new ArrayList<>();
		testFiles.add(new SourceFile(testFilePath));

		final TargetProject targetProject = new TargetProject(sourceFiles, testFiles, Collections.emptyList());
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outputDirectory = "example" + separator + "example01" + separator + "bin";

		final File outputDirectoryFile = new File(outputDirectory);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}
		final boolean success = projectBuilder.build(outputDirectory);

		assertTrue(success);
	}

	@Test
	public void testBuildStringForExample02() {

		final String separator = File.separator;
		final String sourceFilePath1 = "example" + separator + "example02" + separator + "src" + separator + "jp"
				+ separator + "kusumotolab" + separator + "BuggyCalculator.java";
		final String sourceFilePath2 = "example" + separator + "example02" + separator + "src" + separator + "jp"
				+ separator + "kusumotolab" + separator + "Util.java";
		final List<SourceFile> sourceFiles = new ArrayList<>();
		sourceFiles.add(new SourceFile(sourceFilePath1));
		sourceFiles.add(new SourceFile(sourceFilePath2));
		final String testFilePath1 = "example" + separator + "example01" + separator + "src" + separator + "jp"
				+ separator + "kusumotolab" + separator + "BuggyCalculatorTest.java";
		final String testFilePath2 = "example" + separator + "example01" + separator + "src" + separator + "jp"
				+ separator + "kusumotolab" + separator + "UtilTest.java";
		final List<SourceFile> testFiles = new ArrayList<>();
		testFiles.add(new SourceFile(testFilePath1));
		testFiles.add(new SourceFile(testFilePath2));

		final TargetProject targetProject = new TargetProject(sourceFiles, testFiles, Collections.emptyList());
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outputDirectory = "example" + separator + "example02" + separator + "bin";

		final File outputDirectoryFile = new File(outputDirectory);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}
		final boolean success = projectBuilder.build(outputDirectory);

		assertTrue(success);
	}
}
