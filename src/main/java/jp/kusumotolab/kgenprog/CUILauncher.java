package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.RandomNumberGeneration;
import jp.kusumotolab.kgenprog.ga.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.DiffOutput;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.ResultOutput;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class CUILauncher {

  private static Logger log = LoggerFactory.getLogger(CUILauncher.class);
  // region Fields
  private Path rootDir;
  private List<ProductSourcePath> productSourcePaths = new ArrayList<>();
  private List<TestSourcePath> testSourcePaths = new ArrayList<>();
  private List<ClassPath> classPaths = new ArrayList<>();
  // endregion

  // region Getter/Setter

  public Path getRootDir() {
    return rootDir;
  }

  @Option(name = "-r", aliases = "--root-dir", required = true, metaVar = "<path>",
      usage = "Path of a root directory of a target project")
  public void setRootDir(String rootDir) {
    log.debug("enter setRootDir(String)");
    this.rootDir = Paths.get(rootDir).toAbsolutePath();
  }

  public List<ProductSourcePath> getProductSourcePaths() {
    log.debug("enter getSourcePaths()");
    return productSourcePaths;
  }

  @Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class,
      metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
  public void addProductSourcePath(String sourcePaths) {
    log.debug("enter addSourcePath(String)");
    this.productSourcePaths.add(new ProductSourcePath(Paths.get(sourcePaths).toAbsolutePath()));
  }

  public List<TestSourcePath> getTestSourcePaths() {
    log.debug("enter getTestPaths()");
    return testSourcePaths;
  }

  @Option(name = "-t", aliases = "--test", required = true,
      handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
      usage = "Paths of the root directories holding test codes")
  public void addTestSourcePath(String testPaths) {
    log.debug("enter addTestPath(String)");
    this.testSourcePaths.add(new TestSourcePath(Paths.get(testPaths).toAbsolutePath()));
  }

  public List<ClassPath> getClassPaths() {
    log.debug("enter getClassPaths()");
    return classPaths;
  }

  @Option(name = "-c", aliases = "--cp", required = true, handler = StringArrayOptionHandler.class,
      metaVar = "<class path> ...", usage = "Class paths required to build the target project")
  public void addClassPath(String classPaths) {
    log.debug("enter addClassPath(String)");
    this.classPaths.add(new ClassPath(Paths.get(classPaths).toAbsolutePath()));
  }

  // endregion

  public static void main(String[] args) {
    log.info("start kGenProg");

    CUILauncher launcher = new CUILauncher();
    CmdLineParser parser = new CmdLineParser(launcher);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
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

    final TargetProject targetProject = TargetProjectFactory.create(getRootDir(), getProductSourcePaths(),
        getTestSourcePaths(), getClassPaths(), JUnitVersion.JUNIT4);

    final FaultLocalization faultLocalization = new Ochiai();
    final RandomNumberGeneration randomNumberGeneration = new RandomNumberGeneration();
    final RouletteStatementSelection rouletteStatementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final Mutation mutation =
        new RandomMutation(10, randomNumberGeneration, rouletteStatementSelection);
    final Crossover crossover = new SinglePointCrossover(randomNumberGeneration);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection();
    final Path workingPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    final ResultOutput resultGenerator = new DiffOutput(workingPath);

    final KGenProgMain kGenProgMain = new KGenProgMain(targetProject, faultLocalization, mutation,
        crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection, resultGenerator,
        workingPath);
    kGenProgMain.run();

    log.debug("exit launch()");
  }
}
