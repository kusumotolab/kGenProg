package jp.kusumotolab.kgenprog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class Version {

  public final String id;
  public final static Version instance = new Version();

  private Version() {
    id = resolveVersionInfo();
  }

  private String resolveVersionInfo() {
    try {

      // load properties file as resource when executed on jar
      if (isRunningFromJar()) {
        final ClassLoader cl = getClass().getClassLoader();
        final InputStream is = cl.getResourceAsStream("gradle.properties");
        final Properties properties = getProperty(is);
        String id = properties.getProperty("currentVersion");
        if (properties.containsKey("ci") && properties.getProperty("ci")
            .equals("true")) {
          return id;
        }
        return id += "+"; // mark as "under development"
      }

      // load properties file as just a file located on the current dir.
      final InputStream is = new FileInputStream("./gradle.properties");
      final Properties properties = getProperty(is);
      String id = properties.getProperty("currentVersion");
      return id += "+"; // mark as "under development"

    } catch (final IOException | NullPointerException e) {
      return "unresolved";
    }
  }

  private Properties getProperty(final InputStream is) throws IOException {
    final Properties properties = new Properties();
    properties.load(is);
    return properties;
  }

  /**
   * Determine whether the runtime environment is jar or not.
   *
   * @return
   */
  private boolean isRunningFromJar() {
    final String protocol = this.getClass()
        .getResource("")
        .getProtocol();
    if (Objects.equals(protocol, "jar")) {
      return true;
    }
    return false;
  }

}

