package com.overu.vertx.channel.core;

public interface PlatformFactory {

  boolean cancelTimer(int id);

  Net net();

  void scheduleDefferrd(Handler<Void> handler);

  int setPeriodic(int delayMs, Handler<Void> handler);

  Platform.Type type();

}
