package com.overu.vertx.channel.core;

public class Platform {

  public enum Type {
    JAVA;
  }

  private static PlatformFactory FACTORY;

  public static Net net() {
    return get().net();
  }

  public static Scheduler schedule() {
    return get().schedule();
  }

  public static void setFactory(PlatformFactory factory) {
    FACTORY = factory;
  }

  public static Platform.Type type() {
    return get().type();
  }

  private static PlatformFactory get() {
    assert FACTORY == null : "You must register a platform first by invoke {Java|Android}Platform.register()";
    return FACTORY;
  }

  protected Platform() {
  }

}
