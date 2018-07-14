package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class KGenProgMain {

  private static Logger log = LoggerFactory.getLogger(KGenProgMain.class);

  private TargetProject targetProject;
  private FaultLocalization faultLocalization;
  private Mutation mutation;
  private Crossover crossover;
  private SourceCodeGeneration sourceCodeGeneration;
  private SourceCodeValidation sourceCodeValidation;
  private VariantSelection variantSelection;
  private TestProcessBuilder testProcessBuilder;
  private final List<Variant> completedVariants;

  // 以下，一時的なフィールド #146 で解決すべき
  private final long timeout;
  private final int maxGeneration;
  private final int requiredSolutions;

  // TODO #146
  // workingdirのパスを一時的にMainに記述
  // 別クラスが管理すべき情報？
  public final Path workingDir;

  public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization,
      Mutation mutation, Crossover crossover, SourceCodeGeneration sourceCodeGeneration,
      SourceCodeValidation sourceCodeValidation, VariantSelection variantSelection) {

    this(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, 60, 10, 1);
  }

  public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization,
      Mutation mutation, Crossover crossover, SourceCodeGeneration sourceCodeGeneration,
      SourceCodeValidation sourceCodeValidation, VariantSelection variantSelection,
      final long timeout, final int maxGeneration, final int requiredSolutions) {

    this.workingDir = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");
    try {
      if (Files.exists(this.workingDir)) {
        FileUtils.deleteDirectory(this.workingDir.toFile());
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

    this.targetProject = targetProject;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testProcessBuilder = new TestProcessBuilder(targetProject, this.workingDir);
    this.completedVariants = new ArrayList<>();

    this.timeout = timeout;
    this.maxGeneration = maxGeneration;
    this.requiredSolutions = requiredSolutions;


  }

  public void run() {
    log.debug("enter run()");
    List<Variant> selectedVariants = new ArrayList<>();
    final Variant initialVariant = targetProject.getInitialVariant();
    selectedVariants.add(initialVariant);

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getFiles());
    final long startTime = System.nanoTime();
    int generation = 0;
    List<Variant> previousGenerationVariants = new ArrayList<>();
    while (true) {

      // 制限時間に達したか，最大世代数に到達した場合には GA を抜ける
      if (isTimedOut(startTime) || reachedMaxGeneration(generation++)) {
        break;
      }

      final List<Gene> genes = new ArrayList<>();
      for (Variant variant : selectedVariants) {
        final List<Suspiciouseness> suspiciousenesses =
            faultLocalization.exec(targetProject, variant, testProcessBuilder);

        final List<Base> bases = mutation.exec(suspiciousenesses);
        genes.addAll(variant.getGene()
            .generateNextGenerationGenes(bases));
      }

      genes.addAll(crossover.exec(selectedVariants));

      final List<Variant> currentGenerationVariants = new ArrayList<>();
      for (final Gene gene : genes) {
        final GeneratedSourceCode generatedSourceCode =
            sourceCodeGeneration.exec(gene, targetProject);

        final Fitness fitness =
            sourceCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

        final Variant variant = new Variant(gene, fitness, generatedSourceCode);
        currentGenerationVariants.add(variant);
      }

      // TODO #171 で導入するAPIを使うべき
      // この世代で生成された Variants のうち，Fitnessが 1.0 なものを completedVariants に追加
      final List<Variant> newCompletedVariants = currentGenerationVariants.stream()
          .filter(v -> 0 == Double.compare(v.getFitness()
              .getValue(), 1.0d))
          .collect(Collectors.toList());
      completedVariants.addAll(newCompletedVariants);

      // しきい値以上の completedVariants が生成された場合は，GAを抜ける
      if (areEnoughCompletedVariants()) {
        break;
      }

      // TODO #171 で導入するAPIを使うべき
      // Fitness が 1.0 な Variants は除いた上で，前の世代のバリアントも併せた上で，次世代を生成するための Variants を選択
      currentGenerationVariants.removeAll(newCompletedVariants);
      previousGenerationVariants.addAll(currentGenerationVariants);
      selectedVariants = variantSelection.exec(previousGenerationVariants);

      // 現在の世代を前の世代にする
      previousGenerationVariants = currentGenerationVariants;
    }
    log.debug("exit run()");
  }

  private boolean reachedMaxGeneration(final int generation) {
    log.debug("enter reachedMaxGeneration()");
    return this.maxGeneration <= generation;
  }

  private boolean isTimedOut(final long startTime) {
    log.debug("enter isTimedOut()");
    final long elapsedTime = System.nanoTime() - startTime;
    return elapsedTime > this.timeout * 1000 * 1000 * 1000;
  }

  @Deprecated
  private boolean isSuccess(List<Variant> variants) {
    log.debug("enter isSuccess(List<>)");
    return false;
  }

  private boolean areEnoughCompletedVariants() {
    return this.requiredSolutions <= completedVariants.size();
  }

  public List<Variant> getCompletedVariants() {
    return this.completedVariants;
  }
}
