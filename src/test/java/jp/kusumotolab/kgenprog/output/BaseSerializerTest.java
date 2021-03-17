package jp.kusumotolab.kgenprog.output;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias.LineNumberRange;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class BaseSerializerTest {

  private final Gson gson = TestUtil.createGson();

  @Test
  public void testBaseGeneratedByDeletion() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 適当な場所を削除するoperationを作る
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location =
        new JDTASTLocation(new ProductSourcePath(basePath, Src.FOO), statement, ast);
    final DeleteOperation operation = new DeleteOperation();
    final Base base = new Base(location, operation);
    final JsonObject serializedBase = gson.toJsonTree(base)
        .getAsJsonObject();

    // チェック
    assertBase(base, serializedBase.toString());
  }

  @Test
  public void testBaseGeneratedByInsertion() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 適当な場所にコードを挿入するoperationを作る
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location =
        new JDTASTLocation(new ProductSourcePath(basePath, Src.FOO), statement, ast);
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);

    final InsertAfterOperation operation = new InsertAfterOperation(insertStatement);
    final Base base = new Base(location, operation);
    final JsonObject serializedBase = gson.toJsonTree(base)
        .getAsJsonObject();

    // チェック
    assertBase(base, serializedBase.toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testBaseGeneratedByReplacement() {
    final Path basePath = Paths.get("example/BuildSuccess01/");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);

    // 適当な場所のコードを適当なコードに置き換えるoperationを作る
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location =
        new JDTASTLocation(new ProductSourcePath(basePath, Src.FOO), statement, ast);
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("a"));
    statement = jdtAST.newExpressionStatement(invocation);
    final Block replaceBlock = jdtAST.newBlock();
    replaceBlock.statements()
        .add(statement);

    final ReplaceOperation operation = new ReplaceOperation(replaceBlock);
    final Base base = new Base(location, operation);
    final JsonObject serializedBase = gson.toJsonTree(base)
        .getAsJsonObject();

    // チェック
    assertBase(base, serializedBase.toString());
  }

  private void assertBase(final Base base, final String serializedBase) {
    final String expectFqn = base.getTargetLocation()
        .getSourcePath().path.toString();
    final DocumentContext context = JsonPath.parse(serializedBase);

    assertThatJson(serializedBase).isObject()
        .containsOnlyKeys(JsonKeyAlias.Base.FILE_NAME,
            JsonKeyAlias.Base.LINE_NUMBER_RANGE,
            JsonKeyAlias.Base.NAME,
            JsonKeyAlias.Base.SNIPPET);

    assertThatJson(serializedBase).node(JsonKeyAlias.Base.FILE_NAME)
        .isEqualTo(expectFqn);

    assertThatJson(serializedBase).node(JsonKeyAlias.Base.LINE_NUMBER_RANGE)
        .isObject()
        .containsOnlyKeys(LineNumberRange.START, LineNumberRange.END);
    assertThatJson(serializedBase).node(LineNumberRange.START)
        .isEqualTo(base.getTargetLocation()
            .inferLineNumbers().start);
    assertThatJson(serializedBase).node(LineNumberRange.END)
        .isEqualTo(base.getTargetLocation()
            .inferLineNumbers().end);

    assertThatJson(context).node(JsonKeyAlias.Base.NAME)
        .isEqualTo(base.getOperation()
            .getName());

    assertThatJson(context).node(JsonKeyAlias.Base.SNIPPET)
        .isEqualTo(base.getOperation()
            .getTargetSnippet());
  }
}
