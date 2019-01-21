package jp.kusumotolab.kgenprog.project.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * A class loader that loads classes from in-memory data.
 * 
 * @see https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
 */
public class MemoryClassLoader extends URLClassLoader {

  public MemoryClassLoader() throws MalformedURLException {
    this(new URL[] {});
  }

  public MemoryClassLoader(URL[] urls) {
    super(urls);
  }

  /**
   * クラス定義を表すMap． クラス名とバイト配列のペアを持つ．
   */
  private final Map<String, byte[]> definitions = new HashMap<>();

  /**
   * メモリ上のバイト配列をクラス定義に追加する．
   * 
   * @param name 定義するクラス名
   * @param bytes 追加するクラス定義
   */
  public void addDefinition(final FullyQualifiedName fqn, final byte[] bytes) {
    definitions.put(fqn.value, bytes);
  }

  public Class<?> loadClass(final FullyQualifiedName fqn) throws ClassNotFoundException {
    return loadClass(fqn.value);
  }

  /**
   * クラスロード． メモリ上のバイト配列のクラス定義を優先で探し，それがなければファイルシステム上の.classファイルからロードを行う．
   */
  @Override
  public Class<?> loadClass(final String name, final boolean resolve)
      throws ClassNotFoundException {

    // まず既にロードされているクラスを探す
    Class<?> clazz = findLoadedClass(name);

    if (clazz == null) {
      // 無理ならメモリからロードを試みる
      final byte[] bytes = definitions.get(name);
      if (bytes != null) {
        try {
          clazz = defineClass(name, bytes, 0, bytes.length);
        } catch (final LinkageError e) {
          // おそらくバイナリ不正
          throw e;
        }
      }

      if (clazz == null) {
        // 無理なら親にまかす
        clazz = super.loadClass(name, resolve);
      }
    }

    if (resolve) {
      resolveClass(clazz);
    }
    return clazz;
  }

  @Override
  public InputStream getResourceAsStream(final String name) {
    final String fqn = convertStringNameToFqn(name);
    final byte[] bytes = definitions.get(fqn);
    if (null == bytes) {
      return super.getResourceAsStream(name);
    }
    return new ByteArrayInputStream(bytes);
  }

  private String convertStringNameToFqn(final String name) {
    return name.replaceAll("\\.class$", "")
        .replaceAll("\\/", ".");
  }
}
