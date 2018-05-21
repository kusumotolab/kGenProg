package jp.kusumotolab.kgenprog.project;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ProjectBuilderTest {

	@Test
	public void testBuildStringForExample01() {
		final String separator = File.separator;
		final TargetProject targetProject = TargetProject.generate("example/example01");
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example01" + separator + "bin";

		final boolean success = projectBuilder.build(outDirPath);

		assertTrue(success);
	}

	@Test
	public void testBuildStringForExample02() {
		final String separator = File.separator;
		final TargetProject targetProject = TargetProject.generate("example/example02");
		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example02" + separator + "bin";

		final boolean success = projectBuilder.build(outDirPath);

		assertTrue(success);
	}
}
