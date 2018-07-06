package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.ClassPath;
import org.apache.commons.io.FileUtils;

public class JUnitLibraryResolver {

  public enum JUnitVersion {
    JUNIT3, JUNIT4
  }

  public final static EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {
    libraries.put(JUnitVersion.JUNIT3, listJUnitLibraries(Paths.get("lib/junit3")));
    libraries.put(JUnitVersion.JUNIT4, listJUnitLibraries(Paths.get("lib/junit4")));
  }

  private static List<ClassPath> listJUnitLibraries(final Path path) {
    return FileUtils.listFiles(path.toFile(), new String[] {"jar"}, false).stream()
        .map(File::toPath).map(ClassPath::new).collect(Collectors.toList());
  }
}
