package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.assertEquals;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

public class InsertOperationTest {

  @Test
  public void testInsertStatement() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 挿入対象生成
    Statement insertStatement = createInsertionTarget();
    InsertOperation operation = new InsertOperation(insertStatement);

    GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    GeneratedJDTAST newAST = (GeneratedJDTAST) code.getFiles()
        .get(0);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n    a=1;\n    a();\n  }\n}\n",
        newAST.getRoot()
            .toString());

  }

  @Test
  public void testInsertStatementDirectly() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(1);
    JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 挿入対象生成
    Statement insertStatement = createInsertionTarget();
    InsertOperation operation = new InsertOperation(insertStatement);

    operation.applyDirectly(generatedSourceCode, location);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n    a=1;\n    a();\n  }\n}\n",
        ast.getRoot()
            .toString());

  }

  private Statement createInsertionTarget() {
    String target = "class B{ public void a() { a(); } }";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("B.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, target);

    TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    return (Statement) type.getMethods()[0].getBody()
        .statements()
        .get(0);
  }
}
