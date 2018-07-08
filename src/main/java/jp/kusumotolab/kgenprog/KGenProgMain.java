package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
  private final Path WORKING_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");

  public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization,
      Mutation mutation, Crossover crossover, SourceCodeGeneration sourceCodeGeneration,
      SourceCodeValidation sourceCodeValidation, VariantSelection variantSelection) {
    
    this(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, 600, 10, 1);
  }

  public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization,
      Mutation mutation, Crossover crossover, SourceCodeGeneration sourceCodeGeneration,
      SourceCodeValidation sourceCodeValidation, VariantSelection variantSelection,
      final long timeout, final int maxGeneration, final int requiredSolutions) {
    this.targetProject = targetProject;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testProcessBuilder = new TestProcessBuilder(targetProject, WORKING_DIR);
    this.completedVariants = new ArrayList<>();

    this.timeout = timeout;
    this.maxGeneration = maxGeneration;
    this.requiredSolutions = requiredSolutions;
  }

  public void run() {
    List<Variant> selectedVariants = new ArrayList<>();
    final Variant initialVariant = targetProject.getInitialVariant();
    selectedVariants.add(initialVariant);
    mutation.setCandidates(initialVariant.getGeneratedSourceCode().getFiles());

    final long startTime = System.nanoTime();
    int generation = 0;
    while (true) {

      // 制限時間に達したか，最大世代数に到達した場合には GA を抜ける
      if (isTimedOut(startTime) || reachedMaxGeneration(generation++)) {
        break;
      }

      List<Gene> genes = new ArrayList<>();
      for (Variant variant : selectedVariants) {
        List<Suspiciouseness> suspiciousenesses =
            faultLocalization.exec(targetProject, variant, testProcessBuilder);

        List<Base> bases = mutation.exec(suspiciousenesses);
        genes.addAll(variant.getGene().generateNextGenerationGenes(bases));
      }

      genes.addAll(crossover.exec(selectedVariants));

      List<Variant> variants = new ArrayList<>();
      for (Gene gene : genes) {
        GeneratedSourceCode generatedSourceCode = sourceCodeGeneration.exec(gene, targetProject);

        Fitness fitness =
            sourceCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

        Variant variant = new Variant(gene, fitness, generatedSourceCode);
        variants.add(variant);
      }

      // この世代で生成された Variants のうち，Fitnessが 1.0 なものを complatedVariants に追加
      final List<Variant> newComplatedVariants =
          variants.stream().filter(v -> 0 == Double.compare(v.getFitness().getValue(), 1.0d))
              .collect(Collectors.toList());
      completedVariants.addAll(newComplatedVariants);

      // しきい値以上の complatedVariants が生成された場合は，GAを抜ける
      if (areEnoughComplatedVariants()) {
        break;
      }

      // Fitness が 1.0 な Variants は除いた上で，次世代を生成するための Variants を選択
      variants.removeAll(newComplatedVariants);
      selectedVariants = variantSelection.exec(variants);
    }
  }

  // hitori
  private boolean reachedMaxGeneration(final int generation) {
    return this.maxGeneration <= generation;
  }

  // hitori
  private boolean isTimedOut(final long startTime) {
    final long elapsedTime = System.nanoTime() - startTime;
    return elapsedTime > this.timeout * 1000 * 1000;
  }

  @Deprecated
  private boolean isSuccess(List<Variant> variants) {
    return false;
  }

  private boolean areEnoughComplatedVariants() {
    return this.requiredSolutions <= completedVariants.size();
  }
}
