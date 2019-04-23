package jp.kusumotolab.kgenprog.project;

import java.util.Collections;
import jp.kusumotolab.kgenprog.ga.codegeneration.ReproducedStatus;

public class ReproducedSourceCode extends GeneratedSourceCode {

  private final boolean isGenerationSuccess;
  private final String generationMessage;

  public ReproducedSourceCode(final ReproducedStatus status) {
    super(Collections.emptyList(), Collections.emptyList());

    this.isGenerationSuccess = status.isGenerationSuccess();
    this.generationMessage = status.getGenerationMessage();
  }

  @Override
  public boolean isGenerationSuccess() {
    return isGenerationSuccess;
  }

  @Override
  public String getGenerationMessage() {
    return "(Reproduced Source Code) " + generationMessage;
  }

  @Override
  public boolean isReproducedSourceCode() {
    return true;
  }
}
