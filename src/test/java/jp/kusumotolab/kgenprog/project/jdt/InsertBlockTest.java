package jp.kusumotolab.kgenprog.project.jdt;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;


public class InsertBlockTest {

  @Test
  public void testInsertBlock() throws InvocationTargetException {
    final Path rootPath = Paths.get("example/BuildSuccess24");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);
    final ProductSourcePath sourcePath = new ProductSourcePath(rootPath, ExampleAlias.Src.FOO);
    final InsertBlockOperation operation = new InsertBlockOperation();
    final GeneratedJDTAST<ProductSourcePath> ast = getAST(source, sourcePath);
    final ASTLocation location = new JDTASTLocation(sourcePath, null, ast);
    final GeneratedSourceCode appliedSource = operation.apply(source, location);

    assertThat(appliedSource.isGenerationSuccess()).isTrue();
    assertThat(appliedSource.getProductAsts()).hasSize(1);
    final GeneratedJDTAST<ProductSourcePath> appliedAst = getAST(appliedSource, sourcePath);

    final Path compareRootPath = Paths.get("example/BuildSuccess01");
    final TargetProject compareTargetProject = TargetProjectFactory.create(compareRootPath);
    final GeneratedSourceCode compareSource =
        TestUtil.createGeneratedSourceCode(compareTargetProject);
    final GeneratedJDTAST<ProductSourcePath> compareAst = getAST(compareSource, sourcePath);

    assertThat(appliedAst.getRoot()).isSameSourceCodeAs(compareAst.getSourceCode());

  }

  private GeneratedJDTAST<ProductSourcePath> getAST(final GeneratedSourceCode source,
      final ProductSourcePath sourcePath) {
    return (GeneratedJDTAST<ProductSourcePath>) source.getProductAst(sourcePath);
  }
}
