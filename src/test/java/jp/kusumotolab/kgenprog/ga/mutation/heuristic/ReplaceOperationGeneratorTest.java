package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class ReplaceOperationGeneratorTest extends OperationGeneratorTest {

  @Test
  public void testCanBeApply() {
    final List<GeneratedAST<ProductSourcePath>> asts = createASTs(
        Paths.get("example", "CloseToZero01", "src", "example", "CloseToZero.java"));
    final ReplaceOperationGenerator operationGenerator = new ReplaceOperationGenerator(1.0);
    final List<JDTASTLocation> nonReplacableLocations = asts.get(0)
        .createLocations()
        .getAll()
        .stream()
        .map(e -> ((JDTASTLocation) e))
        .filter(e -> !operationGenerator.canBeApply(e)) // 対象のノードと置換できないノードのみ抽出
        .collect(Collectors.toList());

    // メソッド宣言のBlockのみ
    assertThat(nonReplacableLocations).hasSize(1);

    final JDTASTLocation jdtastLocation = nonReplacableLocations.get(0);
    final ASTNode node = jdtastLocation.getNode();
    final ASTNode parent = node.getParent();

    assertThat(parent).isInstanceOf(MethodDeclaration.class);
    assertThat(node).isInstanceOf(Block.class);
  }
}