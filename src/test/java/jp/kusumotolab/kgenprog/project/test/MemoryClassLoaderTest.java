package jp.kusumotolab.kgenprog.project.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

/**
 * 
 * @author shinsuke
 * @see http://www.nminoru.jp/~nminoru/java/class_unloading.html
 */
public class MemoryClassLoaderTest {

	final static String outdir = "example/example01/_bin/";
	final static String buggyCalculator = "jp.kusumotolab.BuggyCalculator";
	final static String buggyCalculatorTest = buggyCalculator + "Test";

	@BeforeClass
	public static void beforeClass() {
		final TargetProject targetProject = TargetProject.generate("example/example01");
		new ProjectBuilder(targetProject).build(outdir);
	}

	@Test
	public void testDynamicClassLoading01() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// 動的ロード
		Class<?> clazz = loader.loadClass(buggyCalculator);
		Object instance = clazz.newInstance();

		// きちんと存在するか？その名前は正しいか？
		assertThat(instance, is(notNullValue()));
		assertThat(instance.toString(), is(startsWith(buggyCalculator)));
		assertThat(clazz.getName(), is(buggyCalculator));

		loader.close();
	}

	@Test(expected = ClassNotFoundException.class)
	public void testDynamicClassLoading02() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// SystemLoaderで動的ロード，失敗するはず (Exceptionを期待)
		ClassLoader.getSystemClassLoader().loadClass(buggyCalculator);

		loader.close();
	}

	@Test(expected = ClassNotFoundException.class)
	public void testDynamicClassLoading03() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// リフレクションで動的ロード，失敗するはず (Exceptionを期待)
		// 処理自体は02と等価なはず
		Class.forName(buggyCalculator);

		loader.close();
	}

	@Test
	public void testDynamicClassLoading04() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// リフレクション + MemoryLoaderで動的ロード，これは成功するはず
		Class<?> clazz = Class.forName(buggyCalculator, true, loader);

		assertThat(clazz.getName(), is(buggyCalculator));
		loader.close();
	}

	@Test
	public void testClassUnloadingByGC01() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

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
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// まず動的ロード
		Class<?> clazz = loader.loadClass(buggyCalculator);

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
		loader.close();
	}

	@Test
	public void testClassUnloadingByGC03() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

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
		loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });
		clazz = loader.loadClass(buggyCalculator);
		targetClassWR = new WeakReference<>(clazz);

		// ロードされているはず
		assertThat(targetClassWR.get(), is(notNullValue()));
		loader.close();

	}

	@Test
	public void testJUnitWithMemoryLoader01() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// BuggyCalculatorTest（BCTest）をロードしておく
		Class<?> clazz = loader.loadClass(buggyCalculatorTest);

		// テストを実行
		// * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
		// * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
		final JUnitCore junitCore = new JUnitCore();
		final Result result = junitCore.run(clazz);

		// きちんと実行できるはず
		assertThat(result.getRunCount(), is(4));
		assertThat(result.getFailureCount(), is(1));

		loader.close();
	}

	@Test
	public void testJUnitWithMemoryLoader02() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// まず何もロードされていないはず
		assertThat(listLoadedClasses(loader), is(empty()));

		// テストだけをロード
		Class<?> clazz = loader.loadClass(buggyCalculatorTest);

		// BCTestがロードされているはず
		assertThat(listLoadedClasses(loader), hasItems(buggyCalculatorTest));

		// テストを実行
		// * ここでBCTestのClassLoaderには上記MemoryClassLoaderが紐づく（自身をロードしたローダーが指定される）
		// * BCTestはBCインスタンス化のためにMemoryClassLoaderを使って（暗黙的に）BCをロードする
		final JUnitCore junitCore = new JUnitCore();
		junitCore.run(clazz);

		// 上記テストの実行により，BCTestに加えBCもロードされているはず
		assertThat(listLoadedClasses(loader), hasItems(buggyCalculatorTest, buggyCalculator));

		loader.close();
	}

	/**
	 * 指定クラスローダによってロードされたクラス名一覧の取得
	 * 
	 * @param classLoader
	 * @return
	 */
	private List<String> listLoadedClasses(ClassLoader classLoader) {
		Class<?> clClass = classLoader.getClass();
		while (clClass != java.lang.ClassLoader.class) {
			clClass = clClass.getSuperclass();
		}
		try {
			java.lang.reflect.Field fldClasses = clClass.getDeclaredField("classes");
			fldClasses.setAccessible(true);

			@SuppressWarnings("unchecked")
			Vector<Class<?>> classes = (Vector<Class<?>>) fldClasses.get(classLoader);

			return classes.stream().map(s -> s.getName()).collect(Collectors.toList());
		} catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
