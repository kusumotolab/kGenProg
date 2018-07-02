package jp.kusumotolab.kgenprog.project.jdt;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Range;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class JDTLocationTest {

  @Test
  public void testInferLineNumbers() {
    final TargetProject targetProject = TargetProjectFactory.create("example/example01");
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();

    GeneratedJDTAST ast = (GeneratedJDTAST) generatedSourceCode.getAST((new TargetSourceFile(
        Paths.get("example", "example01", "src", "jp", "kusumotolab", "BuggyCalculator.java"))));

    CompilationUnit root = ast.getRoot();
    TypeDeclaration type = (TypeDeclaration) root.types().get(0);
    MethodDeclaration method = type.getMethods()[0];

    Statement statement1 = (Statement) method.getBody().statements().get(0);
    Location location1 = new JDTLocation(null, statement1);
    assertThat(location1.inferLineNumbers(), is(new Range(4, 9)));

    Statement statement2 = (Statement) method.getBody().statements().get(1);
    Location location2 = new JDTLocation(null, statement2);
    assertThat(location2.inferLineNumbers(), is(new Range(10, 10)));
  }

}
