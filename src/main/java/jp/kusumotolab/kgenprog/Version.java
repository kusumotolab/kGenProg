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
        return loadVersionInfo(is);
      }

      // load properties file as just a file located on the current dir.
      String id = loadVersionInfo(new FileInputStream("./gradle.properties"));
      id += "+"; // mark as "under development"
      return id;

    } catch (IOException e) {
      return "unresolved";
    }
  }

  /**
   * Load version info from the given inputstream
   * @param is
   * @return
   * @throws IOException
   */
  private String loadVersionInfo(final InputStream is) throws IOException {
    final Properties properties = new Properties();
    properties.load(is);
    return properties.getProperty("currentVersion");
  }

  /**
   * Determine whether the runtime environment is jar or not.
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

