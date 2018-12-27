package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;

public class JDTOperationTest {

  @Test
  public void testApplyHandlingException() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final JDTOperation operation = new ExceptionOperation();
    final ProductSourcePath productSourcePath = new ProductSourcePath(rootPath, Src.FOO);
    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode = constructor
        .constructAST(Collections.singletonList(productSourcePath), Collections.emptyList());

    final GeneratedSourceCode applied =
        operation.apply(generatedSourceCode, new JDTASTLocation(productSourcePath, null, null));

    assertThat(applied).isInstanceOf(GenerationFailedSourceCode.class);
    assertThat(applied.isGenerationSuccess()).isFalse();
    assertThat(applied.getGenerationMessage()).isEqualTo("generation failed");
  }

  static class ExceptionOperation extends JDTOperation {

    @Override
    public <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
        final JDTASTLocation location, final ASTRewrite astRewrite) {
      throw new IllegalArgumentException("generation failed");
    }
  }
}
