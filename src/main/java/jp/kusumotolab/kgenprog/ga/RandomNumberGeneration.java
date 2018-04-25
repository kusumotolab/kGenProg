package jp.kusumotolab.kgenprog.ga;

import java.util.Random;

public class RandomNumberGeneration {

    public final static RandomNumberGeneration sharedInstance = new RandomNumberGeneration();

    private int seed;
    private Random random;

    private RandomNumberGeneration() {
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
}
