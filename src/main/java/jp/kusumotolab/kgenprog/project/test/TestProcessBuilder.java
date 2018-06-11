package jp.kusumotolab.kgenprog.project.test;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.BuildResults;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

/**
 * テスト実行クラス． 外部プロジェクトの単体テストclassファイルを実行してその結果を回収する．
 * 
 * @author shinsuke
 *
 */
public class TestProcessBuilder {

  final private TargetProject targetProject;
  final private BuildResults buildResults;

  final static private String javaHome = System.getProperty("java.home");
  final static private String javaBin = Paths.get(javaHome + "/bin/java").toString();
  final static private String testExecutorMain =
      "jp.kusumotolab.kgenprog.project.test.TestExecutorMain";

  // for compatibility
  @Deprecated
  public TestProcessBuilder(final TargetProject targetProject) {
    this(targetProject, Paths.get("")); // TODO
  }

  @Deprecated
  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    return null;
  }

  public TestProcessBuilder(final TargetProject targetProject, final Path outDir) {
    this.targetProject = targetProject;
    this.buildResults = new ProjectBuilder(this.targetProject).build(outDir);
  }

  public TestResults start() {
    final String classpath = filterClasspathFromSystemClasspath();
    final String sourceFQNs = joinFQNs(getSourceFQNs());
    final String testFQNs = joinFQNs(getTestFQNs());

    // start()時にワーキングディレクトリを変更するために，binDirはrootPathからの相対パスに変更
    // TODO いろんな状況でバグるので要修正．一時的な処置．
    final Path relativeOutDir = this.targetProject.rootPath.relativize(this.buildResults.outDir);

    final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, testExecutorMain,
        "-b", relativeOutDir.toString(), // TODO should-use-path
        "-s", sourceFQNs, "-t", testFQNs);

    // テスト実行のためにworking dirを移動（対象プロジェクトが相対パスを利用している可能性が高いため）
    builder.directory(this.targetProject.rootPath.toFile());

    try {
      final Process process = builder.start();
      process.waitFor();
      return TestResults.deserialize();
      /*
       * String out_result = IOUtils.toString(process.getInputStream(), "UTF-8"); String err_result
       * = IOUtils.toString(process.getErrorStream(), "SJIS"); System.out.println(out_result);
       * System.err.println(err_result); System.out.println(process.exitValue());
       */
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
    return null;
  }

  private String joinFQNs(Collection<FullyQualifiedName> fqns) {
    return fqns.stream().map(fqn -> fqn.value).collect(joining(TestExecutorMain.SEPARATOR));
  }

  private Set<FullyQualifiedName> getSourceFQNs() {
    final Set<FullyQualifiedName> sourceFQNs = getFQNs(this.targetProject.getSourceFiles());

    // TODO testにsourceが含まれるのでsubtractしておく．
    // https://github.com/kusumotolab/kGenProg/issues/79
    sourceFQNs.removeAll(getTestFQNs());

    return sourceFQNs;
  }

  private Set<FullyQualifiedName> getTestFQNs() {
    return getFQNs(this.targetProject.getTestFiles());
  }

  private Set<FullyQualifiedName> getFQNs(final List<SourceFile> sources) {
    return sources.stream().map(source -> this.buildResults.getPathToFQNs(source.path))
        .flatMap(c -> c.stream()).collect(toSet());
  }

  private final String jarFileTail = "-(\\d+\\.)+jar$";

  /**
   * 現在実行中のjavaプロセスのcpから，TestExecutorMain実行に必要なcpをフィルタリングする．
   * 
   * @return
   */
  private String filterClasspathFromSystemClasspath() {
    // 依存する外部ライブラリを定義
    // TODO もうちょいcoolに改善
    final String[] classpaths = System.getProperty("java.class.path").split(File.pathSeparator);
    final List<String> filter = new ArrayList<>();
    filter.add("args4j");
    filter.add("jacoco\\.core");
    filter.add("asm");
    filter.add("asm-commons");
    filter.add("asm-tree");
    filter.add("junit");
    filter.add("hamcrest-core");

    // cp一覧から必須外部ライブラリのみをフィルタリング
    final List<String> result = Arrays.stream(classpaths)
        .filter(cp -> filter.stream().anyMatch(f -> cp.matches(".*" + f + jarFileTail)))
        .collect(Collectors.toList());

    // 自身（TestProcessBuilder.class）へのcpを追加
    try {
      result.add(Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
          .toString());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return String.join(File.pathSeparator, result);
  }
}
