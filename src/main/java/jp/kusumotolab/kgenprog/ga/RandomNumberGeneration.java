package jp.kusumotolab.kgenprog.ga;

import java.util.Random;

public class RandomNumberGeneration {

  private int seed;
  private Random random;

  public RandomNumberGeneration() {
    setSeed(0);
  }

  public void setSeed(final int seed) {
    this.seed = seed;
    random = new Random(seed);
  }

  public int getSeed() {
    return seed;
  }

  public int getInt() {
    return random.nextInt();
  }

  public int getInt(final int divisor) {
    return Math.abs(random.nextInt()) % divisor;
  }

  public double getDouble(final double max) {
    return max * random.nextDouble();
  }

  public boolean getBoolean() {
    return random.nextBoolean();
  }
}
