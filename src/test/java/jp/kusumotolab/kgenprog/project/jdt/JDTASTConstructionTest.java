package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;


public class JDTASTConstructionTest {

  @Test
  public void testConstructAST() {
    final Path basePath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final JDTASTConstruction construction = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode = construction.constructAST(targetProject);
    final List<GeneratedAST<ProductSourcePath>> productAsts = generatedSourceCode.getProductAsts();

    assertThat(productAsts).hasSize(2)
        .extracting(GeneratedAST::getSourcePath)
        .extracting(p -> p.path)
        .containsExactlyInAnyOrder(FOO, BAR);

    final List<GeneratedAST<TestSourcePath>> testAsts = generatedSourceCode.getTestAsts();
    assertThat(testAsts).hasSize(2)
        .extracting(GeneratedAST::getSourcePath)
        .extracting(p -> p.path)
        .containsExactlyInAnyOrder(FOO_TEST, BAR_TEST);
  }

  @Test
  public void testConstrutASTWithFailedSourceCode() {
    final Path basePath = Paths.get("example/BuildFailure02");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final JDTASTConstruction construction = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode = construction.constructAST(targetProject);

    assertThat(generatedSourceCode).isInstanceOf(GenerationFailedSourceCode.class);
    assertThat(generatedSourceCode.getGenerationMessage()).isNotBlank();
  }

  @Test
  public void testConstructASTWithNoTestCode() {
    // テストクラスがない場合の確認
    final Path basePath = Paths.get("example/BuildSuccess13");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final JDTASTConstruction construction = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode = construction.constructAST(targetProject);
    final List<GeneratedAST<ProductSourcePath>> productAsts = generatedSourceCode.getProductAsts();

    assertThat(productAsts).hasSize(1)
        .extracting(GeneratedAST::getSourcePath)
        .extracting(p -> p.path)
        .containsExactlyInAnyOrder(FOO);

    // テストのASTはゼロのはず
    final List<GeneratedAST<TestSourcePath>> testAsts = generatedSourceCode.getTestAsts();
    assertThat(testAsts).hasSize(0);
  }
}
