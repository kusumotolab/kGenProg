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

  private static final String JUnit3Dir = "junit3/";
  private static final String JUnit4Dir = "junit4/";
  private static final String JUnit3J = "junit-3.8.2.jar";
  private static final String JUnit4J = "junit-4.12.jar";
  private static final String JUnit4H = "hamcrest-core-1.3.jar";

  public final static EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {

    try {
      final ClassLoader classLoader = CUILauncher.class.getClassLoader();
      final InputStream junit3JInputStream = classLoader.getResourceAsStream(JUnit3Dir + JUnit3J);
      final InputStream junit4JInputStream = classLoader.getResourceAsStream(JUnit4Dir + JUnit4J);
      final InputStream junit4HInputStream = classLoader.getResourceAsStream(JUnit4Dir + JUnit4H);

      final Path junit3JPath = Paths.get(JUnit3J);
      final Path junit4JPath = Paths.get(JUnit4J);
      final Path junit4HPath = Paths.get(JUnit4H);

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
