package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class ProjectBuilderTest {

  @Test
  public void testBuildStringForExample01() {
    final Path rootPath = Paths.get("example/example01");
    final Path outDirPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(sourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample02() {
    final Path rootPath = Paths.get("example/example02");
    final Path outDirPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(sourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample03() {
    final Path rootPath = Paths.get("example/example03");
    final Path outDirPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, outDirPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(sourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }

    for (final SourcePath sourcePath : targetProject.getSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(sourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(sourcePath.path);
    }
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {
    final Path basePath03 = Paths.get("example/example03");
    final Path basePath02 = Paths.get("example/example02");

    final Path workingDir = basePath03.resolve("bin");

    // example03のビルドが成功するかテスト
    final TargetProject targetProject03 = TargetProjectFactory.create(basePath03);
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final Variant variant03 = targetProject03.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode03 = variant03.getGeneratedSourceCode();
    final BuildResults buildResults03 = projectBuilder03.build(generatedSourceCode03, workingDir);

    assertThat(buildResults03.isBuildFailed).isFalse();
    assertThat(buildResults03.isMappingAvailable()).isTrue();

    // example02のビルドが成功するかテスト
    final TargetProject targetProject02 = TargetProjectFactory.create(basePath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final Variant variant02 = targetProject02.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode02 = variant02.getGeneratedSourceCode();
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02, workingDir);

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(buildResults02.isMappingAvailable()).isTrue();

    final Collection<File> classFiles =
        FileUtils.listFiles(workingDir.toFile(), new String[] {"class"}, true);
    final Path e1 = workingDir.resolve("jp/kusumotolab/BuggyCalculator.class");
    final Path e2 = workingDir.resolve("jp/kusumotolab/BuggyCalculatorTest.class");
    final Path e3 = workingDir.resolve("jp/kusumotolab/Util.class");
    final Path e4 = workingDir.resolve("jp/kusumotolab/UtilTest.class");

    assertThat(classFiles).extracting(File::toPath)
        .containsExactlyInAnyOrder(e1, e2, e3, e4);
  }
}
