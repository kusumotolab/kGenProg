package jp.kusumotolab.kgenprog.project.test;

import java.io.InputStream;
import java.util.List;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

class TestExecutor {
	private final MemoryClassLoader memoryClassLoader;
	private final IRuntime runtime;
	private final Instrumenter instrumenter;
	
	public TestExecutor() {
		this.memoryClassLoader = new MemoryClassLoader();
		this.runtime = new LoggerRuntime();
		this.instrumenter = new Instrumenter(runtime);
	}

	/**
	 * JaCoCO + JUnitの実行．<br>
	 * sourceClassesで指定したソースをJaCoCoでinstrumentして，JUnitを実行する．<br>
	 * classpathは通ってることが前提．
	 * 
	 * @param sourceClasses
	 *            計測対象のソースコードのリスト
	 * @param testClasses
	 *            実行する単体テストのリスト
	 * @return
	 * @throws Exception
	 */
	public TestResults exec(final List<String> sourceClasses, final List<String> testClasses) throws Exception {

		final TestResults testResults = new TestResults();

		for (final String sourceClass : sourceClasses) {
			final String targetName = Class.forName(sourceClass).getName();
			loadClass(targetName, instrument(targetName));
		}

		final RuntimeData runtimeData = new RuntimeData();
		this.runtime.startup(runtimeData);

		final JUnitCore junitCore = new JUnitCore();
		for (final String testClass : testClasses) {
			final String targetName = Class.forName(testClass).getName();
			final Class<?> junitClass = loadClass(targetName, instrument(targetName));
			final Result result = junitCore.run(junitClass);
			testResults.add(result);
			System.out.println("Failure count: " + result.getFailureCount() + " (" + targetName);
		}

		final ExecutionDataStore executionData = new ExecutionDataStore();
		runtimeData.collect(executionData, new SessionInfoStore(), false);
		this.runtime.shutdown();

		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);

		String targetName = sourceClasses.get(0); //

		analyzer.analyzeClass(getTargetClass(targetName), targetName);
		for (final IClassCoverage cc : coverageBuilder.getClasses()) {
			for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
				System.out.printf("  Line %s: %s%n", Integer.valueOf(i), cc.getLine(i).getStatus());
			}
		}
		return testResults;
	}

	private byte[] instrument(final String targetName) throws Exception {
		return this.instrumenter.instrument(getTargetClass(targetName), "");
	}
	
	private Class<?> loadClass(final String targetName, final byte[] bytes) throws ClassNotFoundException {
		this.memoryClassLoader.addDefinition(targetName, bytes);
		return this.memoryClassLoader.loadClass(targetName); // force load instrumented class.
	}

	private InputStream getTargetClass(final String name) {
		final String resource = "/" + name.replace('.', '/') + ".class";
		return getClass().getResourceAsStream(resource);
	}

}