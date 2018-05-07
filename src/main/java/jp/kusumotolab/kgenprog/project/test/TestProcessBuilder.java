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

	@SuppressWarnings("unused") // TODO
	private TargetProject targetProject;

	public TestProcessBuilder(TargetProject targetProject) {
		this.targetProject = targetProject;
	}

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
	public void build(final List<String> sourceClasses, final List<String> testClasses, final String classpath) {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String _classpath = "bin/main" + File.pathSeparator //
				+ "lib/junit-4.12.jar" + File.pathSeparator //
				+ "lib/hamcrest-core-1.3.jar" + File.pathSeparator
				+ "lib/args4j-2.33.jar" + File.pathSeparator
				+ "lib/org.jacoco.core-0.8.1.jar" + File.pathSeparator
				+ "lib/asm-6.0.jar" + File.pathSeparator
				+ "lib/asm-commons-6.0.jar" + File.pathSeparator
				+ "lib/asm-tree-6.0.jar" + File.pathSeparator
				+ classpath;
		//_classpath += System.getProperty("java.class.path");
		
		final String main = "jp.kusumotolab.kgenprog.project.test.TestExecutorMain";
		final String[] args = new String[] {"-s ", String.join(",", sourceClasses), String.join(",", testClasses)};

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", _classpath, main, "-s", String.join(",", sourceClasses), String.join(",", testClasses));

		try {
			Process process = builder.start();
			process.waitFor();

			String result = IOUtils.toString(process.getInputStream(), "UTF-8");
			String errresult = IOUtils.toString(process.getErrorStream(), "SJIS");
			System.out.println(result);
			System.out.println(errresult);
			System.out.println(process.exitValue());

		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
