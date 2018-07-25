package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class JDTLocationTest {

  @Test
  public void testInferLineNumbers() {
    final Path rootPath = Paths.get("example/example01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();

    final Path path = rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java");
    final TargetSourcePath sourcePath = new TargetSourcePath(path);
    final GeneratedJDTAST ast = (GeneratedJDTAST) generatedSourceCode.getAst(sourcePath);

    final CompilationUnit root = ast.getRoot();
    final TypeDeclaration type = (TypeDeclaration) root.types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];

    final Statement statement1 = (Statement) method.getBody()
        .statements()
        .get(0);
    final ASTLocation location1 = new JDTASTLocation(null, statement1);

    assertThat(location1.inferLineNumbers()).isEqualTo(new LineNumberRange(4, 9));

    final Statement statement2 = (Statement) method.getBody()
        .statements()
        .get(1);
    final ASTLocation location2 = new JDTASTLocation(null, statement2);

    assertThat(location2.inferLineNumbers()).isEqualTo(new LineNumberRange(10, 10));
  }

}
