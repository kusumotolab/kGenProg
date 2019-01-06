package jp.kusumotolab.kgenprog.project.factory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
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
  private static final String JUNIT4_HAMCREST = "hamcrest-core-1.3.jar";
  private static final String SYSTEM_TEMP_DIR = System.getProperty("java.io.tmpdir");

  public final static EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {

    try {
      final ClassLoader classLoader = CUILauncher.class.getClassLoader();
      final InputStream junit3JInputStream =
          classLoader.getResourceAsStream(JUNIT3_DIR + JUNIT3_JUNIT);
      final InputStream junit4JInputStream =
          classLoader.getResourceAsStream(JUNIT4_DIR + JUNIT4_JUNIT);
      final InputStream junit4HInputStream =
          classLoader.getResourceAsStream(JUNIT4_DIR + JUNIT4_HAMCREST);

      final Path systemTempPath = Paths.get(SYSTEM_TEMP_DIR);
      final Path junit3JPath = systemTempPath.resolve(JUNIT3_JUNIT);
      final Path junit4JPath = systemTempPath.resolve(JUNIT4_JUNIT);
      final Path junit4HPath = systemTempPath.resolve(JUNIT4_HAMCREST);

      Files.copy(junit3JInputStream, junit3JPath, StandardCopyOption.REPLACE_EXISTING);
      Files.copy(junit4JInputStream, junit4JPath, StandardCopyOption.REPLACE_EXISTING);
      Files.copy(junit4HInputStream, junit4HPath, StandardCopyOption.REPLACE_EXISTING);

      junit3JPath.toFile()
          .deleteOnExit();
      junit4JPath.toFile()
          .deleteOnExit();
      junit4HPath.toFile()
          .deleteOnExit();

      libraries.put(JUnitVersion.JUNIT3, Arrays.asList(new ClassPath(junit3JPath)));
      libraries.put(JUnitVersion.JUNIT4,
          Arrays.asList(new ClassPath(junit4JPath), new ClassPath(junit4HPath)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
