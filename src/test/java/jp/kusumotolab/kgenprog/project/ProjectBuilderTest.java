package jp.kusumotolab.kgenprog.project;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ProjectBuilderTest {

	@Test
	public void testBuildStringForExample01() {
		final String separator = File.separator;
		final TargetProject targetProject = TargetProject.generate("example/example01");
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
		final TargetProject targetProject = TargetProject.generate("example/example02");		final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
		final String outDirPath = "example" + separator + "example02" + separator + "bin";

		final File outputDirectoryFile = new File(outDirPath);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}
		final boolean success = projectBuilder.build(outDirPath);

		assertTrue(success);
	}
}
