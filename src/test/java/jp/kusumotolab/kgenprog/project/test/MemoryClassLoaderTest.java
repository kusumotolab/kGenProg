package jp.kusumotolab.kgenprog.project.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.ref.WeakReference;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author shinsuke
 * @see http://www.nminoru.jp/~nminoru/java/class_unloading.html
 */
public class MemoryClassLoaderTest {

	final String outdir = "example/example01/_bin/";
	final String targetClass = "jp.kusumotolab.BuggyCalculator";

	@Before
	public void before() {
	}

	@Test
	public void testDynamicClassLoading01() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// 動的ロード
		Class<?> clazz = loader.loadClass(targetClass);
		Object instance = clazz.newInstance();

		// きちんと存在するか？
		assertNotNull(instance);
		loader.close();
	}

	@SuppressWarnings("unused")
	@Test(expected = ClassNotFoundException.class)
	public void testDynamicClassLoading02() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// SystemLoaderで動的ロード，失敗するはず
		Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(targetClass);
		loader.close();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testClassUnloadingByGC01() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// まず動的ロード
		Class<?> clazz = loader.loadClass(targetClass);

		// 弱参照（アンロードの監視）の準備
		final WeakReference<?> targetClassWR = new WeakReference(clazz);

		// まずロードされていることを確認
		assertNotNull(targetClassWR.get());

		// ロード先への参照（インスタンス）を完全に削除
		loader.close();
		loader = null;
		clazz = null;

		// GCして
		System.gc();

		// アンロードされていることを確認
		assertNull(targetClassWR.get());
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testClassUnloadingByGC02() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// まず動的ロード
		Class<?> clazz = loader.loadClass(targetClass);

		// 弱参照（アンロードの監視）の準備
		WeakReference<?> targetClassWR = new WeakReference(clazz);

		// まずロードされていることを確認
		assertNotNull(targetClassWR.get());

		// ロード先への参照（インスタンス）を完全に削除
		loader.close();
		loader = null;
		clazz = null;

		// GCして
		System.gc();

		// アンロードされていることを確認
		assertNull(targetClassWR.get());

		// もう一度ロードすると
		loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });
		clazz = loader.loadClass(targetClass);
		targetClassWR = new WeakReference(clazz);

		// ロードされているはず
		assertNotNull(targetClassWR.get());
		loader.close();

	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testClassUnloadingByGC03() throws Exception {
		MemoryClassLoader loader = new MemoryClassLoader(new URL[] { new URL("file:./" + outdir) });

		// まず動的ロード
		Class<?> clazz = loader.loadClass(targetClass);

		// 弱参照（アンロードの監視）の準備
		final WeakReference<?> targetClassWR = new WeakReference(clazz);

		// まずロードされていることを確認
		assertNotNull(targetClassWR.get());

		// ロード先への参照（インスタンス）を削除せずに
		// loader.close();
		// loader = null;
		// clazz = null;

		// GCして
		System.gc();

		// アンロードされていないことを確認
		assertNotNull(targetClassWR.get());
		loader.close();
	}

}
