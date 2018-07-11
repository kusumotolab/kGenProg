package jp.kusumotolab.kgenprog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

  // TODO #146
  // workingdirのパスを一時的にMainに記述
  // 別クラスが管理すべき情報？
  private final Path WORKING_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-work");

  public KGenProgMain(TargetProject targetProject, FaultLocalization faultLocalization,
      Mutation mutation, Crossover crossover, SourceCodeGeneration sourceCodeGeneration,
      SourceCodeValidation sourceCodeValidation, VariantSelection variantSelection) {
    this.targetProject = targetProject;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testProcessBuilder = new TestProcessBuilder(targetProject, WORKING_DIR);
  }

  public void run() {
    log.debug("enter run()");
    List<Variant> selectedVariants = new ArrayList<>();
    final Variant initialVariant = targetProject.getInitialVariant();
    selectedVariants.add(initialVariant);
    mutation.setCandidates(initialVariant.getGeneratedSourceCode().getFiles());
    while (true) {
      if (isTimedOut() || reachedMaxGeneration() || isSuccess(selectedVariants)) {
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

      selectedVariants = variantSelection.exec(variants);
    }
    log.debug("exit run()");
  }

  // hitori
  private boolean reachedMaxGeneration() {
    log.debug("enter reachedMaxGeneration()");
    // TODO Auto-generated method stub
    return false;
  }

  // hitori
  private boolean isTimedOut() {
    log.debug("enter isTimedOut()");
    // TODO Auto-generated method stub
    return false;
  }

  // hitori
  private boolean isSuccess(List<Variant> variants) {
    log.debug("enter isSuccess(List<>)");
    return false;
  }
}
