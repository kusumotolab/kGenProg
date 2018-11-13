package jp.kusumotolab.kgenprog;

public class Counter {

  private long count;

  public Counter(final long initialValue) {
    count = initialValue;
  }

  public Counter() {
    count = 0;
  }

  public long getAndIncrement() {
    return count++;
  }
}
