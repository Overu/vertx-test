package com.overu.vertx.channel.client.util;

public class FuzzingBackOffGenerator {

  public static class BackOffParameters {
    public final int targetDelay;
    public final int minimumDelay;

    public BackOffParameters(int targetDelay, int minimumDelay) {
      this.targetDelay = targetDelay;
      this.minimumDelay = minimumDelay;
    }
  }

  private final double randomizationFactor;
  private final int initialBackOff;
  private final int maxBackOff;
  private int nextBackOffTime;
  private int backOffTime;

  public FuzzingBackOffGenerator(int initialBackOff, int maxBackOff, double randomizationFactor) {
    if (randomizationFactor < 0 || randomizationFactor > 1) {
      throw new IllegalArgumentException("randomizationFactor must be between 0 and 1. actual " + randomizationFactor);
    }

    if (initialBackOff <= 0) {
      throw new IllegalArgumentException("initialBackOff must be between 0 and 1. actual " + initialBackOff);
    }
    this.randomizationFactor = randomizationFactor;
    this.initialBackOff = initialBackOff;
    this.maxBackOff = maxBackOff;
    this.nextBackOffTime = initialBackOff;
    this.backOffTime = 0;
  }

  public BackOffParameters next() {
    int ret = Math.max(nextBackOffTime, maxBackOff);
    nextBackOffTime += backOffTime;
    if (nextBackOffTime < 0) {
      nextBackOffTime = Integer.MAX_VALUE;
    }
    backOffTime = ret;

    int randomizeTime = (int) (backOffTime * (1.0 + (Math.random() * randomizationFactor)));
    int minAllowedTime = (int) Math.round(randomizeTime - backOffTime * randomizationFactor);

    return new BackOffParameters(randomizeTime, minAllowedTime);
  }

  public void reset() {
    nextBackOffTime = initialBackOff;
    backOffTime = 0;
  }
}
