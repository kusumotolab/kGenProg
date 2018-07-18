package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProcessBuilder.class);

  private final TargetProject targetProject;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
  }

  /**
   * @param generatedSourceCode null でなければ与えられた generatedSourceCode からビルド．null の場合は，初期ソースコードからビルド
   * @param workingDir バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode, final Path workingDir) {
    log.debug("enter build(GeneratedSourceCode, Path)");

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    // workingDir が存在しなければ生成
    if (Files.notExists(workingDir)) {
      try {
        Files.createDirectories(workingDir);
      } catch (IOException e) {
        log.error(e.getMessage(), e);

        // TODO should be considered
        return null;
      }
    }

    // コンパイル対象の JavaFileObject を生成
    final Iterable<? extends JavaFileObject> javaFileObjects =
        generateAllJavaFileObjects(generatedSourceCode.getAsts(), fileManager);

    final List<String> compilationOptions = new ArrayList<>();
    compilationOptions.add("-d");
    compilationOptions.add(workingDir.toAbsolutePath()
        .toString());
    compilationOptions.add("-encoding");
    compilationOptions.add("UTF-8");
    compilationOptions.add("-classpath");
    compilationOptions.add(String.join(File.pathSeparator, this.targetProject.getClassPaths()
        .stream()
        .map(cp -> cp.path.toString())
        .collect(Collectors.toList())));
    compilationOptions.add("-verbose");

    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final List<String> verboseLines = new ArrayList<>();
    final CompilationTask task = compiler.getTask(new Writer() {

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        final String text = new String(cbuf);
        verboseLines.add(text.substring(off, off + len));
      }

      @Override
      public void flush() throws IOException {}

      @Override
      public void close() throws IOException {}
    }, fileManager, diagnostics, compilationOptions, null, javaFileObjects);

    try {
      fileManager.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    final boolean isFailed = !task.call();

    final BuildResults buildResults =
        new BuildResults(generatedSourceCode, isFailed, workingDir, diagnostics);

    if (buildResults.isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path) -- build failed.");
      return buildResults;
    }

    // ソースファイルとクラスファイルのマッピング
    final Collection<File> classFiles =
        FileUtils.listFiles(workingDir.toFile(), new String[] {"class"}, true);

    // TODO: https://github.com/kusumotolab/kGenProg/pull/154
    // final Set<String> updatedFiles = getUpdatedFiles(verboseLines);

    final List<SourcePath> allSourcePaths = new ArrayList<>();
    allSourcePaths.addAll(this.targetProject.getSourcePaths());
    allSourcePaths.addAll(this.targetProject.getTestPaths());

    for (final File classFile : classFiles) {

      // TODO: https://github.com/kusumotolab/kGenProg/pull/154
      // 更新されたファイルの中に classFile が含まれていない場合は削除．この機能はとりあえず無しで問題ない
      // if (!updatedFiles.isEmpty() && !updatedFiles.contains(classFile.getAbsolutePath())) {
      // if (!classFile.delete()) {
      // throw new RuntimeException();
      // }
      // continue;
      // }

      // クラスファイルのパース
      final ClassParser parser = this.parse(classFile);

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
        buildResults.addMapping(correspondingSourceFile.path,
            Paths.get(classFile.getAbsolutePath()));
        buildResults.addMapping(correspondingSourceFile.path, fqn);
      } else {
        buildResults.setMappingAvailable(false);
      }
    }
    log.debug("exit build(GeneratedSourceCode, Path) -- build succeeded.");
    return buildResults;
  }

  /**
   * すべて（ターゲットソースファイルとテストコード）の JavaFileObject を生成するメソッド
   * 
   * @param list
   * @param fileManager
   * @return
   */
  private Iterable<? extends JavaFileObject> generateAllJavaFileObjects(
      final List<GeneratedAST> list, final StandardJavaFileManager fileManager) {

    final Iterable<? extends JavaFileObject> targetIterator =
        generateJavaFileObjectsFromGeneratedAst(list);
    final Iterable<? extends JavaFileObject> testIterator =
        generateJavaFileObjectsFromSourceFile(this.targetProject.getTestPaths(), fileManager);

    return Stream.concat( //
        StreamSupport.stream(targetIterator.spliterator(), false), //
        StreamSupport.stream(testIterator.spliterator(), false))
        .collect(Collectors.toSet());
  }

  /**
   * GeneratedAST の List からJavaFileObject を生成するメソッド
   * 
   * @param asts
   * @return
   */
  private Iterable<? extends JavaFileObject> generateJavaFileObjectsFromGeneratedAst(
      final List<GeneratedAST> asts) {
    return asts.stream()
        .map(JavaSourceFromString::new)
        .collect(Collectors.toSet());
  }

  /**
   * ソースファイルから JavaFileObject を生成するメソッド
   * 
   * @param paths
   * @param fileManager
   * @return
   */
  private Iterable<? extends JavaFileObject> generateJavaFileObjectsFromSourceFile(
      final List<SourcePath> paths, final StandardJavaFileManager fileManager) {
    final Set<String> sourceFileNames = paths.stream()
        .map(f -> f.path.toString())
        .collect(Collectors.toSet());
    return fileManager.getJavaFileObjectsFromStrings(sourceFileNames);
  }


  private ClassParser parse(final File classFile) {
    log.debug("enter parse(File)");
    try (final InputStream is = new FileInputStream(classFile)) {
      final ClassReader reader = new ClassReader(is);
      final ClassParser parser = new ClassParser(Opcodes.ASM6);
      reader.accept(parser, ClassReader.SKIP_CODE);
      log.debug("exit parse(File)");
      return parser;
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
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


class JavaSourceFromString extends SimpleJavaFileObject {

  final String code;

  JavaSourceFromString(final GeneratedAST ast) {
    this(ast.getPrimaryClassName(), ast.getSourceCode());
  }

  JavaSourceFromString(final String name, final String code) {
    super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
}
