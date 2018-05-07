package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetProject;

/**
 * テスト実行クラス． 外部プロジェクトの単体テストclassファイルを実行してその結果を回収する．
 * 
 * @author shinsuke
 *
 */
public class TestExecutor {

	private TargetProject targetProject;

	public TestExecutor(TargetProject targetProject) {
		this.targetProject = targetProject;
	}

	public TestResults exec(GeneratedSourceCode generatedSourceCode) {
		return null;

	}

	/**
	 * exec(GeneratedSourceCode)の代替メソッド．
	 * 本メソッドがGeneratedSourceCodeの実装に強依存なので，ひとまずStringで処理．
	 * 
	 * @param sourcePath
	 *            対象プロジェクトのclassPath
	 * @param testClasses
	 *            実行対象のテストクラス名の集合
	 * @return
	 */
	public TestResults exec(final String sourcePath, final String testPath, final List<String> testClasses) {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String classpath = "lib/junit-4.12.jar" + File.pathSeparator + "lib/hamcrest-core-1.3.jar"
				+ File.pathSeparator + sourcePath;
		final String junitMain = "org.junit.runner.JUnitCore";
		final String className = String.join(" ", testClasses);

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, junitMain, className);

		try {
			Process process = builder.start();
			process.waitFor();

			String result = IOUtils.toString(process.getInputStream(), "UTF-8");
			System.out.println(result);
			System.out.println(process.exitValue());
			
			// simplified parser for test results using regex
			Matcher successMatcher = Pattern.compile("OK \\((\\d+) tests\\)").matcher(result);
			if (successMatcher.find()) {
				System.out.println(successMatcher.group(1));
			} else {
				Matcher failureMatcher = Pattern.compile("Tests run: (\\d+),  Failures: (\\d+)").matcher(result);
				if (failureMatcher.find()) {
					System.out.println(">" + failureMatcher.group(1));
					System.out.println(">" + failureMatcher.group(2));
				}
			}
			

		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return null;
	}

	// stub for JaCoCoExecutor
	public TestResults exec(final String classPath, final Class<?> testClass) {
		final TestResults testResults = new TestResults();
		final JUnitCore junit = new JUnitCore();

		// ClassPathHacker.addFile(classPath);
		final Result result = junit.run(testClass);

		System.out.println(result.getFailures() + "!!!!!!!!!");
		// testResults.add(testClass, result);

		return testResults;

	}
	
	public static void main(String args[]) {
		TestExecutor executor = new TestExecutor(null);
		TestResults results = executor.exec("example/example01/bin/", "", Arrays.asList("BuggyCalculatorTest"));
	}
}
