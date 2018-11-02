package jp.kusumotolab.kgenprog.project.build;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProjectBuilder.class);

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
    final List<GeneratedAST<?>> allAsts = generatedSourceCode.getAllAsts();
    final Set<JavaSourceObject> javaSourceObjects = generateJavaSourceObjects(allAsts);

    // コンパイル対象が存在しない場合 ≒ 全てのコンパイル対象がキャッシュ済みの場合
    // AST構築では，個々のASTのDigestではなくAST全体のDigestを確認しているため，この状況が発生する．
    // TODO
    // きちんとハンドルしないといけない．少なくともEmptyBuildResultsはまずい．
    if (javaSourceObjects.isEmpty()) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    // binaryStoreからコンパイル済みバイナリを取り出してIMFMにセットしておく
    final Set<JavaBinaryObject> resusableBinaryObject = extractJavaBinaryObjects(allAsts);
    inMemoryFileManager.setClassPathBinaries(resusableBinaryObject);

    // コンパイル状況や診断情報等の保持オブジェクトを用意
    final StringWriter buildProgressWriter = new StringWriter();
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    // コンパイルタスクを生成
    final CompilationTask task = compiler.getTask(buildProgressWriter, inMemoryFileManager,
        diagnostics, compilationOptions, null, javaSourceObjects);

    log.trace("-----------------------------------------");
    log.trace("build:        " + javaSourceObjects);
    log.trace("   reused:    " + resusableBinaryObject);
    log.trace("   all-cache: " + binaryStore.getAll());

    // コンパイルを実行
    final boolean isBuildFailed = !task.call();

    if (isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      // diagnostics.getDiagnostics().stream().forEach(System.err::println); //xxxxxxxxxxxx
      return EmptyBuildResults.instance;
    }

    // コンパイル済みバイナリを取り出してセットしておく．
    final BinaryStore compiledBinaryStore = new BinaryStore();
    final Set<JavaBinaryObject> compiledBinaries = extractJavaBinaryObjects(allAsts);
    compiledBinaryStore.addAll(compiledBinaries);

    final BuildResults buildResults = new BuildResults(generatedSourceCode, false, diagnostics,
        buildProgressWriter.toString(), compiledBinaryStore);
    return buildResults;
  }

  /**
   * 指定astに対応するJavaBinaryObjectをbinaryStoreから取得する．
   * 
   * @param asts
   * @return
   */
  private Set<JavaBinaryObject> extractJavaBinaryObjects(final List<GeneratedAST<?>> asts) {
    return asts.stream()
        .map(ast -> binaryStore.get(new TargetFullyQualifiedName(ast.getPrimaryClassName()),
            ast.getMessageDigest())) // TODO
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
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
        .filter(ast -> !binaryStore.exists(new TargetFullyQualifiedName(ast.getPrimaryClassName()),
            ast.getMessageDigest()))
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
    final String classPaths = String.join(File.pathSeparator, classpathList);

    final List<String> options = new ArrayList<>();
    options.add("-encoding");
    options.add("UTF-8");
    options.add("-classpath");
    options.add(classPaths);
    options.add("-verbose");
    return options;
  }

}
