package com.overu.vertx.channel.impl.java;

import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaScheduler implements Scheduler {

  private final ScheduledExecutorService executor;
  private final AtomicInteger timerId;
  private final Map<Integer, ScheduledFuture<?>> timers;

  protected JavaScheduler() {
    executor = Executors.newScheduledThreadPool(4);
    timerId = new AtomicInteger(0);
    timers = new HashMap<Integer, ScheduledFuture<?>>();

    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        try {
          executor.shutdown();
          executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
      }

    });
  }

  @Override
  public boolean cancelTimer(int id) {
    if (timers.containsKey(id)) {
      return timers.remove(id).cancel(false);
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handle(Object handler, Object event) {
    ((Handler<Object>) handler).handle(event);
  }

  @Override
  public void scheduleDeferred(final Handler<Void> handler) {
    executor.execute(new Runnable() {

      @Override
      public void run() {
        handler.handle(null);
      }
    });
  }

  @Override
  public int scheduleDelay(int delayMs, final Handler<Void> handler) {
    final int id = timerId.getAndIncrement();
    ScheduledFuture<?> future = executor.schedule(new Runnable() {

      @Override
      public void run() {
        timers.remove(id);
        handler.handle(null);
      }
    }, delayMs, TimeUnit.MILLISECONDS);
    timers.put(id, future);
    return id;
  }

  @Override
  public int schedulePeriodic(int delayMs, final Handler<Void> handler) {
    final int id = timerId.getAndIncrement();
    ScheduledFuture<?> future = executor.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        handler.handle(null);
      }
    }, delayMs, delayMs, TimeUnit.MILLISECONDS);
    timers.put(id, future);
    return id;
  }
}
