package jp.kusumotolab.kgenprog;

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
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.Configuration.Builder;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class ConfigurationBuilderTest {

  private final Path rootDir = Paths.get("example/BuildSuccess08");
  private final Path productPath = rootDir.resolve("src");
  private final Path testPath = rootDir.resolve("test");
  private final List<Path> productPaths = ImmutableList.of(productPath);
  private final List<Path> testPaths = ImmutableList.of(testPath);

  @Test
  public void testBuild() {
    final Builder builder = new Builder(rootDir, productPaths, testPaths);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithHeadcount() {
    final int headcount = 50;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setHeadcount(headcount);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithTimeLimit() {
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setTimeLimit(timeLimit);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimitSeconds));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitSeconds);
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevel() {
    final Level logLevel = Level.DEBUG;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel(logLevel);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevelString() {
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel("DEBUG");
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithClassPath() {
    final List<Path> classPaths = ImmutableList.of(rootDir.resolve("lib"));
    final Builder builder = new Builder(rootDir, productPaths, testPaths).addClassPaths(classPaths);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, classPaths, JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithRandomSeed() {
    final long randomSeed = 10;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setRandomSeed(randomSeed);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithScope() {
    final Scope.Type scope = Type.FILE;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setScope(scope);
    final Configuration config = builder.build();

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(needNotOutput);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgs() {
    final String[] args =
        {"-r", rootDir.toString(), "-s", productPath.toString(), "-t", testPath.toString(),};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);

    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.ERROR);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithScope() {
    final Scope.Type scope = Type.FILE;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "--scope", scope.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(scope);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isTrue();

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);

    final int mutationGeneratingCount = 50;
    assertThat(config.getMutationGeneratingCount()).isEqualTo(mutationGeneratingCount);

    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);

    final int crossoverGeneratingCount = 50;
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(crossoverGeneratingCount);

    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);

    final int headCount = 50;
    assertThat(config.getHeadcount()).isEqualTo(headCount);

    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);

    final int maxGeneration = 50;
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);

    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);

    final long timeLimit = 50;
    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimit));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit);

    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());

    final long testTimeLimit = 50;
    assertThat(config.getTestTimeLimit()).isEqualTo(Duration.ofSeconds(testTimeLimit));
    assertThat(config.getTestTimeLimitSeconds()).isEqualTo(testTimeLimit);

    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());

    final int requiredSolutionsCount = 50;
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);

    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);

    final Level logLevel = Level.DEBUG;
    assertThat(config.getLogLevel()).isEqualTo(logLevel);

    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);

    final int randomSeed = 50;
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);

    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final Scope.Type scope = Type.FILE;
    assertThat(config.getScope()).isEqualTo(scope);

    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final String executionTest1 = "example.FooTest";
    final String executionTest2 = "example.BarTest";
    assertThat(config.getExecutedTests()).containsExactlyInAnyOrder(executionTest1, executionTest2);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);

    final boolean needNotOutput = true;
    assertThat(config.needNotOutput()).isEqualTo(needNotOutput);

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

    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);

    final int mutationGeneratingCountFromConfigFile = 50;
    assertThat(config.getMutationGeneratingCount()).isNotEqualTo(
        mutationGeneratingCountFromConfigFile);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        mutationGeneratingCountFromCmdLine);

    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);

    final int crossoverGeneratingCountFromConfigFile = 50;
    assertThat(config.getCrossoverGeneratingCount()).isNotEqualTo(
        crossoverGeneratingCountFromConfigFile);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        crossoverGeneratingCountFromCmdLine);

    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);

    final int headCountFromConfigFile = 50;
    assertThat(config.getHeadcount()).isNotEqualTo(headCountFromConfigFile);
    assertThat(config.getHeadcount()).isEqualTo(headCountFromCmdLine);

    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);

    final int maxGenerationFromConfigFile = 50;
    assertThat(config.getMaxGeneration()).isNotEqualTo(maxGenerationFromConfigFile);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGenerationFromCmdLine);

    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);

    final long timeLimitFromConfigFile = 50;
    assertThat(config.getTimeLimit()).isNotEqualTo(Duration.ofSeconds(timeLimitFromConfigFile));
    assertThat(config.getTimeLimitSeconds()).isNotEqualTo(timeLimitFromConfigFile);
    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimitFromCmdLine));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitFromCmdLine);

    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());

    final long testTimeLimitFromConfigFile = 50;
    assertThat(config.getTestTimeLimit()).isNotEqualTo(
        Duration.ofSeconds(testTimeLimitFromConfigFile));
    assertThat(config.getTestTimeLimitSeconds()).isNotEqualTo(testTimeLimitFromConfigFile);
    assertThat(config.getTestTimeLimit()).isEqualTo(
        Duration.ofSeconds(testTimeLimitFromCmdLine));
    assertThat(config.getTestTimeLimitSeconds()).isEqualTo(testTimeLimitFromCmdLine);

    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());

    final int requiredSolutionsCountFromConfigFile = 50;
    assertThat(config.getRequiredSolutionsCount()).isNotEqualTo(
        requiredSolutionsCountFromConfigFile);
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCountFromCmdLine);

    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);

    final Level logLevelFromConfigFile = Level.DEBUG;
    assertThat(config.getLogLevel()).isNotEqualTo(logLevelFromConfigFile);
    assertThat(config.getLogLevel()).isEqualTo(logLevelFromCmdLine);

    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);

    final int randomSeedFromConfigFile = 50;
    assertThat(config.getRandomSeed()).isNotEqualTo(randomSeedFromConfigFile);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeedFromCmdLine);

    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, productPaths, testPaths, Collections.emptyList(),
            JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromConfigFileWithScopeOverwrittenFromCmdLineArgs() {
    final Path configPath = rootDir.resolve("withScope.toml");
    final Scope.Type scopeFromCmdLine = Type.PROJECT;
    final String[] args = {"--config", configPath.toString(), "--scope",
        scopeFromCmdLine.toString()};
    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final Scope.Type scopeFromConfigFile = Type.FILE;
    assertThat(config.getScope()).isNotEqualTo(scopeFromConfigFile);
    assertThat(config.getScope()).isEqualTo(scopeFromCmdLine);

    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final String executionTestFromConfigFile1 = "example.FooTest";
    final String executionTestFromConfigFile2 = "example.BarTest";
    assertThat(config.getExecutedTests()).doesNotContain(executionTestFromConfigFile1,
        executionTestFromConfigFile2);
    assertThat(config.getExecutedTests()).contains(executionTestFromCmdLine);

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);

    final boolean needNotOutputFromConfigFile = false;
    assertThat(config.needNotOutput()).isNotEqualTo(needNotOutputFromConfigFile);
    assertThat(config.needNotOutput()).isTrue();

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

    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getMutationGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_MUTATION_GENERATING_COUNT);
    assertThat(config.getCrossoverGeneratingCount()).isEqualTo(
        Configuration.DEFAULT_CROSSOVER_GENERATING_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getTestTimeLimit()).isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT);
    assertThat(config.getTestTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);
    assertThat(config.getScope()).isEqualTo(Configuration.DEFAULT_SCOPE);
    assertThat(config.needNotOutput()).isEqualTo(Configuration.DEFAULT_NEED_NOT_OUTPUT);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir,
        ImmutableList.of(link), testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
    assertThat(out.toString()).contains("symbolic link may not be resolved:");

    Files.delete(link);
    System.setOut(printStream);
  }

  // todo: 引数がなかった場合の挙動を確かめるために，カレントディレクトリを変更した上でテスト実行
}
