package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import java.nio.file.Path;
import java.util.List;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.HeuristicProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;

public class OperationGeneratorTest {

  protected List<GeneratedAST<ProductSourcePath>> createASTs(final Path path) {
    final HeuristicProjectFactory factory = new HeuristicProjectFactory(path);
    final GeneratedSourceCode sourceCode = new JDTASTConstruction().constructAST(factory.create());
    return sourceCode.getProductAsts();
  }
}
