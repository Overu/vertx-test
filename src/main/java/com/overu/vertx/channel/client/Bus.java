package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;

public interface Bus {

  String LOCAL = "@";
  String LOCAL_ON_OPEN = "local.onOpen";
  String LOCAL_ON_CLOSE = "local.onClose";
  String LOCAL_ON_ERROR = "local.onError";

  void close();

  State getReadyState();

  Bus publish(String address, Object message);

  @SuppressWarnings("rawtypes")
  HandlerRegistration registerHandler(String address, Handler<? extends Message> handler);

  <T> Bus send(String address, Object message, Handler<Message<T>> replyHandler);

}
