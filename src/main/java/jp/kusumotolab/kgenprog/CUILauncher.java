package jp.kusumotolab.kgenprog;

import java.util.Random;
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

public class CUILauncher {

  // region Fields
  private static final Logger log = LoggerFactory.getLogger(CUILauncher.class);
  // endregion

  public static void main(final String[] args) {
    log.info("start kGenProg");

    final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
    final CUILauncher launcher = new CUILauncher();
    launcher.launch(config);

    log.info("end kGenProg");
  }

  public void launch(final Configuration config) {
    log.debug("enter launch()");

    setLogLevel(config.getLogLevel());

    final FaultLocalization faultLocalization = new Ochiai();
    final Random random = new Random(config.getRandomSeed());
    final RouletteStatementSelection rouletteStatementSelection =
        new RouletteStatementSelection(random);
    final Mutation mutation =
        new RandomMutation(config.getSiblingsCount(), random, rouletteStatementSelection);
    final Crossover crossover = new SinglePointCrossover(random);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount());
    final PatchGenerator patchGenerator = new PatchGenerator();

    final KGenProgMain kGenProgMain = new KGenProgMain(config, faultLocalization, mutation,
        crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection, patchGenerator);

    kGenProgMain.run();

    log.debug("exit launch()");
  }

  // region Private Method

  private void setLogLevel(Level logLevel) {
    ch.qos.logback.classic.Logger rootLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(logLevel);
  }

  // endregion
}
