package jp.kusumotolab.kgenprog;

public class Counter {

  private long count = 0;

  public void increment() {
    count++;
  }

  public long getValue() {
    return count;
  }
}
