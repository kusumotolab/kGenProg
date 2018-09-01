package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import jp.kusumotolab.kgenprog.CUILauncher;
import jp.kusumotolab.kgenprog.project.ClassPath;

public class JUnitLibraryResolver {

  public enum JUnitVersion {
    JUNIT3, JUNIT4
  }

  public final static EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {

    try {
      final ClassLoader classLoader = CUILauncher.class.getClassLoader();
      final InputStream junit3JUnitIS = classLoader.getResourceAsStream("junit3/junit-3.8.2.jar");
      final InputStream junit4JUnitIS = classLoader.getResourceAsStream("junit4/junit-4.12.jar");
      final InputStream junit4HamcrestIS =
          classLoader.getResourceAsStream("junit4/hamcrest-core-1.3.jar");

      File junit3JUnit = new File("junit-3.8.2.jar");
      File junit4JUnit = new File("junit-4.12.jar");
      File junit4Hamcrest = new File("hamcrest-core-1.3.jar");

      Files.copy(junit3JUnitIS, junit3JUnit.toPath());
      Files.copy(junit4JUnitIS, junit4JUnit.toPath());
      Files.copy(junit4HamcrestIS, junit4Hamcrest.toPath());

      junit3JUnit.deleteOnExit();
      junit4JUnit.deleteOnExit();
      junit4Hamcrest.deleteOnExit();

      libraries.put(JUnitVersion.JUNIT3, Arrays.asList(new ClassPath(junit3JUnit.toPath())));
      libraries.put(JUnitVersion.JUNIT4, Arrays.asList(new ClassPath(junit4JUnit.toPath()),
          new ClassPath(junit4Hamcrest.toPath())));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
