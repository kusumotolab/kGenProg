package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.PatchGenerator;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class CUILauncher {

  // region Fields
  private static final Logger log = LoggerFactory.getLogger(CUILauncher.class);
  private final List<ClassPath> classPaths = new ArrayList<>();
  private final List<Path> productSourcePaths = new ArrayList<>();
  private final List<Path> testSourcePaths = new ArrayList<>();
  private final ch.qos.logback.classic.Logger rootLogger =
      (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  private Path rootDir;
  private int headcount = 100;
  private int maxGeneration = 10;
  private long timeLimit = 60;
  // endregion

  // region Constructor

  CUILauncher() {
    rootLogger.setLevel(Level.INFO);
  }

  // endregion

  // region Getter/Setter

  public Path getRootDir() {
    return rootDir;
  }

  @Option(name = "-r", aliases = "--root-dir", required = true, metaVar = "<path>",
      usage = "Path of a root directory of a target project")
  public void setRootDir(final String rootDir) {
    log.debug("enter setRootDir(String)");
    this.rootDir = Paths.get(rootDir);
  }

  public List<Path> getProductSourcePaths() {
    log.debug("enter getSourcePaths()");
    return productSourcePaths;
  }

  @Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class,
      metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
  public void addProductSourcePath(final String sourcePaths) {
    log.debug("enter addSourcePath(String)");
    this.productSourcePaths.add(Paths.get(sourcePaths));
  }

  public List<Path> getTestSourcePaths() {
    log.debug("enter getTestPaths()");
    return testSourcePaths;
  }

  @Option(name = "-t", aliases = "--test", required = true,
      handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
      usage = "Paths of the root directories holding test codes")
  public void addTestSourcePath(final String testPaths) {
    log.debug("enter addTestPath(String)");
    this.testSourcePaths.add(Paths.get(testPaths));
  }

  public List<ClassPath> getClassPaths() {
    log.debug("enter getClassPaths()");
    return classPaths;
  }

  @Option(name = "-c", aliases = "--cp", handler = StringArrayOptionHandler.class,
      metaVar = "<class path> ...", usage = "Class paths required to build the target project")
  public void addClassPath(final String classPaths) {
    log.debug("enter addClassPath(String)");
    this.classPaths.add(new ClassPath(Paths.get(classPaths)));
  }

  public Level getLogLevel() {
    return rootLogger.getLevel();
  }

  @Option(name = "-v", aliases = "--verbose", usage = "Verbose mode. Print DEBUG level logs.")
  public void setLogLevelDebug(boolean isVerbose) {
    log.debug("enter setLogLevelDebug(boolean)");
    log.info("log level was set DEBUG");
    rootLogger.setLevel(Level.DEBUG);
  }

  @Option(name = "-q", aliases = "--quiet", usage = "Quiet mode. Print ERROR level logs.")
  public void setLogLevelError(boolean isQuiet) {
    log.debug("enter setLogLevelError(boolean)");
    log.info("log level was set ERROR");
    rootLogger.setLevel(Level.ERROR);
  }

  public int getHeadcount() {
    return headcount;
  }

  @Option(name = "-h", aliases = "--headcount",
      usage = "The number of how many variants are generated maximally in a generation")
  public void setHeadcount(int headcount) {
    log.debug("enter setHeadcount(int)");
    this.headcount = headcount;
  }

  public int getMaxGeneration() {
    return maxGeneration;
  }

  @Option(name = "-g", aliases = "--max-generation", usage = "Maximum generation")
  public void setMaxGeneration(int maxGeneration) {
    log.debug("enter setMaxGeneration(int)");
    this.maxGeneration = maxGeneration;
  }

  public long getTimeLimit() {
    return timeLimit;
  }

  @Option(name = "-l", aliases = "--time-limit", usage = "Time limit for repairing in second")
  public void setTimeLimit(long timeLimit) {
    log.debug("enter setTimeLimit(long)");
    this.timeLimit = timeLimit;
  }

  // endregion

  public static void main(final String[] args) {
    log.info("start kGenProg");

    final CUILauncher launcher = new CUILauncher();
    final CmdLineParser parser = new CmdLineParser(launcher);

    try {
      parser.parseArgument(args);
    } catch (final CmdLineException e) {
      log.error(e.getMessage());
      // System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.exit(1);
    }
    launcher.launch();

    log.info("end kGenProg");
  }

  public void launch() {
    log.debug("enter launch()");

    final TargetProject targetProject = TargetProjectFactory.create(getRootDir(),
        getProductSourcePaths(), getTestSourcePaths(), getClassPaths(), JUnitVersion.JUNIT4);

    final FaultLocalization faultLocalization = new Ochiai();
    final Random random = new Random();
    random.setSeed(0);
    final RouletteStatementSelection rouletteStatementSelection =
        new RouletteStatementSelection(random);
    final Mutation mutation =
        new RandomMutation(10, random, rouletteStatementSelection);
    final Crossover crossover = new SinglePointCrossover(random);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(getHeadcount());
    final Path workingPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final PatchGenerator patchGenerator = new PatchGenerator();

    final KGenProgMain kGenProgMain = new KGenProgMain(targetProject, faultLocalization, mutation,
        crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection, patchGenerator,
        workingPath);
    kGenProgMain.run();

    log.debug("exit launch()");
  }
}
