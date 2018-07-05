package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.assertEquals;
import java.nio.file.Paths;
import java.util.Collections;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;

public class ReplaceOperationTest {

  @Test
  public void testReplaceStatement() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Block block = method.getBody();
    JDTLocation location = new JDTLocation(testSourceFile, block);

    // 置換対象生成
    Block replaceBlock = createReplacementBlockTarget();

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
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
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


  @Test
  public void testReplaceStatementDirectly() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Block block = method.getBody();
    JDTLocation location = new JDTLocation(testSourceFile, block);

    // 置換対象生成
    Block replaceBlock = createReplacementBlockTarget();
    ReplaceOperation operation = new ReplaceOperation(replaceBlock);

    operation.applyDirectly(generatedSourceCode, location);
    assertEquals("class A {\n  public void a(){\n    a();\n  }\n}\n", ast.getRoot().toString());

  }

  @Test
  public void testReplaceStatementInListDirectly() {
    String testSource = "class A{public void a(){int a = 0;a = 1;}}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("A.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, testSource);
    GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Collections.singletonList(ast));

    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody().statements().get(1);
    JDTLocation location = new JDTLocation(testSourceFile, statement);

    // 置換対象生成
    Statement replaceStatement = createReplacementTarget();
    ReplaceOperation operation = new ReplaceOperation(replaceStatement);

    operation.applyDirectly(generatedSourceCode, location);
    assertEquals("class A {\n  public void a(){\n    int a=0;\n    a();\n  }\n}\n",
        ast.getRoot().toString());

  }
  
  private Statement createReplacementTarget() {
    String target = "class B{ public void a() { a(); } }";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("B.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, target);
    
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    return (Statement) type.getMethods()[0].getBody().statements().get(0);
  }
  
  private Block createReplacementBlockTarget() {
    String target = "class B{ public void a() { a(); } }";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("B.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast = constructor.constructAST(testSourceFile, target);
    
    TypeDeclaration type = (TypeDeclaration) ast.getRoot().types().get(0);
    return type.getMethods()[0].getBody();
  }
}
