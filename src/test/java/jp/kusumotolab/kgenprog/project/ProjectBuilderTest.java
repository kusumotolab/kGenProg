package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.Bar;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BarTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.Baz;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BazAnonymous;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BazInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BazOuter;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BazStaticInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.BazTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.Foo;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Bin.FooTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.build.CompilationPackage;
import jp.kusumotolab.kgenprog.project.build.CompilationUnit;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.MemoryClassLoader;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ProjectBuilderTest {

  private final static Path WorkPath = Paths.get("tmp/work");
  private final static String[] BinExtension = new String[] {"class"};

  @Before
  public void before() throws IOException {
    TestUtil.deleteWorkDirectory(WorkPath);
  }

  @Test
  public void testBuildStringForExample00() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, WorkPath);

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
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, WorkPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(WorkPath.toFile(), BinExtension, true);
    final Path foo = WorkPath.resolve(Foo);
    final Path fooTest = WorkPath.resolve(FooTest);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

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
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, WorkPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(WorkPath.toFile(), BinExtension, true);
    final Path foo = WorkPath.resolve(Foo);
    final Path fooTest = WorkPath.resolve(FooTest);
    final Path bar = WorkPath.resolve(Bar);
    final Path barTest = WorkPath.resolve(BarTest);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest, bar, barTest);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

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
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, WorkPath);

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(buildResults.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(WorkPath.toFile(), BinExtension, true);
    final Path foo = WorkPath.resolve(Foo);
    final Path fooTest = WorkPath.resolve(FooTest);
    final Path bar = WorkPath.resolve(Bar);
    final Path barTest = WorkPath.resolve(BarTest);
    final Path baz = WorkPath.resolve(Baz);
    final Path bazTest = WorkPath.resolve(BazTest);
    final Path inner = WorkPath.resolve(BazInner);
    final Path staticInner = WorkPath.resolve(BazStaticInner);
    final Path anonymous = WorkPath.resolve(BazAnonymous);
    final Path outer = WorkPath.resolve(BazOuter);
    assertThat(classFiles).extracting(c -> c.toPath())
        .containsExactlyInAnyOrder(foo, fooTest, bar, barTest, baz, bazTest, inner, staticInner,
            anonymous, outer);

    for (final ProductSourcePath productSourcePath : targetProject.getProductSourcePaths()) {
      final Set<Path> paths = buildResults.getPathToClasses(productSourcePath.path);
      assertThat(paths).extracting(f -> buildResults.getPathToSource(f))
          .containsOnly(productSourcePath.path);
    }

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
    final BuildResults buildResults03 = projectBuilder03.build(generatedSourceCode03, WorkPath);

    assertThat(buildResults03.isBuildFailed).isFalse();
    assertThat(buildResults03.isMappingAvailable()).isTrue();

    // example02のビルドが成功するかテスト
    final Path rootPath02 = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject02 = TargetProjectFactory.create(rootPath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final Variant variant02 = targetProject02.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode02 = variant02.getGeneratedSourceCode();
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02, WorkPath);

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(buildResults02.isMappingAvailable()).isTrue();

    final Collection<File> classFiles = FileUtils.listFiles(WorkPath.toFile(), BinExtension, true);
    final Path e1 = WorkPath.resolve(Foo);
    final Path e2 = WorkPath.resolve(FooTest);
    final Path e3 = WorkPath.resolve(Bar);
    final Path e4 = WorkPath.resolve(BarTest);

    assertThat(classFiles).extracting(File::toPath)
        .containsExactlyInAnyOrder(e1, e2, e3, e4);
  }


  @Test
  public void testBuildForInMemoryByteCode01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode, WorkPath);

    // buildResultsからバイトコードを取り出す
    final CompilationPackage compilationPackage = buildResults.getCompilationPackage();
    final List<CompilationUnit> units = compilationPackage.getUnits();
    assertThat(units).hasSize(1);

    final CompilationUnit unit = compilationPackage.getUnits()
        .get(0);
    final MemoryClassLoader loader = new MemoryClassLoader(Paths.get(""));
    final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(unit.getName());
    loader.addDefinition(fqn, unit.getBytecode());

    // バイトコードが正しいのでうまくロードできるはず
    loader.loadClass(fqn);

    loader.close();

  }
}
