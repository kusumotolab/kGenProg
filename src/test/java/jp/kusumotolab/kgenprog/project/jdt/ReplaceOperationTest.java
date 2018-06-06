package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.assertEquals;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

public class ReplaceOperationTest {

  @Test
  public void testReplaceStatement() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Block block = method.getBody();
    JDTLocation location = new JDTLocation(testSourceFile, block);

    // 置換対象生成
    AST jdtAST = ast.getRoot().getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    Statement statement = jdtAST.newExpressionStatement(invocation);
    Block replaceBlock = jdtAST.newBlock();
    replaceBlock.statements().add(statement);

    ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    GeneratedJDTAST newAST = (GeneratedJDTAST) code.getFiles().get(0);
    assertEquals("class A {\n  public void a(){\n    a();\n  }\n}\n", newAST.getRoot().toString());

  }

  @Test
  public void testReplaceStatementInList() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(1);
    JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 置換対象生成
    AST jdtAST = ast.getRoot().getAST();
    MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    Statement replaceStatement = jdtAST.newExpressionStatement(invocation);

    ReplaceOperation operation = new ReplaceOperation(replaceStatement);

    GeneratedSourceCode code = operation.apply(generatedSourceCode, location);
    GeneratedJDTAST newAST = (GeneratedJDTAST) code.getFiles().get(0);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n    a();\n  }\n}\n",
        newAST.getRoot().toString());

  }
}
