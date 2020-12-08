package jp.kusumotolab.kgenprog;

import static jp.kusumotolab.kgenprog.ConfigurationBuilderTest.ConfigurationAssert.configAssertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.Configuration.Builder;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class ConfigurationBuilderTest {

  private final Path rootDir = Paths.get("example/BuildSuccess08");
  private final Path productPath = rootDir.resolve("src");
  private final Path testPath = rootDir.resolve("test");
  private final List<Path> productPaths = ImmutableList.of(productPath);
  private final List<Path> testPaths = ImmutableList.of(testPath);
  private final Configuration defaultConfig = generateDefaultConfiguration();

  @Test
  public void testBuild() {
    final Builder builder = new Builder(rootDir, productPaths, testPaths);
    final Configuration config = builder.build();
    assertThat(config).usingRecursiveComparison()
        .isEqualTo(defaultConfig);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithOutDir() {
    final Path outDir = rootDir.resolve("out");
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setOutDir(outDir);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(outDir);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "outDir");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithMutationGeneratingCount() {
    final int mutationGeneratingCount = 50;
    final Builder builder =
        new Builder(rootDir, productPaths, testPaths).setMutationGeneratingCount(
            mutationGeneratingCount);
    final Configuration config = builder.build();

    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "mutationGeneratingCount");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithCrossoverGeneratingCount() {
    final int crossoverGeneratingCount = 50;
    final Builder builder = new Builder(rootDir, productPaths, testPaths)
        .setCrossoverGeneratingCount(crossoverGeneratingCount);
    final Configuration config = builder.build();

    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverGeneratingCount");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithHeadcount() {
    final int headcount = 50;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setHeadcount(headcount);
    final Configuration config = builder.build();

    assertThat(config.getHeadcount()).isEqualTo(headcount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "headcount");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithMaxGeneration() {
    final int maxGeneration = 50;
    final Builder builder =
        new Builder(rootDir, productPaths, testPaths).setMaxGeneration(maxGeneration);
    final Configuration config = builder.build();

    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "maxGeneration");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithTimeLimit() {
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setTimeLimit(timeLimit);
    final Configuration config = builder.build();

    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "timeLimit");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithTimeLimitSeconds() {
    final int timeLimitSeconds = 1800;
    final Builder builder =
        new Builder(rootDir, productPaths, testPaths).setTimeLimitSeconds(timeLimitSeconds);
    final Configuration config = builder.build();

    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimitSeconds));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitSeconds);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "timeLimit", "timeLimitSeconds");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithRequiredSolutionsCount() {
    final int requiredSolutionsCount = 10;
    final Builder builder = new Builder(rootDir, productPaths, testPaths)
        .setRequiredSolutionsCount(requiredSolutionsCount);
    final Configuration config = builder.build();

    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "requiredSolutionsCount");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevel() {
    final Level logLevel = Level.DEBUG;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel(logLevel);
    final Configuration config = builder.build();

    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "logLevel");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevelString() {
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel("DEBUG");
    final Configuration config = builder.build();

    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "logLevel");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithClassPath() {
    final List<Path> classPaths = ImmutableList.of(rootDir.resolve("lib"));
    final Builder builder = new Builder(rootDir, productPaths, testPaths).addClassPaths(classPaths);
    final Configuration config = builder.build();

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, classPaths, JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithRandomSeed() {
    final long randomSeed = 10;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setRandomSeed(randomSeed);
    final Configuration config = builder.build();

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "randomSeed");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithScope() {
    final Scope.Type scope = Scope.Type.FILE;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setScope(scope);
    final Configuration config = builder.build();

    assertThat(config.getScope()).isEqualTo(scope);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "scope");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithNeedNotOutput() {
    final boolean needNotOutput = true;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setNeedNotOutput(
        needNotOutput);
    final Configuration config = builder.build();

    assertThat(config.needNotOutput()).isEqualTo(needNotOutput);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "needNotOutput");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithFaultLocalization() {
    final FaultLocalization.Technique faultLocalization = FaultLocalization.Technique.Ochiai;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setFaultLocalization(
        faultLocalization);
    final Configuration config = builder.build();

    assertThat(config.getFaultLocalization()).isEqualTo(faultLocalization);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "faultLocalization");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithCrossoverType() {
    final Crossover.Type crossoverType = Crossover.Type.Random;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setCrossoverType(
        crossoverType);
    final Configuration config = builder.build();

    assertThat(config.getCrossoverType()).isEqualTo(crossoverType);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverType");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithFirstVariantSelectionStrategy() {
    final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategy =
        FirstVariantSelectionStrategy.Strategy.Random;
    final Builder builder = new Builder(rootDir, productPaths, testPaths)
        .setFirstVariantSelectionStrategy(firstVariantSelectionStrategy);
    final Configuration config = builder.build();

    assertThat(config.getFirstVariantSelectionStrategy()).isEqualTo(firstVariantSelectionStrategy);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "firstVariantSelectionStrategy");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithSecondVariantSelectionStrategy() {
    final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategy =
        SecondVariantSelectionStrategy.Strategy.Random;
    final Builder builder = new Builder(rootDir, productPaths, testPaths)
        .setSecondVariantSelectionStrategy(secondVariantSelectionStrategy);
    final Configuration config = builder.build();

    assertThat(config.getSecondVariantSelectionStrategy()).isEqualTo(
        secondVariantSelectionStrategy);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "secondVariantSelectionStrategy");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgs() {
    final String[] args =
        {"-r", rootDir.toString(), "-s", productPath.toString(), "-t", testPath.toString(),};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithOutDir() {
    final Path outDir = rootDir.resolve("out");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-o", outDir.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(outDir);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "outDir");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithMutationGeneratingCount() {
    final int mutationGeneratingCount = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--mutation-generating-count",
        Integer.toString(mutationGeneratingCount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "mutationGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithCrossoverGeneratingCount() {
    final int crossoverGeneratingCount = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--crossover-generating-count",
        Integer.toString(crossoverGeneratingCount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithHeadcount() {
    final int headcount = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--headcount", Integer.toString(headcount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getHeadcount()).isEqualTo(headcount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "headcount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithMaxGeneration() {
    final int maxGeneration = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--max-generation", Integer.toString(maxGeneration)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "maxGeneration");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithTimeLimit() {
    final Duration timeLimit = Duration.ofSeconds(1800);
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--time-limit", Long.toString(timeLimit.getSeconds())};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "timeLimit");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithRequiredSolutionsCount() {
    final int requiredSolutionsCount = 10;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--required-solutions", Integer.toString(requiredSolutionsCount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "requiredSolutionsCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithVerbose() {
    final String[] args =
        {"-r", rootDir.toString(), "-s", productPath.toString(), "-t", testPath.toString(), "-v"};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "logLevel");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithQuiet() {
    final String[] args =
        {"-r", rootDir.toString(), "-s", productPath.toString(), "-t", testPath.toString(), "-q"};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getLogLevel()).isEqualTo(Level.ERROR);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "logLevel");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithClassPath() {
    final Path classPath = rootDir.resolve("lib");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-c", classPath.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), ImmutableList.of(classPath), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithRandomSeed() {
    final long randomSeed = 10;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--random-seed", Long.toString(randomSeed)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithScope() {
    final Scope.Type scope = Scope.Type.FILE;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--scope", scope.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getScope()).isEqualTo(scope);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "scope");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithExecTest() {
    final String executionTest = "example.FooTest";
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-x", executionTest};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest);
  }

  @Test
  public void testBuildFromCmdLineArgsWithExecTests() {
    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-x", executionTest1, executionTest2};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);
  }

  @Test
  public void testBuildFromCmdLineArgsWithTestTimeLimit() {
    final Duration testTimeLimit = Duration.ofSeconds(99);
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--test-time-limit", String.valueOf(testTimeLimit.getSeconds())};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getTestTimeLimit()).isEqualTo(testTimeLimit);
    assertThat(config.getTestTimeLimitSeconds()).isEqualTo(testTimeLimit.getSeconds());
  }

  @Test
  public void testBuildFromCmdLineArgsWithDifferentRootDir() {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final PrintStream printStream = System.out;
    System.setOut(new PrintStream(out));

    // 別のディレクトリで指定すると
    final String[] args =
        {"-r", "example/BuildSuccess01", "-s", productPath.toString(), "-t", testPath.toString()};
    Builder.buildFromCmdLineArgs(args);

    // 警告でるはず
    assertThat(out.toString()).contains("The directory where kGenProg is running is different");

    System.setOut(printStream);
  }

  @Test
  public void testBuildFromCmdLineArgsWithSameRootDir() {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final PrintStream printStream = System.out;
    System.setOut(new PrintStream(out));

    // 同じディレクトリで指定すると
    final String[] args = {"-r", "./", "-s", productPath.toString(), "-t", testPath.toString()};
    Builder.buildFromCmdLineArgs(args);

    // 警告でないはず
    assertThat(out.toString()).doesNotContain(
        "The directory where kGenProg is running is different");

    System.setOut(printStream);
  }

  @Test
  public void testBuildFromCmdLineArgsWithNeedNotOutput() {
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--no-output"};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    assertThat(config.needNotOutput()).isTrue();
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "needNotOutput");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testNotExistingRootDir() {
    final Path notExistingRootDir = Paths.get("notExistingRootDir");
    final String[] args = {"-r", notExistingRootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString()};

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(notExistingRootDir.toString() + " does not exist.");
  }

  @Test
  public void testNotExistingProductDir() {
    final Path notExistingProductDir = Paths.get("notExistingProductDir");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(),
        notExistingProductDir.toString(), "-t", testPath.toString()};

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(notExistingProductDir.toString() + " does not exist.");
  }

  @Test
  public void testNotExistingTestDir() {
    final Path notExistingTestDir = Paths.get("notExistingTestDir");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), notExistingTestDir.toString()};

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(notExistingTestDir.toString() + " does not exist.");
  }

  @Test
  public void testNotExistingClassPath() {
    final Path notExistingClassPath = Paths.get("notExistingClassPath");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-c", notExistingClassPath.toString()};

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(notExistingClassPath.toString() + " does not exist.");
  }

  @Test
  public void testBuildFromConfigFile() {
    final Path configPath = rootDir.resolve("kgenprog.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithOutDir() {
    final Path configPath = rootDir.resolve("withOutDir.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Path outDir = rootDir.resolve("out");

    assertThat(config.getOutDir()).isEqualTo(outDir);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "outDir");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithMutationGeneratingCount() {
    final Path configPath = rootDir.resolve("withMutationGeneratingCount.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int mutationGeneratingCount = 50;

    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "mutationGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithCrossoverGeneratingCount() {
    final Path configPath = rootDir.resolve("withCrossoverGeneratingCount.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int crossoverGeneratingCount = 50;

    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithHeadCount() {
    final Path configPath = rootDir.resolve("withHeadCount.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int headCount = 50;

    assertThat(config.getHeadcount()).isEqualTo(headCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "headcount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithMaxGeneration() {
    final Path configPath = rootDir.resolve("withMaxGeneration.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int maxGeneration = 50;

    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "maxGeneration");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithTimeLimit() {
    final Path configPath = rootDir.resolve("withTimeLimit.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final long timeLimit = 50;

    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimit));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "timeLimit", "timeLimitSeconds");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithTestTimeLimit() {
    final Path configPath = rootDir.resolve("withTestTimeLimit.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final long testTimeLimit = 50;

    assertThat(config.getTestTimeLimit()).isEqualTo(Duration.ofSeconds(testTimeLimit));
    assertThat(config.getTestTimeLimitSeconds()).isEqualTo(testTimeLimit);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "testTimeLimit", "testTimeLimitSeconds");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithRequiredSolutionsCount() {
    final Path configPath = rootDir.resolve("withRequiredSolutionsCount.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int requiredSolutionsCount = 50;

    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "requiredSolutionsCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithLogLevel() {
    final Path configPath = rootDir.resolve("withLogLevel.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Level logLevel = Level.DEBUG;

    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "logLevel");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithClassPath() {
    final Path configPath = rootDir.resolve("withClassPath.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "");

    final Path classPath = rootDir.resolve("lib");
    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, ImmutableList.of(classPath),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithRandomSeed() {
    final Path configPath = rootDir.resolve("withRandomSeed.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int randomSeed = 50;

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithScope() {
    final Path configPath = rootDir.resolve("withScope.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Scope.Type scope = Scope.Type.FILE;

    assertThat(config.getScope()).isEqualTo(scope);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "scope");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithExecTests() {
    final Path configPath = rootDir.resolve("withExecTests.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";

    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "executionTests");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithNeedNotOutput() {
    final Path configPath = rootDir.resolve("withNeedNotOutputTrue.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final boolean needNotOutput = true;

    assertThat(config.needNotOutput()).isEqualTo(needNotOutput);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "needNotOutput");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithOutDirOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withOutDir.toml");
    final Path outDirFromCmdLine = rootDir.resolve("out-dir");
    final String[] args = {"--config", configPath.toString(), "-o", outDirFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Path outDirFromConfigFile = rootDir.resolve("out");

    assertThat(config.getOutDir()).isNotEqualTo(outDirFromConfigFile);
    assertThat(config.getOutDir()).isEqualTo(outDirFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "outDir");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithMutationGeneratingCountOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withMutationGeneratingCount.toml");
    final int mutationGeneratingCountFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--mutation-generating-count",
        Integer.toString(mutationGeneratingCountFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int mutationGeneratingCountFromConfigFile = 50;

    assertThat(config.getMutationGeneratingCount()).isNotEqualTo(
        mutationGeneratingCountFromConfigFile);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        mutationGeneratingCountFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "mutationGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithCrossoverGeneratingCountOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withCrossoverGeneratingCount.toml");
    final int crossoverGeneratingCountFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--crossover-generating-count",
        Integer.toString(crossoverGeneratingCountFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int crossoverGeneratingCountFromConfigFile = 50;

    assertThat(config.getCrossoverGeneratingCount()).isNotEqualTo(
        crossoverGeneratingCountFromConfigFile);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        crossoverGeneratingCountFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverGeneratingCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithHeadCountOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withHeadCount.toml");
    final int headCountFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--headcount",
        Integer.toString(headCountFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int headCountFromConfigFile = 50;

    assertThat(config.getHeadcount()).isNotEqualTo(headCountFromConfigFile);
    assertThat(config.getHeadcount()).isEqualTo(headCountFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "headcount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithMaxGenerationOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withMaxGeneration.toml");
    final int maxGenerationFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--max-generation",
        Integer.toString(maxGenerationFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int maxGenerationFromConfigFile = 50;

    assertThat(config.getMaxGeneration()).isNotEqualTo(maxGenerationFromConfigFile);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGenerationFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "maxGeneration");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithTimeLimitOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withTimeLimit.toml");
    final long timeLimitFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--time-limit",
        Long.toString(timeLimitFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final long timeLimitFromConfigFile = 50;

    assertThat(config.getTimeLimit()).isNotEqualTo(Duration.ofSeconds(timeLimitFromConfigFile));
    assertThat(config.getTimeLimitSeconds()).isNotEqualTo(timeLimitFromConfigFile);
    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimitFromCmdLine));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "timeLimit", "timeLimitSeconds");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithTestTimeLimitOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withTestTimeLimit.toml");
    final long testTimeLimitFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--test-time-limit",
        Long.toString(testTimeLimitFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final long testTimeLimitFromConfigFile = 50;

    assertThat(config.getTestTimeLimit()).isNotEqualTo(
        Duration.ofSeconds(testTimeLimitFromConfigFile));
    assertThat(config.getTestTimeLimitSeconds()).isNotEqualTo(testTimeLimitFromConfigFile);
    assertThat(config.getTestTimeLimit()).isEqualTo(
        Duration.ofSeconds(testTimeLimitFromCmdLine));
    assertThat(config.getTestTimeLimitSeconds()).isEqualTo(testTimeLimitFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "testTimeLimit", "testTimeLimitSeconds");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithRequiredSolutionsCountOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withRequiredSolutionsCount.toml");
    final int requiredSolutionsCountFromCmdLine = 500;
    final String[] args = {"--config", configPath.toString(), "--required-solutions",
        Integer.toString(requiredSolutionsCountFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int requiredSolutionsCountFromConfigFile = 50;

    assertThat(config.getRequiredSolutionsCount()).isNotEqualTo(
        requiredSolutionsCountFromConfigFile);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCountFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "requiredSolutionsCount");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithLogLevelOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withLogLevel.toml");
    final Level logLevelFromCmdLine = Level.ERROR;
    final String[] args = {"--config", configPath.toString(), "-q"};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Level logLevelFromConfigFile = Level.DEBUG;

    assertThat(config.getLogLevel()).isNotEqualTo(logLevelFromConfigFile);
    assertThat(config.getLogLevel()).isEqualTo(logLevelFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "logLevel");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithClassPathOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withClassPath.toml");
    final Path classPathFromCmdLine = rootDir.resolve("library");
    final String[] args = {"--config", configPath.toString(), "-c",
        classPathFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "");

    final Path classPathFromConfigFile = rootDir.resolve("lib");
    final TargetProject projectWithClassPathFromConfigFile =
        TargetProjectFactory.create(rootDir, productPaths, testPaths,
            ImmutableList.of(classPathFromConfigFile), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isNotEqualTo(projectWithClassPathFromConfigFile);

    final TargetProject projectWithClassPathFromCmdLine =
        TargetProjectFactory.create(rootDir, productPaths, testPaths,
            ImmutableList.of(classPathFromCmdLine), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(projectWithClassPathFromCmdLine);
  }

  @Test
  public void testBuildFromConfigFileWithRandomSeedOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withRandomSeed.toml");
    final int randomSeedFromCmdLine = 100;
    final String[] args = {"--config", configPath.toString(), "--random-seed",
        String.valueOf(randomSeedFromCmdLine)};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final int randomSeedFromConfigFile = 50;

    assertThat(config.getRandomSeed()).isNotEqualTo(randomSeedFromConfigFile);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeedFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "randomSeed");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithScopeOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withScope.toml");
    final Scope.Type scopeFromCmdLine = Scope.Type.PROJECT;
    final String[] args = {"--config", configPath.toString(), "--scope",
        scopeFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Scope.Type scopeFromConfigFile = Scope.Type.FILE;

    assertThat(config.getScope()).isNotEqualTo(scopeFromConfigFile);
    assertThat(config.getScope()).isEqualTo(scopeFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "scope");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithExecTestsOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withExecTests.toml");
    final String executionTestFromCmdLine = "example.BazTest";
    final String[] args = {"--config", configPath.toString(), "-x", executionTestFromCmdLine};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final String executionTestFromConfigFile1 = "example.FooTest";
    final String executionTestFromConfigFile2 = "example.BarTest";

    assertThat(config.getExecutedTests()).doesNotContain(executionTestFromConfigFile1,
        executionTestFromConfigFile2);
    assertThat(config.getExecutedTests()).contains(executionTestFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "executionTests");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithNeedNotOutputOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withNeedNotOutputFalse.toml");
    final String[] args = {"--config", configPath.toString(), "--no-output"};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final boolean needNotOutputFromConfigFile = false;

    assertThat(config.needNotOutput()).isNotEqualTo(needNotOutputFromConfigFile);
    assertThat(config.needNotOutput()).isTrue();
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "needNotOutput");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithFaultLocalizationOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withFaultLocalization.toml");
    final FaultLocalization.Technique faultLocalizationFromCmdLine = FaultLocalization.Technique.Ochiai;
    final String[] args = {"--config", configPath.toString(), "--fault-localization",
        faultLocalizationFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final FaultLocalization.Technique faultLocalizationFromConfigFile = FaultLocalization.Technique.Ample;

    assertThat(config.getFaultLocalization()).isNotEqualTo(faultLocalizationFromConfigFile);
    assertThat(config.getFaultLocalization()).isEqualTo(faultLocalizationFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "faultLocalization");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithCrossoverTypeOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withCrossoverType.toml");
    final Crossover.Type crossoverTypeFromCmdLine = Crossover.Type.Random;
    final String[] args = {"--config", configPath.toString(), "--crossover-type",
        crossoverTypeFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final Crossover.Type crossoverTypeFromConfigFile = Crossover.Type.SinglePoint;

    assertThat(config.getCrossoverType()).isNotEqualTo(crossoverTypeFromConfigFile);
    assertThat(config.getCrossoverType()).isEqualTo(crossoverTypeFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "crossoverType");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithFirstVariantSelectionStrategyOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withFirstVariantSelectionStrategy.toml");
    final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategyFromCmdLine =
        FirstVariantSelectionStrategy.Strategy.Elite;
    final String[] args = {"--config", configPath.toString(), "--crossover-first-variant",
        firstVariantSelectionStrategyFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategyFromConfigFile =
        FirstVariantSelectionStrategy.Strategy.Random;

    assertThat(config.getFirstVariantSelectionStrategy()).isNotEqualTo(
        firstVariantSelectionStrategyFromConfigFile);
    assertThat(config.getFirstVariantSelectionStrategy()).isEqualTo(
        firstVariantSelectionStrategyFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "firstVariantSelectionStrategy");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithSecondVariantSelectionStrategyOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withSecondVariantSelectionStrategy.toml");
    final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategyFromCmdLine =
        SecondVariantSelectionStrategy.Strategy.Elite;
    final String[] args = {"--config", configPath.toString(), "--crossover-second-variant",
        secondVariantSelectionStrategyFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategyFromConfigFile =
        SecondVariantSelectionStrategy.Strategy.Random;

    assertThat(config.getSecondVariantSelectionStrategy()).isNotEqualTo(
        secondVariantSelectionStrategyFromConfigFile);
    assertThat(config.getSecondVariantSelectionStrategy()).isEqualTo(
        secondVariantSelectionStrategyFromCmdLine);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "secondVariantSelectionStrategy");

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithSymbolicLink() throws IOException {
    final Path src = rootDir.resolve("src");
    final Path link = rootDir.resolve("src-example");
    Files.deleteIfExists(link);
    Files.createSymbolicLink(link, src.toAbsolutePath());

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final PrintStream printStream = System.out;
    System.setOut(new PrintStream(out));

    final Path configPath = rootDir.resolve("withSymbolicLink.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir,
        ImmutableList.of(link), testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
    assertThat(out.toString()).contains("symbolic link may not be resolved:");

    Files.delete(link);
    System.setOut(printStream);
  }

  @Test
  public void testBuildWithHistoryRecord() {
    final boolean historyRecord = false;
    final Builder builder =
        new Builder(rootDir, productPaths, testPaths).setHistoryRecord(historyRecord);
    final Configuration config = builder.build();

    assertThat(config.isHistoryRecord()).isEqualTo(historyRecord);
    configAssertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "historyRecord");

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }
  // todo: 引数がなかった場合の挙動を確かめるために，カレントディレクトリを変更した上でテスト実行

  private Configuration generateDefaultConfiguration() {
    return new Builder(rootDir, productPaths, testPaths).build();
  }

  public static class ConfigurationAssert extends
      AbstractAssert<ConfigurationAssert, Configuration> {

    public ConfigurationAssert(final Configuration actual) {
      super(actual, ConfigurationAssert.class);
    }

    public static ConfigurationAssert configAssertThat(final Configuration actual) {
      return new ConfigurationAssert(actual);
    }

    public ConfigurationAssert isEqualToRecursivelyIgnoringGivenFields(final Configuration subject,
        final String... ignoredFieldNames) {
      Assertions.assertThat(actual)
          .usingRecursiveComparison()
          .ignoringFields("builder")
          .ignoringFields("targetProject")
          .ignoringFields(ignoredFieldNames)
          .isEqualTo(subject);
      return this;
    }
  }
}
