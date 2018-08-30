package jp.kusumotolab.kgenprog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import jp.kusumotolab.kgenprog.project.PatchGenerator;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class CUILauncher {

  // region Fields
  private static final Logger log = LoggerFactory.getLogger(CUILauncher.class);
  private final List<Path> classPaths = new ArrayList<>();
  private final List<Path> productSourcePaths = new ArrayList<>();
  private final List<Path> testSourcePaths = new ArrayList<>();
  private final ch.qos.logback.classic.Logger rootLogger =
      (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  private Path rootDir;
  private Path kGenProgDir = Paths.get(System.getProperty("user.dir"))
      .toAbsolutePath();
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

  public List<Path> getClassPaths() {
    log.debug("enter getClassPaths()");
    return classPaths;
  }

  @Option(name = "-c", aliases = "--cp", handler = StringArrayOptionHandler.class,
      metaVar = "<class path> ...", usage = "Class paths required to build the target project")
  public void addClassPath(final String classPaths) {
    log.debug("enter addClassPath(String)");
    this.classPaths.add(Paths.get(classPaths));
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

  @Option(name = "-k", aliases = "--kgenprog-dir", metaVar = "<path>",
      usage = "Path of kGenProg directory")
  public void setKGenProgDir(final String kGenProgDir) {
    this.kGenProgDir = Paths.get(kGenProgDir);
  }

  public Path getKGenProgDir() {
    return kGenProgDir;
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

    final Path currentDir = Paths.get(System.getProperty("user.dir"));
    final Path projectRootDir = launcher.getRootDir();
    try {
      if (Files.isSameFile(currentDir, projectRootDir)) {
        launcher.launch();
      } else {
        launcher.launchAsAnotherProcess();
      }
    } catch (final IOException e) {
      log.error("directory \"{}\" is not accessible", projectRootDir);
      System.exit(1);
    }

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
    final Mutation mutation = new RandomMutation(10, random, rouletteStatementSelection);
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

  public void launchAsAnotherProcess() {
    final List<String> commandLineOptions = new ArrayList<>();

    // System.out.println("classpath : " + System.getProperty("java.class.path"));

    commandLineOptions.add("java");

    commandLineOptions.add("-classpath");
    final String classpathOfCurrentProcess = System.getProperty("java.class.path");
    commandLineOptions.add(classpathOfCurrentProcess);

    commandLineOptions.add("jp.kusumotolab.kgenprog.CUILauncher");

    commandLineOptions.add("--root-dir");
    commandLineOptions.add(".");

    commandLineOptions.add("--src");
    for (final Path productSourcePath : getProductSourcePaths()) {
      commandLineOptions.add(productSourcePath.toString());
    }

    commandLineOptions.add("--test");
    for (final Path testSourcePath : getTestSourcePaths()) {
      commandLineOptions.add(testSourcePath.toString());
    }

    final List<Path> classPaths = getClassPaths();
    if (!classPaths.isEmpty()) {
      commandLineOptions.add("--cp");
      for (final Path classPath : classPaths) {
        commandLineOptions.add(classPath.toAbsolutePath()
            .toString());
      }
    }

    if (Level.DEBUG == getLogLevel()) {
      commandLineOptions.add("-v");
    }

    if (Level.ERROR == getLogLevel()) {
      commandLineOptions.add("q");
    }

    commandLineOptions.add("--headcount");
    commandLineOptions.add(Integer.toString(getHeadcount()));

    commandLineOptions.add("--max-generation");
    commandLineOptions.add(Integer.toString(getMaxGeneration()));

    commandLineOptions.add("--time-limit");
    commandLineOptions.add(Long.toString(getTimeLimit()));

    commandLineOptions.add("--kgenprog-dir");
    commandLineOptions.add(getKGenProgDir().toAbsolutePath()
        .toString());

    final File workDir = getRootDir().toFile();
    final ProcessBuilder processBuilder = new ProcessBuilder(commandLineOptions).directory(workDir);

    try {
      final ConcurrentLinkedQueue<String> outputBuffer = new ConcurrentLinkedQueue<>();
      final Process process = processBuilder.start();
      final InputStreamThread it =
          new InputStreamThread(process.getInputStream(), StandardCharsets.UTF_8, outputBuffer);
      final InputStreamThread et =
          new InputStreamThread(process.getErrorStream(), StandardCharsets.UTF_8, outputBuffer);
      it.start();
      et.start();

      while (process.isAlive() || !outputBuffer.isEmpty()) {
        if (!outputBuffer.isEmpty()) {
          final String line = outputBuffer.poll();
          System.out.println(line);
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      process.waitFor();
      it.alive = false;
      et.alive = false;
      it.join();
      et.join();

    } catch (final IOException e) {
      log.error("IO error happend in starting the KGenProg main process");
      System.exit(1);
    } catch (final InterruptedException e) {
      log.error("the KGenProg main process has terminated improperly");
      System.exit(1);
    }
  }

  class InputStreamThread extends Thread {

    boolean alive;
    private BufferedReader inputBuffer;
    private ConcurrentLinkedQueue<String> outputBuffer;

    public InputStreamThread(final InputStream is, final Charset charset,
        final ConcurrentLinkedQueue<String> outputBuffer) {
      this.alive = true;
      this.outputBuffer = outputBuffer;
      this.inputBuffer = new BufferedReader(new InputStreamReader(is, charset));
    }

    @Override
    public void run() {
      while (alive) {
        try {
          final String line = inputBuffer.readLine();
          if (null != line) {
            outputBuffer.add(line);
          }
        } catch (final IOException e) {
          log.error(e.getMessage());
        }
      }
    }
  }
}
