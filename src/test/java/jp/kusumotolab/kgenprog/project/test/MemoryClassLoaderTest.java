package jp.kusumotolab.kgenprog.project.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import jp.kusumotolab.kgenprog.project.BuildResults;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.build.CompilationUnit;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

/**
 * 
 * @author shinsuke
 * @see http://www.nminoru.jp/~nminoru/java/class_unloading.html
 */
public class MemoryClassLoaderTest {

  final static Path RootPath = Paths.get("example/BuildSuccess01");
  static BuildResults buildResults;

  final static FullyQualifiedName sourceFqn = ExampleAlias.Fqn.Foo;
  final static FullyQualifiedName testFqn = ExampleAlias.Fqn.FooTest;

  static MemoryClassLoader Loader;

  @BeforeClass
  public static void beforeClass() {
    // 一度だけビルドしておく
    final TargetProject targetProject = TargetProjectFactory.create(RootPath);
    final GeneratedSourceCode generatedSourceCode = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    buildResults = projectBuilder.build(generatedSourceCode);
  }

  @Before
  public void before() throws MalformedURLException {
    setupMemoryClassLoader();
  }

  private void setupMemoryClassLoader() throws MalformedURLException {
    Loader = new MemoryClassLoader();
    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    final List<CompilationUnit> units = compilationPackage.getUnits();
    units.forEach(unit -> Loader.addDefinition(new TargetFullyQualifiedName(unit.getName()),
        unit.getBytecode()));
  }

  @After
  public void after() throws IOException {
    if (Loader != null) {
      Loader.close();
    }
  }

  @Test
  public void testDynamicClassLoading01() throws Exception {
    // 動的ロード
    final Class<?> clazz = Loader.loadClass(sourceFqn);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance).isNotNull();
    assertThat(instance.toString()).startsWith(sourceFqn.toString());
    assertThat(clazz.getName()).isEqualTo(sourceFqn.toString());
  }

  @Test
  public void testDynamicClassLoading02() throws Exception {
    // 動的ロード（Override側のメソッドで試す）
    final Class<?> clazz = Loader.loadClass(sourceFqn.toString(), false);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance).isNotNull();
    assertThat(instance.toString()).startsWith(sourceFqn.toString());
    assertThat(clazz.getName()).isEqualTo(sourceFqn.toString());
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading03() throws Exception {
    // SystemLoaderで動的ロード，失敗するはず (Exceptionを期待)
    ClassLoader.getSystemClassLoader()
        .loadClass(sourceFqn.toString());
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading04() throws Exception {
    // リフレクションで動的ロード，失敗するはず (Exceptionを期待)
    // 処理自体は02と等価なはず
    Class.forName(sourceFqn.toString());
  }

  @Test
  public void testDynamicClassLoading05() throws Exception {
    // リフレクション + MemoryLoaderで動的ロード，これは成功するはず
    final Class<?> clazz = Class.forName(sourceFqn.toString(), true, Loader);

    assertThat(clazz.getName()).isEqualTo(sourceFqn.toString());
  }

  @Test
  public void testClassUnloadingByGC01() throws Exception {
    // まず動的ロード
    Class<?> clazz = Loader.loadClass(sourceFqn);

    // 弱参照（アンロードの監視）の準備
    final WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get()).isNotNull();

    // ロード先への参照（インスタンス）を完全に削除
    Loader.close();
    Loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get()).isNull();
  }

  @Test
  public void testClassUnloadingByGC02() throws Exception {
    // まず動的ロード
    final Class<?> clazz = Loader.loadClass(sourceFqn);

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
    // まず動的ロード
    Class<?> clazz = Loader.loadClass(sourceFqn);

    // 弱参照（アンロードの監視）の準備
    WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get()).isNotNull();

    // ロード先への参照（インスタンス）を完全に削除
    Loader.close();
    Loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get()).isNull();

    // もう一度ロードすると
    setupMemoryClassLoader();

    clazz = Loader.loadClass(sourceFqn);
    targetClassWR = new WeakReference<>(clazz);

    // ロードされているはず
    assertThat(targetClassWR.get()).isNotNull();
  }

  @Test
  public void testJUnitWithMemoryLoader01() throws Exception {
    // CloseToZeroTestをロードしておく
    final Class<?> clazz = Loader.loadClass(testFqn);

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
    // まず何もロードされていないはず
    assertThat(listLoadedClasses(Loader)).isEmpty();

    // テストだけをロード
    final Class<?> clazz = Loader.loadClass(testFqn);

    // BCTestがロードされているはず
    assertThat(listLoadedClasses(Loader)).contains(testFqn.toString());

    // テストを実行
    // * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
    // * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
    final JUnitCore junitCore = new JUnitCore();
    junitCore.run(clazz);

    // 上記テストの実行により，BCTestに加えBCもロードされているはず
    assertThat(listLoadedClasses(Loader)).contains(testFqn.toString(), sourceFqn.toString());
  }

  @Test(expected = ClassFormatError.class)
  public void testAddDefinition02() throws Exception {
    // 不正なバイトコードを生成
    final byte[] invalidByteCode = new byte[] {0, 0, 0, 0, 0};

    // addDefinitionで定義追加
    Loader.addDefinition(sourceFqn, invalidByteCode);

    // クラスロード（バグるはず）
    Loader.loadClass(sourceFqn);
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
