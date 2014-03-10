package com.overu.vertx.channel.impl.java;

import com.overu.vertx.channel.core.Net;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.channel.core.Platform.Type;
import com.overu.vertx.channel.core.PlatformFactory;
import com.overu.vertx.channel.core.Scheduler;

public class JavaPlatform implements PlatformFactory {

  public static void register() {
    Platform.setFactory(new JavaPlatform());
  }

  protected JavaNet net;
  protected JavaScheduler scheduler;

  protected JavaPlatform() {
    this(new JavaScheduler());
  }

  protected JavaPlatform(JavaScheduler scheduler) {
    net = new JavaNet();
    this.scheduler = scheduler;
  }

  @Override
  public Net net() {
    return net;
  }

  @Override
  public Scheduler schedule() {
    return this.scheduler;
  }

  @Override
  public Type type() {
    return Type.JAVA;
  }

}
