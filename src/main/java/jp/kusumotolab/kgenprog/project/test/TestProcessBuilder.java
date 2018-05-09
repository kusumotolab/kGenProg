package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
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

	/**
	 * TestResults.exec(GeneratedSourceCode)の代替メソッド．
	 * 本メソッドがGeneratedSourceCodeの実装に強依存なので，ひとまずStringで処理．
	 * 
	 * @param sourceClasses
	 *            対象プロジェクトのclassPath
	 * @param testClasses
	 *            実行対象のテストクラス名の集合
	 * @return
	 */
	public void start(final List<String> sourceClasses, final List<String> testClasses, final String classpath) {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String _classpath = System.getProperty("java.class.path") + File.pathSeparator + classpath;
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

}
