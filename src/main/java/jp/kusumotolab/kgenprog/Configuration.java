package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.FaultLocalization.Technique;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
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
  public static final Path DEFAULT_OUT_DIR = Paths.get("kgenprog-out");
  public static final boolean DEFAULT_IS_FORCE = false;
  public static final long DEFAULT_RANDOM_SEED = 0;
  public static final Scope.Type DEFAULT_SCOPE = Scope.Type.PACKAGE;
  public static final boolean DEFAULT_NEED_NOT_OUTPUT = false;
  public static final FaultLocalization.Technique DEFAULT_FAULT_LOCALIZATION =
      FaultLocalization.Technique.Ochiai;
  public static final Crossover.Type DEFAULT_CROSSOVER_TYPE = Crossover.Type.Random;
  public static final FirstVariantSelectionStrategy.Strategy DEFAULT_FIRST_VARIANT_SELECTION_STRATEGY =
      FirstVariantSelectionStrategy.Strategy.Random;
  public static final SecondVariantSelectionStrategy.Strategy DEFAULT_SECOND_VARIANT_SELECTION_STRATEGY =
      SecondVariantSelectionStrategy.Strategy.Random;
  public static final boolean DEFAULT_HISTORY_RECORD = false;

  private final TargetProject targetProject;
  private final List<String> executionTests;
  private final Path outDir;
  private final boolean isForce;
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
  private final FaultLocalization.Technique faultLocalization;
  private final Crossover.Type crossoverType;
  private final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategy;
  private final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategy;
  private final boolean historyRecord;
  private final Builder builder;

  // endregion

  // region Constructor

  private Configuration(final Builder builder) {
    this.targetProject = builder.targetProject;
    this.executionTests = builder.executionTests;
    this.outDir = builder.outDir;
    this.isForce = builder.isForce;
    this.mutationGeneratingCount = builder.mutationGeneratingCount;
    this.crossoverGeneratingCount = builder.crossoverGeneratingCount;
    this.headcount = builder.headcount;
    this.maxGeneration = builder.maxGeneration;
    this.timeLimit = builder.timeLimit;
    this.testTimeLimit = builder.testTimeLimit;
    this.requiredSolutionsCount = builder.requiredSolutionsCount;
    this.logLevel = builder.logLevel;
    this.randomSeed = builder.randomSeed;
    this.scope = builder.scope;
    this.needNotOutput = builder.needNotOutput;
    this.faultLocalization = builder.faultLocalization;
    this.crossoverType = builder.crossoverType;
    this.firstVariantSelectionStrategy = builder.firstVariantSelectionStrategy;
    this.secondVariantSelectionStrategy = builder.secondVariantSelectionStrategy;
    this.historyRecord = builder.historyRecord;
    this.builder = builder;
  }

  // endregion

  public TargetProject getTargetProject() {
    return targetProject;
  }

  public List<String> getExecutedTests() {
    return executionTests;
  }

  public Path getOutDir() {
    return outDir;
  }

  public boolean getIsForce() {
    return isForce;
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

  public FaultLocalization.Technique getFaultLocalization() {
    return faultLocalization;
  }

  public Crossover.Type getCrossoverType() {
    return crossoverType;
  }

  public FirstVariantSelectionStrategy.Strategy getFirstVariantSelectionStrategy() {
    return firstVariantSelectionStrategy;
  }

  public SecondVariantSelectionStrategy.Strategy getSecondVariantSelectionStrategy() {
    return secondVariantSelectionStrategy;
  }

  public boolean getHistoryRecord() {
    return historyRecord;
  }

  @Override
  public String toString() {
    return builder.toString();
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

    @com.electronwill.nightconfig.core.conversion.Path("out-dir")
    @PreserveNotNull
    @Conversion(PathToString.class)
    private Path outDir = DEFAULT_OUT_DIR;

    @com.electronwill.nightconfig.core.conversion.Path("is-force")
    @PreserveNotNull
    private boolean isForce = DEFAULT_IS_FORCE;

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

    @com.electronwill.nightconfig.core.conversion.Path("fault-localization")
    @PreserveNotNull
    @Conversion(FaultLocalizationTechniqueToString.class)
    private FaultLocalization.Technique faultLocalization = DEFAULT_FAULT_LOCALIZATION;

    @com.electronwill.nightconfig.core.conversion.Path("crossover-type")
    @PreserveNotNull
    @Conversion(CrossoverTypeToString.class)
    private Crossover.Type crossoverType = DEFAULT_CROSSOVER_TYPE;

    @com.electronwill.nightconfig.core.conversion.Path("crossover-first-variant")
    @PreserveNotNull
    @Conversion(FirstVariantSelectionStrategyToString.class)
    private FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategy =
        DEFAULT_FIRST_VARIANT_SELECTION_STRATEGY;

    @com.electronwill.nightconfig.core.conversion.Path("crossover-second-variant")
    @PreserveNotNull
    @Conversion(SecondVariantSelectionStrategyToString.class)
    private SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategy =
        DEFAULT_SECOND_VARIANT_SELECTION_STRATEGY;

    @com.electronwill.nightconfig.core.conversion.Path("history-record")
    @PreserveNotNull
    private boolean historyRecord = DEFAULT_HISTORY_RECORD;

    private transient final Set<String> optionsSetByCmdLineArgs = new HashSet<>();
    private transient final Set<String> optionsSetByConfigFile = new HashSet<>();

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
      final Builder builder = createFromCmdLineArgs(args);
      return builder.build();
    }

    public static Builder createFromCmdLineArgs(final String[] args)
        throws IllegalArgumentException {

      final Builder builder = new Builder();
      final CmdLineParser parser = new CmdLineParser(builder);

      try {
        parser.parseArgument(args);
        final List<String> executionTestsFromCmdLine = builder.executionTests;
        final List<Path> classPathsFromCmdLine = builder.classPaths;

        if (needsParseConfigFile(args)) {
          builder.parseConfigFile();
          builder.findOptionsSetByConfigFile();

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

      return builder;
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

    public Builder setOutDir(final Path outDir) {
      this.outDir = outDir;
      return this;
    }

    public Builder setIsForce(final boolean isForce) {
      this.isForce = isForce;
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
      this.optionsSetByConfigFile.add("logLevel");
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

    public Builder setFaultLocalization(final FaultLocalization.Technique faultLocalization) {
      this.faultLocalization = faultLocalization;
      return this;
    }

    public Builder setCrossoverType(final Crossover.Type crossoverType) {
      this.crossoverType = crossoverType;
      return this;
    }

    public Builder setFirstVariantSelectionStrategy(
        final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategy) {
      this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
      return this;
    }

    public Builder setSecondVariantSelectionStrategy(
        final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategy) {
      this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
      return this;
    }

    public Builder setHistoryRecord(final boolean historyRecord) {
      this.historyRecord = historyRecord;
      return this;
    }

    // endregion

    // region Private methods

    private static void validateArgument(final Builder builder) throws IllegalArgumentException {
      validateExistences(builder);
      validateCurrentDir(builder);
      validateOutDir(builder);
    }

    private static void validateExistences(final Builder builder) throws IllegalArgumentException {
      validateExistence(builder.rootDir);
      builder.productPaths.forEach(Builder::validateExistence);
      builder.testPaths.forEach(Builder::validateExistence);
      builder.classPaths.forEach(Builder::validateExistence);
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
              "If the target project includes test cases with file I/O, such test cases won't run correctly.");
          log.warn(
              "We recommend you running kGenProg with the root directory of the target project as the current directory.");
        }
      } catch (final IOException e) {
        throw new IllegalArgumentException("directory " + projectRootDir + " is not accessible.");
      }
    }

    private static void validateOutDir(final Builder builder) {

      if (Files.notExists(builder.outDir)) {
        return;
      }

      try {
        final List<Path> subFiles = Files.walk(builder.outDir, FileVisitOption.FOLLOW_LINKS)
            .filter(e -> !e.equals(builder.outDir))
            .collect(Collectors.toList());

        if (subFiles.isEmpty() && !builder.isForce) {
          final String outDirName = builder.outDir.toString();
          log.warn("Cannot write patches, because directory {} is not empty.", outDirName);
          log.warn("If you want patches, please run with -f or empty {}.", outDirName);
        }
      } catch (final IOException e) {
        throw new IllegalArgumentException("directory " + builder.outDir + " is not accessible.");
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
      try (final FileConfig config = loadConfig()) {
        final ObjectConverter converter = new ObjectConverter();
        converter.toObject(config, this);
        resolvePaths();
      }
    }

    private void findOptionsSetByConfigFile() throws NoSuchFileException {
      try (final FileConfig config = loadConfig()) {

        // 設定ファイルに記述されているオプション一覧を取得
        final Set<String> optionNames = config.entrySet()
            .stream()
            .map(o -> o.getKey())
            .collect(Collectors.toSet());

        final Class<?> clazz = this.getClass();
        for (final Field field : clazz.getDeclaredFields()) {

          // Builderオブジェクトの各フィールドに対して，
          // Pathアノテーションが有る場合はその値を取得
          field.setAccessible(true);
          final String name = field.getName();
          final com.electronwill.nightconfig.core.conversion.Path[] annotations =
              field.getDeclaredAnnotationsByType(
                  com.electronwill.nightconfig.core.conversion.Path.class);

          // Pathアノテーションが有り，その値が設定ファイルに現れている場合は，
          // そのフィールドの値は設定ファイルから読み込まれたとみなす
          if (0 < annotations.length && optionNames.contains(annotations[0].value())) {
            optionsSetByConfigFile.add(name);
          }
        }
      }
    }

    private FileConfig loadConfig() throws NoSuchFileException {
      if (Files.notExists(configPath)) {
        throw new NoSuchFileException("config file \"" + configPath.toAbsolutePath()
            .toString() + "\" is not found.");
      }
      final FileConfig config = FileConfig.of(configPath);
      config.load();
      return config;
    }

    private void resolvePaths() {
      rootDir = resolveAgainstConfigDirAndNormalize(rootDir);
      productPaths = productPaths.stream()
          .map(this::resolveAgainstConfigDirAndNormalize)
          .collect(Collectors.toList());
      testPaths = testPaths.stream()
          .map(this::resolveAgainstConfigDirAndNormalize)
          .collect(Collectors.toList());
      classPaths = classPaths.stream()
          .map(this::resolveAgainstConfigDirAndNormalize)
          .collect(Collectors.toList());

      if (!outDir.equals(DEFAULT_OUT_DIR)) {
        outDir = resolveAgainstConfigDirAndNormalize(outDir);
      }
    }

    private Path resolveAgainstConfigDirAndNormalize(final Path path) {
      return checkSymbolicLink(configPath.resolveSibling(path)
          .normalize());
    }

    /**
     * Checks whether the given path is a symbolic link, and returns it as is. If it is a symbolic
     * link, outputs warning message; otherwise do nothing.
     *
     * @param path the path to be checked
     * @return the given path
     */
    private Path checkSymbolicLink(final Path path) {
      if (Files.isSymbolicLink(path)) {
        log.warn("symbolic link may not be resolved: " + path.toString());
      }

      return path;
    }

    // endregion

    // region Methods for CmdLineParser

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      final Class<?> clazz = this.getClass();
      for (final Field field : clazz.getDeclaredFields()) {
        try {
          field.setAccessible(true);
          final String name = field.getName();
          sb.append(name)
              .append(" = ")
              .append(field.get(this));
          if (optionsSetByCmdLineArgs.contains(name)) {
            sb.append(" (set by command line)");
          } else if (optionsSetByConfigFile.contains(name)) {
            sb.append(" (set in config file)");
          }
          sb.append(System.lineSeparator());
        } catch (final IllegalAccessException e) {
          sb.append(field.getName())
              .append(" = ")
              .append("access denied")
              .append(System.lineSeparator());
        }
      }
      return sb.toString();
    }

    @Option(name = "--config", metaVar = "<path>", usage = "Specifies the path to the config file.")
    private void setConfigPathFromCmdLineParser(final String configPath) {
      this.configPath = Paths.get(configPath);
      this.optionsSetByCmdLineArgs.add("configPath");
    }

    @Option(name = "-r", aliases = "--root-dir", metaVar = "<path>",
        usage = "Specifies the path to the root directory of the target project.",
        depends = {"-s", "-t"}, forbids = {"--config"})
    private void setRootDirFromCmdLineParser(final String rootDir) {
      this.rootDir = Paths.get(rootDir);
      this.optionsSetByCmdLineArgs.add("rootDir");
    }

    @Option(name = "-s", aliases = "--src", metaVar = "<path> ...",
        usage = " Specifies paths to \"product\" source code (i.e. main, non-test code),"
            + " or to directories containing them.",
        depends = {"-r", "-t"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addProductPathFromCmdLineParser(final String productPath) {
      this.productPaths.add(Paths.get(productPath));
      this.optionsSetByCmdLineArgs.add("productPaths");
    }

    @Option(name = "-t", aliases = "--test", metaVar = "<path> ...",
        usage = "Specifies paths to test source code, or to directories containing them.",
        depends = {"-r", "-s"}, forbids = {"--config"}, handler = StringArrayOptionHandler.class)
    private void addTestPathFromCmdLineParser(final String testPath) {
      this.testPaths.add(Paths.get(testPath));
      this.optionsSetByCmdLineArgs.add("testPaths");
    }

    @Option(name = "-c", aliases = "--cp", metaVar = "<class path> ...",
        usage = "Specifies class paths needed to build the target project.",
        handler = StringArrayOptionHandler.class)
    private void addClassPathFromCmdLineParser(final String classPath) {
      this.classPaths.add(Paths.get(classPath));
      this.optionsSetByCmdLineArgs.add("classPaths");
    }

    @Option(name = "-x", aliases = "--exec-test", metaVar = "<fqn> ...",
        usage = "Specifies fully qualified names of test classes executed"
            + " during evaluation of variants (i.e. fix-candidates).",
        handler = StringArrayOptionHandler.class)
    private void addExecutionTestFromCmdLineParser(final String executionTest) {
      this.executionTests.add(executionTest);
      this.optionsSetByCmdLineArgs.add("executionTests");
    }

    @Option(name = "-o", aliases = "--out-dir", metaVar = "<path>",
        usage = "Writes patches kGenProg generated to the specified directory.")
    private void setOutDirFromCmdLineParser(final String outDir) {
      this.outDir = Paths.get(outDir);
      this.optionsSetByCmdLineArgs.add("outDir");
    }

    @Option(name = "-f", aliases = "--force",
        usage = "Remove file in output directory when write patches.")
    private void setIsForceFromCmdLineParser(final boolean isForce) {
      this.isForce = isForce;
      this.optionsSetByCmdLineArgs.add("isForce");
    }

    @Option(name = "--mutation-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a mutation.")
    private void setMutationGeneratingCountFromCmdLineParser(final int mutationGeneratingCount) {
      this.mutationGeneratingCount = mutationGeneratingCount;
      this.optionsSetByCmdLineArgs.add("mutationGeneratingCount");
    }

    @Option(name = "--crossover-generating-count", metaVar = "<num>",
        usage = "Specifies how many variants are generated in a generation by a crossover.")
    private void setCrossOverGeneratingCountFromCmdLineParser(final int crossoverGeneratingCount) {
      this.crossoverGeneratingCount = crossoverGeneratingCount;
      this.optionsSetByCmdLineArgs.add("crossoverGenetingCount");
    }

    @Option(name = "--headcount", metaVar = "<num>",
        usage = "Specifies how many variants survive in a generation.")
    private void setHeadcountFromCmdLineParser(final int headcount) {
      this.headcount = headcount;
      this.optionsSetByCmdLineArgs.add("headcount");
    }

    @Option(name = "--max-generation", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of generations reached.")
    private void setMaxGenerationFromCmdLineParser(final int maxGeneration) {
      this.maxGeneration = maxGeneration;
      this.optionsSetByCmdLineArgs.add("maxGeneration");
    }

    @Option(name = "--time-limit", metaVar = "<sec>",
        usage = "Terminates searching solutions when the specified time has passed.")
    private void setTimeLimitFromCmdLineParser(final long timeLimit) {
      this.timeLimit = Duration.ofSeconds(timeLimit);
      this.optionsSetByCmdLineArgs.add("timeLimit");
    }

    // todo update usage
    @Option(name = "--test-time-limit", metaVar = "<sec>",
        usage = "Specifies time limit to build and test for each variant in second")
    private void setTestTimeLimitFromCmdLineParser(final long testTimeLimit) {
      this.testTimeLimit = Duration.ofSeconds(testTimeLimit);
      this.optionsSetByCmdLineArgs.add("testTimeLimit");
    }

    @Option(name = "--required-solutions", metaVar = "<num>",
        usage = "Terminates searching solutions when the specified number of solutions are found.")
    private void setRequiredSolutionsCountFromCmdLineParser(final int requiredSolutionsCount) {
      this.requiredSolutionsCount = requiredSolutionsCount;
      this.optionsSetByCmdLineArgs.add("requiredSolutionsCount");
    }

    @Option(name = "-v", aliases = "--verbose",
        usage = "Be more verbose, printing DEBUG level logs.")
    private void setLogLevelDebugFromCmdLineParser(final boolean isVerbose) {
      logLevel = Level.DEBUG;
      this.optionsSetByCmdLineArgs.add("logLevel");
    }

    @Option(name = "-q", aliases = "--quiet", usage = "Be more quiet, suppressing non-ERROR logs.")
    private void setLogLevelErrorFromCmdLineParser(final boolean isQuiet) {
      logLevel = Level.ERROR;
      this.optionsSetByCmdLineArgs.add("logLevel");
    }

    @Option(name = "--random-seed", metaVar = "<num>",
        usage = "Specifies random seed used by random number generator.")
    private void setRandomSeedFromCmdLineParser(final long randomSeed) {
      this.randomSeed = randomSeed;
      this.optionsSetByCmdLineArgs.add("randomSeed");
    }

    @Option(name = "--scope",
        usage = "Specify the scope from which source code to be reused is selected.")
    private void setScopeFromCmdLineParser(final Scope.Type scope) {
      this.scope = scope;
      this.optionsSetByCmdLineArgs.add("scope");
    }

    @Option(name = "--fault-localization", usage = "Specifies technique of fault localization.")
    private void setFaultLocalizationFromCmdLineParser(
        final FaultLocalization.Technique faultLocalization) {
      this.faultLocalization = faultLocalization;
      this.optionsSetByCmdLineArgs.add("faultLocalization");
    }

    @Option(name = "--crossover-type", usage = "Specifies crossover type.")
    private void setCrossoverTypeFromCmdLineParser(final Crossover.Type crossoverType) {
      this.crossoverType = crossoverType;
      this.optionsSetByCmdLineArgs.add("crossoverType");
    }

    @Option(name = "--crossover-first-variant",
        usage = "Specifies first variant selection strategy for crossover.")
    private void setFirstVariantSelectionStrategyFromCmdLineParser(
        final FirstVariantSelectionStrategy.Strategy firstVariantSelectionStrategy) {
      this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
      this.optionsSetByCmdLineArgs.add("firstVariantSelectionStrategy");
    }

    @Option(name = "--crossover-second-variant",
        usage = "Specifies second variant selection strategy for crossover.")
    private void setSecondVariantSelectionStrategyFromCmdLineParser(
        final SecondVariantSelectionStrategy.Strategy secondVariantSelectionStrategy) {
      this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
      this.optionsSetByCmdLineArgs.add("secondVariantSelectionStrategy");
    }

    @Option(name = "--history-record", usage = "Record historical element.")
    private void setHistoryRecordFromCmdLineParser(final boolean historyRecord) {
      this.historyRecord = historyRecord;
      this.optionsSetByCmdLineArgs.add("historyRecord");
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
      public Scope.Type convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return Scope.Type.valueOf(value);
      }

      @Override
      public String convertFromField(final Scope.Type value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }

    private static class FaultLocalizationTechniqueToString
        implements Converter<FaultLocalization.Technique, String> {

      @Override
      public Technique convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return Technique.valueOf(value);
      }

      @Override
      public String convertFromField(final Technique value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }

    private static class CrossoverTypeToString implements Converter<Crossover.Type, String> {

      @Override
      public Crossover.Type convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return Crossover.Type.valueOf(value);
      }

      @Override
      public String convertFromField(final Crossover.Type value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }

    private static class FirstVariantSelectionStrategyToString
        implements Converter<FirstVariantSelectionStrategy.Strategy, String> {

      @Override
      public FirstVariantSelectionStrategy.Strategy convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return FirstVariantSelectionStrategy.Strategy.valueOf(value);
      }

      @Override
      public String convertFromField(final FirstVariantSelectionStrategy.Strategy value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }

    private static class SecondVariantSelectionStrategyToString
        implements Converter<SecondVariantSelectionStrategy.Strategy, String> {

      @Override
      public SecondVariantSelectionStrategy.Strategy convertToField(final String value) {
        if (value == null) {
          return null;
        }
        return SecondVariantSelectionStrategy.Strategy.valueOf(value);
      }

      @Override
      public String convertFromField(final SecondVariantSelectionStrategy.Strategy value) {
        if (value == null) {
          return null;
        }
        return value.toString();
      }
    }
  }
}

