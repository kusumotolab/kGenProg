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
		final String sourceDirPath = "example" + separator + "example01" + separator + "src" + separator;
		final SourceFile sourceFile = new SourceFile(sourceDirPath + "BuggyCalculator.java");
		final SourceFile testFile = new SourceFile(sourceDirPath + "BuggyCalculatorTest.java");
		System.out.println(sourceFile.path);
		final List<SourceFile> sourceFiles = new ArrayList<>();
		sourceFiles.add(sourceFile);
//		sourceFiles.add(testFile);

		final List<SourceFile> testFiles = new ArrayList<>();
		testFiles.add(testFile);

		final TargetProject targetProject = new TargetProject(sourceFiles, testFiles, Collections.emptyList());
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example01" + separator + "bin";

		final File outputDirectoryFile = new File(outDirPath);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}
		final boolean success = projectBuilder.build(outDirPath);

		assertTrue(success);
	}

	@Test
	public void testBuildStringForExample02() {

		final String separator = File.separator;
		final String sourceDirPath = "example" + separator + "example02" + separator + "src" + separator + "jp"
				+ separator + "kusumotolab" + separator;
		final SourceFile sourceFile1 = new SourceFile(sourceDirPath + "BuggyCalculator.java");
		final SourceFile sourceFile2 = new SourceFile(sourceDirPath + "Util.java");
		final SourceFile testFile1 = new SourceFile(sourceDirPath + "BuggyCalculatorTest.java");
		final SourceFile testFile2 = new SourceFile(sourceDirPath + "UtilTest.java");

		final List<SourceFile> sourceFiles = new ArrayList<>();
		sourceFiles.add(sourceFile1);
		sourceFiles.add(sourceFile2);
//		sourceFiles.add(testFile1);
//		sourceFiles.add(testFile2);
		
		final List<SourceFile> testFiles = new ArrayList<>();
		testFiles.add(testFile1);
		testFiles.add(testFile2);

		final TargetProject targetProject = new TargetProject(sourceFiles, testFiles, Collections.emptyList());
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example02" + separator + "bin";

		final File outputDirectoryFile = new File(outDirPath);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}
		final boolean success = projectBuilder.build(outDirPath);

		assertTrue(success);
	}
}
