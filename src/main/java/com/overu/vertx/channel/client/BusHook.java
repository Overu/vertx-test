package com.overu.vertx.channel.client;

import com.overu.vertx.channel.core.Handler;

public interface BusHook {

  public static abstract class BusHookProxy implements BusHook {

    @Override
    public void handleOpened() {
      if (delegate() != null) {
        delegate().handleOpened();
      }
    }

    @Override
    public void handlePostClose() {
      if (delegate() != null) {
        delegate().handlePostClose();
      }
    }

    @Override
    public boolean handlePreClose() {
      return delegate() == null ? true : delegate().handlePreClose();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean handlePreRegister(String address, Handler<? extends Message> handler) {
      return delegate() == null ? true : delegate().handlePreRegister(address, handler);
    }

    @Override
    public boolean handleReceivMessage(Message<?> message) {
      return delegate() == null ? true : delegate().handleReceivMessage(message);
    }

    @Override
    public <T> boolean handleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler) {
      return delegate() == null ? true : delegate().handleSendOrPub(send, address, message, replyHandler);
    }

    @Override
    public boolean handleUnregister(String address) {
      return delegate() == null ? true : delegate().handleUnregister(address);
    }

    protected abstract BusHook delegate();

  }

  void handleOpened();

  void handlePostClose();

  boolean handlePreClose();

  @SuppressWarnings("rawtypes")
  boolean handlePreRegister(String address, Handler<? extends Message> handler);

  boolean handleReceivMessage(Message<?> message);

  <T> boolean handleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler);

  boolean handleUnregister(String address);

}
