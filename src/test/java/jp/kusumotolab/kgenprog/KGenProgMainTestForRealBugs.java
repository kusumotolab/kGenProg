package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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

public class KGenProgMainTestForRealBugs {

  private final static Path WORK_PATH = Paths.get("tmp/work");

  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(WORK_PATH.toFile());
  }

  // helper to create main
  private KGenProgMain createMain(final Path rootPath, final List<String> products,
      final List<String> tests, final String executionTest) {

    final List<Path> productSourcePaths = products.stream()
        .map(rootPath::resolve)
        .collect(Collectors.toList());
    final List<Path> testSourcePaths = tests.stream()
        .map(rootPath::resolve)
        .collect(Collectors.toList());

    final Configuration config =
        new Configuration.Builder(rootPath, productSourcePaths, testSourcePaths)
            .setWorkingDir(WORK_PATH)
            .addExecutionTest(executionTest)
            .setTimeLimitSeconds(600)
            .setTestTimeLimitSeconds(30)
            .setMaxGeneration(10)
            .setRandomSeed(0)
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

  @Ignore
  @Test
  public void testMath02() {
    final Path rootPath = Paths.get("example/real-bugs/Math02");
    final String execTest = "org.apache.commons.math3.distribution.HypergeometricDistributionTest";
    final List<String> products = Arrays.asList("src/main/java");
    final List<String> tests = Arrays.asList("src/test/java");

    final KGenProgMain main = createMain(rootPath, products, tests, execTest);
    final List<Variant> variants = main.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testMath05() {
    final Path rootPath = Paths.get("example/real-bugs/Math05");
    final String execTest = "org.apache.commons.math3.complex.ComplexTest";
    final List<String> products = Arrays.asList("src/main/java");
    final List<String> tests = Arrays.asList("src/test/java");

    final KGenProgMain main = createMain(rootPath, products, tests, execTest);
    final List<Variant> variants = main.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  //@Ignore
  @Test
  public void testMath70() {
    final Path rootPath = Paths.get("example/real-bugs/Math70");
    final String execTest = "org.apache.commons.math.analysis.solvers.BisectionSolverTest";
    final List<String> products = Arrays.asList("src/main/java");
    final List<String> tests = Arrays.asList("src/test/java");

    final KGenProgMain main = createMain(rootPath, products, tests, execTest);
    final List<Variant> variants = main.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testMath73() {
    final Path rootPath = Paths.get("example/real-bugs/Math73");
    final String execTest = "org.apache.commons.math.analysis.solvers.BrentSolverTest";
    final List<String> products = Arrays.asList("src/main/java");
    final List<String> tests = Arrays.asList("src/test/java");

    final KGenProgMain main = createMain(rootPath, products, tests, execTest);
    final List<Variant> variants = main.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }

  @Ignore
  @Test
  public void testMath85() {
    final Path rootPath = Paths.get("example/real-bugs/Math85");
    final String execTest = "org.apache.commons.math.distribution.NormalDistributionTest";
    final List<String> products = Arrays.asList("src/java");
    final List<String> tests = Arrays.asList("src/test");

    final KGenProgMain main = createMain(rootPath, products, tests, execTest);
    final List<Variant> variants = main.run();

    // アサートは適当．現在無限ループにより修正がそもそもできていないので，要検討
    assertThat(variants).hasSize(1)
        .allMatch(Variant::isCompleted);
  }
}