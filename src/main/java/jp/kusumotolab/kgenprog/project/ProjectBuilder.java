package jp.kusumotolab.kgenprog.project;

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
import jp.kusumotolab.kgenprog.project.build.BinaryStore;
import jp.kusumotolab.kgenprog.project.build.BinaryStoreKey;
import jp.kusumotolab.kgenprog.project.build.InMemoryClassManager;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.build.JavaSourceObject;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * @author shin
 *
 */
public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProjectBuilder.class);

  private final TargetProject targetProject;
  private final BinaryStore binaryStore;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
    this.binaryStore = new BinaryStore();
  }

  /**
   * @param generatedSourceCode
   * @return
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode) {
    log.debug("enter build(GeneratedSourceCode)");

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager standardFileManager =
        compiler.getStandardFileManager(null, null, null);
    final InMemoryClassManager inMemoryFileManager =
        new InMemoryClassManager(standardFileManager, binaryStore);

    final List<String> compilationOptions = createDefaultCompilationOptions();
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    // コンパイル対象の JavaFileObject を生成
    final Set<JavaFileObject> javaSourceObjects =
        generateAllJavaSourceObjects(generatedSourceCode.getAllAsts());

    if (javaSourceObjects.isEmpty()) { // xxxxxxxxxxxx
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final Set<JavaBinaryObject> resusedBinaries = extractBinaries(generatedSourceCode.getAllAsts());
    inMemoryFileManager.setClassPathBinaries(resusedBinaries);

    // コンパイルの進捗状況を得るためのWriterを生成
    final StringWriter buildProgressWriter = new StringWriter();

    // コンパイルのタスクを生成
    final CompilationTask task = compiler.getTask(buildProgressWriter, inMemoryFileManager,
        diagnostics, compilationOptions, null, javaSourceObjects);

    System.out.println("-----------------------------------------");
    System.out.println("build:        " + javaSourceObjects);
    System.out.println("   reused:    " + resusedBinaries); // xxxxxxxxxxxxxxxxx
    System.out.println("   all-cache: " + binaryStore.getAll()); // xxxxxxxxxxxxxxxxx

    // コンパイルを実行
    final boolean isBuildFailed = !task.call();

    if (isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final String buildProgressText = buildProgressWriter.toString();

    final Set<JavaBinaryObject> compiledBinaries =
        extractBinaries(generatedSourceCode.getAllAsts());
    final BinaryStore generatedBinaryStore = new BinaryStore();
    generatedBinaryStore.addAll(compiledBinaries);

    final BuildResults buildResults = new BuildResults(generatedSourceCode, false, diagnostics,
        buildProgressText, generatedBinaryStore);

    log.debug("exit build(GeneratedSourceCode, Path) -- build succeeded.");
    return buildResults;
  }


  /**
   * 指定astに対応する全JavaBinaryObjectをbinaryStoreから取得する．
   * 
   * @param asts
   * @return
   */
  private Set<JavaBinaryObject> extractBinaries(List<GeneratedAST<? extends SourcePath>> asts) {
    return asts.stream()
        .map(BinaryStoreKey::new)
        .map(key -> binaryStore.get(key))
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  /**
   * 指定astからコンパイル用のJavaSourceObjectを生成する．
   * 
   * @param asts
   * @return
   */
  private Set<JavaFileObject> generateAllJavaSourceObjects(
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
    final List<String> options = new ArrayList<>();
    options.add("-encoding");
    options.add("UTF-8");
    options.add("-classpath");
    options.add(String.join(File.pathSeparator, this.targetProject.getClassPaths()
        .stream()
        .map(cp -> cp.path.toString())
        .collect(Collectors.toList())));
    options.add("-verbose");
    return options;
  }

}
