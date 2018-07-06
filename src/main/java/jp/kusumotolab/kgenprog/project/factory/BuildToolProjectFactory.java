package jp.kusumotolab.kgenprog.project.factory;

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

  final protected Collection<File> getConfigFile() {
    return FileUtils.listFiles(rootPath.toFile(),
        FileFilterUtils.nameFileFilter(getConfigFileName()), null);
  }

  abstract protected String getConfigFileName();
}
