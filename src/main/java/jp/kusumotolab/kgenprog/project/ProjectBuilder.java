package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProcessBuilder.class);
  static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

  private final TargetProject targetProject;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
  }

  /**
   * 初期ソースコードをビルド
   *
   * @param outDir バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  @Deprecated
  public BuildResults build(final Path outDir) {
    log.debug("enter build(Path)");
    return this.build(null, outDir);
  }

  /**
   * @param generatedSourceCode null でなければ与えられた generatedSourceCode からビルド．null の場合は，初期ソースコードからビルド
   * @param outDir バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode, final Path outDir) {
    log.debug("enter build(GeneratedSourceCode, Path)");

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    // outディレクトリが存在しなければ生成
    final File outputDirectoryFile = outDir.toFile();
    if (!outputDirectoryFile.exists()) {
      outputDirectoryFile.mkdirs();
    }

    final Iterable<? extends JavaFileObject> javaFileObjects;

    // variant が null なら，初期ソースコードをビルド
    if (null == generatedSourceCode) {
      javaFileObjects = fileManager.getJavaFileObjectsFromStrings(this.targetProject
          .getSourceFiles().stream().map(f -> f.path.toString()).collect(Collectors.toList()));
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
    compilationOptions.add(outDir.toFile().getAbsolutePath());
    compilationOptions.add("-encoding");
    compilationOptions.add("UTF-8");
    compilationOptions.add("-classpath");
    compilationOptions.add(String.join(CLASSPATH_SEPARATOR, this.targetProject.getClassPaths()
        .stream().map(cp -> cp.path.toString()).collect(Collectors.toList())));

    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final CompilationTask task =
        compiler.getTask(null, fileManager, diagnostics, compilationOptions, null, javaFileObjects);

    final boolean isFailed = !task.call();
    // TODO コンパイルできないときのエラー出力はもうちょっと考えるべき
    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
//      System.err.println(diagnostic.getCode());
//      System.err.println(diagnostic.getKind());
//      System.err.println(diagnostic.getPosition());
//      System.err.println(diagnostic.getStartPosition());
//      System.err.println(diagnostic.getEndPosition());
//      System.err.println(diagnostic.getSource());
//      System.err.println(diagnostic.getMessage(null));
      log.error(diagnostic.getCode());
      log.error("{}", diagnostic.getKind());
      log.error("{}", diagnostic.getPosition());
      log.error("{}", diagnostic.getStartPosition());
      log.error("{}", diagnostic.getEndPosition());
      log.error("{}", diagnostic.getSource());
      log.error(diagnostic.getMessage(null));
    }

    try {
      fileManager.close();
    } catch (final IOException e) {
      log.error("fail to close file", e);
//      e.printStackTrace();
    }

    final BuildResults buildResults =
        new BuildResults(generatedSourceCode, isFailed, outDir, diagnostics);

    if (buildResults.isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path)");
      return buildResults;
    }

    // ビルドが成功したときは，ソースファイルとクラスファイルのマッピング
    // およびFQNとソースファイルのマッピングを取る
    final Collection<File> classFiles =
        FileUtils.listFiles(outDir.toFile(), new String[] {"class"}, true);
    final List<SourceFile> sourceFiles = this.targetProject.getSourceFiles();
    for (final File classFile : classFiles) {

      // クラスファイルのパース
      final ClassParser parser = this.parse(classFile);

      // 対応関係の構築
      final String partialPath = parser.getPartialPath();
      final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(parser.getFQN());
      SourceFile correspondingSourceFile = null;
      for (final SourceFile sourceFile : sourceFiles) {
        if (sourceFile.path.endsWith(partialPath)) {
          correspondingSourceFile = sourceFile;
          break;
        }
      }
      if (null != correspondingSourceFile) {
        buildResults.addMapping(correspondingSourceFile.path,
            Paths.get(classFile.getAbsolutePath()));
        buildResults.addMapping(correspondingSourceFile.path, fqn);
      } else {
        buildResults.setMappingAvailable(false);
      }
    }
    log.debug("exit build(GeneratedSourceCode, Path)");
    return buildResults;
  }

  private ClassParser parse(final File classFile) {
    log.debug("enter parse(File)");

    ClassReader reader = null;
    try {
      reader = new ClassReader(new FileInputStream(classFile));
    } catch (final Exception e) {
      log.error("fail to read or open file", e);
//      e.printStackTrace();
    }
    final ClassParser parser = new ClassParser(Opcodes.ASM6);
    reader.accept(parser, ClassReader.SKIP_CODE);

    log.debug("exit parse(File)");
    return parser;
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
