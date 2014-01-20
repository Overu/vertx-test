package com.overu.vertx.channel.core;

public class Platform {

  public enum Type {
    JAVA;
  }

  private static PlatformFactory FACTORY;

  public static boolean cancelTimer(int id) {
    return get().cancelTimer(id);
  }

  public static PlatformFactory get() {
    assert FACTORY == null : "You must register a platform first by invoke {Java|Android}Platform.register()";
    return FACTORY;
  }

  public static void handle(Object handler, Object event) {
    get().handle(handler, event);
  }

  public static Net net() {
    return get().net();
  }

  public static void scheduleDefferrd(Handler<Void> handler) {
    get().scheduleDefferrd(handler);
  }

  public static void setFactory(PlatformFactory factory) {
    FACTORY = factory;
  }

  public static int setPeriodic(int delayMs, Handler<Void> handler) {
    return get().setPeriodic(delayMs, handler);
  }

  public static Platform.Type type() {
    return get().type();
  }

  protected Platform() {
  }

}
