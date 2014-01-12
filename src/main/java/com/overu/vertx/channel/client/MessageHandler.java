package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;

public interface MessageHandler<T> extends Handler<Message<T>> {

  @Override
  public void handle(Message<T> event);

}
