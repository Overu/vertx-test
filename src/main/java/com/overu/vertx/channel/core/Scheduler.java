package com.overu.vertx.channel.core;

public interface Scheduler {

  boolean cancelTimer(int id);

  void handle(Object handler, Object event);

  void scheduleDeferred(Handler<Void> handler);

  int scheduleDelay(int delayMs, Handler<Void> handler);

  int schedulePeriodic(int delayMs, Handler<Void> handler);

}
