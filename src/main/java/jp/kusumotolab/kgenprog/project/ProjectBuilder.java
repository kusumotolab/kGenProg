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
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class ProjectBuilder {

  static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

  private final TargetProject targetProject;

  public ProjectBuilder(final TargetProject targetProject) {
    this.targetProject = targetProject;
  }

  /**
   * 初期ソースコードをビルド
   * 
   * @param workingDir バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  @Deprecated
  public BuildResults build(final Path workingDir) {
    return this.build(null, workingDir);
  }

  /**
   * @param generatedSourceCode null でなければ与えられた generatedSourceCode からビルド．null の場合は，初期ソースコードからビルド
   * @param workingDir バイトコード出力ディレクトリ
   * @return ビルドに関するさまざまな情報
   */
  public BuildResults build(final GeneratedSourceCode generatedSourceCode, final Path workingDir) {

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    // outディレクトリが存在しなければ生成
    final File outputDirectoryFile = workingDir.toFile();
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
    compilationOptions.add(workingDir.toFile().getAbsolutePath());
    compilationOptions.add("-encoding");
    compilationOptions.add("UTF-8");
    compilationOptions.add("-classpath");
    compilationOptions.add(String.join(CLASSPATH_SEPARATOR, this.targetProject.getClassPaths()
        .stream().map(cp -> cp.path.toString()).collect(Collectors.toList())));
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
      System.err.println(diagnostic.getCode());
      System.err.println(diagnostic.getKind());
      System.err.println(diagnostic.getPosition());
      System.err.println(diagnostic.getStartPosition());
      System.err.println(diagnostic.getEndPosition());
      System.err.println(diagnostic.getSource());
      System.err.println(diagnostic.getMessage(null));
    }

    final BuildResults buildResults =
        new BuildResults(generatedSourceCode, isFailed, workingDir, diagnostics);

    if (buildResults.isBuildFailed) {
      return buildResults;
    }

    // ソースファイルとクラスファイルのマッピング
    final Collection<File> classFiles =
        FileUtils.listFiles(workingDir.toFile(), new String[] {"class"}, true);
    final List<SourceFile> sourceFiles = this.targetProject.getSourceFiles();
    final Set<String> updatedFiles = getUpdatedFiles(verboseLines);
    for (final File classFile : classFiles) {

      // 更新されたファイルの中に classFile が含まれていない場合は削除
      if (!updatedFiles.isEmpty() && !updatedFiles.contains(classFile.getAbsolutePath())) {
        if (!classFile.delete()) {
          throw new RuntimeException();
        }
        continue;
      }

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

    return buildResults;
  }

  private ClassParser parse(final File classFile) {

    try (final InputStream is = new FileInputStream(classFile)) {
      final ClassReader reader = new ClassReader(is);
      final ClassParser parser = new ClassParser(Opcodes.ASM6);
      reader.accept(parser, ClassReader.SKIP_CODE);
      return parser;
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private Set<String> getUpdatedFiles(final List<String> lines) {
    final String prefixWindowsOracle = "[RegularFileObject[";
    final String prefixMacOracle = "[DirectoryFileObject[";
    final Set<String> updatedFiles = new HashSet<>();
    for (final String line : lines) {

      // for OracleJDK in Mac environment
      if (line.startsWith(prefixMacOracle)) {
        final int startIndex = prefixMacOracle.length();
        final int endIndex = line.indexOf(']');
        final String updatedFile =
            line.substring(startIndex, endIndex).replace(":", File.separator);
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

  JavaSourceFromString(String name, String code) {
    super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
}
