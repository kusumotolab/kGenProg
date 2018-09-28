package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class Configuration {

  // region Fields
  public static final int DEFAULT_MAX_GENERATION = 10;
  public static final int DEFAULT_SIBLINGS_COUNT = 10;
  public static final int DEFAULT_HEADCOUNT = 100;
  public static final int DEFAULT_REQUIRED_SOLUTIONS_COUNT = 1;
  public static final Duration DEFAULT_TIME_LIMIT = Duration.ofSeconds(60);
  public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
  public static final Path DEFAULT_WORKING_DIR;
  public static final Path DEFAULT_OUT_DIR = Paths.get("kgenprog-out");
  public static final long DEFAULT_RANDOM_SEED = 0;

  static {
    try {
      DEFAULT_WORKING_DIR = Files.createTempDirectory("kgenprog-work");
    } catch (final IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Creating a temporary directory has failed.");
    }
  }

  private final TargetProject targetProject;
  private final List<String> executionTests;
  private final Path workingDir;
  private final Path outDir;
  private final int siblingsCount;
  private final int headcount;
  private final int maxGeneration;
  private final Duration timeLimit;
  private final int requiredSolutionsCount;
  private final Level logLevel;
  private final long randomSeed;
  // endregion

  // region Constructor

  private Configuration(final Builder builder) {
    targetProject = builder.targetProject;
    executionTests = builder.executionTests;
    workingDir = builder.workingDir;
    outDir = builder.outDir;
    siblingsCount = builder.siblingsCount;
    headcount = builder.headcount;
    maxGeneration = builder.maxGeneration;
    timeLimit = builder.timeLimit;
    requiredSolutionsCount = builder.requiredSolutionsCount;
    logLevel = builder.logLevel;
    randomSeed = builder.randomSeed;
  }

  // endregion

  public TargetProject getTargetProject() {
    return targetProject;
  }

  public List<String> getExecutedTests() {
    return executionTests;
  }

  public Path getWorkingDir() {
    return workingDir;
  }

  public Path getOutDir() {
    return outDir;
  }

  public int getSiblingsCount() {
    return siblingsCount;
  }

  public int getHeadcount() {
    return headcount;
  }

  public int getMaxGeneration() {
    return maxGeneration;
  }

  public long getTimeLimitSeconds() {
    return getTimeLimit().getSeconds();
  }

  public Duration getTimeLimit() {
    return timeLimit;
  }

  public int getRequiredSolutionsCount() {
    return requiredSolutionsCount;
  }

  public Level getLogLevel() {
    return logLevel;
  }

  public long getRandomSeed() {
    return randomSeed;
  }

  public static class Builder {

    // region Fields
    private static final Logger log = LoggerFactory.getLogger(Builder.class);
    private Path rootDir;
    private List<Path> productPaths = new ArrayList<>();
    private List<Path> testPaths = new ArrayList<>();
    private List<Path> classPaths = new ArrayList<>();
    private List<String> executionTests = new ArrayList<>();
    private TargetProject targetProject;
    private Path workingDir = DEFAULT_WORKING_DIR;
    private Path outDir = DEFAULT_OUT_DIR;
    private int siblingsCount = DEFAULT_SIBLINGS_COUNT;
    private int headcount = DEFAULT_HEADCOUNT;
    private int maxGeneration = DEFAULT_MAX_GENERATION;
    private Duration timeLimit = DEFAULT_TIME_LIMIT;
    private int requiredSolutionsCount = DEFAULT_REQUIRED_SOLUTIONS_COUNT;
    private Level logLevel = DEFAULT_LOG_LEVEL;
    private long randomSeed = DEFAULT_RANDOM_SEED;
    // endregion

    // region Constructors

    public Builder(final Path rootDir, final Path productPath, final Path testPath) {
      this(rootDir, ImmutableList.of(productPath), ImmutableList.of(testPath));
    }

    public Builder(final Path rootDir, final List<Path> productPaths, final List<Path> testPaths) {
      this.rootDir = rootDir;
      this.productPaths = productPaths;
      this.testPaths = testPaths;
    }

    public Builder(final TargetProject targetProject) {
      this.targetProject = targetProject;
    }

    private Builder() {
      // do nothing
      // do not call me except from buildFromCmdLineArgs
    }

    // endregion

    // region Methods

    public static Configuration buildFromCmdLineArgs(final String[] args) {
      log.debug("enter buildFromCmdLineArgs(String[])");

      final Builder builder = new Builder();
      final CmdLineParser parser = new CmdLineParser(builder);

      try {
        parser.parseArgument(args);
        validateArgument(builder);
      } catch (final CmdLineException | IllegalArgumentException e) {
        log.error(e.getMessage());
        parser.printUsage(System.err);
        System.exit(1);
      }

      return builder.build();
    }

    public Configuration build() {
      log.debug("enter build()");

      if (targetProject == null) {
        targetProject = TargetProjectFactory.create(rootDir, productPaths, testPaths, classPaths,
            JUnitVersion.JUNIT4);
      }

      return new Configuration(this);
    }

    public Builder addClassPaths(final Collection<Path> classPaths) {
      log.debug("enter addClassPaths(Collection)");

      this.classPaths.addAll(classPaths);
      return this;
    }

    public Builder addClassPath(final Path classPath) {
      log.debug("enter addClassPath(Path)");

      this.classPaths.add(classPath);
      return this;
    }

    public Builder setWorkingDir(final Path workingDir) {
      log.debug("enter setWorkingDir(Path)");

      this.workingDir = workingDir;
      return this;
    }

    public Builder setOutDir(final Path outDir) {
      log.debug("enter setOutDir(Path)");

      this.outDir = outDir;
      return this;
    }

    public Builder setSiblingsCount(final int siblingsCount) {
      log.debug("enter setSiblingsCount(int)");

      this.siblingsCount = siblingsCount;
      return this;
    }

    public Builder setHeadcount(final int headcount) {
      log.debug("enter setHeadcount(int)");

      this.headcount = headcount;
      return this;
    }

    public Builder setMaxGeneration(final int maxGeneration) {
      log.debug("enter setMaxGeneration(int)");

      this.maxGeneration = maxGeneration;
      return this;
    }

    public Builder setTimeLimitSeconds(final long timeLimitSeconds) {
      log.debug("enter setTimeLimitSeconds(long)");

      this.timeLimit = Duration.ofSeconds(timeLimitSeconds);
      return this;
    }

    public Builder setTimeLimit(final Duration timeLimit) {
      log.debug("enter setTimeLimit(Duration)");

      this.timeLimit = timeLimit;
      return this;
    }

    public Builder setRequiredSolutionsCount(final int requiredSolutionsCount) {
      log.debug("enter setRequiredSolutionsCount(int)");

      this.requiredSolutionsCount = requiredSolutionsCount;
      return this;
    }

    public Builder setLogLevel(final String logLevel) {
      log.debug("enter setLogLevel(String)");

      return setLogLevel(Level.toLevel(logLevel.toUpperCase(Locale.ROOT)));
    }

    public Builder setLogLevel(final Level logLevel) {
      log.debug("enter setLogLevel(Level)");

      this.logLevel = logLevel;
      return this;
    }

    public Builder setRandomSeed(final long randomSeed) {
      log.debug("enter setRandomSeed(long)");

      this.randomSeed = randomSeed;
      return this;
    }

    public Builder addExecutionTest(final String executionTest) {
      log.debug("enter addExecutionTest(String)");

      this.executionTests.add(executionTest);
      return this;
    }

    // endregion

    // region Private methods

    private static void validateArgument(final Builder builder) throws IllegalArgumentException {
      final Path currentDir = Paths.get(".");
      final Path projectRootDir = builder.rootDir;

      try {
        if (!isQuiet(builder) && !Files.isSameFile(currentDir, projectRootDir)) {
          log.warn(
              "The directory where kGenProg is running is different from the root directory of the given target project.");
          log.warn(
              "If the target project include test cases with file I/O, such test cases won't run correctly.");
          log.warn(
              "We recommend that you run kGenProg with the root directory of the target project as the current directory.");
        }
      } catch (final IOException e) {
        throw new IllegalArgumentException("directory " + projectRootDir + " is not accessible");
      }
    }

    private static boolean isQuiet(final Builder builder) {
      return builder.logLevel.equals(Level.ERROR);
    }

    // endregion

    // region Methods for CmdLineParser

    @Option(name = "-r", aliases = "--root-dir", required = true, metaVar = "<path>",
        usage = "Path of a root directory of a target project")
    private void setRootDirFromCmdLineParser(final String rootDir) {
      log.debug("enter setRootDirFromCmdLineParser(String)");
      this.rootDir = Paths.get(rootDir);
    }

    @Option(name = "-s", aliases = "--src", required = true,
        handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
        usage = "Paths of the root directories holding src codes")
    private void addProductPathFromCmdLineParser(final String sourcePath) {
      log.debug("enter addProductPathFromCmdLineParser(String)");
      this.productPaths.add(Paths.get(sourcePath));
    }

    @Option(name = "-t", aliases = "--test", required = true,
        handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
        usage = "Paths of the root directories holding test codes")
    private void addTestPathFromCmdLineParser(final String testPath) {
      log.debug("enter addTestPathFromCmdLineParser(String)");
      this.testPaths.add(Paths.get(testPath));
    }

    @Option(name = "-c", aliases = "--cp", handler = StringArrayOptionHandler.class,
        metaVar = "<class path> ...", usage = "Class paths required to build the target project")
    private void addClassPathFromCmdLineParser(final String classPath) {
      log.debug("enter addClassPathFromCmdLineParser(String)");
      this.classPaths.add(Paths.get(classPath));
    }

    @Option(name = "-x", aliases = "--exec-test", handler = StringArrayOptionHandler.class,
        metaVar = "<fqn> ...", usage = "Execution test cases.")
    private void addExecutionTestFromCmdLineParser(final String executionTest) {
      log.debug("enter addExecutionTestFromCmdLineParser(String)");
      this.executionTests.add(executionTest);
    }

    @Option(name = "-w", aliases = "--working-dir", metaVar = "<path>",
        usage = "Path of a working directory")
    private void setWorkingDirFromCmdLineParser(final String workingDir) {
      log.debug("enter setWorkingDirFromCmdLineParser(String)");
      this.workingDir = Paths.get(workingDir);
    }

    @Option(name = "-o", aliases = "--out-dir", metaVar = "<path>",
        usage = "Path of a output directory")
    private void setOutDirFromCmdLineParser(final String outDir) {
      log.debug("enter setOutDirFromCmdLineParser(String)");
      this.outDir = Paths.get(outDir);
    }

    @Option(name = "-i", aliases = "--siblings-count",
        usage = "The number of how many child variants are generated from a parent")
    private void setSiblingsCountFromCmdLineParser(final int siblingsCount) {
      log.debug("enter setSiblingsCountFromCmdLineParser(int)");
      this.siblingsCount = siblingsCount;
    }

    @Option(name = "-h", aliases = "--headcount",
        usage = "The number of how many variants are generated maximally in a generation")
    private void setHeadcountFromCmdLineParser(final int headcount) {
      log.debug("enter setHeadcountFromCmdLineParser(int)");
      this.headcount = headcount;
    }

    @Option(name = "-g", aliases = "--max-generation", usage = "Maximum generation")
    private void setMaxGenerationFromCmdLineParser(final int maxGeneration) {
      log.debug("enter setMaxGenerationFromCmdLineParser(int)");
      this.maxGeneration = maxGeneration;
    }

    @Option(name = "-l", aliases = "--time-limit", usage = "Time limit for repairing in second")
    private void setTimeLimitFromCmdLineParser(final long timeLimit) {
      log.debug("enter setTimeLimitFromCmdLineParser(long)");
      this.timeLimit = Duration.ofSeconds(timeLimit);
    }

    @Option(name = "-e", aliases = "--required-solutions",
        usage = "The number of solutions needed to be searched")
    private void setRequiredSolutionsCountFromCmdLineParser(final int requiredSolutionsCount) {
      log.debug("enter setTimeLimitFromCmdLineParser(int)");
      this.requiredSolutionsCount = requiredSolutionsCount;
    }

    @Option(name = "-v", aliases = "--verbose", usage = "Verbose mode. Print DEBUG level logs.")
    private void setLogLevelDebugFromCmdLineParser(final boolean isVerbose) {
      log.debug("enter setLogLevelDebugFromCmdLineParser(boolean)");
      log.debug("log level has been set DEBUG");
      logLevel = Level.DEBUG;
    }

    @Option(name = "-q", aliases = "--quiet", usage = "Quiet mode. Print ERROR level logs.")
    private void setLogLevelErrorFromCmdLineParser(final boolean isQuiet) {
      log.debug("enter setLogLevelErrorFromCmdLineParser(boolean)");
      log.debug("log level has been set ERROR");
      logLevel = Level.ERROR;
    }

    @Option(name = "-a", aliases = "--random-seed",
        usage = "The seed of a random seed generator used across this program")
    private void setRandomSeedFromCmdLineParser(final long randomSeed) {
      log.debug("enter setRandomSeedFromCmdLineParser(long)");
      this.randomSeed = randomSeed;
    }

    // endregion
  }
}

