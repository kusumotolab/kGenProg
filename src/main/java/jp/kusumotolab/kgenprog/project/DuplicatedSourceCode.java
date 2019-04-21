package jp.kusumotolab.kgenprog.project;

import java.util.Collections;

public class DuplicatedSourceCode extends GeneratedSourceCode {

  private final boolean isGenerationSuccess;
  private final String generationMessage;

  public DuplicatedSourceCode(final boolean isGenerationSuccess, final String generationMessage) {
    super(Collections.emptyList(), Collections.emptyList());

    this.isGenerationSuccess = isGenerationSuccess;
    this.generationMessage = generationMessage;
  }

  @Override
  public boolean isGenerationSuccess() {
    return isGenerationSuccess;
  }

  @Override
  public String getGenerationMessage() {
    return "(Duplicated Source Code) " + generationMessage;
  }
}
