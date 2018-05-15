package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestProcessBuilderTest {

	private static TargetProject targetProject;

	@Before
	public void before() throws IOException {
		ClassPathHacker.addFile("example/example01/bin/");
		new File(TestResults.getSerFilename()).delete();
	}

	@Test
	public void exec01() {
		final TestProcessBuilder builder = new TestProcessBuilder(null);
		builder.start( //
				Arrays.asList("jp.kusumotolab.BuggyCalculator"), //
				Arrays.asList("jp.kusumotolab.BuggyCalculatorTest"), //
				"example/example01/bin/");
		
		TestResults tr = TestResults.deserialize();
		assertEquals(4, tr.getTestResults().size());
		assertEquals(1, tr.getFailedTestResults().size());
	}
}
