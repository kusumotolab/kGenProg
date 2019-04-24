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
 * メモリ上からバイナリを読み込む特殊クラスローダ．<br>
 * バイナリを格納する配列を抱えており，これを用いてメモリからのクラスロードを試みる．<br>
 * 
 * URLClassLoaderを継承しており，メモリロードを行う前に一般的なクラスパス指定の（すなわちファイルシステムからの）クラスロードを試みていることに注意．<br>
 * 
 * @see https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
 */
public class MemoryClassLoader extends URLClassLoader {


  public MemoryClassLoader() throws MalformedURLException {
    this(new URL[] {});
  }

  public MemoryClassLoader(final URL[] urls) {
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
   * メモリ上からクラスを探す． <br>
   * まずURLClassLoaderによるファイルシステム上のクラスのロードを試み，それがなければメモリ上のクラスロードを試す．
   * 
   */
  @Override
  protected Class<?> findClass(final String name) throws ClassNotFoundException {
    Class<?> c = null;

    // try to load from classpath
    try {
      c = super.findClass(name);
    } catch (final ClassNotFoundException e1) {
      // ignore
    }

    // if fails, try to load from memory
    if (null == c) {
      final byte[] bytes = definitions.get(name);
      if (bytes != null) {
        try {
          c = defineClass(name, bytes, 0, bytes.length);
        } catch (final ClassFormatError e) {
          throw e;
        }
      }
    }

    // otherwise, class not found
    if (null == c) {
      throw new ClassNotFoundException(name);
    }
    return c;
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
