package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class BuildResults {

  public final boolean isBuildFailed;
  public final Path outDir;
  // TODO コンパイルできないときのエラー情報はほんとにこの型でいいか？
  public final DiagnosticCollector<JavaFileObject> diagnostics;

  // ソースとクラスファイル間のマッピング
  private final Map<Path, Set<Path>> sourceToClassMap;
  private final Map<Path, Path> classToSourceMap;

  // ソースとFQN間のマッピング
  private final Map<Path, Set<FullyQualifiedName>> sourceToFQNMap;
  private final Map<FullyQualifiedName, Path> fqnToSourceMap;

  // 対応関係がうまく構築できたかの可否
  private boolean isMappingAvaiable;

  // ビルド元となったソースコード
  public final GeneratedSourceCode sourceCode;

  private CompilationPackage compilationPackage;

  /**
   * 
   * @param sourceCode ビルド元となったソースコード
   * @param outDir クラスファイル生成ディレクトリ
   * @param compilationPackage バイトコード
   * @param diagnostics ビルド時の詳細情報
   */
  public BuildResults(final GeneratedSourceCode sourceCode, final Path outDir,
      final CompilationPackage compilationPackage,
      final DiagnosticCollector<JavaFileObject> diagnostics) {
    this(sourceCode, false, outDir, compilationPackage, diagnostics);
  }

  /**
   * コンストラクタ（後で書き換え TODO）
   * 
   * @param sourceCode ビルド元となったソースコード
   * @param isBuildFailed ビルドの成否
   * @param outDir クラスファイル生成ディレクトリ
   * @param diagnostics ビルド時の詳細情報
   */
  protected BuildResults(final GeneratedSourceCode sourceCode, final boolean isBuildFailed,
      final Path outDir, final CompilationPackage compilationPackage,
      final DiagnosticCollector<JavaFileObject> diagnostics) {
    this.sourceCode = sourceCode;
    this.isBuildFailed = isBuildFailed;
    this.outDir = outDir;
    this.compilationPackage = compilationPackage;
    this.diagnostics = diagnostics;
    this.sourceToClassMap = new HashMap<>();
    this.classToSourceMap = new HashMap<>();
    this.fqnToSourceMap = new HashMap<>();
    this.sourceToFQNMap = new HashMap<>();
    this.isMappingAvaiable = true;
  }

  public CompilationPackage getCompilationPackage() {
    return compilationPackage;
  }

  /**
   * ソースファイルとクラスファイル間のマッピングを追加する
   * 
   * @param pathToSource ソースファイルのPath
   * @param pathToClass クラスファイルのPath
   */
  public void addMapping(final Path pathToSource, final Path pathToClass) {
    Set<Path> pathToClasses = this.sourceToClassMap.get(pathToSource);
    if (null == pathToClasses) {
      pathToClasses = new HashSet<>();
      this.sourceToClassMap.put(pathToSource, pathToClasses);
    }
    pathToClasses.add(pathToClass);
    this.classToSourceMap.put(pathToClass, pathToSource);
  }

  /**
   * 引数で与えたソースファイルに対応するクラスファイルのPath（PathのSet）を返す
   * 
   * @param pathToSource ソースファイルの Path
   * @return 引数で与えたソースファイルに対応するクラスファイルの Path の Set
   */
  public Set<Path> getPathToClasses(final Path pathToSource) {
    return this.sourceToClassMap.get(pathToSource);
  }

  /**
   * 引数絵与えたソースファイルに対応するFQNのPath（FQNのSet）を返す
   * 
   * @param pathToSource ソースファイルの Path
   * @return 引数で与えたソースファイルに対応する FQN の Set
   */
  public Set<FullyQualifiedName> getPathToFQNs(final Path pathToSource) {
    return this.sourceToFQNMap.get(pathToSource);
  }

  /**
   * ソースファイルと FQN 間のマッピングを追加する
   * 
   * @param source ソースファイルの Path
   * @param fqn FQN
   */
  public void addMapping(final Path source, final FullyQualifiedName fqn) {

    Set<FullyQualifiedName> fqns = this.sourceToFQNMap.get(source);
    if (null == fqns) {
      fqns = new HashSet<>();
      this.sourceToFQNMap.put(source, fqns);
    }
    fqns.add(fqn);

    // TODO すでに同じfqnな別のsourceが登録されているかチェックすべき
    // 登録されている場合はillegalStateExceptionを投げるべき？
    this.fqnToSourceMap.put(fqn, source);
  }

  /**
   * クラスファイルの Path から対応するソースファイルの Path を返す
   * 
   * @param pathToClass クラスファイルの Path
   * @return 対応するソースファイルの Path
   */
  public Path getPathToSource(final Path pathToClass) {
    return this.classToSourceMap.get(pathToClass);
  }

  /**
   * FQN から対応するソースファイルの Path を返す
   * 
   * @param fqn FQN
   * @return 対応するソースファイルの FQN
   */
  public Path getPathToSource(final FullyQualifiedName fqn) {
    return this.fqnToSourceMap.get(fqn);
  }

  /**
   * 「ソースファイルとクラスファイルの対応関係」および「ソースファイルとFQNの対応関係」の構築の成否を登録する
   * 
   * @param available true なら成功，false なら失敗
   */
  public void setMappingAvailable(final boolean available) {
    this.isMappingAvaiable = available;
  }

  /**
   * 「ソースファイルとクラスファイルの対応関係」および「ソースファイルとFQNの対応関係」の構築の成否を返す
   * 
   * @return true なら成功，false なら失敗
   */
  public boolean isMappingAvailable() {
    return this.isMappingAvaiable;
  }
}
