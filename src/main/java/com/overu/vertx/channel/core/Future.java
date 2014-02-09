package com.overu.vertx.channel.core;

public interface Future<T> extends AsyncResult<T> {

  boolean complete();

  Future<T> setFailure(Throwable throwable);

  Future<T> setHandler(Handler<AsyncResult<T>> handler);

  Future<T> setResult(T result);

}
