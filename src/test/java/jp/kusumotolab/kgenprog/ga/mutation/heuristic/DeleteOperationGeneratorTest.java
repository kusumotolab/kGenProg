package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class DeleteOperationGeneratorTest extends OperationGeneratorTest {

  @Test
  public void testCanBeApply() {
    final List<GeneratedAST<ProductSourcePath>> asts = createASTs(
        Paths.get("example", "CloseToZero01", "src", "example", "CloseToZero.java"));
    final DeleteOperationGenerator deleteOperationGenerator = new DeleteOperationGenerator(1.0);
    final List<JDTASTLocation> nonDeletableASTs = asts.get(0)
        .createLocations()
        .getAll()
        .stream()
        .map(e -> ((JDTASTLocation) e))
        .filter(e -> !deleteOperationGenerator.canBeApply(e)) // 削除することのできないASTLocationのみ抽出
        .collect(Collectors.toList());

    // CTZ01で削除できないのはBlock文とReturn文のみ
    assertThat(nonDeletableASTs).allMatch(
        e -> e.getNode() instanceof ReturnStatement || e.getNode() instanceof InfixExpression);

    // その数は3つ
    assertThat(nonDeletableASTs).hasSize(3);
  }
}