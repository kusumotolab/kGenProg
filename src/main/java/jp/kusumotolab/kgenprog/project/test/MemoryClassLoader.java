package jp.kusumotolab.kgenprog.project.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * A class loader that loads classes from in-memory data.
 * 
 * @see https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
 */
public class MemoryClassLoader extends URLClassLoader {

	public MemoryClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * クラス定義を表すMap．
	 * クラス名とバイト配列のペアを持つ．
	 */
	private final Map<String, byte[]> definitions = new HashMap<>();

	/**
	 * メモリ上のバイト配列をクラス定義に追加する．
	 * 
	 * @param name 定義するクラス名
	 * @param bytes 追加するクラス定義
	 */
	public void addDefinition(final String name, final byte[] bytes) {
		definitions.put(name, bytes);
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	/**
	 * クラスロード．
	 * メモリ上のバイト配列のクラス定義を優先で探し，それがなければファイルシステム上の.classファイルからロードを行う．
	 */
	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final byte[] bytes = definitions.get(name);
		if (bytes != null) {
			return defineClass(name, bytes, 0, bytes.length);
		}
		return super.loadClass(name, resolve);
	}

}