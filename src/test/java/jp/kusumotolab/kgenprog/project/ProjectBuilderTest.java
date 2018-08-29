package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Bar;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Baz;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazAnonymous;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazOuter;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazStaticInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Foo;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.Dir;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.DirTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.build.CompilationUnit;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.MemoryClassLoader;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ProjectBuilderTest {

  private final static Path WorkPath = Paths.get("tmp/work");

  @Before
  public void before() throws IOException {
    TestUtil.deleteWorkDirectory(WorkPath);
  }

  @Test
  public void testBuildStringForBuildFailure01() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults).isInstanceOf(EmptyBuildResults.class);
    assertThat(buildResults.isBuildFailed).isTrue();
  }

  @Test
  public void testBuildStringForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    assertThat(compilationPackage.getUnits()).extracting(unit -> unit.getName())
        .containsExactlyInAnyOrder(Foo.value, FooTest.value);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    assertThat(compilationPackage.getUnits()).extracting(unit -> unit.getName())
        .containsExactlyInAnyOrder(Foo.value, FooTest.value, Bar.value, BarTest.value);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  @Test
  public void testBuildStringForExample03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    assertThat(compilationPackage.getUnits()).extracting(unit -> unit.getName())
        .containsExactlyInAnyOrder(Foo.value, FooTest.value, Bar.value, BarTest.value, Baz.value,
            BazTest.value, BazInner.value, BazStaticInner.value, BazAnonymous.value,
            BazOuter.value);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  @Test
  public void testBuildStringForBuildSuccess07() {
    final Path rootPath = Paths.get("example/BuildSuccess07");
    final Path srcPath = rootPath.resolve(Dir);
    final Path testPath = rootPath.resolve(DirTest);
    final TargetProject targetProject =
        TargetProjectFactory.create(rootPath, Arrays.asList(srcPath), Arrays.asList(testPath),
            new ArrayList<Path>(), JUnitVersion.JUNIT4);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();


    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    assertThat(compilationPackage.getUnits()).extracting(unit -> unit.getName())
        .containsExactlyInAnyOrder(Foo.value, FooTest.value, Bar.value, BarTest.value);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<FullyQualifiedName> fqns = buildResults.getPathToFQNs(productSourcePath.path);
      assertThat(fqns).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {

    // example03のビルドが成功するかテスト
    final Path rootPath03 = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject03 = TargetProjectFactory.create(rootPath03);
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final Variant variant03 = targetProject03.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode03 = variant03.getGeneratedSourceCode();
    final BuildResults buildResults03 = projectBuilder03.build(generatedSourceCode03);

    assertThat(buildResults03.isBuildFailed).isFalse();
    assertThat(buildResults03.isMappingAvailable()).isTrue();

    // example02のビルドが成功するかテスト
    final Path rootPath02 = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject02 = TargetProjectFactory.create(rootPath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final Variant variant02 = targetProject02.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode02 = variant02.getGeneratedSourceCode();
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02);

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(buildResults02.isMappingAvailable()).isTrue();

    final CompilationPackage compilationPackage = buildResults02.getCompilationPackage();
    assertThat(compilationPackage.getUnits()).extracting(unit -> unit.getName())
        .containsExactlyInAnyOrder(Foo.value, FooTest.value, Bar.value, BarTest.value);
  }


  @Test
  public void testBuildForInMemoryByteCode01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    // buildResultsからバイトコードを取り出す
    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    final List<CompilationUnit> units = compilationPackage.getUnits();
    assertThat(units).hasSize(2);

    final CompilationUnit unit = compilationPackage.getUnits()
        .get(0);
    final MemoryClassLoader loader = new MemoryClassLoader();
    final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(unit.getName());
    loader.addDefinition(fqn, unit.getBytecode());

    // バイトコードが正しいのでうまくロードできるはず
    loader.loadClass(fqn);

    loader.close();

  }
}
