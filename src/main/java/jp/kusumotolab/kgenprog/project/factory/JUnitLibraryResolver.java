package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.CUILauncher;
import jp.kusumotolab.kgenprog.project.ClassPath;

public class JUnitLibraryResolver {

  private final static Logger log = LoggerFactory.getLogger(JUnitLibraryResolver.class);

  public enum JUnitVersion {
    JUNIT3, JUNIT4
  }

  public final static EnumMap<JUnitVersion, List<ClassPath>> libraries =
      new EnumMap<>(JUnitVersion.class);

  static {
    try {
      final ClassLoader classLoader = CUILauncher.class.getClassLoader();
      final URL junit3URL = classLoader.getResource("junit3");
      final URL junit4URL = classLoader.getResource("junit4");
      libraries.put(JUnitVersion.JUNIT3, listJUnitLibraries(Paths.get(junit3URL.toURI())));
      libraries.put(JUnitVersion.JUNIT4, listJUnitLibraries(Paths.get(junit4URL.toURI())));
    } catch (final URISyntaxException e) {
      log.error(e.getMessage());
      System.exit(1);
    }
  }

  private static List<ClassPath> listJUnitLibraries(final Path path) {
    return FileUtils.listFiles(path.toFile(), new String[] {"jar"}, false)
        .stream()
        .map(File::toPath)
        .map(ClassPath::new)
        .collect(Collectors.toList());
  }
}
