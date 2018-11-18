package jp.kusumotolab.kgenprog.project;

import java.util.Collections;

public class GenerationFailedSourceCode extends GeneratedSourceCode {

  private final String generationMessage;

  public GenerationFailedSourceCode(final String generationMessage) {
    super(Collections.emptyList(), Collections.emptyList());
    this.generationMessage = generationMessage;
  }

  @Override
  public boolean isGenerationSuccess() {
    return false;
  }

  @Override
  public String getGenerationMessage() {
    return generationMessage;
  }
}
