package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorTest {

	@Before
	public void before() throws IOException {
	}

	@Test
	public void exec01() throws Exception {
		final String outdir = "example/example01/_bin/";

		final TargetProject targetProject = TargetProject.generate("example/example01");
		new ProjectBuilder(targetProject).build(outdir);

		TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./example/example01/_bin/") });
		TestResults r = executor.exec( //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculator")), //
				Arrays.asList(new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest")));

		assertEquals(4, r.getTestResults().size());
		assertEquals(new Double(1 / 4), new Double(r.getSuccessRate()));

		assertFalse(r.getTestResults().get(0).wasFailed());
		assertFalse(r.getTestResults().get(1).wasFailed());
		assertTrue(r.getTestResults().get(2).wasFailed());
		assertFalse(r.getTestResults().get(3).wasFailed());

		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test01", r.getTestResults().get(0).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test02", r.getTestResults().get(1).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test03", r.getTestResults().get(2).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test04", r.getTestResults().get(3).getMethodName().value);

		Coverage c = r.getTestResults().get(0).getCoverages().get(0);
		executor = null;
	}

	// TODO
	// このテストは動作しない．
	// 単体では正常に動作するが，ClassPathHackerによるクラスローダの問題で，クラスロードが2回行われてしまう．
	// よって，exec01()の実行後では期待通りに動作しない．
	// このテストは外部プロセスを起動するTestProcessBuilderTestから実行すること．
	@Test
	public void exec02() throws Exception {
		final String outdir = "example/example02/_bin/";
		// ClassPathHacker.addFile(outdir);

		final TargetProject targetProject = TargetProject.generate("example/example02");
		new ProjectBuilder(targetProject).build(outdir);

		TestResults r = new TestExecutor(new URL[] { new URL("file:./example/example02/_bin/") }).exec( //
				Arrays.asList( //
						new FullyQualifiedName("jp.kusumotolab.BuggyCalculator"), //
						new FullyQualifiedName("jp.kusumotolab.Util")), //
				Arrays.asList( //
						new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest"),
						new FullyQualifiedName("jp.kusumotolab.UtilTest")));

		assertEquals(10, r.getTestResults().size());
		assertEquals(new Double(1 / 10), new Double(r.getSuccessRate()));

		assertFalse(r.getTestResults().get(0).wasFailed());
		assertFalse(r.getTestResults().get(1).wasFailed());
		assertTrue(r.getTestResults().get(2).wasFailed());
		assertFalse(r.getTestResults().get(3).wasFailed());

		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test01", r.getTestResults().get(0).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test02", r.getTestResults().get(1).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test03", r.getTestResults().get(2).getMethodName().value);
		assertEquals("jp.kusumotolab.BuggyCalculatorTest.test04", r.getTestResults().get(3).getMethodName().value);

		Coverage c = r.getTestResults().get(0).getCoverages().get(0);
		// assertEquals(c.)
	}

}
