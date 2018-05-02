package jp.kusumotolab.kgenprog.project.test;

import java.io.IOException;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetProject;

/**
 * テスト実行クラス．
 * 外部プロジェクトの単体テストclassファイルを実行してその結果を回収する．
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
	 * @param classPath
	 *            対象プロジェクトのclassPath
	 * @param testClasses
	 *            実行対象のテストクラス名の集合
	 * @return
	 */
	public TestResults exec(final String classPath, final List<String> testClasses) {
		final TestResults testResults = new TestResults();
		final JUnitCore junit = new JUnitCore();
		try {
			ClassPathHacker.addFile(classPath);
			for(final String testClass: testClasses) {
				final Result result = junit.run(Request.classes(Class.forName(testClass)));
				testResults.add(testClass, result);
			}
			return testResults;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return null;
	}

}
