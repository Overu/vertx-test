package com.overu.vertx.channel.core;

public interface PlatformFactory {

  Net net();

  Scheduler schedule();

  Platform.Type type();

}
