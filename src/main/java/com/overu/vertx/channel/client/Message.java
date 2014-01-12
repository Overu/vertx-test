package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;

public interface Message<M> {

  String address();

  M body();

  void fail(int failureCode, String message);

  void reply(Object message);

  @SuppressWarnings("hiding")
  <M> void reply(Object message, Handler<Message<M>> replyHandler);

  String replyAdress();

}
