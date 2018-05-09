package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.util.Arrays;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class TestExecutorMain {

	@Option(name = "-s", aliases = "source", usage = "Specify source classes")
	private String sourceClass;

	@Argument(index = 0, metaVar = "test-classes", usage = "Specify executed test classes")
	private String testClass;

	public static final String SEPARATOR = File.pathSeparator;

	/**
	 * Application entry point <br>
	 * e.g.,) java Main -s jp.kusu.TargetClass jp.kusu.TargetClassTest
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final TestExecutorMain main = new TestExecutorMain();
		final CmdLineParser parser = new CmdLineParser(main);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			return;
		}

		final TestExecutor executor = new TestExecutor();
		final TestResults testResults = executor.exec( //
				Arrays.asList(main.sourceClass.split(SEPARATOR)), //
				Arrays.asList(main.testClass.split(SEPARATOR)));
		TestResults.serialize(testResults);
	}

}