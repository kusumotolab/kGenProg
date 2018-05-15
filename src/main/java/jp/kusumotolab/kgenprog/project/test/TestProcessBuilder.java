package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

/**
 * テスト実行クラス． 外部プロジェクトの単体テストclassファイルを実行してその結果を回収する．
 * 
 * @author shinsuke
 *
 */
public class TestProcessBuilder {

	private TargetProject targetProject;

	public TestProcessBuilder(TargetProject targetProject) {
		this.targetProject = targetProject;
	}

	@Deprecated
	public TestResults exec(GeneratedSourceCode generatedSourceCode) {
		return null;
	}

	private List<FullyQualifiedName> createFQNs(List<SourceFile> sourceFiles) {
		return sourceFiles.stream().map(s -> new FullyQualifiedName(s)).collect(Collectors.toList());
	}

	public void start() {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String classpath = filterClasspathFromSystemClasspath();
		final String main = "jp.kusumotolab.kgenprog.project.test.TestExecutorMain";
		final String sourceFiles = String.join(TestExecutorMain.SEPARATOR,
				createFQNs(targetProject.getSourceFiles()).stream().map(f -> f.value).collect(Collectors.toList()));
		final String testFiles = String.join(TestExecutorMain.SEPARATOR,
				createFQNs(targetProject.getTestFiles()).stream().map(f -> f.value).collect(Collectors.toList()));

		final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, main, "-s", sourceFiles,
				testFiles);

		try {
			final Process process = builder.start();

			// TODO
			// process.waitFor()するとhangする．謎
			// たぶんこの問題
			// https://stackoverflow.com/questions/42436307/process-hanging-on-the-process-builder
			String out_result = IOUtils.toString(process.getInputStream(), "UTF-8");
			String err_result = IOUtils.toString(process.getErrorStream(), "SJIS");
			System.out.println(out_result);
			System.err.println(err_result);
			System.out.println(process.exitValue());

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * TestResults.exec(GeneratedSourceCode)の代替メソッド．
	 * 本メソッドがGeneratedSourceCodeの実装に強依存なので，ひとまずStringで処理．
	 * 
	 * @param sourceClasses 対象プロジェクトのclassPath
	 * @param testClasses 実行対象のテストクラス名の集合
	 * @return
	 */
	public void start(final List<String> sourceClasses, final List<String> testClasses, final String classpath) {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String _classpath = filterClasspathFromSystemClasspath() + File.pathSeparator + classpath;
		final String main = "jp.kusumotolab.kgenprog.project.test.TestExecutorMain";

		final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", _classpath, main, "-s",
				String.join(TestExecutorMain.SEPARATOR, sourceClasses),
				String.join(TestExecutorMain.SEPARATOR, testClasses));

		try {
			final Process process = builder.start();

			// TODO
			// process.waitFor()するとhangする．謎
			// たぶんこの問題
			// https://stackoverflow.com/questions/42436307/process-hanging-on-the-process-builder
			String out_result = IOUtils.toString(process.getInputStream(), "UTF-8");
			String err_result = IOUtils.toString(process.getErrorStream(), "SJIS");
			System.out.println(out_result);
			System.err.println(err_result);
			System.out.println(process.exitValue());

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private final String jarFileTail = "-(\\d+\\.)+jar$";

	private String filterClasspathFromSystemClasspath() {
		final String[] classpaths = System.getProperty("java.class.path").split(File.pathSeparator);

		final List<String> filter = new ArrayList<>();
		filter.add(".*args4j" + jarFileTail);
		filter.add(".*jacoco\\.core" + jarFileTail);
		filter.add(".*asm" + jarFileTail);
		filter.add(".*asm-commons" + jarFileTail);
		filter.add(".*asm-tree" + jarFileTail);
		filter.add(".*junit" + jarFileTail);
		filter.add(".*hamcrest-core" + jarFileTail);
		filter.add(".*bin" + "\\" + File.separator + "main$");
		filter.add(".*bin" + "\\" + File.separator + "test$");

		List<String> result = Arrays.asList(classpaths).stream()
				.filter(cp -> filter.stream().anyMatch(f -> cp.matches(f))).collect(Collectors.toList());

		return String.join(File.pathSeparator, result);
	}
}
