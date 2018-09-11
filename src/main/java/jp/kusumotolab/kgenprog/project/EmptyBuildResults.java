package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class EmptyBuildResults extends BuildResults {

  private Logger log = LoggerFactory.getLogger(Ochiai.class);

  public static final EmptyBuildResults instance = new EmptyBuildResults();

  private EmptyBuildResults() {
    super(null, true, null, null, null);
  }

  @Override
  public Set<FullyQualifiedName> getPathToFQNs(final Path pathToSource) {
    return Collections.emptySet();
  }

  @Override
  public void addMapping(final Path source, final FullyQualifiedName fqn) {
    // do nothing
  }

  @Override
  public Path getPathToSource(final Path pathToClass) {
    log.error("getPathToSource(Path) is unavailable in EmptyBuildResults");
    return null;
  }

  @Override
  public Path getPathToSource(final FullyQualifiedName fqn) {
    log.error("getPathToSource(FullyQualifiedName) is unavailable in EmptyBuildResults");
    return super.getPathToSource(fqn);
  }

  @Override
  public void setMappingAvailable(boolean available) {
    // do nothing
  }

  @Override
  public boolean isMappingAvailable() {
    return false;
  }
}
