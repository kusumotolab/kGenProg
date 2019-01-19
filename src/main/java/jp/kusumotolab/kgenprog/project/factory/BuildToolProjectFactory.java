package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class BuildToolProjectFactory implements ProjectFactory {

  protected final Path rootPath;

  public BuildToolProjectFactory(final Path rootPath) {
    this.rootPath = rootPath;
  }

  final protected Collection<Path> getConfigPath() {
    try {
      return Files.walk(rootPath)
          .filter(p -> p.toString()
              .endsWith(getConfigFileName()))
          .collect(Collectors.toList());
    } catch (IOException e) {
      ; // do nothing
    }
    return Collections.emptyList();
  }

  abstract protected String getConfigFileName();
}
