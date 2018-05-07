package jp.kusumotolab.kgenprog.project.test;

import java.util.Arrays;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class TestExecutorMain {

	@Option(name = "-s", aliases = "source", usage = "specify instrumented source classes")
	private String sourceClass;

	@Argument(index = 0, metaVar = "test-classes", usage = "specify execute test classes")
	private String testClass;

	static final String SEPARATOR = ",";

	/**
	 * Application entry point
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		System.out.println(System.getProperty("java.class.path"));		
		final TestExecutorMain main = new TestExecutorMain();
		final CmdLineParser parser = new CmdLineParser(main);
		System.out.println(String.join(" ", args));
		try {
			parser.parseArgument(args);
			System.out.println(main.testClass.length());
			System.out.println(main.sourceClass.length());
		} catch(CmdLineException e) {
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