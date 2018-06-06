package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.assertEquals;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
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
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    // 挿入位置のLocation生成
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(1);
    JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 挿入対象生成
    AST jdtAST = ast.getRoot().getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    InsertOperation operation = new InsertOperation(insertStatement);

    GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    GeneratedJDTAST newAST = (GeneratedJDTAST) code.getFiles().get(0);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n    a=1;\n    a();\n  }\n}\n",
        newAST.getRoot().toString());

  }

}
