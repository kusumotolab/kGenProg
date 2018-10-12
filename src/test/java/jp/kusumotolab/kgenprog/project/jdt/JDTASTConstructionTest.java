package jp.kusumotolab.kgenprog.project.jdt;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;


public class JDTASTConstructionTest {

  @Test
  public void testConstrutASTWithFailedSourceCode() {
    final Path basePath = Paths.get("example/BuildFailure02");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    final JDTASTConstruction construction = new JDTASTConstruction();
    final GeneratedSourceCode generatedSourceCode = construction.constructAST(targetProject);

    assertThat(generatedSourceCode).isInstanceOf(GenerationFailedSourceCode.class);
    assertThat(generatedSourceCode.getGenerationMessage()).isNotBlank();
  }

}
