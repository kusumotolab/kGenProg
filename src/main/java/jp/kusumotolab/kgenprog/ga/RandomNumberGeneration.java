package jp.kusumotolab.kgenprog.ga;

import java.util.Random;
import static java.lang.Math.abs;

public class RandomNumberGeneration {

  private int seed;
  private Random random;

  public RandomNumberGeneration() {
    setSeed(0);
  }

  public void setSeed(int seed) {
    this.seed = seed;
    random = new Random(seed);
  }

  public int getSeed() {
    return seed;
  }

  public int getRandomNumber() {
    return random.nextInt();
  }

  public int getRandomNumber(int divisor) {
    return abs(random.nextInt()) % divisor;
  }

  public boolean getRandomBoolean() {
    return random.nextBoolean();
  }
}
