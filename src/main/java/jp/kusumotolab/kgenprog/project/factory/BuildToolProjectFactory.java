package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BuildToolProjectFactory implements ProjectFactory {

  protected final Path rootPath;

  public BuildToolProjectFactory(final Path rootPath) {
    this.rootPath = rootPath;
  }

  protected final Collection<Path> getConfigPath() {
    try (final Stream<Path> stream = Files.walk(rootPath, 1)) { // max depth should be 1
      return stream.filter(p -> p.endsWith(getConfigFileName()))
          .collect(Collectors.toList());
    } catch (final IOException e) {
      // do nothing
    }
    return Collections.emptyList();
  }

  @Override
  public boolean isApplicable() {
    return !getConfigPath().isEmpty();
  }

  protected abstract String getConfigFileName();
}
