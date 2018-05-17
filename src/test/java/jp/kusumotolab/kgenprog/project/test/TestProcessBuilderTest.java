package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestProcessBuilderTest {

	@Before
	public void before() throws IOException {
		new File(TestResults.getSerFilename()).delete();
	}

	@Test
	public void exec01() {
		final TargetProject targetProject = TargetProject.generate("example/example01");
		final TestProcessBuilder builder = new TestProcessBuilder(targetProject);
		builder.start();

		TestResults tr = TestResults.deserialize();
		assertEquals(4, tr.getTestResults().size());
		assertEquals(1, tr.getFailedTestResults().size());
	}

}
