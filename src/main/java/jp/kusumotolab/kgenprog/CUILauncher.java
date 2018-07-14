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
import jp.kusumotolab.kgenprog.ga.SiglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class CUILauncher {

  private static Logger log = LoggerFactory.getLogger(CUILauncher.class);
  // region Fields
  private Path rootDir;
  private List<SourcePath> sourcePaths = new ArrayList<>();
  private List<SourcePath> testPaths = new ArrayList<>();
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
    this.rootDir = Paths.get(rootDir);
  }

  public List<SourcePath> getSourcePaths() {
    log.debug("enter getSourcePaths()");
    return sourcePaths;
  }

  @Option(name = "-s", aliases = "--src", required = true, handler = StringArrayOptionHandler.class,
      metaVar = "<path> ...", usage = "Paths of the root directories holding src codes")
  public void setSourcePaths(String sourcePaths) {
    log.debug("enter setSourcePaths(String)");
    this.sourcePaths.add(new TargetSourcePath(Paths.get(sourcePaths)));
  }

  public List<SourcePath> getTestPaths() {
    log.debug("enter getTestPaths()");
    return testPaths;
  }

  @Option(name = "-t", aliases = "--test", required = true,
      handler = StringArrayOptionHandler.class, metaVar = "<path> ...",
      usage = "Paths of the root directories holding test codes")
  public void setTestPaths(String testPaths) {
    log.debug("enter setTestPaths(String)");
    this.testPaths.add(new TestSourcePath(Paths.get(testPaths)));
  }

  public List<ClassPath> getClassPaths() {
    log.debug("enter getClassPaths()");
    return classPaths;
  }

  @Option(name = "-c", aliases = "--cp", required = true, handler = StringArrayOptionHandler.class,
      metaVar = "<class path> ...", usage = "Class paths required to build the target project")
  public void setClassPaths(String classPaths) {
    log.debug("enter setClassPaths(String)");
    this.classPaths.add(new ClassPath(Paths.get(classPaths)));
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

    TargetProject targetProject = TargetProjectFactory.create(getRootDir(), getSourcePaths(),
        getTestPaths(), getClassPaths(), JUnitVersion.JUNIT4);

    FaultLocalization faultLocalization = new Ochiai();
    Mutation mutation = new RandomMutation();
    Crossover crossover = new SiglePointCrossover();
    SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    VariantSelection variantSelection = new DefaultVariantSelection();

    KGenProgMain kGenProgMain = new KGenProgMain(targetProject, faultLocalization, mutation,
        crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection);
    kGenProgMain.run();

    log.debug("exit launch()");
  }
}
