package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_ANONYMOUS;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_INNER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_OUTER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_STATIC_INNER;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.tools.JavaFileObject.Kind;
import org.junit.Ignore;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.build.BinaryStore;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.EmptyBuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.MemoryClassLoader;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class ProjectBuilderTest {

  @Test
  public void testBuildStringForBuildFailure01() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults).isInstanceOf(EmptyBuildResults.class);
    assertThat(buildResults.isBuildFailed).isTrue();
  }

  @Test
  public void testBuildStringForExample01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    final BinaryStore binaryStore = buildResults.getBinaryStore();

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value, FOO_TEST.value);
  }

  @Test
  public void testBuildStringForExample02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value, FOO_TEST.value, BAR.value, BAR_TEST.value);
  }

  @Test
  public void testBuildStringForExample03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    final BinaryStore binaryStore = buildResults.getBinaryStore();

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value, FOO_TEST.value, BAR.value, BAR_TEST.value, BAZ.value,
            BAZ_TEST.value, BAZ_INNER.value, BAZ_STATIC_INNER.value, BAZ_ANONYMOUS.value,
            BAZ_OUTER.value);
  }

  @Test
  public void testBuildStringForBuildSuccess07() {
    final Path rootPath = Paths.get("example/BuildSuccess07");
    final List<Path> srcPaths = Arrays.asList(rootPath.resolve("src"));
    final List<Path> testPaths = Arrays.asList(rootPath.resolve("test"));
    final TargetProject targetProject = TargetProjectFactory.create(rootPath, srcPaths, testPaths,
        Collections.emptyList(), JUnitVersion.JUNIT4);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);


    final BinaryStore binaryStore = buildResults.getBinaryStore();

    assertThat(buildResults.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value, FOO_TEST.value, BAR.value, BAR_TEST.value);
  }

  // TODO: https://github.com/kusumotolab/kGenProg/pull/154
  // @Test
  public void testRemovingOldClassFiles() throws Exception {

    // example03のビルドが成功するかテスト
    final Path rootPath03 = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject03 = TargetProjectFactory.create(rootPath03);
    final ProjectBuilder projectBuilder03 = new ProjectBuilder(targetProject03);
    final GeneratedSourceCode generatedSourceCode03 =
        TestUtil.createGeneratedSourceCode(targetProject03);
    final BuildResults buildResults03 = projectBuilder03.build(generatedSourceCode03);

    assertThat(buildResults03.isBuildFailed).isFalse();

    // example02のビルドが成功するかテスト
    final Path rootPath02 = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject02 = TargetProjectFactory.create(rootPath02);
    final ProjectBuilder projectBuilder02 = new ProjectBuilder(targetProject02);
    final GeneratedSourceCode generatedSourceCode02 =
        TestUtil.createGeneratedSourceCode(targetProject02);
    final BuildResults buildResults02 = projectBuilder02.build(generatedSourceCode02);

    final BinaryStore binaryStore = buildResults02.getBinaryStore();

    assertThat(buildResults02.isBuildFailed).isFalse();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value, FOO_TEST.value, BAR.value, BAR_TEST.value);
  }

  @Test
  public void testBuildForInMemoryByteCode01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    // buildResultsからバイトコードを取り出す
    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(binaryStore.getAll()).hasSize(2);

    final JavaBinaryObject jmo = buildResults.getBinaryStore()
        .getAll()
        .stream()
        .findFirst()
        .orElse(null);
    final MemoryClassLoader loader = new MemoryClassLoader();
    final TargetFullyQualifiedName fqn = new TargetFullyQualifiedName(jmo.getBinaryName());
    loader.addDefinition(fqn, jmo.getByteCode());

    // バイトコードが正しいのでうまくロードできるはず
    loader.loadClass(fqn);
    loader.close();
  }

  @Ignore // 未知のバイナリはキャッシュできないのでこのテストはfail
  @Test
  // インメモリビルドの確認．直接BinaryStoreを操作してバイナリ追加
  public void testBuildWithBinaryStoreByDirectBinaryAddition01() throws Exception {
    // Bar.javaが存在しないのでビルドできない題材
    final Path rootPath = Paths.get("example/BuildFailure03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);

    // ビルド．まずは失敗する
    final BuildResults buildResults1 = projectBuilder.build(source);
    assertThat(buildResults1.isBuildFailed).isTrue();

    // Bar.classをファイルから直接読み込みJMOを生成
    final Path bin = rootPath.resolve("bin/example/Bar.class");
    final byte[] bytes = Files.readAllBytes(bin);
    final JavaBinaryObject object = null;
    // new JavaMemoryObject(Fqn.BAR.toString() + "#" + "d4eaa21b82a9ace3015e1ced616b9ce6",
    // Fqn.BAR.toString(), Kind.CLASS, "d4eaa21b82a9ace3015e1ced616b9ce6");
    object.openOutputStream()
        .write(bytes);

    // BinaryStoreにJMOバイナリを直接保存しておく
    final BinaryStore binStore = new BinaryStore();
    binStore.add(object);

    // リフレクションで書き換え
    final Class<?> clazz = projectBuilder.getClass();
    final Field field = clazz.getDeclaredField("binaryStore");
    field.setAccessible(true);
    field.set(projectBuilder, binStore);

    // ビルド．成功するはず
    final BuildResults buildResults2 = projectBuilder.build(source);
    assertThat(buildResults2.isBuildFailed).isFalse();

    // TODO this must be true
    // assertThat(buildResults.getCompilationPackage().getUnits()).hasSize(2);
  }

  @Ignore // 未知のバイナリはキャッシュできないのでこのテストはfail
  @Test
  // インメモリビルドの確認．直接BinaryStoreを操作してバイナリ追加．重複の場合．
  public void testBuildWithBinaryStoreByDirectBinaryAddition02() throws Exception {

    // Bar.javaが存在しないのでビルドできない題材
    final Path rootPath = Paths.get("example/BuildFailure03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);

    final BinaryStore binStore = new BinaryStore();

    // Bar.classをファイルから読み込み
    final Path bin1 = rootPath.resolve("bin/example/Bar.class");
    final byte[] bytes1 = Files.readAllBytes(bin1);
    final JavaBinaryObject object1 = new JavaBinaryObject(Fqn.BAR.toString(), Fqn.BAR.toString(), Kind.CLASS, "", null);
    object1.openOutputStream()
        .write(bytes1);
    binStore.add(object1);
    // BinaryStorexxx.instance.put(new BinaryStoreKey(Fqn.BAR.value), object1);

    // Foo.classをファイルから読み込み
    final Path bin2 = rootPath.resolve("bin/example/Foo.class");
    final byte[] bytes2 = Files.readAllBytes(bin2);
    final JavaBinaryObject object2 = new JavaBinaryObject(Fqn.FOO.toString(), Fqn.FOO.toString(), Kind.CLASS, "", null);
    object1.openOutputStream()
        .write(bytes2);
    binStore.add(object2);
    // BinaryStorexxx.instance.put(new BinaryStoreKey(Fqn.FOO.value), object2);

    // リフレクションで書き換え
    final Class<?> clazz = projectBuilder.getClass();
    final Field field = clazz.getDeclaredField("binaryStore");
    field.setAccessible(true);
    field.set(projectBuilder, binStore);

    // ビルド
    final BuildResults buildResults = projectBuilder.build(source);

    // 成功するはず
    assertThat(buildResults.isBuildFailed).isFalse();
  }


  @Ignore // 未知のバイナリはキャッシュできないのでこのテストはfail
  @Test
  // インメモリビルドの確認
  public void testBuildWithBinaryStore01() throws Exception {

    // example02のビルドが成功するかテスト
    final Path rootPath1 = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject1 = TargetProjectFactory.create(rootPath1);
    final ProjectBuilder projectBuilder1 = new ProjectBuilder(targetProject1);
    final GeneratedSourceCode source1 = TestUtil.createGeneratedSourceCode(targetProject1);
    final BuildResults buildResults1 = projectBuilder1.build(source1);

    // まずは普通に成功するはず
    assertThat(buildResults1.isBuildFailed).isFalse();

    // Bar.javaが存在しないのでビルドできない題材をビルド
    // 直前にexample02をビルドしているので，BinaryStoreにBar.classが残っており成功するはず
    final Path rootPath2 = Paths.get("example/BuildFailure03");
    final TargetProject targetProject2 = TargetProjectFactory.create(rootPath2);
    final GeneratedSourceCode source2 = TestUtil.createGeneratedSourceCode(targetProject2);
    final ProjectBuilder projectBuilder2 = new ProjectBuilder(targetProject2);

    // ビルド
    final BuildResults buildResults2 = projectBuilder2.build(source2);

    // 成功するはず
    assertThat(buildResults2.isBuildFailed).isFalse();
  }

  @Test
  public void testBuildWithExternalBinaryFile() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess13");
    final List<Path> sources = Arrays.asList(rootPath.resolve("src"));
    final List<Path> tests = Collections.emptyList();
    final List<Path> cps = Arrays.asList(rootPath.resolve("lib"));

    final TargetProject targetProject =
        TargetProjectFactory.create(rootPath, sources, tests, cps, JUnitVersion.JUNIT4);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);

    assertThat(buildResults.isBuildFailed).isFalse();

    final BinaryStore binaryStore = buildResults.getBinaryStore();
    assertThat(binaryStore.getAll()).extracting(jmo -> jmo.getBinaryName())
        .containsExactlyInAnyOrder(FOO.value);

  }
}
