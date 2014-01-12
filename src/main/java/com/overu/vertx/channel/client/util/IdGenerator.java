package com.overu.vertx.channel.client.util;

import java.util.Random;

public class IdGenerator {
  static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
  static final char[] NUMBERS = "0123456789".toCharArray();

  private final Random random;

  public IdGenerator() {
    this(new Random());
  }

  public IdGenerator(Random random) {
    this.random = random;
  }

  public String next(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(ALPHABET[random.nextInt(36)]);
    }
    return sb.toString();
  }

  public String nextNumber(int length) {
    StringBuffer sb = new StringBuffer(length);
    for (int i = 0; i < length; i++) {
      sb.append(NUMBERS[random.nextInt(10)]);
    }
    return sb.toString();
  }
}
