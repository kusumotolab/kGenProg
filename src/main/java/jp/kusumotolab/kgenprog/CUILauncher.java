package jp.kusumotolab.kgenprog;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.Ample;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Jaccard;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.fl.Tarantula;
import jp.kusumotolab.kgenprog.fl.Zoltar;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantRandomSelection;
import jp.kusumotolab.kgenprog.ga.crossover.RandomCrossover;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantRandomSelection;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.RandomMutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.output.PatchGenerator;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class CUILauncher {

  public static void main(final String[] args) {
    try {
      final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
      final CUILauncher launcher = new CUILauncher();
      launcher.launch(config);
    } catch (IllegalArgumentException e) {
      System.exit(1);
    }
  }

  public void launch(final Configuration config) {
    setLogLevel(config.getLogLevel());

    final FaultLocalization faultLocalization = config.getFaultLocalization().initialize();
    final Random random = new Random(config.getRandomSeed());
    final RouletteStatementSelection rouletteStatementSelection =
        new RouletteStatementSelection(random);
    final Mutation mutation = new RandomMutation(config.getMutationGeneratingCount(), random,
        rouletteStatementSelection, config.getScope());
    final Crossover crossover = new RandomCrossover(random, new FirstVariantRandomSelection(random),
        new SecondVariantRandomSelection(random), config.getCrossoverGeneratingCount());
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount());
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final PatchGenerator patchGenerator = new PatchGenerator();

    final KGenProgMain kGenProgMain =
        new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, testExecutor, patchGenerator);

    kGenProgMain.run();
  }

  // region Private Method

  private void setLogLevel(final Level logLevel) {
    final ch.qos.logback.classic.Logger rootLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(logLevel);
  }

  // endregion
}
