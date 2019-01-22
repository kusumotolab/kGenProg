package jp.kusumotolab.kgenprog.project.test;

import java.net.URL;

/**
 * MemoryClassLoaderの拡張．<br>
 * クラスローダの委譲関係をあえて崩すことで，KGP本体のクラスロード（AppClassLoader）の副作用を回避する．<br>
 * 委譲の流れは以下の通り．<br>
 * - SkippingMemoryClassLoader<br>
 * -> AppClassLoader (ここをスキップ)<br>
 * -> ExtensionClassLoader (ここにダイレクトに委譲)<br>
 * -> BootstrapClassLoader<br>
 * 
 * ただし例外として，JUnit関係のクラスのみ，そのロードをAppClassLoaderに委譲する．<br>
 * KGPのテスト実行時のJUnitクラス，及び題材のテスト実行時のJUnitクラスを同一のクラスローダでロードしないと，<br>
 * JUnitが期待通りに動作しないため．<br>
 * 
 * @author shinsuke
 *
 */
public class SkippingMemoryClassLoader extends MemoryClassLoader {

  final private ClassLoader extensionClassLoader;

  public SkippingMemoryClassLoader(final URL[] urls) {
    super(urls);
    extensionClassLoader = findExtClassLoader(getClass().getClassLoader());
  }

  /**
   * クラスローダの親子関係を探索して，委譲先のextensionClassLoaderを探す．
   * 
   * @param cl
   * @return
   */
  private ClassLoader findExtClassLoader(final ClassLoader cl) {
    if (null == cl) {
      throw new RuntimeException("Cannot find extension class loader.");
    }
    if (!cl.toString()
        .contains("$ExtClassLoader@")) {
      return findExtClassLoader(cl.getParent());
    }
    return cl;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

    if (name.startsWith("org.junit.Test") || name.startsWith("junit.framework")) {
      return getParent().loadClass(name);
    }

    synchronized (getClassLoadingLock(name)) {
      // First, check if the class has already been loaded
      Class<?> c = findLoadedClass(name);

      if (c == null) {
        try {
          // Second, try to load using extension class loader
          c = extensionClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
          // ignore
        }
      }
      if (c == null) {
        try {
          // Finally, try to load from memory
          c = findClass(name);
        } catch (Exception e) {
          // ignore
        }
      }
      if (c == null) {
        throw new ClassNotFoundException(name);
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    }
  }

}
