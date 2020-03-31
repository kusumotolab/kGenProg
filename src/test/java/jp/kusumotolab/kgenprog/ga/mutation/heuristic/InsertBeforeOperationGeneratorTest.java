package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class InsertBeforeOperationGeneratorTest extends OperationGeneratorTest {

  @Test
  public void testCanBeApply() {
    final List<GeneratedAST<ProductSourcePath>> asts = createASTs(
        Paths.get("example", "CloseToZero01", "src", "example", "CloseToZero.java"));
    final InsertBeforeOperationGenerator operationGenerator = new InsertBeforeOperationGenerator(
        1.0);
    final List<JDTASTLocation> nonInsertableLocations = asts.get(0)
        .createLocations()
        .getAll()
        .stream()
        .map(e -> ((JDTASTLocation) e))
        .filter(e -> !operationGenerator.canBeApply(e)) // 対象のノードの後ろに挿入できないノードのみ抽出
        .collect(Collectors.toList());

    // 2つの条件式
    // 上記の計2つになるはず
    assertThat(nonInsertableLocations).hasSize(2);

    assertThat(nonInsertableLocations).allMatch(e -> {
      final ASTNode node = e.getNode();
      return node instanceof InfixExpression;
    });
  }
}