package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
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
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  private static Logger log = LoggerFactory.getLogger(ProcessBuilder.class);
  static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

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
    final File workingDireFile = workingDir.toFile();
    if (!workingDireFile.exists()) {
      workingDireFile.mkdirs();
    }

    // コンパイル対象の JavaFileObject を生成
    final Iterable<? extends JavaFileObject> javaFileObjects =
        generateAllJavaFileObjects(generatedSourceCode.getFiles(), fileManager);

    final List<String> compilationOptions = new ArrayList<>();
    compilationOptions.add("-d");
    compilationOptions.add(workingDir.toFile()
        .getAbsolutePath());
    compilationOptions.add("-encoding");
    compilationOptions.add("UTF-8");
    compilationOptions.add("-classpath");
    compilationOptions.add(String.join(CLASSPATH_SEPARATOR, this.targetProject.getClassPaths()
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

    // TODO コンパイルできないときのエラー出力はもうちょっと考えるべき
    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
      // System.err.println(diagnostic.getCode());
      // System.err.println(diagnostic.getKind());
      // System.err.println(diagnostic.getPosition());
      // System.err.println(diagnostic.getStartPosition());
      // System.err.println(diagnostic.getEndPosition());
      // System.err.println(diagnostic.getSource());
      // System.err.println(diagnostic.getMessage(null));
      log.error(diagnostic.getCode());
      log.error("{}", diagnostic.getKind());
      log.error("{}", diagnostic.getPosition());
      log.error("{}", diagnostic.getStartPosition());
      log.error("{}", diagnostic.getEndPosition());
      log.error("{}", diagnostic.getSource());
      log.error(diagnostic.getMessage(null));
    }

    final BuildResults buildResults =
        new BuildResults(generatedSourceCode, isFailed, workingDir, diagnostics);

    if (buildResults.isBuildFailed) {
      log.debug("exit build(GeneratedSourceCode, Path)");
      return buildResults;
    }

    // ソースファイルとクラスファイルのマッピング
    final Collection<File> classFiles =
        FileUtils.listFiles(workingDir.toFile(), new String[] {"class"}, true);

    // TODO: https://github.com/kusumotolab/kGenProg/pull/154
    // final Set<String> updatedFiles = getUpdatedFiles(verboseLines);

    final List<SourceFile> allSourceFiles = new ArrayList<>();
    allSourceFiles.addAll(this.targetProject.getSourceFiles());
    allSourceFiles.addAll(this.targetProject.getTestFiles());

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
      SourceFile correspondingSourceFile = null;
      for (final SourceFile sourceFile : allSourceFiles) {
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
        generateJavaFileObjectsFromSourceFile(this.targetProject.getTestFiles(), fileManager);

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
   * @param files
   * @param fileManager
   * @return
   */
  private Iterable<? extends JavaFileObject> generateJavaFileObjectsFromSourceFile(
      final List<SourceFile> files, final StandardJavaFileManager fileManager) {
    final Set<String> sourceFileNames = files.stream()
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
