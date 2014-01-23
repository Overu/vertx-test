package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;

public interface BusHook {

  @SuppressWarnings("rawtypes")
  boolean handlePreRegister(String address, Handler<? extends Message> handler);

  boolean handleReceivMessage(Message<?> message);

  <T> boolean handleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler);

  boolean handleUnregister(String address);

}
