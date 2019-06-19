package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * すでに生成済みのソースコードの情報を保持するクラス
 */
public class ReproducedStatus {

  public final boolean isGenerationSuccess;
  public final String generationMessage;
  private final AtomicInteger counter = new AtomicInteger(0);

  /**
   * @param isGenerationSuccess コード生成に成功したかどうか
   * @param generationMessage ソースコードのハッシュ値
   */
  public ReproducedStatus(final boolean isGenerationSuccess, final String generationMessage) {
    this.isGenerationSuccess = isGenerationSuccess;
    this.generationMessage = generationMessage;
  }

  /**
   * 重複している回数を 1 増やす
   */
  public void incrementCounter() {
    counter.incrementAndGet();
  }

  /**
   * @return 重複している回数
   */
  public int count() {
    return counter.get();
  }
}
