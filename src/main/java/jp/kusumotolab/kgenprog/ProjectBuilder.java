package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class ProjectBuilder {

	static private final String CLASSPATH_SEPARATOR = System.getProperty("os.name").toLowerCase().contains("windows")
			? ";" : ":";

	private final TargetProject targetProject;

	public ProjectBuilder(final TargetProject targetProject) {
		this.targetProject = targetProject;
	}

	/**
	 * 初期ソースコードをビルド
	 * 
	 * @param outDir バイトコード出力ディレクトリ
	 * @return ビルドが成功すれば true，失敗すれば false
	 */
	public boolean build(final String outDir){
		return this.build(null, outDir);
	}
	
	/**
	 * @param variant null でなければ与えられた variant からビルド．null の場合は，初期ソースコードからビルド
	 * @param outDir バイトコード出力ディレクトリ
	 * @return ビルドが成功すれば true，失敗すれば false
	 */
	public boolean build(final Variant variant, final String outDir) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		final Iterable<? extends JavaFileObject> javaFileObjects;
		
		// variant が null なら，初期ソースコードをビルド
		if (null == variant) {
			javaFileObjects = fileManager.getJavaFileObjectsFromStrings(
					this.targetProject.getSourceFiles().stream().map(f -> f.path).collect(Collectors.toList()));
		} 
		
		// variant が null でなければ，バリアントのソースコードをビルド
		else {
			final List<GeneratedAST> generatedASTs = variant.getGeneratedSourceCode().getFiles();
			javaFileObjects = generatedASTs.stream()
					.map(a -> new JavaSourceFromString(a.getPrimaryClassName(), a.getSourceCode()))
					.collect(Collectors.toList());
		}

		final List<String> compilationOptions = new ArrayList<>();
		compilationOptions.add("-d");
		compilationOptions.add(outDir);
		compilationOptions.add("-classpath");
		compilationOptions.add(String.join(CLASSPATH_SEPARATOR,
				this.targetProject.getClassPaths().stream().map(cp -> cp.path).collect(Collectors.toList())));

		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		final CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null,
				javaFileObjects);

		final boolean isSuccess = task.call();

		try {
			fileManager.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return isSuccess;
	}
}

class JavaSourceFromString extends SimpleJavaFileObject {

	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}
