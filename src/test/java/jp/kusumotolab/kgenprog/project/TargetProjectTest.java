package jp.kusumotolab.kgenprog.project;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TargetProjectTest {
	@Test
	public void testGenerate01() throws IOException {
		final String basePath = "example/example01/";
		final TargetProject project = TargetProject.generate(basePath);
		
		// File.separatorのOS依存回避のためにFileでラップして比較
		assertEquals( //
				new File(basePath + "src/BuggyCalculator.java"),
				new File(project.getSourceFiles().get(0).path));
		assertEquals( //
				new File(basePath + "src/BuggyCalculatorTest.java"),
				new File(project.getTestFiles().get(0).path));
	}

	@Test
	public void testGenerate02() throws IOException {
		final String basePath = "example/example02/";
		final TargetProject project = TargetProject.generate(basePath);
		
		assertEquals( //
				new File(basePath + "src/jp/kusumotolab/BuggyCalculator.java"),
				new File(project.getSourceFiles().get(0).path));
		assertEquals( //
				new File(basePath + "src/jp/kusumotolab/Util.java"),
				new File(project.getSourceFiles().get(1).path));
		assertEquals( //
				new File(basePath + "src/jp/kusumotolab/BuggyCalculatorTest.java"),
				new File(project.getTestFiles().get(0).path));
		assertEquals( //
				new File(basePath + "src/jp/kusumotolab/UtilTest.java"),
				new File(project.getTestFiles().get(1).path));
	}
}
