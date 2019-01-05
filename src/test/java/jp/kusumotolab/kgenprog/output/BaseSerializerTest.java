package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class BaseSerializerTest {

  private Gson gson;

  @Before
  public void setup() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .create();
  }

  @Test
  public void testBase01() {
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
    check(base, serializedBase);
  }

  @Test
  public void testBase02() {
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

    final InsertOperation operation = new InsertOperation(insertStatement);
    final Base base = new Base(location, operation);
    final JsonObject serializedBase = gson.toJsonTree(base)
        .getAsJsonObject();

    // チェック
    check(base, serializedBase);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testBase03() {
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
    check(base, serializedBase);
  }

  private void check(final Base base, final JsonObject serializedBase) {
    // キーの存在チェック
    assertThat(serializedBase.keySet()).containsOnly(
        JsonKeyAlias.Base.FILE_NAME,
        JsonKeyAlias.Base.LINE_NUMBER_RANGE,
        JsonKeyAlias.Base.NAME,
        JsonKeyAlias.Base.SNIPPET);

    // ファイル名のチェック
    final String expectFqn = base.getTargetLocation()
        .getSourcePath().path.toString();
    final String serializedFqn = serializedBase.get(JsonKeyAlias.Base.FILE_NAME)
        .getAsString();
    assertThat(serializedFqn).isEqualTo(expectFqn);

    // 行範囲のチェック
    final JsonObject serializedLineNumberRange = serializedBase.get(
        JsonKeyAlias.Base.LINE_NUMBER_RANGE)
        .getAsJsonObject();
    final int serializedLineStart = serializedLineNumberRange.get(
        JsonKeyAlias.LineNumberRange.START)
        .getAsInt();
    final int serializedLineEnd = serializedLineNumberRange.get(
        JsonKeyAlias.LineNumberRange.END)
        .getAsInt();
    assertThat(serializedLineStart).isEqualTo(base.getTargetLocation()
        .inferLineNumbers().start);
    assertThat(serializedLineEnd).isEqualTo(base.getTargetLocation()
        .inferLineNumbers().end);

    // 操作名のチェック
    final String serializedOperationName = serializedBase.get(
        JsonKeyAlias.Base.NAME)
        .getAsString();
    assertThat(serializedOperationName).isEqualTo(base.getOperation()
        .getName());

    // コード片のチェック
    final String serializedSnippet = serializedBase.get(JsonKeyAlias.Base.SNIPPET)
        .getAsString();
    assertThat(serializedSnippet).isEqualTo(base.getOperation()
        .getTargetSnippet());
  }
}
