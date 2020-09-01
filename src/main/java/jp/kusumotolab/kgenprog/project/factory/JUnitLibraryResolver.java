package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.MissingResourceException;
import jp.kusumotolab.kgenprog.CUILauncher;
import jp.kusumotolab.kgenprog.project.ClassPath;

public class JUnitLibraryResolver {

  public enum JUnitVersion {
    JUNIT3, JUNIT4
  }

  private static final String JUNIT3_DIR = "junit3/";
  private static final String JUNIT4_DIR = "junit4/";
  private static final String JUNIT3_JUNIT = "junit-3.8.2.jar";
  private static final String JUNIT4_JUNIT = "junit-4.12-kgp-custom.jar";

  protected static final EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {
    setupResourceJar(JUNIT3_DIR, JUNIT3_JUNIT, JUnitVersion.JUNIT3);
    setupResourceJar(JUNIT4_DIR, JUNIT4_JUNIT, JUnitVersion.JUNIT4);
  }

  private static void setupResourceJar(final String dir, final String jar,
      final JUnitVersion version) {
    final String jarPath = dir + jar;
    final ClassLoader classLoader = CUILauncher.class.getClassLoader();
    final InputStream is = classLoader.getResourceAsStream(jarPath);
    if (null == is) {
      throw new MissingResourceException("Missing runtime junit library: " + jarPath, jarPath, "");
    }

    final Path tempPath = getTempDirectory().resolve(jar);
    try {
      Files.copy(is, tempPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    libraries.put(version, Collections.singletonList(new ClassPath(tempPath)));
  }

  // TODO 一時dirの責務をひとまずこのクラスに任せたが，巨大になるなら別クラスに切った方がよさそう．
  private static Path tempDir;

  public static Path getTempDirectory() {
    try {
      if (null == tempDir) {
        tempDir = Files.createTempDirectory("kgp-");
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    return tempDir;
  }
}
