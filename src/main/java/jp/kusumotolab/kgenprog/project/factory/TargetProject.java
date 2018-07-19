package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.SimpleGene;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class TargetProject {

  public final Path rootPath; // TODO ひとまずrootPathだけpublicに．他フィールドは要検討
  private final List<SourcePath> sourcePaths;
  private final List<SourcePath> testPaths;
  private final List<ClassPath> classPaths;

  // Must be package-private. Should be created only from TargetProjectFactory#create
  TargetProject(final Path rootPath, final List<SourcePath> sourcePaths,
      final List<SourcePath> testPaths, List<ClassPath> classPaths) {
    this.rootPath = rootPath;
    this.sourcePaths = sourcePaths;
    this.testPaths = testPaths;
    this.classPaths = classPaths;
  }

  public List<SourcePath> getSourcePaths() {
    return sourcePaths;
  }

  public List<SourcePath> getTestPaths() {
    return testPaths;
  }

  public List<ClassPath> getClassPaths() {
    return classPaths;
  }

  public Variant getInitialVariant() {
    Gene gene = new SimpleGene(Collections.emptyList());
    Fitness fitness = null;
    GeneratedSourceCode generatedSourceCode = new GeneratedSourceCode(constructAST());

    return new Variant(gene, fitness, generatedSourceCode);
  }

  // hitori
  private List<GeneratedAST> constructAST() {
    // TODO: ここにDIする方法を検討
    return new JDTASTConstruction().constructAST(this);
  }

}