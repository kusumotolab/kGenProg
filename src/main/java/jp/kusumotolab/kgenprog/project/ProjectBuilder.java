package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.spell.LevensteinDistance;

public class ProjectBuilder {

	static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

	private final TargetProject targetProject;

	public ProjectBuilder(final TargetProject targetProject) {
		this.targetProject = targetProject;
	}

	/**
	 * 初期ソースコードをビルド
	 * 
	 * @param outDir
	 *            バイトコード出力ディレクトリ
	 * @return ビルドに関するさまざまな情報
	 */
	public BuildResults build(final String outDir) {
		return this.build(null, outDir);
	}

	/**
	 * @param generatedSourceCode
	 *            null でなければ与えられた generatedSourceCode からビルド．null
	 *            の場合は，初期ソースコードからビルド
	 * @param outDir
	 *            バイトコード出力ディレクトリ
	 * @return ビルドに関するさまざまな情報
	 */
	public BuildResults build(final GeneratedSourceCode generatedSourceCode, final String outDir) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		// outディレクトリが存在しなければ生成
		final File outputDirectoryFile = new File(outDir);
		if (!outputDirectoryFile.exists()) {
			outputDirectoryFile.mkdirs();
		}

		final Iterable<? extends JavaFileObject> javaFileObjects;

		// variant が null なら，初期ソースコードをビルド
		if (null == generatedSourceCode) {
			javaFileObjects = fileManager.getJavaFileObjectsFromStrings(
					this.targetProject.getSourceFiles().stream().map(f -> f.path).collect(Collectors.toList()));
		}

		// variant が null でなければ，バリアントのソースコードをビルド
		else {
			final List<GeneratedAST> generatedASTs = generatedSourceCode.getFiles();
			javaFileObjects = generatedASTs.stream()
					.map(a -> new JavaSourceFromString(a.getPrimaryClassName(), a.getSourceCode()))
					.collect(Collectors.toList());
		}

		final List<String> compilationOptions = new ArrayList<>();
		compilationOptions.add("-d");
		compilationOptions.add(outDir);
		compilationOptions.add("-encoding");
		compilationOptions.add("UTF-8");
		compilationOptions.add("-classpath");
		compilationOptions.add(String.join(CLASSPATH_SEPARATOR,
				this.targetProject.getClassPaths().stream().map(cp -> cp.path).collect(Collectors.toList())));

		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		final CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null,
				javaFileObjects);

		try {
			fileManager.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final boolean isFailed = !task.call();
		// TODO コンパイルできないときのエラー出力はもうちょっと考えるべき
		for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
			System.err.println(diagnostic.getCode());
			System.err.println(diagnostic.getKind());
			System.err.println(diagnostic.getPosition());
			System.err.println(diagnostic.getStartPosition());
			System.err.println(diagnostic.getEndPosition());
			System.err.println(diagnostic.getSource());
			System.err.println(diagnostic.getMessage(null));
		}

		final BuildResults buildResults = new BuildResults(isFailed, outDir, diagnostics);

		// ビルドが成功したときは，ソースファイルとクラスファイルのマッピングを取る
		if (!isFailed) {
			final Collection<File> classFiles = FileUtils.listFiles(new File(outDir), new String[] { "class" }, true);
			final List<SourceFile> sourceFiles = this.targetProject.getSourceFiles();

			final LevensteinDistance levensteinDistance = new LevensteinDistance();
			for (final File classFile : classFiles) {
				float maxSimilarity = 0f;
				SourceFile correspondingSourceFile = null;
				for (final SourceFile sourceFile : sourceFiles) {
					final float similarity = levensteinDistance.getDistance(classFile.getAbsolutePath(),
							sourceFile.path.intern());
					if (similarity > maxSimilarity) {
						maxSimilarity = similarity;
						correspondingSourceFile = sourceFile;
					}
				}
				if (maxSimilarity > 0f) {
					buildResults.addMapping(Paths.get(correspondingSourceFile.path),
							Paths.get(classFile.getAbsolutePath()));
				}
			}
		}

		return buildResults;
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
