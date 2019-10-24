package jp.kusumotolab.kgenprog;

import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.SimpleMutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.StatementSelection;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.output.Exporter;
import jp.kusumotolab.kgenprog.output.ExporterFactory;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class CUILauncher {

  public static void main(final String[] args) {
    try {
      final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
      final CUILauncher launcher = new CUILauncher();
      launcher.launch(config);
    } catch (final RuntimeException e) {
      System.exit(1);
    }
  }

  public void launch(final Configuration config) throws RuntimeException {
    setLogLevel(config.getLogLevel());

    final FaultLocalization faultLocalization = config.getFaultLocalization()
        .initialize();
    final Random random = new Random(config.getRandomSeed());
    final StatementSelection rouletteStatementSelection =
        new RouletteStatementSelection(random);
    final Mutation mutation = new SimpleMutation(config.getMutationGeneratingCount(), random,
        rouletteStatementSelection, config.getScope(), config.getNeedHistoricalElement());
    final FirstVariantSelectionStrategy firstVariantSelectionStrategy =
        config.getFirstVariantSelectionStrategy()
            .initialize(random);
    final SecondVariantSelectionStrategy secondVariantSelectionStrategy =
        config.getSecondVariantSelectionStrategy()
            .initialize(random);
    final Crossover crossover = config.getCrossoverType()
        .initialize(random, firstVariantSelectionStrategy,
            secondVariantSelectionStrategy, config.getCrossoverGeneratingCount(),
            config.getNeedHistoricalElement());
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount(),
        random);
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final List<Exporter> exporters = ExporterFactory.create(config);
    exporters.stream()
        .findFirst()
        .ifPresent(Exporter::clearPreviousResults);

    final KGenProgMain kGenProgMain =
        new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, testExecutor, exporters);

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
