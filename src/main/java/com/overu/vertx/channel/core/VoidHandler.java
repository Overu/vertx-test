package com.overu.vertx.channel.core;

public abstract class VoidHandler implements Handler<Void> {

  @Override
  public void handle(Void event) {
    handle();
  }

  protected abstract void handle();

}
