package jp.kusumotolab.kgenprog.project.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
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

  final static Path rootDir = Paths.get("example/example01");
  final static Path outDir = rootDir.resolve("bin");

  final static TargetFullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");
  final static TestFullyQualifiedName buggyCalculatorTest =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");

  static MemoryClassLoader loader;

  @BeforeClass
  public static void beforeClass() {
    try {
      FileUtils.deleteDirectory(outDir.toFile());
    } catch (IOException e) {
      // TODO #97
      // temporal patch
      // nothing todo
    }
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final GeneratedSourceCode generatedSourceCode = targetProject.getInitialVariant()
        .getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);
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
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // 動的ロード
    final Class<?> clazz = loader.loadClass(buggyCalculator);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance, is(notNullValue()));
    assertThat(instance.toString(), is(startsWith(buggyCalculator.toString())));
    assertThat(clazz.getName(), is(buggyCalculator.toString()));
  }

  @Test
  public void testDynamicClassLoading02() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // 動的ロード（Override側のメソッドで試す）
    final Class<?> clazz = loader.loadClass(buggyCalculator.toString(), false);
    final Object instance = clazz.newInstance();

    // きちんと存在するか？その名前は正しいか？
    assertThat(instance, is(notNullValue()));
    assertThat(instance.toString(), is(startsWith(buggyCalculator.toString())));
    assertThat(clazz.getName(), is(buggyCalculator.toString()));
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading03() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // SystemLoaderで動的ロード，失敗するはず (Exceptionを期待)
    ClassLoader.getSystemClassLoader()
        .loadClass(buggyCalculator.toString());
  }

  @Test(expected = ClassNotFoundException.class)
  public void testDynamicClassLoading04() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // リフレクションで動的ロード，失敗するはず (Exceptionを期待)
    // 処理自体は02と等価なはず
    Class.forName(buggyCalculator.toString());
  }

  @Test
  public void testDynamicClassLoading05() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // リフレクション + MemoryLoaderで動的ロード，これは成功するはず
    final Class<?> clazz = Class.forName(buggyCalculator.toString(), true, loader);

    assertThat(clazz.getName(), is(buggyCalculator.toString()));
  }

  @Test
  public void testClassUnloadingByGC01() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // まず動的ロード
    Class<?> clazz = loader.loadClass(buggyCalculator);

    // 弱参照（アンロードの監視）の準備
    final WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get(), is(notNullValue()));

    // ロード先への参照（インスタンス）を完全に削除
    loader.close();
    loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get(), is(nullValue()));
  }

  @Test
  public void testClassUnloadingByGC02() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // まず動的ロード
    final Class<?> clazz = loader.loadClass(buggyCalculator);

    // 弱参照（アンロードの監視）の準備
    final WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get(), is(notNullValue()));

    // ロード先への参照（インスタンス）を削除せずに
    // loader.close();
    // loader = null;
    // clazz = null;

    // GCして
    System.gc();

    // アンロードされていないことを確認
    assertThat(targetClassWR.get(), is(notNullValue()));
  }

  @Test
  public void testClassUnloadingByGC03() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // まず動的ロード
    Class<?> clazz = loader.loadClass(buggyCalculator);

    // 弱参照（アンロードの監視）の準備
    WeakReference<?> targetClassWR = new WeakReference<>(clazz);

    // まずロードされていることを確認
    assertThat(targetClassWR.get(), is(notNullValue()));

    // ロード先への参照（インスタンス）を完全に削除
    loader.close();
    loader = null;
    clazz = null;

    // GCして
    System.gc();

    // アンロードされていることを確認
    assertThat(targetClassWR.get(), is(nullValue()));

    // もう一度ロードすると
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });
    clazz = loader.loadClass(buggyCalculator);
    targetClassWR = new WeakReference<>(clazz);

    // ロードされているはず
    assertThat(targetClassWR.get(), is(notNullValue()));

  }

  @Test
  public void testJUnitWithMemoryLoader01() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // BuggyCalculatorTest（BCTest）をロードしておく
    final Class<?> clazz = loader.loadClass(buggyCalculatorTest);

    // テストを実行
    // * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
    // * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
    final JUnitCore junitCore = new JUnitCore();
    final Result result = junitCore.run(clazz);

    // きちんと実行できるはず
    assertThat(result.getRunCount(), is(4));
    assertThat(result.getFailureCount(), is(1));
  }

  @Test
  public void testJUnitWithMemoryLoader02() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // まず何もロードされていないはず
    assertThat(listLoadedClasses(loader), is(empty()));

    // テストだけをロード
    final Class<?> clazz = loader.loadClass(buggyCalculatorTest);

    // BCTestがロードされているはず
    assertThat(listLoadedClasses(loader), hasItems(buggyCalculatorTest.toString()));

    // テストを実行
    // * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
    // * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
    final JUnitCore junitCore = new JUnitCore();
    junitCore.run(clazz);

    // 上記テストの実行により，BCTestに加えBCもロードされているはず
    assertThat(listLoadedClasses(loader),
        hasItems(buggyCalculatorTest.toString(), buggyCalculator.toString()));
  }

  @Test
  public void testAddDefinition01() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // .classファイルを探す
    final Path buggyCalculatorClassFilePath = Paths.get(buggyCalculator.toString()
        .replace(".", "/") + ".class");
    final Path classFilePath = Files.walk(outDir)
        .filter(path -> path.endsWith(buggyCalculatorClassFilePath))
        .findFirst()
        .get();

    // .classファイルを直接読み込んでメモリに格納
    final byte[] byteCode = Files.readAllBytes(classFilePath);

    // addDefinitionで定義追加
    loader.addDefinition(buggyCalculator, byteCode);

    // クラスロードできるはず
    final Class<?> clazz = loader.loadClass(buggyCalculator);
    assertThat(clazz.getName(), is(buggyCalculator.toString()));
  }

  @Test(expected = ClassFormatError.class)
  public void testAddDefinition02() throws Exception {
    loader = new MemoryClassLoader(new URL[] { outDir.toUri()
        .toURL() });

    // 不正なバイトコードを生成
    final byte[] invalidByteCode = new byte[] { 0, 0, 0, 0, 0 };

    // addDefinitionで定義追加
    loader.addDefinition(buggyCalculator, invalidByteCode);

    // クラスロード（バグるはず）
    loader.loadClass(buggyCalculator);
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
