package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.Configuration.Builder;
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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithWorkingDir() {
    final Path workingDir = rootDir.resolve("work");
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setWorkingDir(workingDir);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(workingDir);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithOutDir() {
    final Path outDir = rootDir.resolve("out");
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setOutDir(outDir);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(outDir);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithSiblingsCount() {
    final int siblingsCount = 50;
    final Builder builder =
        new Builder(rootDir, productPaths, testPaths).setSiblingsCount(siblingsCount);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(siblingsCount);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithHeadcount() {
    final int headcount = 50;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setHeadcount(headcount);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithTimeLimit() {
    final Duration timeLimit = Duration.ofSeconds(1800);
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setTimeLimit(timeLimit);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Duration.ofSeconds(timeLimitSeconds));
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimitSeconds);
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevel() {
    final Level logLevel = Level.DEBUG;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel(logLevel);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(logLevel);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithLogLevelString() {
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setLogLevel("DEBUG");
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithClassPath() {
    final List<Path> classPaths = ImmutableList.of(rootDir.resolve("lib"));
    final Builder builder = new Builder(rootDir, productPaths, testPaths).addClassPaths(classPaths);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, classPaths, JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildWithRandomSeed() {
    final long randomSeed = 10;
    final Builder builder = new Builder(rootDir, productPaths, testPaths).setRandomSeed(randomSeed);
    final Configuration config = builder.build();

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);

    final TargetProject expectedProject = TargetProjectFactory.create(rootDir, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgs() {
    final String[] args =
        {"-r", rootDir.toString(), "-s", productPath.toString(), "-t", testPath.toString(),};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithWorkingDir() {
    final Path workingDir = rootDir.resolve("work");
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-w", workingDir.toString()};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(workingDir);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(outDir);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithSiblingsCount() {
    final int siblingsCount = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-i", Integer.toString(siblingsCount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(siblingsCount);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithHeadcount() {
    final int headcount = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-h", Integer.toString(headcount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(headcount);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithMaxGeneration() {
    final int maxGeneration = 50;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-g", Integer.toString(maxGeneration)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(maxGeneration);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithTimeLimit() {
    final Duration timeLimit = Duration.ofSeconds(1800);
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-l", Long.toString(timeLimit.getSeconds())};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(timeLimit);
    assertThat(config.getTimeLimitSeconds()).isEqualTo(timeLimit.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithRequiredSolutionsCount() {
    final int requiredSolutionsCount = 10;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-e", Integer.toString(requiredSolutionsCount)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount()).isEqualTo(requiredSolutionsCount);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.DEBUG);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Level.ERROR);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

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

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(Configuration.DEFAULT_RANDOM_SEED);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), ImmutableList.of(classPath), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
  }

  @Test
  public void testBuildFromCmdLineArgsWithRandomSeed() {
    final long randomSeed = 10;
    final String[] args = {"-r", rootDir.toString(), "-s", productPath.toString(), "-t",
        testPath.toString(), "-a", Long.toString(randomSeed)};
    final Configuration config = Builder.buildFromCmdLineArgs(args);

    assertThat(config.getWorkingDir()).isEqualTo(Configuration.DEFAULT_WORKING_DIR);
    assertThat(config.getOutDir()).isEqualTo(Configuration.DEFAULT_OUT_DIR);
    assertThat(config.getSiblingsCount()).isEqualTo(Configuration.DEFAULT_SIBLINGS_COUNT);
    assertThat(config.getHeadcount()).isEqualTo(Configuration.DEFAULT_HEADCOUNT);
    assertThat(config.getMaxGeneration()).isEqualTo(Configuration.DEFAULT_MAX_GENERATION);
    assertThat(config.getTimeLimit()).isEqualTo(Configuration.DEFAULT_TIME_LIMIT);
    assertThat(config.getTimeLimitSeconds())
        .isEqualTo(Configuration.DEFAULT_TIME_LIMIT.getSeconds());
    assertThat(config.getRequiredSolutionsCount())
        .isEqualTo(Configuration.DEFAULT_REQUIRED_SOLUTIONS_COUNT);
    assertThat(config.getLogLevel()).isEqualTo(Configuration.DEFAULT_LOG_LEVEL);
    assertThat(config.getRandomSeed()).isEqualTo(randomSeed);

    final TargetProject expectedProject =
        TargetProjectFactory.create(rootDir, ImmutableList.of(productPath),
            ImmutableList.of(testPath), Collections.emptyList(), JUnitVersion.JUNIT4);
    assertThat(config.getTargetProject()).isEqualTo(expectedProject);
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
    assertThat(out.toString()
        .trim()).isNotEmpty();

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
    assertThat(out.toString()
        .trim()).isEmpty();

    System.setOut(printStream);
  }
}
