package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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

	final private TargetProject targetProject;
	final private Path binDir;

	// stub for compatibility
	public TestProcessBuilder(TargetProject targetProject) {
		this(targetProject, Paths.get("")); // TODO
	}

	// stub for compatibility
	public TestProcessBuilder(TargetProject targetProject, String outDir) {
		this(targetProject, Paths.get(outDir)); // TODO
	}

	public TestProcessBuilder(TargetProject targetProject, Path binDir) {
		this.targetProject = targetProject;

		// start()時にワーキングディレクトリを変更するために，binDirはrootPathからの相対パスに変更
		// TODO いろんな状況でバグるので要修正．一時的な処置．
		this.binDir = targetProject.rootPath.relativize(binDir);
	}

	@Deprecated
	public TestResults exec(GeneratedSourceCode generatedSourceCode) {
		return null;
	}

	public void start() {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String classpath = filterClasspathFromSystemClasspath();
		final String main = "jp.kusumotolab.kgenprog.project.test.TestExecutorMain";

		@SuppressWarnings("unchecked")
		final Collection<FullyQualifiedName> c = CollectionUtils.subtract( //
				getSourceFQNs(this.targetProject), getTestFQNs(this.targetProject));

		final String sourceFiles = c.stream().map(f -> f.value).collect(Collectors.joining(TestExecutorMain.SEPARATOR));
		final String testFiles = getTestFQNs(this.targetProject).stream().map(f -> f.value)
				.collect(Collectors.joining(TestExecutorMain.SEPARATOR));

		final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, main, "-b", this.binDir.toString(),
				"-s", sourceFiles, "-t", testFiles);

		// テスト実行のためにworking dirを移動（対象プロジェクトが相対パスを利用している可能性が高いため）
		builder.directory(this.targetProject.rootPath.toFile());

		try {
			final Process process = builder.start();
			process.waitFor();
			/*
			String out_result = IOUtils.toString(process.getInputStream(), "UTF-8");
			String err_result = IOUtils.toString(process.getErrorStream(), "SJIS");
			System.out.println(out_result);
			System.err.println(err_result);
			System.out.println(process.exitValue());
			*/
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// TODO
	// 現在FQN生成の責務がどこにもないので一時的に本クラスで実装．srcパスは"/src"に固定しているので潜在的バグ．
	@Deprecated
	private List<FullyQualifiedName> getSourceFQNs(final TargetProject targetProject) {
		return getFQNs(targetProject.getSourceFiles(), targetProject.rootPath);
	}

	// TODO
	// 現在FQN生成の責務がどこにもないので一時的に本クラスで実装．srcパスは"/src"に固定しているので潜在的バグ．
	@Deprecated
	private List<FullyQualifiedName> getTestFQNs(final TargetProject targetProject) {
		return getFQNs(targetProject.getTestFiles(), targetProject.rootPath);
	}

	@Deprecated
	private List<FullyQualifiedName> getFQNs(List<SourceFile> files, Path rootPath) {
		return files.stream() //
				.map(s -> Paths.get(s.path)) //
				.map(p -> rootPath.resolve("src").relativize(p)) //
				.map(p -> new TestFullyQualifiedName(p)) //
				.collect(Collectors.toList());

	}

	/**
	 * TestResults.exec(GeneratedSourceCode)の代替メソッド．
	 * 本メソッドがGeneratedSourceCodeの実装に強依存なので，ひとまずStringで処理．
	 * 
	 * @param sourceClasses 対象プロジェクトのclassPath
	 * @param testClasses 実行対象のテストクラス名の集合
	 * @return
	 */
	@Deprecated
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

	/**
	 * 現在実行中のjavaプロセスのcpから，TestExecutorMain実行に必要なcpをフィルタリングする．
	 * @return
	 */
	private String filterClasspathFromSystemClasspath() {
		// 依存する外部ライブラリを定義
		final String[] classpaths = System.getProperty("java.class.path").split(File.pathSeparator);
		final List<String> filter = new ArrayList<>();
		filter.add("args4j");
		filter.add("jacoco\\.core");
		filter.add("asm");
		filter.add("asm-commons");
		filter.add("asm-tree");
		filter.add("junit");
		filter.add("hamcrest-core");

		// cp一覧から必須外部ライブラリのみをフィルタリング
		List<String> result = Arrays.asList(classpaths).stream()
				.filter(cp -> filter.stream().anyMatch(f -> cp.matches(".*" + f + jarFileTail)))
				.collect(Collectors.toList());

		// 自身（TestProcessBuilder.class）へのcpを追加
		try {
			result.add(Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return String.join(File.pathSeparator, result);
	}
}
