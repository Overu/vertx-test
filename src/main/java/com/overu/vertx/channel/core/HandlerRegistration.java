package com.overu.vertx.channel.core;

public interface HandlerRegistration {

  HandlerRegistration EMPTY = new HandlerRegistration() {

    @Override
    public void unRegisterHandler() {
    }
  };

  void unRegisterHandler();

}
