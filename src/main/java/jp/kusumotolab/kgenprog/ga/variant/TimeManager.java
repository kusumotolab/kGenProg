package jp.kusumotolab.kgenprog.ga.variant;

public class TimeManager {
  private double totalTime;

  public double getTime() {
    return totalTime;
  }
  public void addTime(double time) {
    totalTime += !Double.isNaN(time) ? time : 0d;
  }
}
