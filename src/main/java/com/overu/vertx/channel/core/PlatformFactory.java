package com.overu.vertx.channel.core;

public interface PlatformFactory {

  boolean cancelTimer(int id);

  Net net();

  void scheduleDefferrd(VoidHandler handler);

  int setPeriodic(int delayMs, VoidHandler handler);

  Platform.Type type();

}
