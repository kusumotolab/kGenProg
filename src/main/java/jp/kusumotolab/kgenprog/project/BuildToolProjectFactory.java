package jp.kusumotolab.kgenprog.project;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public abstract class BuildToolProjectFactory implements IProjectFactory {

  protected final Path rootPath;

  public BuildToolProjectFactory(final Path rootPath) {
    this.rootPath = rootPath;
  }

  protected Collection<File> getConfigFile() {
    return FileUtils.listFiles(rootPath.toFile(),
        FileFilterUtils.nameFileFilter(getConfigFileName()), null);
  }

  abstract protected String getConfigFileName();
}
