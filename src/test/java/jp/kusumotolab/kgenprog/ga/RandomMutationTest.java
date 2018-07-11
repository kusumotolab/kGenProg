package jp.kusumotolab.kgenprog.ga;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
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

  private class StaticNumberGeneration extends RandomNumberGeneration {

    @Override
    public int getRandomNumber() {
      return 0;
    }

    @Override
    public int getRandomNumber(int divisor) {
      return 1;
    }

    @Override
    public boolean getRandomBoolean() {
      return true;
    }
  }

  @Test
  public void testExec() throws NoSuchFieldException, IllegalAccessException {
    final String basePath = "example/example01/";
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final Variant initialVariant = targetProject.getInitialVariant();
    final RandomMutation randomMutation = new RandomMutation(new StaticNumberGeneration());
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

    final float[] value = {0};
    final List<Suspiciouseness> suspiciousenesses = statements.stream()
        .map(e -> new JDTLocation(sourceFile, e))
        .map(e -> {
          value[0] += 0.1;
          return new Suspiciouseness(e, value[0]);
        })
        .collect(Collectors.toList());

    final List<Base> baseList = randomMutation.exec(suspiciousenesses);
    final Base base = baseList.get(0);
    final JDTLocation targetLocation = (JDTLocation) base.getTargetLocation();

    assertEquals(targetLocation.node.toString(), "return n;\n");

    final Operation operation = base.getOperation();
    assertTrue(operation instanceof InsertOperation);

    final InsertOperation insertOperation = (InsertOperation) operation;
    final Field field = insertOperation.getClass()
        .getDeclaredField("astNode");
    field.setAccessible(true);
    final ASTNode node = (ASTNode) field.get(insertOperation);
    assertEquals(node.toString(), "n--;\n");
  }
}
