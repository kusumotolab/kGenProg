package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.Comparator;
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
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;

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
    final String basePath = "example/example01/";
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final RandomMutation randomMutation = new RandomMutation(10, new TestNumberGeneration());
    randomMutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getFiles());

    final GeneratedAST generatedAST = initialVariant.getGeneratedSourceCode()
        .getFiles()
        .stream()
        .sorted(Comparator.comparing(x -> x.getSourceFile().path))
        .collect(Collectors.toList())
        .get(0);
    final SourceFile sourceFile = generatedAST.getSourceFile();
    final CompilationUnit root = (CompilationUnit) ((GeneratedJDTAST) generatedAST).getRoot()
        .getRoot()
        .getRoot();
    final TypeDeclaration typeRoot = (TypeDeclaration) root.types()
        .get(0);
    final List<Statement> statements = typeRoot.getMethods()[0].getBody()
        .statements();

    final double[] value = {0.8};
    final List<Suspiciouseness> suspiciousenesses = statements.stream()
        .map(e -> new JDTLocation(sourceFile, e))
        .map(e -> {
          value[0] += 0.1;
          return new Suspiciouseness(e, value[0]);
        })
        .collect(Collectors.toList());

    // 正しく10個のBaseが生成されるかのテスト
    final List<Base> baseList = randomMutation.exec(suspiciousenesses);
    assertThat(baseList.size(), is(10));


    // Suspiciousenessが高い場所ほど多くの操作が生成されているかのテスト
    final Map<String, List<Base>> map = baseList.stream()
        .collect(Collectors.groupingBy(e -> ((JDTLocation) e.getTargetLocation()).node.toString()));
    final String weakSuspiciouseness = ((JDTLocation) suspiciousenesses.get(0)
        .getLocation()).node
        .toString();
    final String strongSuspiciouseness = ((JDTLocation) suspiciousenesses.get(1)
        .getLocation()).node
        .toString();
    final boolean result = map.get(weakSuspiciouseness)
        .size() < map.get(strongSuspiciouseness)
        .size();
    assertTrue(result);

    // TestNumberGenerationにしたがってOperationが生成されているかのテスト
    final Base base = baseList.get(0);
    final JDTLocation targetLocation = (JDTLocation) base.getTargetLocation();
    assertThat(targetLocation.node.toString(), is("return n;\n"));

    final Operation operation = base.getOperation();
    assertThat(operation instanceof InsertOperation, is(true));

    final InsertOperation insertOperation = (InsertOperation) operation;
    final Field field = insertOperation.getClass()
        .getDeclaredField("astNode");
    field.setAccessible(true);
    final ASTNode node = (ASTNode) field.get(insertOperation);
    assertThat(node.toString(), is("n--;\n"));
  }
}
