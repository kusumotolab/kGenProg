package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;

public class JDTOperationTest {

  @Test
  public void testApplyHandlingException() {
    final JDTOperation operation = new ExceptionOperation();

    final Path path = Paths.get("example/example01/src/jp/kusumotolab/BuggyCalculator.java");
    final TargetSourcePath sourcePath = new TargetSourcePath(path);

    final JDTASTConstruction constructor = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(constructor.constructAST(Collections.singletonList(sourcePath)));

    final GeneratedSourceCode applied =
        operation.apply(generatedSourceCode, new JDTLocation(sourcePath, null));

    assertThat(applied).isEqualTo(GenerationFailedSourceCode.GENERATION_FAILED);

  }

  static class ExceptionOperation implements JDTOperation {

    @Override
    public GeneratedSourceCode applyDirectly(final GeneratedSourceCode generatedSourceCode,
        final Location location) {
      throw new IllegalArgumentException();
    }

    @Override
    public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTLocation location,
        final ASTRewrite astRewrite) {
      throw new IllegalArgumentException();
    }

  }
}
