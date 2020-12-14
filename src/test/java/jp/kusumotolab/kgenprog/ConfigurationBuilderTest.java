package jp.kusumotolab.kgenprog;

import static jp.kusumotolab.kgenprog.ConfigurationAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.Configuration.Builder;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover.Type;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class ConfigurationBuilderTest {

  private final Path rootDir = Paths.get("example/TestConfigFiles");
  private final Path productPath = rootDir.resolve("src");
  private final Path testPath = rootDir.resolve("test");
  private final List<Path> productPaths = List.of(productPath);
  private final List<Path> testPaths = List.of(testPath);

  private final Path noSuchDir = Paths.get("no-such-dir");
  private final Configuration defaultConfig = new Builder(rootDir, productPaths, testPaths).build();

  // capture sysout to assert some warnings in abnormal tests.
  @Rule
  public final SystemOutRule sysout = new SystemOutRule().enableLog();

  ////////////////////////////////////////////////////////////////////////////////
  // standard tests using Builder.build()

  @Test
  public void testBuild() {
    final Configuration config = new Builder(rootDir, productPaths, testPaths)
        .build();

    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig);
  }

  @Test
  public void testBuildWithBasicParameters() {
    final long randomSeed = 9;
    final int mutationGeneratingCount = 10;
    final int crossoverGeneratingCount = 20;
    final int headcount = 30;
    final int maxGeneration = 40;
    final int requiredSolutionsCount = 50;
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Duration testTimeLimit = Duration.ofSeconds(3);
    final Scope.Type scope = Scope.Type.FILE;

    final Configuration config = new Builder(rootDir, productPaths, testPaths)
        .setRandomSeed(randomSeed)
        .setMutationGeneratingCount(mutationGeneratingCount)
        .setCrossoverGeneratingCount(crossoverGeneratingCount)
        .setHeadcount(headcount)
        .setMaxGeneration(maxGeneration)
        .setRequiredSolutionsCount(requiredSolutionsCount)
        .setScope(scope)
        .setTimeLimit(timeLimit)
        .setTestTimeLimit(testTimeLimit)
        .build();

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTestTimeLimit()).isEqualTo(testTimeLimit);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed", "mutationGeneratingCount", "crossoverGeneratingCount", "headcount",
        "maxGeneration", "requiredSolutionsCount", "timeLimit", "testTimeLimit", "scope");
  }

  @Test
  public void testBuildWithAdvancedParameters() {
    final Path classPath1 = rootDir.resolve("lib");
    final Path classPath2 = rootDir.resolve("library");
    final FaultLocalization.Technique faultLocalization = FaultLocalization.Technique.Ochiai;
    final Crossover.Type crossoverType = Type.SinglePoint;
    final FirstVariantSelectionStrategy.Strategy firstStrategy = FirstVariantSelectionStrategy.Strategy.Elite;
    final SecondVariantSelectionStrategy.Strategy secondStrategy = SecondVariantSelectionStrategy.Strategy.TestComplementary;
    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";

    final Configuration config = new Builder(rootDir, productPaths, testPaths)
        .addClassPaths(Set.of(classPath1, classPath2))
        .setFaultLocalization(faultLocalization)
        .setCrossoverType(crossoverType)
        .setFirstVariantSelectionStrategy(firstStrategy)
        .setSecondVariantSelectionStrategy(secondStrategy)
        .addExecutionTest(executionTest1)
        .addExecutionTest(executionTest2)
        .build();

    assertThat(config.getTargetProject()
        .getClassPaths()).contains(new ClassPath(classPath1), new ClassPath(classPath2));
    assertThat(config.getFaultLocalization()).isEqualTo(faultLocalization);
    assertThat(config.getCrossoverType()).isEqualTo(crossoverType);
    assertThat(config.getFirstVariantSelectionStrategy()).isEqualTo(firstStrategy);
    assertThat(config.getSecondVariantSelectionStrategy()).isEqualTo(secondStrategy);
    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "targetProject.classPaths", "faultLocalization", "crossoverType",
        "firstVariantSelectionStrategy", "secondVariantSelectionStrategy", "executionTests");
  }

  @Test
  public void testBuildWithOutputParameters() {
    final Path outDir = rootDir.resolve("out");
    final boolean isPatchOutput = true;
    final boolean isHistoryRecord = true;
    final Level logLevel = Level.DEBUG;

    final Configuration config = new Builder(rootDir, productPaths, testPaths)
        .setOutDir(outDir)
        .setPatchOutput(isPatchOutput)
        .setHistoryRecord(isHistoryRecord)
        .setLogLevel(logLevel)
        .build();

    assertThat(config.getOutDir()).isEqualTo(outDir);
    assertThat(config.isPatchOutput()).isEqualTo(isPatchOutput);
    assertThat(config.isHistoryRecord()).isEqualTo(isHistoryRecord);
    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "outDir", "isPatchOutput", "isHistoryRecord", "logLevel");
  }

  @Test
  public void testBuildWithMethodOverload() {
    final int timeLimitSeconds = 1800;
    final String logLevel = "DEBUG";
    final Configuration config = new Builder(rootDir, productPaths, testPaths)
        .setTimeLimitSeconds(timeLimitSeconds)
        .setLogLevel(logLevel)
        .build();

    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitSeconds);
    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "timeLimit", "logLevel");
  }

  ////////////////////////////////////////////////////////////////////////////////
  // abnormal cases

  @Test
  public void testBuildWithDifferentRootDir() {
    // 別のディレクトリで指定すると
    final String[] args = {
        "-r", "example/BuildSuccess01",
        "-s", productPath.toString(),
        "-t", testPath.toString()
    };

    Builder.buildFromCmdLineArgs(args);

    // 警告でるはず
    assertThat(sysout.getLog()).contains("The directory where kGenProg is running is different");
  }

  @Test
  public void testBuildWithSameRootDir() {
    // 同じディレクトリで指定すると
    final String[] args = {
        "-r", "./",
        "-s", productPath.toString(),
        "-t", testPath.toString()
    };

    Builder.buildFromCmdLineArgs(args);

    // 警告でないはず
    assertThat(sysout.getLog()).doesNotContain(
        "The directory where kGenProg is running is different");
  }

  @Test
  public void testBuildNotExistingRootDir() {
    final String[] args = {
        "-r", noSuchDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString()
    };

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(noSuchDir.toString() + " does not exist.");
  }

  @Test
  public void testBuildNotExistingProductDir() {
    final String[] args = {
        "-r", rootDir.toString(),
        "-s", noSuchDir.toString(),
        "-t", testPath.toString()
    };

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(noSuchDir.toString() + " does not exist.");
  }

  @Test
  public void testBuildNotExistingTestDir() {
    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", noSuchDir.toString()
    };

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(noSuchDir.toString() + " does not exist.");
  }

  @Test
  public void testBuildNotExistingClassPath() {
    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
        "-c", noSuchDir.toString()
    };

    assertThatThrownBy(() -> Builder.buildFromCmdLineArgs(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(noSuchDir.toString() + " does not exist.");
  }

  @Test
  public void testBuildWithSymbolicLink() {
    final Path srcSymlink = rootDir.resolve("src-symlink");
    final Path testSymlink = rootDir.resolve("test-symlink");

    try {
      Files.deleteIfExists(srcSymlink);
      Files.deleteIfExists(testSymlink);
      Files.createSymbolicLink(srcSymlink, productPath.toAbsolutePath());
      Files.createSymbolicLink(testSymlink, productPath.toAbsolutePath());
    } catch (final IOException e) {
      // skip this test if fail to create symlink
      return;
    }

    final Configuration config = new Builder(
        rootDir, List.of(srcSymlink), List.of(testSymlink))
        .build();

    // used to compare config.targetProject
    final TargetProject expectedProject = TargetProjectFactory.create(
        rootDir, List.of(srcSymlink), List.of(testSymlink),
        Collections.emptyList(), JUnitVersion.JUNIT4);

    assertThat(config.getTargetProject())
        .usingRecursiveComparison()
        .isEqualTo(expectedProject);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "targetProject");
    assertThat(sysout.getLog())
        .contains("symbolic link may not be resolved: " + srcSymlink.toString())
        .contains("symbolic link may not be resolved: " + testSymlink.toString());

    try {
      Files.deleteIfExists(srcSymlink);
      Files.deleteIfExists(testSymlink);
    } catch (final IOException e) {
      // no need to handle
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // standard tests using Builder.buildFromCmdLineArgs()

  @Test
  public void testBuildFromCmdArgs() {
    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig);
  }

  @Test
  public void testBuildFromCmdArgsWithBasicParameters() {
    final long randomSeed = 9;
    final int mutationGeneratingCount = 10;
    final int crossoverGeneratingCount = 20;
    final int headcount = 30;
    final int maxGeneration = 40;
    final int requiredSolutionsCount = 50;
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Duration testTimeLimit = Duration.ofSeconds(3);
    final Scope.Type scope = Scope.Type.FILE;

    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
        "--random-seed", String.valueOf(randomSeed),
        "--mutation-generating-count", String.valueOf(mutationGeneratingCount),
        "--crossover-generating-count", String.valueOf(crossoverGeneratingCount),
        "--headcount", String.valueOf(headcount),
        "--max-generation", String.valueOf(maxGeneration),
        "--required-solutions", String.valueOf(requiredSolutionsCount),
        "--time-limit", String.valueOf(timeLimit.getSeconds()),
        "--test-time-limit", String.valueOf(testTimeLimit.getSeconds()),
        "--scope", String.valueOf(scope),
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTestTimeLimit()).isEqualTo(testTimeLimit);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed", "mutationGeneratingCount", "crossoverGeneratingCount", "headcount",
        "maxGeneration", "requiredSolutionsCount", "timeLimit", "testTimeLimit", "scope");
  }

  @Test
  public void testBuildFromCmdArgsWithAdvancedParameters() {
    final Path classPath1 = rootDir.resolve("lib");
    final Path classPath2 = rootDir.resolve("library");
    final FaultLocalization.Technique faultLocalization = FaultLocalization.Technique.Ochiai;
    final Crossover.Type crossoverType = Type.SinglePoint;
    final FirstVariantSelectionStrategy.Strategy firstStrategy = FirstVariantSelectionStrategy.Strategy.Elite;
    final SecondVariantSelectionStrategy.Strategy secondStrategy = SecondVariantSelectionStrategy.Strategy.TestComplementary;
    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";

    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
        "--cp", String.valueOf(classPath1),
        "--cp", String.valueOf(classPath2),
        "--fault-localization", String.valueOf(faultLocalization),
        "--crossover-type", String.valueOf(crossoverType),
        "--crossover-first-variant", String.valueOf(firstStrategy),
        "--crossover-second-variant", String.valueOf(secondStrategy),
        "-x", executionTest1,
        "-x", executionTest2,
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getTargetProject()
        .getClassPaths()).contains(new ClassPath(classPath1), new ClassPath(classPath2));
    assertThat(config.getFaultLocalization()).isEqualTo(faultLocalization);
    assertThat(config.getCrossoverType()).isEqualTo(crossoverType);
    assertThat(config.getFirstVariantSelectionStrategy()).isEqualTo(firstStrategy);
    assertThat(config.getSecondVariantSelectionStrategy()).isEqualTo(secondStrategy);
    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "targetProject.classPaths", "faultLocalization", "crossoverType",
        "firstVariantSelectionStrategy", "secondVariantSelectionStrategy", "executionTests");
  }

  @Test
  public void testBuildFromCmdArgsWithOutputParameters() {
    final Path outDir = rootDir.resolve("out");
    final boolean isPatchOutput = true;
    final boolean isHistoryRecord = true;
    final Level logLevel = Level.DEBUG; // means "-v"

    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
        "-o", String.valueOf(outDir),
        "--patch-output",
        "--history-record",
        "-v"
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(outDir);
    assertThat(config.isPatchOutput()).isEqualTo(isPatchOutput);
    assertThat(config.isHistoryRecord()).isEqualTo(isHistoryRecord);
    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "outDir", "isPatchOutput", "isHistoryRecord", "logLevel");
  }

  @Test
  public void testBuildFromCmdArgsWithMiscParameters() {
    final Level logLevel = Level.ERROR; // means "-q"

    final String[] args = {
        "-r", rootDir.toString(),
        "-s", productPath.toString(),
        "-t", testPath.toString(),
        "-q"
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig, "logLevel");
  }

  ////////////////////////////////////////////////////////////////////////////////
  // standard tests using "--config" option

  @Test
  public void testBuildFromConfigFile() {
    final Path configPath = rootDir.resolve("withMinimal.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig);
  }

  @Test
  public void testBuildFromConfigFileWithBasicGAParameters() {
    final long randomSeed = 9;
    final int mutationGeneratingCount = 10;
    final int crossoverGeneratingCount = 20;
    final int headcount = 30;
    final int maxGeneration = 40;
    final int requiredSolutionsCount = 50;
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Duration testTimeLimit = Duration.ofSeconds(3);
    final Scope.Type scope = Scope.Type.FILE;

    final Path configPath = rootDir.resolve("withBasicParameters.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTestTimeLimit()).isEqualTo(testTimeLimit);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed", "mutationGeneratingCount", "crossoverGeneratingCount", "headcount",
        "maxGeneration", "requiredSolutionsCount", "timeLimit", "testTimeLimit", "scope");
  }

  @Test
  public void testBuildFromConfigFileWithAdvancedParameters() {
    final Path classPath1 = rootDir.resolve("lib");
    final Path classPath2 = rootDir.resolve("library");
    final FaultLocalization.Technique faultLocalization = FaultLocalization.Technique.Ochiai;
    final Crossover.Type crossoverType = Type.SinglePoint;
    final FirstVariantSelectionStrategy.Strategy firstStrategy = FirstVariantSelectionStrategy.Strategy.Elite;
    final SecondVariantSelectionStrategy.Strategy secondStrategy = SecondVariantSelectionStrategy.Strategy.TestComplementary;
    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";

    final Path configPath = rootDir.resolve("withAdvancedParameters.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getTargetProject()
        .getClassPaths()).contains(new ClassPath(classPath1), new ClassPath(classPath2));
    assertThat(config.getFaultLocalization()).isEqualTo(faultLocalization);
    assertThat(config.getCrossoverType()).isEqualTo(crossoverType);
    assertThat(config.getFirstVariantSelectionStrategy()).isEqualTo(firstStrategy);
    assertThat(config.getSecondVariantSelectionStrategy()).isEqualTo(secondStrategy);
    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "targetProject.classPaths", "faultLocalization", "crossoverType",
        "firstVariantSelectionStrategy", "secondVariantSelectionStrategy", "executionTests");
  }

  @Test
  public void testBuildFromConfigFileWithOutputParameters() {
    final Path outDir = rootDir.resolve("out");
    final boolean isPatchOutput = true;
    final boolean isHistoryRecord = true;
    final Level logLevel = Level.DEBUG;

    final Path configPath = rootDir.resolve("withOutputParameters.toml");
    final String[] args = {"--config", configPath.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(outDir);
    assertThat(config.isPatchOutput()).isEqualTo(isPatchOutput);
    assertThat(config.isHistoryRecord()).isEqualTo(isHistoryRecord);
    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "outDir", "isPatchOutput", "isHistoryRecord", "logLevel");
  }

  ////////////////////////////////////////////////////////////////////////////////
  // corner case. overwrite config file parameters with cmd args

  @Test
  public void testBuildFromConfigFileWithOverwriteFromCmdArgs() {
    // different parameters with config file (x100)
    final long randomSeed = 900;
    final int mutationGeneratingCount = 1000;
    final int crossoverGeneratingCount = 2000;
    final int headcount = 3000;
    final int maxGeneration = 4000;
    final int requiredSolutionsCount = 5000;
    final Duration timeLimit = Duration.ofSeconds(180000);
    final Duration testTimeLimit = Duration.ofSeconds(300);
    final Scope.Type scope = Scope.Type.PROJECT;

    final Path configPath = rootDir.resolve("withBasicParameters.toml");
    final String[] args = {
        // first, specify config file file
        "--config", configPath.toString(),
        // then, try to overwrite all parameters by cmd args
        "--random-seed", String.valueOf(randomSeed),
        "--mutation-generating-count", String.valueOf(mutationGeneratingCount),
        "--crossover-generating-count", String.valueOf(crossoverGeneratingCount),
        "--headcount", String.valueOf(headcount),
        "--max-generation", String.valueOf(maxGeneration),
        "--required-solutions", String.valueOf(requiredSolutionsCount),
        "--time-limit", String.valueOf(timeLimit.getSeconds()),
        "--test-time-limit", String.valueOf(testTimeLimit.getSeconds()),
        "--scope", String.valueOf(scope),
    };

    final Configuration config = Builder.buildFromCmdLineArgs(args);

    // parameters specified in config file should be overwritten by cmd args
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTestTimeLimit()).isEqualTo(testTimeLimit);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config).isEqualToRecursivelyIgnoringGivenFields(defaultConfig,
        "randomSeed", "mutationGeneratingCount", "crossoverGeneratingCount", "headcount",
        "maxGeneration", "requiredSolutionsCount", "timeLimit", "testTimeLimit", "scope");
  }

  // todo: 引数がなかった場合の挙動を確かめるために，カレントディレクトリを変更した上でテスト実行
}
