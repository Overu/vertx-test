package com.overu.vertx.channel.core;

public interface AsyncResult<T> {

  Throwable cause();

  boolean failed();

  T result();

}
