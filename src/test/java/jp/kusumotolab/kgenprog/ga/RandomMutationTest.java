package jp.kusumotolab.kgenprog.ga;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class RandomMutationTest {

  @SuppressWarnings("serial")
  private class MockRandom extends Random {

    @Override
    public int nextInt() {
      return 0;
    }

    @Override
    public int nextInt(int divisor) {
      return 1;
    }

    @Override
    public boolean nextBoolean() {
      return true;
    }
  }

  @Test
  public void testExec() throws NoSuchFieldException, IllegalAccessException {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final Random random = new MockRandom();
    random.setSeed(0);
    final CandidateSelection statementSelection = new RouletteStatementSelection(random);
    final RandomMutation randomMutation = new RandomMutation(15, random, statementSelection);
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
    final List<Suspiciousness> suspiciousnesses = statements.stream()
        .map(e -> new JDTASTLocation(sourcePath, e))
        .map(e -> {
          value[0] += 0.1;
          return new Suspiciousness(e, value[0]);
        })
        .collect(Collectors.toList());

    final Variant variant = new Variant(new SimpleGene(Collections.emptyList()));
    variant.setSuspiciousnesses(suspiciousnesses);

    // 正しく15個のVariantが生成されるかのテスト
    final List<Variant> variantList = randomMutation.exec(Arrays.asList(variant));
    assertThat(variantList).hasSize(15);

    // Suspiciousnessが高い場所ほど多くの操作が生成されているかのテスト
    final Map<String, List<Base>> map = variantList.stream()
        .map(this::getLastBase)
        .collect(
            Collectors.groupingBy(e -> ((JDTASTLocation) e.getTargetLocation()).node.toString()));
    final String weakSuspiciousness = ((JDTASTLocation) suspiciousnesses.get(0)
        .getLocation()).node.toString();
    final String strongSuspiciousness = ((JDTASTLocation) suspiciousnesses.get(1)
        .getLocation()).node.toString();

    assertThat(map.get(weakSuspiciousness)
        .size()).isLessThan(map.get(strongSuspiciousness)
            .size());

    // TestNumberGenerationにしたがってOperationが生成されているかのテスト
    final Base base = getLastBase(variantList.get(0));
    final JDTASTLocation targetLocation = (JDTASTLocation) base.getTargetLocation();
    assertThat(targetLocation.node).isSameSourceCodeAs("return n;");

    final Operation operation = base.getOperation();
    assertThat(operation).isInstanceOf(InsertOperation.class);

    final InsertOperation insertOperation = (InsertOperation) operation;
    final Field field = insertOperation.getClass()
        .getDeclaredField("astNode");
    field.setAccessible(true);
    final ASTNode node = (ASTNode) field.get(insertOperation);
    assertThat(node).isSameSourceCodeAs("n--;");
  }

  private Base getLastBase(final Variant variant) {
    final List<Base> bases = variant.getGene()
        .getBases();
    
    if (bases.size() == 0) {
      return null;
    }
    return bases.get(bases.size() - 1);
  }
}
