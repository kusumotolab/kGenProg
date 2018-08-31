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
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class Configuration {

  // region Fields
  public static final int DEFAULT_MAX_GENERATION = 10;
  public static final int DEFAULT_HEADCOUNT = 100;
  public static final int DEFAULT_REQUIRED_SOLUTIONS_COUNT = 1;
  public static final Duration DEFAULT_TIME_LIMIT = Duration.ofSeconds(60);
  public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
  public static final Path DEFAULT_WORKING_DIR;

  static {
    try {
      DEFAULT_WORKING_DIR = Files.createTempDirectory("kgenprog-work");
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Creating a temporary directory has failed.");
    }
  }

  private final TargetProject targetProject;
  private final Path workingDir;
  private final int headcount;
  private final int maxGeneration;
  private final Duration timeLimit;
  private final int requiredSolutionsCount;
  private final Level logLevel;
  // endregion

  // region Constructor

  private Configuration(Builder builder) {
    targetProject = builder.targetProject;
    workingDir = builder.workingDir;
    headcount = builder.headcount;
    maxGeneration = builder.maxGeneration;
    timeLimit = builder.timeLimit;
    requiredSolutionsCount = builder.requiredSolutionsCount;
    logLevel = builder.logLevel;
  }

  // endregion

  // region Methods

  public TargetProject getTargetProject() {
    return targetProject;
  }

  public Path getWorkingDir() {
    return workingDir;
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

  // endregion

  public static class Builder {

    // region Fields
    private static final Logger log = LoggerFactory.getLogger(Builder.class);
    private Path rootDir;
    private List<ProductSourcePath> productSourcePaths;
    private List<TestSourcePath> testSourcePaths;
    private List<ClassPath> classPaths = new ArrayList<>();
    private TargetProject targetProject;
    private Path workingDir = DEFAULT_WORKING_DIR;
    private int headcount = DEFAULT_HEADCOUNT;
    private int maxGeneration = DEFAULT_MAX_GENERATION;
    private Duration timeLimit = DEFAULT_TIME_LIMIT;
    private int requiredSolutionsCount = DEFAULT_REQUIRED_SOLUTIONS_COUNT;
    private Level logLevel = DEFAULT_LOG_LEVEL;
    // endregion

    // region Constructors

    public Builder(Path rootDir, Path productSourcePath, Path testSourcePath) {
      this(rootDir, new ProductSourcePath(productSourcePath), new TestSourcePath(testSourcePath));
    }

    public Builder(Path rootDir, ProductSourcePath productSourcePath,
        TestSourcePath testSourcePath) {
      this(rootDir, ImmutableList.of(productSourcePath), ImmutableList.of(testSourcePath));
    }

    public Builder(Path rootDir, List<ProductSourcePath> productSourcePaths,
        List<TestSourcePath> testSourcePaths) {
      this.rootDir = rootDir;
      this.productSourcePaths = productSourcePaths;
      this.testSourcePaths = testSourcePaths;
    }

    public Builder(TargetProject targetProject) {
      this.targetProject = targetProject;
    }

    private Builder() {
    }

    // endregion

    // region Methods

    public static Configuration buildFromCmdLineArgs(String[] args) {
      final Builder builder = new Builder();
      final CmdLineParser parser = new CmdLineParser(builder);

      try {
        parser.parseArgument(args);
      } catch (final CmdLineException e) {
        log.error(e.getMessage());
        parser.printUsage(System.err);
        System.exit(1);
      }

      return builder.build();
    }

    public Configuration build() {
      if (targetProject == null) {
        targetProject = TargetProjectFactory.create(rootDir, productSourcePaths,
            testSourcePaths, classPaths, JUnitVersion.JUNIT4);
      }

      return new Configuration(this);
    }

    public Builder addClasPaths(Collection<ClassPath> classPaths) {
      this.classPaths.addAll(classPaths);
      return this;
    }

    public Builder addClasPath(ClassPath classPath) {
      this.classPaths.add(classPath);
      return this;
    }

    public Builder setWorkingDir(Path workingDir) {
      this.workingDir = workingDir;
      return this;
    }

    public Builder setHeadcount(int headcount) {
      this.headcount = headcount;
      return this;
    }

    public Builder setMaxGeneration(int maxGeneration) {
      this.maxGeneration = maxGeneration;
      return this;
    }

    public Builder setTimeLimitSeconds(long timeLimitSeconds) {
      this.timeLimit = Duration.ofSeconds(timeLimitSeconds);
      return this;
    }

    public Builder setTimeLimit(Duration timeLimit) {
      this.timeLimit = timeLimit;
      return this;
    }

    public Builder setRequiredSolutionsCount(int requiredSolutionsCount) {
      this.requiredSolutionsCount = requiredSolutionsCount;
      return this;
    }

    public Builder setLogLevel(String logLevel) {
      return setLogLevel(Level.toLevel(logLevel.toUpperCase(Locale.ROOT)));
    }

    public Builder setLogLevel(Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    // endregion

    // region Methods for CmdLineParser

    @Option(name = "-r", aliases = "--root-dir", required = true, metaVar = "<path>",
        usage = "Path of a root directory of a target project")
    private void setRootDirFromCmdLimeParser(final String rootDir) {
      log.debug("enter setRootDir(String)");
      this.rootDir = Paths.get(rootDir);
    }

    @Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class,
        metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
    private void addProductSourcePathFromCmdLimeParser(final String sourcePaths) {
      log.debug("enter addSourcePath(String)");
      this.productSourcePaths = new ArrayList<>();
      this.productSourcePaths.add(new ProductSourcePath(Paths.get(sourcePaths)));
    }

    @Option(name = "-t", aliases = "--test", required = true,
        handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
        usage = "Paths of the root directories holding test codes")
    private void addTestSourcePathFromCmdLimeParser(final String testPaths) {
      log.debug("enter addTestPath(String)");
      this.testSourcePaths = new ArrayList<>();
      this.testSourcePaths.add(new TestSourcePath(Paths.get(testPaths)));
    }

    @Option(name = "-c", aliases = "--cp", handler = StringArrayOptionHandler.class,
        metaVar = "<class path> ...", usage = "Class paths required to build the target project")
    private void addClassPathFromCmdLimeParser(final String classPaths) {
      log.debug("enter addClassPath(String)");
      this.classPaths = new ArrayList<>();
      this.classPaths.add(new ClassPath(Paths.get(classPaths)));
    }

    @Option(name = "-w", aliases = "--working-dir", metaVar = "<path>",
        usage = "Path of a working directory")
    private void setWorkingDirFromCmdLimeParser(final String workingDir) {
      log.debug("enter setWorkingDir(String)");
      this.workingDir = Paths.get(workingDir);
    }

    @Option(name = "-h", aliases = "--headcount",
        usage = "The number of how many variants are generated maximally in a generation")
    private void setHeadcountFromCmdLimeParser(int headcount) {
      log.debug("enter setHeadcount(int)");
      this.headcount = headcount;
    }

    @Option(name = "-g", aliases = "--max-generation", usage = "Maximum generation")
    private void setMaxGenerationFromCmdLimeParser(int maxGeneration) {
      log.debug("enter setMaxGeneration(int)");
      this.maxGeneration = maxGeneration;
    }

    @Option(name = "-l", aliases = "--time-limit", usage = "Time limit for repairing in second")
    private void setTimeLimitFromCmdLimeParser(long timeLimit) {
      log.debug("enter setTimeLimit(long)");
      this.timeLimit = Duration.ofSeconds(timeLimit);
    }

    @Option(name = "-e", aliases = "--required-solutions", usage = "The number of solutions needed to be searched")
    private void setRequiredSolutionsCountFromCmdLimeParser(int requiredSolutionsCount) {
      log.debug("enter setTimeLimit(long)");
      this.requiredSolutionsCount = requiredSolutionsCount;
    }

    @Option(name = "-v", aliases = "--verbose", usage = "Verbose mode. Print DEBUG level logs.")
    private void setLogLevelDebugFromCmdLimeParser(boolean isVerbose) {
      log.debug("enter setLogLevelDebug(boolean)");
      log.info("log level has been set DEBUG");
      logLevel = Level.DEBUG;
    }

    @Option(name = "-q", aliases = "--quiet", usage = "Quiet mode. Print ERROR level logs.")
    private void setLogLevelErrorFromCmdLimeParser(boolean isQuiet) {
      log.debug("enter setLogLevelError(boolean)");
      log.info("log level has been set ERROR");
      logLevel = Level.ERROR;
    }

    // endregion
  }
}
