package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class ProjectBuilderTest {

  @Test
  public void testBuildStringForExample01() {
    final TargetProject targetProject = TargetProjectFactory.create("example/example01");
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Path outDirPath = Paths.get("example/example01/bin");

    final GeneratedSourceCode generatedSourceCode = targetProject.getInitialVariant()
        .getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream()
          .forEach(c -> {
            final Path correspondingSourcePath = buildResults.getPathToSource(c);
            assertThat(correspondingSourcePath, is(sourceFile.path));
          });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream()
          .forEach(f -> {
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

    final GeneratedSourceCode generatedSourceCode = targetProject.getInitialVariant()
        .getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream()
          .forEach(c -> {
            final Path correspondingSourcePath = buildResults.getPathToSource(c);
            assertThat(correspondingSourcePath, is(sourceFile.path));
          });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream()
          .forEach(f -> {
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

    final GeneratedSourceCode generatedSourceCode = targetProject.getInitialVariant()
        .getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed, is(false));
    assertThat(buildResults.isMappingAvailable(), is(true));

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<Path> pathToClasses = buildResults.getPathToClasses(sourceFile.path);
      pathToClasses.stream()
          .forEach(c -> {
            final Path correspondingSourcePath = buildResults.getPathToSource(c);
            assertThat(correspondingSourcePath, is(sourceFile.path));
          });
    }

    for (final SourceFile sourceFile : targetProject.getSourceFiles()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourceFile.path);
      fqns.stream()
          .forEach(f -> {
            final Path correspondingSourcePath = buildResults.getPathToSource(f);
            assertThat(correspondingSourcePath, is(sourceFile.path));
          });
    }
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {

    final Path workingDir = Paths.get("example/example03/bin");

    // example03のビルドが成功するかテスト
    final TargetProject targetProject03 = TargetProjectFactory.create("example/example03");
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final BuildResults buildResults03 = projectBuilder03.build(targetProject03.getInitialVariant()
        .getGeneratedSourceCode(), workingDir);
    assertThat(buildResults03.isBuildFailed, is(false));
    assertThat(buildResults03.isMappingAvailable(), is(true));

    // example02のビルドが成功するかテスト
    final TargetProject targetProject02 = TargetProjectFactory.create("example/example02");
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final BuildResults buildResults02 = projectBuilder02.build(targetProject02.getInitialVariant()
        .getGeneratedSourceCode(), workingDir);
    assertThat(buildResults02.isBuildFailed, is(false));
    assertThat(buildResults02.isMappingAvailable(), is(true));

    final Collection<File> classFiles =
        FileUtils.listFiles(workingDir.toFile(), new String[] {"class"}, true);
    assertThat(classFiles, is(containsInAnyOrder( //
        new File(workingDir + "/jp/kusumotolab/BuggyCalculator.class"), //
        new File(workingDir + "/jp/kusumotolab/BuggyCalculatorTest.class"), //
        new File(workingDir + "/jp/kusumotolab/Util.class"), //
        new File(workingDir + "/jp/kusumotolab/UtilTest.class"))));
  }
}
