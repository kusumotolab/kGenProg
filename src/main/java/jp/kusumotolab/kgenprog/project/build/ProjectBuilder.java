package jp.kusumotolab.kgenprog.project.build;

import java.io.File;
import java.io.IOException;
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
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * @author shin
 *
 */
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

    // 再利用可能なオブジェクト
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
    log.debug("enter build(GeneratedSourceCode)");

    final List<GeneratedAST<?>> allAsts = generatedSourceCode.getAllAsts();
    final Set<JavaSourceObject> javaSourceObjects = generateJavaSourceObjects(allAsts);

    if (javaSourceObjects.isEmpty()) { // TODO xxxxxxxxxxxx
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final Set<JavaBinaryObject> resusedBinaryObject = extractBinaryObjects(allAsts);
    inMemoryFileManager.setClassPathBinaries(resusedBinaryObject);

    final StringWriter buildProgressWriter = new StringWriter();
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    // コンパイルのタスクを生成
    final CompilationTask task = compiler.getTask(buildProgressWriter, inMemoryFileManager,
        diagnostics, compilationOptions, null, javaSourceObjects);

    log.trace("-----------------------------------------");
    log.trace("build:        " + javaSourceObjects);
    log.trace("   reused:    " + resusedBinaryObject);
    log.trace("   all-cache: " + binaryStore.getAll());

    // コンパイルを実行
    final boolean isBuildFailed = !task.call();

    if (isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final BinaryStore generatedBinaryStore = new BinaryStore();
    final Set<JavaBinaryObject> compiledBinaries = extractBinaryObjects(allAsts);
    generatedBinaryStore.addAll(compiledBinaries);

    final BuildResults buildResults = new BuildResults(generatedSourceCode, false, diagnostics,
        buildProgressWriter.toString(), generatedBinaryStore);

    log.debug("exit build(GeneratedSourceCode, Path) -- build succeeded.");
    return buildResults;
  }


  /**
   * 指定astに対応するJavaBinaryObjectをbinaryStoreから取得する．
   * 
   * @param asts
   * @return
   */
  private Set<JavaBinaryObject> extractBinaryObjects(
      List<GeneratedAST<? extends SourcePath>> asts) {
    return asts.stream()
        .map(BinaryStoreKey::new)
        .map(key -> binaryStore.get(key))
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
  private Set<JavaSourceObject> generateJavaSourceObjects(
      List<GeneratedAST<? extends SourcePath>> asts) {
    return asts.stream()
        .filter(ast -> !binaryStore.exists(new BinaryStoreKey(ast)))
        .map(JavaSourceObject::new)
        .collect(Collectors.toSet());
  }

  /**
   * デフォルトのコンパイルオプションを生成する．
   * 
   * @return
   */
  private List<String> createDefaultCompilationOptions() {
    final String classpaths = String.join(File.pathSeparator, this.targetProject.getClassPaths()
        .stream()
        .map(cp -> cp.path.toString())
        .collect(Collectors.toList()));

    final List<String> options = new ArrayList<>();
    options.add("-encoding");
    options.add("UTF-8");
    options.add("-classpath");
    options.add(classpaths);
    options.add("-verbose");
    return options;
  }

}
