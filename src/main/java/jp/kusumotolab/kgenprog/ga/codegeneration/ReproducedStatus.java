package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.concurrent.atomic.AtomicInteger;

public class ReproducedStatus {

  public final boolean isGenerationSuccess;
  public final String generationMessage;
  private final AtomicInteger counter = new AtomicInteger(0);

  public ReproducedStatus(final boolean isGenerationSuccess, final String generationMessage) {
    this.isGenerationSuccess = isGenerationSuccess;
    this.generationMessage = generationMessage;
  }

  public void incrementCounter() {
    counter.incrementAndGet();
  }

  public int count() {
    return counter.get();
  }
}
