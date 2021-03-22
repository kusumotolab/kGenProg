package jp.kusumotolab.kgenprog.output;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import com.google.gson.Gson;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class PatchSerializerTest {

  private final Gson gson = TestUtil.createGson();
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();
  private final PatchGenerator patchGenerator = new PatchGenerator();

  private Variant createVariant(final Fitness fitness, final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject), new EmptyTestResults("for testing."), fitness,
        Collections.emptyList(), new OriginalHistoricalElement());
  }

  private Variant createVariant(final long id, final int generationNumber, final Fitness fitness,
      final GeneratedSourceCode code, final HistoricalElement historicalElement) {
    return new Variant(id, generationNumber, new Gene(Collections.emptyList()), code,
        new EmptyTestResults("for testing."), fitness, Collections.emptyList(), historicalElement);
  }

  /**
   * パッチが下の形式でシリアライズされるかテストする
   *
   * [{ "fileName":"ファイル名(fqn)", "diff":"元のプログラムとの差分" }]
   */
  @Test
  public void testPatch() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分を作るために適当な位置にコードを挿入する
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(rootPath, Paths.get("src/example/CloseToZero.java")), statement, ast);

    // 挿入操作を適用する
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("json"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);
    final InsertAfterOperation operation = new InsertAfterOperation(insertStatement);
    final Base appendBase = new Base(location, operation);
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final HistoricalElement historicalElement =
        new MutationHistoricalElement(initialVariant, appendBase);
    final Variant modifiedVariant =
        createVariant(1L, 1, new SimpleFitness(0.0d), code, historicalElement);

    final Patch patch = patchGenerator.exec(modifiedVariant);
    final String serializedPatches = gson.toJson(patch);

    assertThatJson(serializedPatches).isArray()
        .hasSize(1);
    assertThatJson(serializedPatches).inPath("$.[0].fileName")
        .isEqualTo("example.CloseToZero");
    assertThatJson(serializedPatches).inPath("$.[0].diff")
        .isString()
        .isEqualTo(
            "--- example.CloseToZero\n+++ example.CloseToZero\n@@ -22,6 +22,7 @@\n     } else {\n       n++;\n     }\n+\tjson();\n     return n;\n   }\n }");
  }
}
