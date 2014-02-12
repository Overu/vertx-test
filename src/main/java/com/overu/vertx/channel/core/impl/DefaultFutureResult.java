package com.overu.vertx.channel.core.impl;

import com.overu.vertx.channel.core.AsyncResult;
import com.overu.vertx.channel.core.Future;
import com.overu.vertx.channel.core.Handler;

public class DefaultFutureResult<T> implements Future<T> {

  private boolean failed;
  private boolean succeeded;
  private Handler<AsyncResult<T>> handler;
  private T result;
  private Throwable throwable;

  public DefaultFutureResult() {
  }

  public DefaultFutureResult(T result) {
    setResult(result);
  }

  public DefaultFutureResult(Throwable t) {
    if (t == null) {
      setResult(null);
    } else {
      setFailure(t);
    }
  }

  @Override
  public Throwable cause() {
    return throwable;
  }

  @Override
  public boolean complete() {
    return failed || succeeded;
  }

  @Override
  public boolean failed() {
    return failed;
  }

  @Override
  public T result() {
    return result;
  }

  @Override
  public DefaultFutureResult<T> setFailure(Throwable throwable) {
    this.throwable = throwable;
    failed = true;
    checkCallHandler();
    return this;
  }

  @Override
  public DefaultFutureResult<T> setHandler(Handler<AsyncResult<T>> handler) {
    this.handler = handler;
    checkCallHandler();
    return this;
  }

  @Override
  public DefaultFutureResult<T> setResult(T result) {
    this.result = result;
    succeeded = true;
    checkCallHandler();
    return this;
  }

  public boolean succeeded() {
    return succeeded;
  }

  private void checkCallHandler() {
    if (handler != null && complete()) {
      handler.handle(this);
    }
  }

}
