package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.assertEquals;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;

public class DeleteOperationTest {

  @Test
  public void testDeleteStatement() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourcePath testSourcePath = new TargetSourcePath(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourcePath, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    JDTLocation location = new JDTLocation(testSourcePath, statement);
    DeleteOperation operation = new DeleteOperation();

    GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    GeneratedJDTAST newAST = (GeneratedJDTAST) code.getAsts()
        .get(0);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n  }\n}\n", newAST.getRoot()
        .toString());

  }


  @Test
  public void testDeleteStatementDirectly() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourcePath testSourcePath = new TargetSourcePath(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourcePath, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    JDTLocation location = new JDTLocation(testSourcePath, statement);
    DeleteOperation operation = new DeleteOperation();

    operation.applyDirectly(generatedSourceCode, location);

    assertEquals("class A {\n  public void a(){\n    int a=0;\n  }\n}\n", ast.getRoot()
        .toString());

  }

}
