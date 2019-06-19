package jp.kusumotolab.kgenprog.project.test;

import java.net.URL;

/**
 * MemoryClassLoaderの拡張．<br>
 * クラスローダの委譲関係をあえて崩すことで，kgp本体のクラスロード（AppClassLoader）の副作用を回避する．<br>
 * これによりkgp本体に指定されたクラスパス情報に一切影響を受けない形，かつ同一javaプロセス内で対象プロジェクトのテストを実行可能とする．
 * 
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

  /**
   * コンストラクタ
   * 
   * @param urls クラスパス
   */
  public SkippingMemoryClassLoader(final URL[] urls) {
    super(urls);
    extensionClassLoader = findExtClassLoader(getClass().getClassLoader());
  }

  /**
   * クラスローダの親子関係を探索して，委譲先となるextensionClassLoaderを探す．
   * 
   * @param cl
   * @return
   */
  private ClassLoader findExtClassLoader(final ClassLoader cl) {
    if (null == cl) {
      throw new RuntimeException("Cannot find extension class loader.");
    }
    if (!cl.toString()
        .contains("$ExtClassLoader@")) { // clが取り出したいExtClassLoaderではない場合，
      return findExtClassLoader(cl.getParent()); // 再帰的に親を探す
    }
    return cl;
  }

  /**
   * クラスロードを行う．<br>
   * ロード対象がjunit関係のものであれば，直接親のAppClassLoaderからロードする．<br>
   * そうでない場合，AppClassLoaderをスキップしてExtClassLoaderからロードを試み，最後にメモリからロードを試す．<br>
   */
  @Override
  protected Class<?> loadClass(final String name, final boolean resolve)
      throws ClassNotFoundException {

    // JUnit関係のクラスのみロードを通常の委譲関係に任す．これがないとJUnitが期待通りに動かない．
    if (name.startsWith("org.junit.") || name.startsWith("junit.")) {
      return getParent().loadClass(name);
    }

    // 委譲処理．java.lang.ClassLoader#loadClassを参考に作成．
    synchronized (getClassLoadingLock(name)) {
      // First, check if the class has already been loaded
      Class<?> c = findLoadedClass(name);

      if (null == c) {
        try {
          // Second, try to load using extension class loader
          c = extensionClassLoader.loadClass(name);

          // Don't delegate to the parent.
          // c = parent.loadClass(name);

          // TODO 可視性の問題で，resolve変数が反映されていない．本来以下であるべき．リフレクションでなんとかなる．
          // c = extensionClassLoader.loadClass(name, resolve);
        } catch (final ClassNotFoundException e) {
          // ignore
        }
      }
      if (null == c) {
        try {
          // Finally, try to load from memory
          c = findClass(name);
        } catch (final ClassNotFoundException e) {
          // ignore
        }
      }
      if (null == c) {
        throw new ClassNotFoundException(name);
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    }
  }

}
