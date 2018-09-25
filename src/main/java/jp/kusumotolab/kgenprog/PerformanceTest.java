package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.GenerationalVariantSelection;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.RandomMutation;
import jp.kusumotolab.kgenprog.ga.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.SinglePointCrossover;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.PatchGenerator;

public class PerformanceTest {

  private final static String PRODUCT_NAME = "src/example/CloseToZero.java";
  private final static String TEST_NAME = "src/example/CloseToZeroTest.java";

  private final static Path WORK_PATH = Paths.get("tmp/work");

  public static void main(String[] args) {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path productPath = rootPath.resolve(PRODUCT_NAME);
    final Path testPath = rootPath.resolve(TEST_NAME);

    final KGenProgMain kGenProgMain = createMain(rootPath, productPath, testPath, 600, 1000, 100);
    final List<Variant> variants = kGenProgMain.run();
    System.out.println(variants.size());
  }

  private static KGenProgMain createMain(final Path rootPath, final Path productPath,
      final Path testPath, final long timeout, final int maxGeneration,
      final int requiredSolutions) {

    final List<Path> productPaths = Arrays.asList(productPath);
    final List<Path> testPaths = Arrays.asList(testPath);

    final Configuration config =
        new Configuration.Builder(rootPath, productPaths, testPaths).setTimeLimitSeconds(timeout)
            .setMaxGeneration(maxGeneration)
            .setRequiredSolutionsCount(requiredSolutions)
            .setWorkingDir(WORK_PATH)
            .build();
    final FaultLocalization faultLocalization = new Ochiai();
    final Random random = new Random();
    final CandidateSelection statementSelection = new RouletteStatementSelection(random);
    final Mutation mutation = new RandomMutation(10, random, statementSelection);
    final Crossover crossover = new SinglePointCrossover(random);
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new GenerationalVariantSelection();
    final PatchGenerator patchGenerator = new PatchGenerator();

    return new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, patchGenerator);
  }
}
