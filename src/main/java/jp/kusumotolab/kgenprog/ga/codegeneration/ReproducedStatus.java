package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.concurrent.atomic.AtomicInteger;

public class ReproducedStatus {

  private final boolean isGenerationSuccess;
  private final String generationMessage;
  private final AtomicInteger counter = new AtomicInteger(0);

  public ReproducedStatus(final boolean isGenerationSuccess, final String generationMessage) {
    this.isGenerationSuccess = isGenerationSuccess;
    this.generationMessage = generationMessage;
  }

  public boolean isGenerationSuccess() {
    return isGenerationSuccess;
  }

  public String getGenerationMessage() {
    return generationMessage;
  }

  public void incrementCounter() {
    counter.addAndGet(1);
  }

  public int count() {
    return counter.get();
  }
}
