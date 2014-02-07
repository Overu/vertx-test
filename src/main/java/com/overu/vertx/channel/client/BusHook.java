package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;

public interface BusHook {

  public static abstract class BusHookProxy implements BusHook {

    @Override
    public boolean handlePreRegister(String address, Handler<? extends Message> handler) {
      return false;
    }

    @Override
    public boolean handleReceivMessage(Message<?> message) {
      return false;
    }

    @Override
    public <T> boolean handleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler) {
      return false;
    }

    @Override
    public boolean handleUnregister(String address) {
      return false;
    }

    protected abstract BusHook delegate();

  }

  @SuppressWarnings("rawtypes")
  boolean handlePreRegister(String address, Handler<? extends Message> handler);

  boolean handleReceivMessage(Message<?> message);

  <T> boolean handleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler);

  boolean handleUnregister(String address);

}
