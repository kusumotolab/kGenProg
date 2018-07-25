package jp.kusumotolab.kgenprog.project.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

/**
 * 
 * @author shinsuke
 * @see http://www.nminoru.jp/~nminoru/java/class_unloading.html
 */
public class MemoryClassLoaderTest {

  final static Path rootPath = Paths.get("example/example01");
  final static Path workPath = rootPath.resolve("bin");

  final static String bc = "jp.kusumotolab.BuggyCalculator";
  final static String bct = "jp.kusumotolab.BuggyCalculatorTest";
  final static FullyQualifiedName bcfqn = new TargetFullyQualifiedName(bc);
  final static FullyQualifiedName bctfqn = new TestFullyQualifiedName(bct);

  static MemoryClassLoader loader;

  @BeforeClass
  public static void beforeClass() {
    try {
      FileUtils.deleteDirectory(workPath.toFile());
    } catch (IOException e) {
      // TODO #97
      // temporal patch
      // nothing todo
    }
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);
  }

  @After
  public void after() throws IOException {
    if (loader != null) {
      loader.close();
    }
    loader = null;
  }

  @Test
  public void testDynamicClassLoading01() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // 動的ロード
    final Class<?> clazz = loader.loadClass(bcfqn);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance).isNotNull();
    assertThat(instance.toString()).startsWith(bcfqn.toString());
    assertThat(clazz.getName()).isEqualTo(bcfqn.toString());
  }

  @Test
  public void testDynamicClassLoading02() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // 動的ロード（Override側のメソッドで試す）
    final Class<?> clazz = loader.loadClass(bcfqn.toString(), false);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance).isNotNull();
    assertThat(instance.toString()).startsWith(bcfqn.toString());
    assertThat(clazz.getName()).isEqualTo(bcfqn.toString());
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading03() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // SystemLoaderで動的ロード，失敗するはず (Exceptionを期待)
    ClassLoader.getSystemClassLoader()
        .loadClass(bcfqn.toString());
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading04() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // リフレクションで動的ロード，失敗するはず (Exceptionを期待)
    // 処理自体は02と等価なはず
    Class.forName(bcfqn.toString());
  }

  @Test
  public void testDynamicClassLoading05() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // リフレクション + MemoryLoaderで動的ロード，これは成功するはず
    final Class<?> clazz = Class.forName(bcfqn.toString(), true, loader);

    assertThat(clazz.getName()).isEqualTo(bcfqn.toString());
  }

  @Test
  public void testClassUnloadingByGC01() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // まず動的ロード
    Class<?> clazz = loader.loadClass(bcfqn);

    // 弱参照（アンロードの監視）の準備
    final WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get()).isNotNull();

    // ロード先への参照（インスタンス）を完全に削除
    loader.close();
    loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get()).isNull();
  }

  @Test
  public void testClassUnloadingByGC02() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // まず動的ロード
    final Class<?> clazz = loader.loadClass(bcfqn);

    // 弱参照（アンロードの監視）の準備
    final WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get()).isNotNull();

    // ロード先への参照（インスタンス）を削除せずに
    // loader.close();
    // loader = null;
    // clazz = null;

    // GCして
    System.gc();

    // アンロードされていないことを確認
    assertThat(targetClassWR.get()).isNotNull();
  }

  @Test
  public void testClassUnloadingByGC03() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // まず動的ロード
    Class<?> clazz = loader.loadClass(bcfqn);

    // 弱参照（アンロードの監視）の準備
    WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get()).isNotNull();

    // ロード先への参照（インスタンス）を完全に削除
    loader.close();
    loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get()).isNull();

    // もう一度ロードすると
    loader = new MemoryClassLoader(workPath);

    clazz = loader.loadClass(bcfqn);
    targetClassWR = new WeakReference<>(clazz);

    // ロードされているはず
    assertThat(targetClassWR.get()).isNotNull();
  }

  @Test
  public void testJUnitWithMemoryLoader01() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // BuggyCalculatorTest（BCTest）をロードしておく
    final Class<?> clazz = loader.loadClass(bctfqn);

    // テストを実行
    // * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
    // * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
    final JUnitCore junitCore = new JUnitCore();
    final Result result = junitCore.run(clazz);

    // きちんと実行できるはず
    assertThat(result.getRunCount()).isEqualTo(4);
    assertThat(result.getFailureCount()).isEqualTo(1);
  }

  @Test
  public void testJUnitWithMemoryLoader02() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // まず何もロードされていないはず
    assertThat(listLoadedClasses(loader)).isEmpty();

    // テストだけをロード
    final Class<?> clazz = loader.loadClass(bctfqn);

    // BCTestがロードされているはず
    assertThat(listLoadedClasses(loader)).contains(bctfqn.toString());

    // テストを実行
    // * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
    // * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
    final JUnitCore junitCore = new JUnitCore();
    junitCore.run(clazz);

    // 上記テストの実行により，BCTestに加えBCもロードされているはず
    assertThat(listLoadedClasses(loader)).contains(bctfqn.toString(), bcfqn.toString());
  }

  @Test
  public void testAddDefinition01() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // .classファイルを探す
    final String className = bcfqn.toString()
        .replace(".", "/") + ".class";
    final Path buggyCalculatorClassFilePath = Paths.get(className);
    final Path classFilePath = Files.walk(workPath)
        .filter(path -> path.endsWith(buggyCalculatorClassFilePath))
        .findFirst()
        .get();

    // .classファイルを直接読み込んでメモリに格納
    final byte[] byteCode = Files.readAllBytes(classFilePath);

    // addDefinitionで定義追加
    loader.addDefinition(bcfqn, byteCode);

    // クラスロードできるはず
    final Class<?> clazz = loader.loadClass(bcfqn);
    assertThat(clazz.getName()).hasToString(bcfqn.toString());
  }

  @Test(expected = ClassFormatError.class)
  public void testAddDefinition02() throws Exception {
    loader = new MemoryClassLoader(workPath);

    // 不正なバイトコードを生成
    final byte[] invalidByteCode = new byte[] {0, 0, 0, 0, 0};

    // addDefinitionで定義追加
    loader.addDefinition(bcfqn, invalidByteCode);

    // クラスロード（バグるはず）
    loader.loadClass(bcfqn);
  }

  /**
   * 指定クラスローダによってロードされたクラス名一覧の取得
   * 
   * @param classLoader
   * @return
   */
  private List<String> listLoadedClasses(final ClassLoader classLoader) throws Exception {
    final Class<?> javaLangClassLoader = findJavaLangClassLoader(classLoader.getClass());

    final Field classesField = javaLangClassLoader.getDeclaredField("classes");
    classesField.setAccessible(true);

    @SuppressWarnings("unchecked")
    final Vector<Class<?>> classes = (Vector<Class<?>>) classesField.get(classLoader);

    return classes.stream()
        .map(Class::getName)
        .collect(Collectors.toList());
  }

  private Class<?> findJavaLangClassLoader(Class<?> classLoader) {
    while (classLoader != java.lang.ClassLoader.class) {
      classLoader = classLoader.getSuperclass();
    }
    return classLoader;
  }
}
