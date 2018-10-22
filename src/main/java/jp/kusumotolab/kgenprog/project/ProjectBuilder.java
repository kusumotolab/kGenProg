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

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
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
    final InMemoryClassManager inMemoryFileManager = new InMemoryClassManager(standardFileManager);

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

    final Set<JavaFileObject> bins = new HashSet<>();
    for (final GeneratedAST<? extends SourcePath> ast : generatedSourceCode.getAllAsts()) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      final Set<JavaFileObject> jfos = BinaryStore.instance.get(key);
      if (null != jfos) {
        bins.addAll(jfos);
      }
    }
    inMemoryFileManager.setClasses(bins);

    if (javaFileObjects.isEmpty()) { // xxxxxxxxxxxx
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    // コンパイルの進捗状況を得るためのWriterを生成
    final StringWriter buildProgressWriter = new StringWriter();

    // コンパイルのタスクを生成
    final CompilationTask task = compiler.getTask(buildProgressWriter, inMemoryFileManager,
        diagnostics, compilationOptions, null, javaFileObjects);

    BinaryStore bin = BinaryStore.instance; // xxxxxxxxxxxxxxxxxxx
    System.out.println("-----------------------------------------");
    System.out.println("build:        " + javaFileObjects);
    System.out.println("   reused:    " + bins); // xxxxxxxxxxxxxxxxx
    System.out.println("   all-cache: " + bin.getAll()); // xxxxxxxxxxxxxxxxx
    String code = generatedSourceCode.getProductAsts().get(0).getSourceCode();

    // コンパイルを実行
    final boolean isBuildFailed = !task.call();


    if (isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return EmptyBuildResults.instance;
    }

    final String buildProgressText = buildProgressWriter.toString();
    final List<CompilationUnit> compilationUnits = inMemoryFileManager.getAllClasses();
    // final CompilationPackage compilationPackage = new CompilationPackage(compilationUnits);

    // final List<CompilationUnit> units = null;
    final List<CompilationUnit> units = new ArrayList<>();
    for (final GeneratedAST<? extends SourcePath> ast : generatedSourceCode.getAllAsts()) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      final Set<JavaFileObject> jfos = BinaryStore.instance.get(key);
      for (JavaFileObject jfo : jfos) {
        units.add(new CompilationUnit(ast.getPrimaryClassName(), (JavaMemoryObject) jfo));
      }
    }
    final CompilationPackage compilationPackage = new CompilationPackage(units);

    final BuildResults buildResults =
        new BuildResults(generatedSourceCode, compilationPackage, diagnostics, buildProgressText);

    // TODO: https://github.com/kusumotolab/kGenProg/pull/154
    // final Set<String> updatedFiles = getUpdatedFiles(verboseLines);

    final List<SourcePath> allSourcePaths = new ArrayList<>();
    allSourcePaths.addAll(this.targetProject.getProductSourcePaths());
    allSourcePaths.addAll(this.targetProject.getTestSourcePaths());

    for (final CompilationUnit compilationUnit : compilationUnits) {

      // TODO: https://github.com/kusumotolab/kGenProg/pull/154
      // 更新されたファイルの中に classFile が含まれていない場合は削除．この機能はとりあえず無しで問題ない
      // if (!updatedFiles.isEmpty() && !updatedFiles.contains(classFile.getAbsolutePath())) {
      // if (!classFile.delete()) {
      // throw new RuntimeException();
      // }
      // continue;
      // }

      // クラスファイルのパース
      final ClassParser parser = this.parse(compilationUnit);

      // 対応関係の構築
      final String partialPath = parser.getPartialPath();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(parser.getFQN());
      SourcePath correspondingSourceFile = null;
      for (final SourcePath sourcePath : allSourcePaths) {
        if (sourcePath.path.endsWith(partialPath)) {
          correspondingSourceFile = sourcePath;
          break;
        }
      }
      if (null != correspondingSourceFile) {
        buildResults.addMapping(correspondingSourceFile.path, fqn);
      } else {
        buildResults.setMappingAvailable(false);
      }
    }
    log.debug("exit build(GeneratedSourceCode, Path) -- build succeeded.");
    return buildResults;
  }

  private Set<JavaFileObject> generateAllJavaFileObjects(
      final List<GeneratedAST<? extends SourcePath>> asts) {

    final Set<JavaFileObject> result = new HashSet<>();
    for (final GeneratedAST<? extends SourcePath> ast : asts) {
      final BinaryStoreKey key = new BinaryStoreKey(ast);
      final Set<JavaFileObject> jfos = BinaryStore.instance.get(key);
      if (null != jfos) {
        // result.addAll(jfos);
        continue;
      }
      final JavaFileObjectFromString m = new JavaFileObjectFromString(ast.getPrimaryClassName(),
          ast.getSourceCode(), ast.getMessageDigest());
      result.add(m);
    }

    return result;
    // return asts.stream()
    // .map(ast -> {
    // final BinaryStoreKey key = new BinaryStoreKey(ast);
    // final Set<JavaFileObject> jfos = BinaryStore.instance.get(key);
    // if (null != jfos) {
    // return jfos.stream();
    // }
    // return Stream.of();
    // })
    // .collect(Collectors.toSet());
  }

  private ClassParser parse(final CompilationUnit compilationUnit) {
    log.debug("enter parse(CompilationUnit)");
    final ClassReader reader = new ClassReader(compilationUnit.getBytecode());
    final ClassParser parser = new ClassParser(Opcodes.ASM6);
    reader.accept(parser, ClassReader.SKIP_CODE);
    log.debug("exit parse(File)");
    return parser;
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  @SuppressWarnings("unused")
  private Set<String> getUpdatedFiles(final List<String> lines) {
    final String prefixWindowsOracle = "[RegularFileObject[";
    final String prefixMacOracle = "[DirectoryFileObject[";
    final Set<String> updatedFiles = new HashSet<>();
    for (final String line : lines) {

      // for OracleJDK in Mac environment
      if (line.startsWith(prefixMacOracle)) {
        final int startIndex = prefixMacOracle.length();
        final int endIndex = line.indexOf(']');
        final String updatedFile = line.substring(startIndex, endIndex)
            .replace(":", File.separator);
        updatedFiles.add(updatedFile);
      }

      // for OracleJDK in Windows environment
      else if (line.startsWith(prefixWindowsOracle)) {
        final int startIndex = prefixWindowsOracle.length();
        final int endIndex = line.indexOf(']');
        final String updatedFile = line.substring(startIndex, endIndex);
        updatedFiles.add(updatedFile);
      }
    }
    return updatedFiles;
  }
}
