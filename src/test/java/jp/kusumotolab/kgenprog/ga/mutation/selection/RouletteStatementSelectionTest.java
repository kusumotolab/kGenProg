package jp.kusumotolab.kgenprog.ga.mutation.selection;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class RouletteStatementSelectionTest {

  @Test
  public void testProjectScope() {
    final RouletteStatementSelection statementSelection = createStatementSelection();
    final Scope scope = new Scope(Type.PROJECT, null);
    final Roulette<ReuseCandidate<ASTNode>> roulette = statementSelection.getRoulette(scope);
    assertThat(roulette.getCandidateList()).hasSize(3);
  }

  @Test
  public void testPackageScope() {
    final RouletteStatementSelection statementSelection = createStatementSelection();
    final Scope scope = new Scope(Type.PACKAGE, ExampleAlias.Fqn.FOO);
    final Roulette<ReuseCandidate<ASTNode>> roulette = statementSelection.getRoulette(scope);
    assertThat(roulette.getCandidateList()).hasSize(2);
  }

  @Test
  public void testFileScope() {
    final RouletteStatementSelection statementSelection = createStatementSelection();
    final Scope scope = new Scope(Type.FILE, ExampleAlias.Fqn.FOO);
    final Roulette<ReuseCandidate<ASTNode>> roulette = statementSelection.getRoulette(scope);
    assertThat(roulette.getCandidateList()).hasSize(1);
  }

  private RouletteStatementSelection createStatementSelection() {
    final Path basePath = Paths.get("example/BuildSuccess15");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode sourceCode = TestUtil.createGeneratedSourceCode(targetProject);
    final List<GeneratedAST<ProductSourcePath>> asts = sourceCode.getProductAsts();

    final Random random = new Random(0);
    final RouletteStatementSelection statementSelection = new RouletteStatementSelection(random);
    statementSelection.setCandidates(asts);
    return statementSelection;
  }
}
