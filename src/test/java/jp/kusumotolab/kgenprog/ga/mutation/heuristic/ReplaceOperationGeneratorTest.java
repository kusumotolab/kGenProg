package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
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

    // 何も入っていないはず．
    assertThat(nonReplacableLocations).isEmpty();
  }
}