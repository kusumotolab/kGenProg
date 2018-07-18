package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;

public class MavenProjectFactory extends BuildToolProjectFactory {

  private final static String CONFIG_FILE_NAME = "pom.xml";

  public MavenProjectFactory(final Path rootPath) {
    super(rootPath);
  }

  @Override
  public boolean isApplicable() {
    return getConfigPath().size() > 0;
  }

  @Override
  protected String getConfigFileName() {
    return CONFIG_FILE_NAME;
  }

  @Override
  public TargetProject create() {
    // TODO
    new RuntimeException("DON'T CALL ME. This method is not implemented").printStackTrace();
    return null;
  }

}
