package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.InvalidValueException;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class Configuration {

  // region Fields
  public static final int DEFAULT_MAX_GENERATION = 10;
  public static final int DEFAULT_MUTATION_GENERATING_COUNT = 10;
  public static final int DEFAULT_CROSSOVER_GENERATING_COUNT = 10;
  public static final int DEFAULT_HEADCOUNT = 100;
  public static final int DEFAULT_REQUIRED_SOLUTIONS_COUNT = 1;
  public static final Duration DEFAULT_TIME_LIMIT = Duration.ofSeconds(60);
  public static final Duration DEFAULT_TEST_TIME_LIMIT = Duration.ofSeconds(10);
  public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
  public static final Path DEFAULT_WORKING_DIR;
  public static final Path DEFAULT_OUT_DIR = Paths.get("kgenprog-out");
  public static final long DEFAULT_RANDOM_SEED = 0;
  public static final boolean DEFAULT_NEED_NOT_OUTPUT = false;

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
  private final int mutationGeneratingCount;
  private final int crossoverGeneratingCount;
  private final int headcount;
  private final int maxGeneration;
  private final Duration timeLimit;
  private final Duration testTimeLimit;
  private final int requiredSolutionsCount;
  private final Level logLevel;
  private final long randomSeed;
  private final boolean needNotOutput;
  // endregion

  // region Constructor

  private Configuration(final Builder builder) {
    targetProject = builder.targetProject;
    executionTests = builder.executionTests;
    workingDir = builder.workingDir;
    outDir = builder.outDir;
    mutationGeneratingCount = builder.mutationGeneratingCount;
    crossoverGeneratingCount = builder.crossoverGeneratingCount;
    headcount = builder.headcount;
    maxGeneration = builder.maxGeneration;
    timeLimit = builder.timeLimit;
    testTimeLimit = builder.testTimeLimit;
    requiredSolutionsCount = builder.requiredSolutionsCount;
    logLevel = builder.logLevel;
    randomSeed = builder.randomSeed;
    needNotOutput = builder.needNotOutput;
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

  public int getMutationGeneratingCount() {
    return mutationGeneratingCount;
  }

  public int getCrossoverGeneratingCount() {
    return crossoverGeneratingCount;
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

  public long getTestTimeLimitSeconds() {
    return getTestTimeLimit().getSeconds();
  }

  public Duration getTestTimeLimit() {
    return testTimeLimit;
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

  public boolean needNotOutput() {
    return needNotOutput;
  }

  public static class Builder {

    // region Fields

    private static transient final Logger log = LoggerFactory.getLogger(Builder.class);

    @PreserveNotNull
    private Path configPath = Paths.get("kgenprog.toml");

    @com.electronwill.nightconfig.core.conversion.Path("root-dir")
    @SpecNotNull
    @Conversion(PathToString.class)
    private Path rootDir;

    @com.electronwill.nightconfig.core.conversion.Path("src")
    @SpecNotNull
    @Conversion(PathsToStrings.class)
    private List<Path> productPaths;

    @com.electronwill.nightconfig.core.conversion.Path("test")
    @SpecNotNull
    @Conversion(PathsToStrings.class)
    private List<Path> testPaths;

    @com.electronwill.nightconfig.core.conversion.Path("cp")
    @PreserveNotNull
    @Conversion(PathsToStrings.class)
    private List<Path> classPaths = new ArrayList<>();

    @com.electronwill.nightconfig.core.conversion.Path("exec-test")
    @PreserveNotNull
    private final List<String> executionTests = new ArrayList<>();

    private transient TargetProject targetProject;

    @com.electronwill.nightconfig.core.conversion.Path("working-dir")
    @PreserveNotNull
    @Conversion(PathToString.class)
    private Path workingDir = DEFAULT_WORKING_DIR;

    @com.electronwill.nightconfig.core.conversion.Path("out-dir")
    @PreserveNotNull
    @Conversion(PathToString.class)
    private Path outDir = DEFAULT_OUT_DIR;

    @com.electronwill.nightconfig.core.conversion.Path("mutation-generating-count")
    @PreserveNotNull
    private int mutationGeneratingCount = DEFAULT_MUTATION_GENERATING_COUNT;

    @com.electronwill.nightconfig.core.conversion.Path("crossover-generating-count")
    @PreserveNotNull
    private int crossoverGeneratingCount = DEFAULT_CROSSOVER_GENERATING_COUNT;

    @PreserveNotNull
    private int headcount = DEFAULT_HEADCOUNT;

    @com.electronwill.nightconfig.core.conversion.Path("max-generation")
    @PreserveNotNull
    private int maxGeneration = DEFAULT_MAX_GENERATION;

    @com.electronwill.nightconfig.core.conversion.Path("time-limit")
    @PreserveNotNull
    @Conversion(DurationToInteger.class)
    private Duration timeLimit = DEFAULT_TIME_LIMIT;

    @com.electronwill.nightconfig.core.conversion.Path("test-time-limit")
    @PreserveNotNull
    @Conversion(DurationToInteger.class)
    private Duration testTimeLimit = DEFAULT_TEST_TIME_LIMIT;

    @com.electronwill.nightconfig.core.conversion.Path("required-solutions")
    @PreserveNotNull
    private int requiredSolutionsCount = DEFAULT_REQUIRED_SOLUTIONS_COUNT;

    @com.electronwill.nightconfig.core.conversion.Path("log-level")
    @PreserveNotNull
    @Conversion(LevelToString.class)
    private Level logLevel = DEFAULT_LOG_LEVEL;

    @com.electronwill.nightconfig.core.conversion.Path("random-seed")
    @PreserveNotNull
    private long randomSeed = DEFAULT_RANDOM_SEED;

    @Option(name = "--no-output", hidden = true)
    @com.electronwill.nightconfig.core.conversion.Path("no-output")
    @PreserveNotNull
    private boolean needNotOutput = DEFAULT_NEED_NOT_OUTPUT;

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

    /**
     * Do not call me except from buildFromCmdLineArgs
     */
    private Builder() {
      productPaths = new ArrayList<>();
      testPaths = new ArrayList<>();
    }

    // endregion

    // region Methods

    public static Configuration buildFromCmdLineArgs(final String[] args) {
      log.debug("enter buildFromCmdLineArgs(String[])");

      final Builder builder = new Builder();
      final CmdLineParser parser = new CmdLineParser(builder);

      try {
        parser.parseArgument(args);

        if (needsParseConfigFile(args)) {
          builder.parseConfigFile();
        }

        validateArgument(builder);
      } catch (final CmdLineException | IllegalArgumentException | InvalidValueException
          | NoSuchFileException e) {
        // todo: make error message of InvalidValueException more user-friendly
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

    public Builder setMutationGeneratingCount(final int mutationGeneratingCount) {
      log.debug("enter setMutationGeneratingCount(int)");

      this.mutationGeneratingCount = mutationGeneratingCount;
      return this;
    }

    public Builder setCrossoverGeneratingCount(final int crossoverGeneratingCount) {
      log.debug("enter setCrossoverGeneratingCount(int)");

      this.crossoverGeneratingCount = crossoverGeneratingCount;
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

    public Builder setTestTimeLimitSeconds(final long testTimeLimitSeconds) {
      log.debug("enter setTestTimeLimitSeconds(long)");

      this.testTimeLimit = Duration.ofSeconds(testTimeLimitSeconds);
      return this;
    }

    public Builder setTestTimeLimit(final Duration testTimeLimit) {
      log.debug("enter setTestTimeLimit(Duration)");

      this.testTimeLimit = testTimeLimit;
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

    public Builder setNeedNotOutput(final boolean needNotOutput) {
      log.debug("enter setNeedNotOutput(boolean)");

      this.needNotOutput = needNotOutput;
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

    private static boolean needsParseConfigFile(final String[] args) {
      return args.length == 0 || Arrays.asList(args)
          .contains("--config");
    }

    private void parseConfigFile() throws InvalidValueException, NoSuchFileException {
      if (Files.notExists(configPath)) {
        throw new NoSuchFileException("config file \"" + configPath.toAbsolutePath()
            .toString() + "\" is not found.");
      }

      try (final FileConfig config = FileConfig.of(configPath)) {
        config.load();

        log.debug("config = {}", config);

        final ObjectConverter converter = new ObjectConverter();
        converter.toObject(config, this);
        resolvePaths();
      }
    }

    private void resolvePaths() {
      final Path configDir = getParent(configPath, Paths.get("."));

      rootDir = configDir.resolve(rootDir)
          .normalize();
      productPaths = productPaths.stream()
          .map(p -> configDir.resolve(p)
              .normalize())
          .collect(Collectors.toList());
      testPaths = testPaths.stream()
          .map(p -> configDir.resolve(p)
              .normalize())
          .collect(Collectors.toList());
      classPaths = classPaths.stream()
          .map(p -> configDir.resolve(p)
              .normalize())
          .collect(Collectors.toList());
      workingDir = configDir.resolve(workingDir)
          .normalize();

      if (!outDir.equals(DEFAULT_OUT_DIR)) {
        outDir = configDir.resolve(outDir)
            .normalize();
      }
    }

    private Path getParent(final Path path, final Path defaultPath) {
      return path.getParent() != null ? path.getParent() : defaultPath;
    }

    // endregion

    // region Methods for CmdLineParser

    @Option(name = "--config", metaVar = "<path>", usage = "Specifies the path to the config file.",
        forbids = {"-r", "-s", "-t"})
    private void setConfigPathFromCmdLineParser(final String configPath) {
      log.debug("enter setConfigPathFromCmdLineParser(String)");
      this.configPath = Paths.get(configPath);
    }

    @Option(name = "-r", aliases = "--root-dir", metaVar = "<path>",
        usage = "Specifies the path to the root directory of the target project.",
        depends = {"-s", "-t"}, forbids = {"--config"})
    private void setRootDirFromCmdLineParser(final String rootDir) {
      log.debug("enter setRootDirFromCmdLineParser(String)");
      this.rootDir = Paths.get(rootDir);
    }

    @Option(name = "-s", aliases = "--src", metaVar = "<path> ...",
        usage = " Specifies paths to \"product\" source code (i.e. main, non-test code),"
            + " or to directories containing them.",
        depends = {"-r", "-t"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addProductPathFromCmdLineParser(final String sourcePath) {
      log.debug("enter addProductPathFromCmdLineParser(String)");
      this.productPaths.add(Paths.get(sourcePath));
    }

    @Option(name = "-t", aliases = "--test", metaVar = "<path> ...",
        usage = "Specifies paths to test source code, or to directories containing them.",
        depends = {"-r", "-s"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addTestPathFromCmdLineParser(final String testPath) {
      log.debug("enter addTestPathFromCmdLineParser(String)");
      this.testPaths.add(Paths.get(testPath));
    }

    @Option(name = "-c", aliases = "--cp", metaVar = "<class path> ...",
        usage = "Specifies class paths needed to build the target project.",
        depends = {"-r", "-s", "-t"}, handler = StringArrayOptionHandler.class)
    private void addClassPathFromCmdLineParser(final String classPath) {
      log.debug("enter addClassPathFromCmdLineParser(String)");
      this.classPaths.add(Paths.get(classPath));
    }

    @Option(name = "-x", aliases = "--exec-test", metaVar = "<fqn> ...",
        usage = "Specifies fully qualified names of test classes executed"
            + " during evaluation of variants (i.e. fix-candidates).",
        depends = {"-r", "-s", "-t"}, handler = StringArrayOptionHandler.class)
    private void addExecutionTestFromCmdLineParser(final String executionTest) {
      log.debug("enter addExecutionTestFromCmdLineParser(String)");
      this.executionTests.add(executionTest);
    }

    @Option(name = "-w", aliases = "--working-dir", metaVar = "<path>",
        usage = "Specifies the path to working directory.", depends = {"-r", "-s", "-t"})
    private void setWorkingDirFromCmdLineParser(final String workingDir) {
      log.debug("enter setWorkingDirFromCmdLineParser(String)");
      this.workingDir = Paths.get(workingDir);
    }

    @Option(name = "-o", aliases = "--out-dir", metaVar = "<path>",
        usage = "Writes patches kGenProg generated to the specified directory.",
        depends = {"-r", "-s", "-t"})
    private void setOutDirFromCmdLineParser(final String outDir) {
      log.debug("enter setOutDirFromCmdLineParser(String)");
      this.outDir = Paths.get(outDir);
    }

    @Option(name = "--mutation-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a mutation.",
        depends = {"-r", "-s", "-t"})
    private void setMutationGeneratingCountFromCmdLineParser(final int mutationGeneratingCount) {
      log.debug("enter setMutationGeneratingCountFromCmdLineParser(int)");
      this.mutationGeneratingCount = mutationGeneratingCount;
    }

    @Option(name = "--crossover-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a crossover.",
        depends = {"-r", "-s", "-t"})
    private void setCrossOverGeneratingCountFromCmdLineParser(final int crossoverGeneratingCount) {
      log.debug("enter setCrossOverGeneratingCountFromCmdLineParser(int)");
      this.crossoverGeneratingCount = crossoverGeneratingCount;
    }

    @Option(name = "--headcount", metaVar = "<num>",
        usage = "Specifies how many variants survive in a generation.",
        depends = {"-r", "-s", "-t"})
    private void setHeadcountFromCmdLineParser(final int headcount) {
      log.debug("enter setHeadcountFromCmdLineParser(int)");
      this.headcount = headcount;
    }

    @Option(name = "--max-generation", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of generations reached.",
        depends = {"-r", "-s", "-t"})
    private void setMaxGenerationFromCmdLineParser(final int maxGeneration) {
      log.debug("enter setMaxGenerationFromCmdLineParser(int)");
      this.maxGeneration = maxGeneration;
    }

    @Option(name = "--time-limit", metaVar = "<sec>",
        usage = "Terminates searching solutions when the specified time has passed.",
        depends = {"-r", "-s", "-t"})
    private void setTimeLimitFromCmdLineParser(final long timeLimit) {
      log.debug("enter setTimeLimitFromCmdLineParser(long)");
      this.timeLimit = Duration.ofSeconds(timeLimit);
    }

    // todo update usage
    @Option(name = "--test-time-limit", metaVar = "<sec>",
        usage = "Specifies time limit to build and test for each variant in second",
        depends = {"-r", "-s", "-t"})
    private void setTestTimeLimitFromCmdLineParser(final long testTimeLimit) {
      log.debug("enter setTestTimeLimitFromCmdLineParser(long)");
      this.testTimeLimit = Duration.ofSeconds(testTimeLimit);
    }

    @Option(name = "--required-solutions", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of solutions are found.",
        depends = {"-r", "-s", "-t"})
    private void setRequiredSolutionsCountFromCmdLineParser(final int requiredSolutionsCount) {
      log.debug("enter setTimeLimitFromCmdLineParser(int)");
      this.requiredSolutionsCount = requiredSolutionsCount;
    }

    @Option(name = "-v", aliases = "--verbose",
        usage = "Be more verbose, printing DEBUG level logs.", depends = {"-r", "-s", "-t"})
    private void setLogLevelDebugFromCmdLineParser(final boolean isVerbose) {
      log.debug("enter setLogLevelDebugFromCmdLineParser(boolean)");
      log.debug("log level has been set DEBUG");
      logLevel = Level.DEBUG;
    }

    @Option(name = "-q", aliases = "--quiet", usage = "Be more quiet, suppressing non-ERROR logs.",
        depends = {"-r", "-s", "-t"})
    private void setLogLevelErrorFromCmdLineParser(final boolean isQuiet) {
      log.debug("enter setLogLevelErrorFromCmdLineParser(boolean)");
      log.debug("log level has been set ERROR");
      logLevel = Level.ERROR;
    }

    @Option(name = "--random-seed", metaVar = "<num>",
        usage = "Specifies random seed used by random number generator.",
        depends = {"-r", "-s", "-t"})
    private void setRandomSeedFromCmdLineParser(final long randomSeed) {
      log.debug("enter setRandomSeedFromCmdLineParser(long)");
      this.randomSeed = randomSeed;
    }

    // endregion

    private static class PathToString implements Converter<Path, String> {

      @Override
      public Path convertToField(final String value) {
        if (value == null) {
          return null;
        }

        return Paths.get(value);
      }

      @Override
      public String convertFromField(final Path value) {
        if (value == null) {
          return null;
        }

        return value.toString();
      }
    }

    private static class PathsToStrings implements Converter<List<Path>, List<String>> {

      @Override
      public List<Path> convertToField(final List<String> value) {
        if (value == null) {
          return null;
        }

        return value.stream()
            .map(Paths::get)
            .collect(Collectors.toList());
      }

      @Override
      public List<String> convertFromField(final List<Path> value) {
        if (value == null) {
          return null;
        }

        return value.stream()
            .map(Path::toString)
            .collect(Collectors.toList());
      }
    }

    private static class LevelToString implements Converter<Level, String> {

      @Override
      public Level convertToField(final String value) {
        if (value == null) {
          return null;
        }

        return Level.toLevel(value);
      }

      @Override
      public String convertFromField(final Level value) {
        if (value == null) {
          return null;
        }

        return value.levelStr;
      }
    }

    private static class DurationToInteger implements Converter<Duration, Integer> {

      // todo: make it possible to take minutes and hours etc.
      @Override
      public Duration convertToField(final Integer value) {
        if (value == null) {
          return null;
        }

        return Duration.ofSeconds(value);
      }

      @Override
      public Integer convertFromField(final Duration value) {
        if (value == null) {
          return null;
        }

        return (int) value.getSeconds();
      }
    }
  }
}

