package com.overu.vertx.channel.impl.java;

import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.Net;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.channel.core.Platform.Type;
import com.overu.vertx.channel.core.PlatformFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaPlatform implements PlatformFactory {

  public static void register() {
    Platform.setFactory(new JavaPlatform());
  }

  private final AtomicInteger timerId;
  private final Map<Integer, TimerTask> timers;
  private final Timer timer;
  protected JavaNet net;

  protected JavaPlatform() {
    timerId = new AtomicInteger(1);
    timers = new HashMap<Integer, TimerTask>();
    timer = new Timer(true);
  }

  @Override
  public boolean cancelTimer(int id) {
    if (timers.containsKey(id)) {
      timers.get(id).cancel();
      timers.remove(id);
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handle(Object handler, Object event) {
    ((Handler<Object>) handler).handle(event);
  }

  @Override
  public Net net() {
    return net == null ? new JavaNet() : net;
  }

  @Override
  public void scheduleDefferrd(final Handler<Void> handler) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        handler.handle(null);
      }
    }).start();
  }

  @Override
  public int setPeriodic(int delayMs, final Handler<Void> handler) {
    final int id = timerId.getAndIncrement();
    TimerTask task = new TimerTask() {

      @Override
      public void run() {
        handler.handle(null);
      }
    };
    timers.put(id, task);
    timer.scheduleAtFixedRate(task, delayMs, delayMs);
    return id;
  }

  @Override
  public Type type() {
    return Type.JAVA;
  }

}
