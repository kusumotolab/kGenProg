package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.lang.reflect.Field;
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
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class Configuration {

  // region Fields
  public static final int DEFAULT_MAX_GENERATION = 10;
  public static final int DEFAULT_MUTATION_GENERATING_COUNT = 10;
  public static final int DEFAULT_CROSSOVER_GENERATING_COUNT = 10;
  public static final int DEFAULT_HEADCOUNT = 10;
  public static final int DEFAULT_REQUIRED_SOLUTIONS_COUNT = 1;
  public static final Duration DEFAULT_TIME_LIMIT = Duration.ofSeconds(60);
  public static final Duration DEFAULT_TEST_TIME_LIMIT = Duration.ofSeconds(10);
  public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
  public static final Path DEFAULT_WORKING_DIR;
  public static final Path DEFAULT_OUT_DIR = Paths.get("kgenprog-out");
  public static final long DEFAULT_RANDOM_SEED = 0;
  public static final Scope.Type DEFAULT_SCOPE = Type.PACKAGE;
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
  private final Scope.Type scope;
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
    scope = builder.scope;
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

  public Scope.Type getScope() {
    return scope;
  }

  public boolean needNotOutput() {
    return needNotOutput;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    final Class<?> clazz = this.getClass();
    for (final Field field : clazz.getDeclaredFields()) {
      try {
        field.setAccessible(true);
        final String name = field.getName();
        if (name.startsWith("DEFAULT_")) {
          continue;
        }
        sb.append(name + " = " + field.get(this) + System.lineSeparator());
      } catch (final IllegalAccessException e) {
        sb.append(field.getName() + " = " + "access denied" + System.lineSeparator());
      }
    }
    return sb.toString();
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

    @com.electronwill.nightconfig.core.conversion.Path("scope")
    @PreserveNotNull
    @Conversion(ScopeTypeToString.class)
    private Scope.Type scope = DEFAULT_SCOPE;

    @Option(name = "--no-output", usage = "Do not output anything.", hidden = true)
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
     * Do not call me except from {@link #buildFromCmdLineArgs}
     */
    private Builder() {
      productPaths = new ArrayList<>();
      testPaths = new ArrayList<>();
    }

    // endregion

    // region Methods

    public static Configuration buildFromCmdLineArgs(final String[] args)
        throws IllegalArgumentException {

      final Builder builder = new Builder();
      final CmdLineParser parser = new CmdLineParser(builder);

      try {
        parser.parseArgument(args);
        final List<String> executionTestsFromCmdLine = builder.executionTests;
        final List<Path> classPathsFromCmdLine = builder.classPaths;

        if (needsParseConfigFile(args)) {
          builder.parseConfigFile();

          // Overwrite config values with ones from CLI
          parser.parseArgument(args);
          if (!executionTestsFromCmdLine.isEmpty()) {
            builder.executionTests.retainAll(executionTestsFromCmdLine);
          }
          if (!classPathsFromCmdLine.isEmpty()) {
            builder.classPaths.retainAll(classPathsFromCmdLine);
          }
        }

        validateArgument(builder);
      } catch (final CmdLineException | IllegalArgumentException | InvalidValueException
          | NoSuchFileException e) {
        // todo: make error message of InvalidValueException more user-friendly
        parser.printUsage(System.err);
        throw new IllegalArgumentException(e.getMessage());
      }

      return builder.build();
    }

    public Configuration build() {

      if (targetProject == null) {
        targetProject = TargetProjectFactory.create(rootDir, productPaths, testPaths, classPaths,
            JUnitVersion.JUNIT4);
      }

      return new Configuration(this);
    }

    public Builder addClassPaths(final Collection<Path> classPaths) {
      this.classPaths.addAll(classPaths);
      return this;
    }

    public Builder addClassPath(final Path classPath) {
      this.classPaths.add(classPath);
      return this;
    }

    public Builder setWorkingDir(final Path workingDir) {
      this.workingDir = workingDir;
      return this;
    }

    public Builder setOutDir(final Path outDir) {
      this.outDir = outDir;
      return this;
    }

    public Builder setMutationGeneratingCount(final int mutationGeneratingCount) {
      this.mutationGeneratingCount = mutationGeneratingCount;
      return this;
    }

    public Builder setCrossoverGeneratingCount(final int crossoverGeneratingCount) {
      this.crossoverGeneratingCount = crossoverGeneratingCount;
      return this;
    }

    public Builder setHeadcount(final int headcount) {
      this.headcount = headcount;
      return this;
    }

    public Builder setMaxGeneration(final int maxGeneration) {
      this.maxGeneration = maxGeneration;
      return this;
    }

    public Builder setTimeLimitSeconds(final long timeLimitSeconds) {
      this.timeLimit = Duration.ofSeconds(timeLimitSeconds);
      return this;
    }

    public Builder setTimeLimit(final Duration timeLimit) {
      this.timeLimit = timeLimit;
      return this;
    }

    public Builder setTestTimeLimitSeconds(final long testTimeLimitSeconds) {
      this.testTimeLimit = Duration.ofSeconds(testTimeLimitSeconds);
      return this;
    }

    public Builder setTestTimeLimit(final Duration testTimeLimit) {
      this.testTimeLimit = testTimeLimit;
      return this;
    }

    public Builder setRequiredSolutionsCount(final int requiredSolutionsCount) {
      this.requiredSolutionsCount = requiredSolutionsCount;
      return this;
    }

    public Builder setLogLevel(final String logLevel) {
      return setLogLevel(Level.toLevel(logLevel.toUpperCase(Locale.ROOT)));
    }

    public Builder setLogLevel(final Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public Builder setRandomSeed(final long randomSeed) {
      this.randomSeed = randomSeed;
      return this;
    }

    public Builder addExecutionTest(final String executionTest) {
      this.executionTests.add(executionTest);
      return this;
    }

    public Builder setScope(final Scope.Type scope) {
      this.scope = scope;
      return this;
    }

    public Builder setNeedNotOutput(final boolean needNotOutput) {
      this.needNotOutput = needNotOutput;
      return this;
    }

    // endregion

    // region Private methods

    private static void validateArgument(final Builder builder) throws IllegalArgumentException {
      validateExistences(builder);
      validateCurrentDir(builder);
    }

    private static void validateExistences(final Builder builder) throws IllegalArgumentException {
      validateExistence(builder.rootDir);
      builder.productPaths.forEach(Builder::validateExistence);
      builder.testPaths.forEach(Builder::validateExistence);
      builder.classPaths.forEach(Builder::validateExistence);
      validateExistence(builder.workingDir);
    }

    private static void validateExistence(final Path path) throws IllegalArgumentException {
      if (Files.notExists(path)) {
        log.error(path.toString() + " does not exist.");
        throw new IllegalArgumentException(path.toString() + " does not exist.");
      }
    }

    private static void validateCurrentDir(Builder builder) {
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

    @Option(name = "--config", metaVar = "<path>", usage = "Specifies the path to the config file.")
    private void setConfigPathFromCmdLineParser(final String configPath) {
      this.configPath = Paths.get(configPath);
    }

    @Option(name = "-r", aliases = "--root-dir", metaVar = "<path>",
        usage = "Specifies the path to the root directory of the target project.",
        depends = {"-s", "-t"}, forbids = {"--config"})
    private void setRootDirFromCmdLineParser(final String rootDir) {
      this.rootDir = Paths.get(rootDir);
    }

    @Option(name = "-s", aliases = "--src", metaVar = "<path> ...",
        usage = " Specifies paths to \"product\" source code (i.e. main, non-test code),"
            + " or to directories containing them.",
        depends = {"-r", "-t"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addProductPathFromCmdLineParser(final String sourcePath) {
      this.productPaths.add(Paths.get(sourcePath));
    }

    @Option(name = "-t", aliases = "--test", metaVar = "<path> ...",
        usage = "Specifies paths to test source code, or to directories containing them.",
        depends = {"-r", "-s"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addTestPathFromCmdLineParser(final String testPath) {
      this.testPaths.add(Paths.get(testPath));
    }

    @Option(name = "-c", aliases = "--cp", metaVar = "<class path> ...",
        usage = "Specifies class paths needed to build the target project.",
        handler = StringArrayOptionHandler.class)
    private void addClassPathFromCmdLineParser(final String classPath) {
      this.classPaths.add(Paths.get(classPath));
    }

    @Option(name = "-x", aliases = "--exec-test", metaVar = "<fqn> ...",
        usage = "Specifies fully qualified names of test classes executed"
            + " during evaluation of variants (i.e. fix-candidates).",
        handler = StringArrayOptionHandler.class)
    private void addExecutionTestFromCmdLineParser(final String executionTest) {
      this.executionTests.add(executionTest);
    }

    @Option(name = "-w", aliases = "--working-dir", metaVar = "<path>",
        usage = "Specifies the path to working directory.")
    private void setWorkingDirFromCmdLineParser(final String workingDir) {
      this.workingDir = Paths.get(workingDir);
    }

    @Option(name = "-o", aliases = "--out-dir", metaVar = "<path>",
        usage = "Writes patches kGenProg generated to the specified directory.")
    private void setOutDirFromCmdLineParser(final String outDir) {
      this.outDir = Paths.get(outDir);
    }

    @Option(name = "--mutation-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a mutation.")
    private void setMutationGeneratingCountFromCmdLineParser(final int mutationGeneratingCount) {
      this.mutationGeneratingCount = mutationGeneratingCount;
    }

    @Option(name = "--crossover-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a crossover.")
    private void setCrossOverGeneratingCountFromCmdLineParser(final int crossoverGeneratingCount) {
      this.crossoverGeneratingCount = crossoverGeneratingCount;
    }

    @Option(name = "--headcount", metaVar = "<num>",
        usage = "Specifies how many variants survive in a generation.")
    private void setHeadcountFromCmdLineParser(final int headcount) {
      this.headcount = headcount;
    }

    @Option(name = "--max-generation", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of generations reached.")
    private void setMaxGenerationFromCmdLineParser(final int maxGeneration) {
      this.maxGeneration = maxGeneration;
    }

    @Option(name = "--time-limit", metaVar = "<sec>",
        usage = "Terminates searching solutions when the specified time has passed.")
    private void setTimeLimitFromCmdLineParser(final long timeLimit) {
      this.timeLimit = Duration.ofSeconds(timeLimit);
    }

    // todo update usage
    @Option(name = "--test-time-limit", metaVar = "<sec>",
        usage = "Specifies time limit to build and test for each variant in second")
    private void setTestTimeLimitFromCmdLineParser(final long testTimeLimit) {
      this.testTimeLimit = Duration.ofSeconds(testTimeLimit);
    }

    @Option(name = "--required-solutions", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of solutions are found.")
    private void setRequiredSolutionsCountFromCmdLineParser(final int requiredSolutionsCount) {
      this.requiredSolutionsCount = requiredSolutionsCount;
    }

    @Option(name = "-v", aliases = "--verbose",
        usage = "Be more verbose, printing DEBUG level logs.")
    private void setLogLevelDebugFromCmdLineParser(final boolean isVerbose) {
      logLevel = Level.DEBUG;
    }

    @Option(name = "-q", aliases = "--quiet", usage = "Be more quiet, suppressing non-ERROR logs.")
    private void setLogLevelErrorFromCmdLineParser(final boolean isQuiet) {
      logLevel = Level.ERROR;
    }

    @Option(name = "--random-seed", metaVar = "<num>",
        usage = "Specifies random seed used by random number generator.")
    private void setRandomSeedFromCmdLineParser(final long randomSeed) {
      this.randomSeed = randomSeed;
    }

    @Option(name = "--scope", usage = "Specify the scope from which source code to be reused is selected.")
    private void setScopeFromCmdLineParser(final Scope.Type scope) {
      this.scope = scope;
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

    private static class ScopeTypeToString implements Converter<Scope.Type, String> {

      @Override
      public Type convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return Type.valueOf(value);
      }

      @Override
      public String convertFromField(final Type value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }
  }
}

