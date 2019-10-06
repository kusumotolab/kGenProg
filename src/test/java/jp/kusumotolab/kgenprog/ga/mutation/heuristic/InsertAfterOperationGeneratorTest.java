package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class InsertAfterOperationGeneratorTest extends OperationGeneratorTest {

  @Test
  public void testCanBeApply() {
    final List<GeneratedAST<ProductSourcePath>> asts = createASTs(
        Paths.get("example", "CloseToZero01", "src", "example", "CloseToZero.java"));
    final InsertAfterOperationGenerator operationGenerator = new InsertAfterOperationGenerator(1.0);
    final List<JDTASTLocation> nonInsertableLocations = asts.get(0)
        .createLocations()
        .getAll()
        .stream()
        .map(e -> ((JDTASTLocation) e))
        .filter(e -> !operationGenerator.canBeApply(e)) // 対象のノードの後ろに挿入できないノードのみ抽出
        .collect(Collectors.toList());

    // block文が4つ，return文が1つ．
    // また，if (A) { B } else if (C) { D } else { E } の
    // if (C) { D } else { E } の親はif文なので，このノードの後ろにも挿入不可
    // よって計6つ
    assertThat(nonInsertableLocations).hasSize(6);

    assertThat(nonInsertableLocations).allMatch(e -> {
      final ASTNode node = e.getNode();
      if (node instanceof ReturnStatement || node instanceof Block) {
        return true;
      }
      return node instanceof IfStatement && node.getParent() instanceof IfStatement;
    });
  }
}