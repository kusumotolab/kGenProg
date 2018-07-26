package jp.kusumotolab.kgenprog.ga;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class RandomMutationTest {

  private class TestNumberGeneration extends RandomNumberGeneration {

    @Override
    public int getInt() {
      return 0;
    }

    @Override
    public int getInt(int divisor) {
      return 1;
    }

    @Override
    public double getDouble(double max) {
      return super.getDouble(max);
    }

    @Override
    public boolean getBoolean() {
      return true;
    }
  }

  @Test
  public void testExec() throws NoSuchFieldException, IllegalAccessException {
    final Path basePath = Paths.get("example/example01");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final TestNumberGeneration randomNumberGeneration = new TestNumberGeneration();
    final StatementSelection statementSelection =
        new RouletteStatementSelection(randomNumberGeneration);
    final RandomMutation randomMutation =
        new RandomMutation(15, new TestNumberGeneration(), statementSelection);
    randomMutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getAsts());

    final GeneratedAST generatedAST = new ArrayList<>(initialVariant.getGeneratedSourceCode()
        .getAsts()).get(0);
    final ProductSourcePath sourcePath = generatedAST.getProductSourcePath();
    final CompilationUnit root = (CompilationUnit) ((GeneratedJDTAST) generatedAST).getRoot()
        .getRoot()
        .getRoot();
    final TypeDeclaration typeRoot = (TypeDeclaration) root.types()
        .get(0);

    @SuppressWarnings("unchecked")
    final List<Statement> statements = typeRoot.getMethods()[0].getBody()
        .statements();

    final double[] value = {0.8};
    final List<Suspiciouseness> suspiciousenesses = statements.stream()
        .map(e -> new JDTASTLocation(sourcePath, e))
        .map(e -> {
          value[0] += 0.1;
          return new Suspiciouseness(e, value[0]);
        })
        .collect(Collectors.toList());

    // 正しく10個のBaseが生成されるかのテスト
    final List<Base> baseList = randomMutation.exec(suspiciousenesses);
    assertThat(baseList).hasSize(15);

    // Suspiciousenessが高い場所ほど多くの操作が生成されているかのテスト
    final Map<String, List<Base>> map = baseList.stream()
        .collect(
            Collectors.groupingBy(e -> ((JDTASTLocation) e.getTargetLocation()).node.toString()));
    final String weakSuspiciouseness = ((JDTASTLocation) suspiciousenesses.get(0)
        .getLocation()).node.toString();
    final String strongSuspiciouseness = ((JDTASTLocation) suspiciousenesses.get(1)
        .getLocation()).node.toString();

    assertThat(map.get(weakSuspiciouseness)
        .size()).isLessThan(map.get(strongSuspiciouseness)
            .size());

    // TestNumberGenerationにしたがってOperationが生成されているかのテスト
    final Base base = baseList.get(0);
    final JDTASTLocation targetLocation = (JDTASTLocation) base.getTargetLocation();
    assertThat(targetLocation.node).isSameSourceCodeAs("return n;");

    final Operation operation = base.getOperation();
    assertThat(operation).isInstanceOf(InsertOperation.class);

    final InsertOperation insertOperation = (InsertOperation) operation;
    final Field field = insertOperation.getClass()
        .getDeclaredField("astNode");
    field.setAccessible(true);
    final ASTNode node = (ASTNode) field.get(insertOperation);
    assertThat(node).isSameSourceCodeAs("n++;");
  }
}
