package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class ProjectBuilderTest {

  @Test
  public void testBuildStringForExample01() {
    final TargetProject targetProject = TargetProjectFactory.create("example/example01");
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Path outDirPath = Paths.get("example/example01/bin");

    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream().forEach(c -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(c);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream().forEach(f -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(f);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }
  }

  @Test
  public void testBuildStringForExample02() {
    final TargetProject targetProject = TargetProjectFactory.create("example/example02");
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Path outDirPath = Paths.get("example/example02/bin");

    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream().forEach(c -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(c);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream().forEach(f -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(f);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }
  }

  @Test
  public void testBuildStringForExample03() {
    final TargetProject targetProject = TargetProjectFactory.create("example/example03");
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Path outDirPath = Paths.get("example/example03/bin");

    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream().forEach(c -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(c);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream().forEach(f -> {
        final Path correspondingSourcePath = buildResults.getPathToSource(f);
        assertThat(correspondingSourcePath, is(sourceFile.path));
      });
    }
  }
}
