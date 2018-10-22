package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.build.BinaryStore;
import jp.kusumotolab.kgenprog.project.build.BinaryStoreKey;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.build.CompilationUnit;
import jp.kusumotolab.kgenprog.project.build.InMemoryClassManager;
import jp.kusumotolab.kgenprog.project.build.JavaFileObjectFromString;
import jp.kusumotolab.kgenprog.project.build.JavaMemoryObject;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProjectBuilder.class);

  private final TargetProject targetProject;
  private final BinaryStore binaryStore;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
    this.binaryStore = new BinaryStore();
  }

  /**
   * @param generatedSourceCode null でなければ与えられた generatedSourceCode からビルド．null の場合は，初期ソースコードからビルド
   * @param workPath バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode) {
    log.debug("enter build(GeneratedSourceCode)");

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager standardFileManager =
        compiler.getStandardFileManager(null, null, null);
    final InMemoryClassManager inMemoryFileManager =
        new InMemoryClassManager(standardFileManager, binaryStore);

    // コンパイルの引数を生成
    final List<String> compilationOptions = new ArrayList<>();
    compilationOptions.add("-encoding");
    compilationOptions.add("UTF-8");
    compilationOptions.add("-classpath");
    compilationOptions.add(String.join(File.pathSeparator, this.targetProject.getClassPaths()
        .stream()
        .map(cp -> cp.path.toString())
        .collect(Collectors.toList())));
    compilationOptions.add("-verbose");
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    // コンパイル対象の JavaFileObject を生成
    final Set<JavaFileObject> javaFileObjects =
        generateAllJavaFileObjects(generatedSourceCode.getAllAsts());

    if (javaFileObjects.isEmpty()) { // xxxxxxxxxxxx
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final Set<JavaMemoryObject> bins = new HashSet<>();
    for (final GeneratedAST<? extends SourcePath> ast : generatedSourceCode.getAllAsts()) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      final Set<JavaMemoryObject> jfos = binaryStore.get(key);
      if (!jfos.isEmpty()) {
        // bins.addAll(jfos);
      }
    }

    inMemoryFileManager.setClassPathBinaries(bins);

    // コンパイルの進捗状況を得るためのWriterを生成
    final StringWriter buildProgressWriter = new StringWriter();

    // コンパイルのタスクを生成
    final CompilationTask task = compiler.getTask(buildProgressWriter, inMemoryFileManager,
        diagnostics, compilationOptions, null, javaFileObjects);

    System.out.println("-----------------------------------------");
    System.out.println("build:        " + javaFileObjects);
    System.out.println("   reused:    " + bins); // xxxxxxxxxxxxxxxxx
    System.out.println("   all-cache: " + binaryStore.getAll()); // xxxxxxxxxxxxxxxxx
    String code = generatedSourceCode.getProductAsts()
        .get(0)
        .getSourceCode();

    // コンパイルを実行
    final boolean isBuildFailed = !task.call();


    if (isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final String buildProgressText = buildProgressWriter.toString();

    final BinaryStore binStore = new BinaryStore();
    for (final GeneratedAST<? extends SourcePath> ast : generatedSourceCode.getAllAsts()) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      final Set<JavaMemoryObject> jfos = binaryStore.get(key);
      for (JavaMemoryObject jfo : jfos) {
        binStore.add(jfo);
      }
    }

    final BuildResults buildResults = new BuildResults(generatedSourceCode, false,
        diagnostics, buildProgressText, binStore);

    log.debug("exit build(GeneratedSourceCode, Path) -- build succeeded.");
    return buildResults;
  }

  private Set<JavaFileObject> generateAllJavaFileObjects(
      final List<GeneratedAST<? extends SourcePath>> asts) {

    final Set<JavaFileObject> result = new HashSet<>();
    for (final GeneratedAST<? extends SourcePath> ast : asts) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      if (!binaryStore.get(key)
          .isEmpty()) {
        // result.addAll(binaryStore.get(key)); // necessary???????????? TODO
        continue;
      }
      final JavaFileObjectFromString m = new JavaFileObjectFromString(ast.getPrimaryClassName(),
          ast.getSourceCode(), ast.getMessageDigest(), ast.getSourcePath());
      result.add(m);
    }

    return result;
  }

}
