package jp.kusumotolab.kgenprog.project.build;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class ProjectBuilder {

  // TODO デフォルトのコンパイラバージョンは要検討．ひとまず1.8固定．
  // TODO #289: 加え，toml からコンパイラバージョンを指定できるようにするべき．
  private static final String DEFAULT_JDK_VERSION = "1.8";

  private final TargetProject targetProject;
  private final BinaryStore binaryStore;
  private final JavaCompiler compiler;
  private final StandardJavaFileManager standardFileManager;
  private final InMemoryFileManager inMemoryFileManager;
  private final List<String> compilationOptions;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;

    // build()メソッドで再利用可能なオブジェクト
    binaryStore = new BinaryStore();
    compiler = ToolProvider.getSystemJavaCompiler();
    standardFileManager = compiler.getStandardFileManager(null, null, null);
    inMemoryFileManager = new InMemoryFileManager(standardFileManager, binaryStore);
    compilationOptions = createDefaultCompilationOptions();
  }

  /**
   * @param generatedSourceCode
   * @return
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode) {

    // コンパイル状況や診断情報等の保持オブジェクトを用意
    final StringWriter progress = new StringWriter();
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    // コンパイル済みキャッシュの有無を問い合わせ
    final List<GeneratedAST<?>> allAsts = generatedSourceCode.getAllAsts();
    final Set<JavaSourceObject> javaSourceObjects = generateJavaSourceObjects(allAsts);

    // コンパイル対象が存在する（≒全コンパイル対象がキャッシュ済みでない）場合はコンパイル
    if (!javaSourceObjects.isEmpty()) {
      final boolean successs = build(allAsts, javaSourceObjects, diagnostics, progress);

      if (!successs) {
        return EmptyBuildResults.instance;
      }
    }

    // コンパイル済みバイナリを取り出してセットしておく
    final BinaryStore compiledBinaries = extractSubBinaryStore(allAsts);

    return new BuildResults(compiledBinaries, diagnostics, progress.toString(), false);
  }

  private boolean build(final List<GeneratedAST<?>> allAsts,
      final Collection<JavaSourceObject> javaSourceObjects,
      final DiagnosticCollector<JavaFileObject> diagnostics, final StringWriter progress) {

    // binaryStoreからコンパイル済みバイナリを取り出してIMFMにセットしておく
    final BinaryStore reusableBinaries = extractSubBinaryStore(allAsts);
    inMemoryFileManager.setClassPathBinaries(reusableBinaries);

    // コンパイルタスクを生成
    final CompilationTask task = compiler.getTask(progress, inMemoryFileManager, diagnostics,
        compilationOptions, null, javaSourceObjects);

    // コンパイルを実行
    return task.call();
  }

  /**
   * binaryStoreから指定astに対応するJavaBinaryObjectの部分集合を取り出す．
   * 
   * @param asts
   * @return
   */
  private BinaryStore extractSubBinaryStore(final List<GeneratedAST<?>> asts) {
    // TODO
    // この処理コスト高い．
    // BinaryStoreからサブBinaryStoreの抜き出し方法は改善したほうが良い．
    final BinaryStore binStore = new BinaryStore();
    asts.stream()
        .map(ast -> binaryStore.get(ast.getPrimaryClassName(), ast.getMessageDigest()))
        .flatMap(Collection::stream)
        .forEach(binStore::add);
    return binStore;
  }

  /**
   * 指定astからコンパイル元となるJavaSourceObjectを生成する．<br>
   * ただしbinaryStoreに保持されているキャッシュがある場合はスキップ．
   * 
   * @param asts
   * @return
   */
  private Set<JavaSourceObject> generateJavaSourceObjects(final List<GeneratedAST<?>> asts) {
    return asts.stream()
        .filter(ast -> !binaryStore.exists(ast.getPrimaryClassName(), ast.getMessageDigest()))
        .map(JavaSourceObject::new)
        .collect(Collectors.toSet());
  }

  /**
   * デフォルトのコンパイルオプションを生成する．
   * 
   * @return
   */
  private List<String> createDefaultCompilationOptions() {
    final List<String> classpathList = targetProject.getClassPaths()
        .stream()
        .map(cp -> cp.path.toString())
        .collect(Collectors.toList());
    final String classpaths = String.join(File.pathSeparator, classpathList);

    return Arrays.asList( //
        "-source", DEFAULT_JDK_VERSION, //
        "-target", DEFAULT_JDK_VERSION, //
        "-encoding", "UTF-8", //
        "-classpath", classpaths, //
        "-verbose");
  }

}
