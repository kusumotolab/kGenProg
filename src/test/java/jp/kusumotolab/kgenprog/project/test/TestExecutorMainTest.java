package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestExecutorMainTest {

	@Before
	public void before() throws IOException {
		ClassPathHacker.addFile("example/example01/bin/");
		new File(TestResults.getSerFilename()).delete();
	}

	// TODO
	// assertionは適当．serializeファイルが生成できているかだけ確認．
	@Test
	public void mainTest01() throws Exception {
		TestExecutorMain.main(new String[] { //
				"-s", //
				"jp.kusumotolab.BuggyCalculator", //
				"jp.kusumotolab.BuggyCalculatorTest" });
		assertTrue(new File(TestResults.getSerFilename()).exists());
	}

}
