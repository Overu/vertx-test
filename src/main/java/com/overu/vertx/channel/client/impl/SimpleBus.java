package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.client.State;
import com.overu.vertx.channel.client.util.IdGenerator;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.channel.core.VoidHandler;
import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonObject;

import org.vertx.java.platform.impl.HAManager;

public class SimpleBus implements Bus {

  protected static void checkNotNull(String paramName, Object param) {
    if (param == null) {
      throw new IllegalArgumentException("Parameter " + paramName + " must be specified");
    }
  }

  protected final JsonObject handlerMap;
  protected final JsonObject repalyHandlers;
  private final IdGenerator idGenerator;
  private final boolean forkLocal;

  private State state = State.CONNECTION;

  public SimpleBus() {
    this(null);
  }

  public SimpleBus(JsonObject options) {
    handlerMap = Json.createObject();
    repalyHandlers = Json.createObject();
    idGenerator = new IdGenerator();
    state = State.OPEN;

    forkLocal = options != null && options.has("forkLocal") ? options.getBoolean("forkLocal") : false;

  }

  @Override
  public void close() {
    state = State.CLOSING;
    deliverMessage(LOCAL_ON_CLOSE, new DefaultMessage<Void>(false, null, LOCAL_ON_CLOSE, null, null));
    state = State.CLOSED;
    clearHandlers();
  }

  @Override
  public State getReadyState() {
    return state;
  }

  @Override
  public Bus publish(String address, Object message) {
    sendOrPub(false, address, message, null);
    return this;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public HandlerRegistration registerHandler(final String address, final Handler<? extends Message> handler) {
    doRegisterHandler(address, handler);
    return new HandlerRegistration() {

      @Override
      public void unRegisterHandler() {
        doUnregisterHandler(address, handler);
      }
    };
  }

  @Override
  public <T> Bus send(String address, Object message, Handler<Message<T>> replyHandler) {
    sendOrPub(true, address, message, replyHandler);
    return null;
  }

  protected void clearHandlers() {
    handlerMap.clear();
    repalyHandlers.clear();
  }

  protected void deliverMessage(String address, Message<?> message) {
    JsonArray handlers = handlerMap.getArray(address);
    if (handlers != null) {
      for (int i = 0, len = handlers.length(); i < len; i++) {
        scheduleHandle(handlers.get(i), message);
      }
    } else {
      Object handler = repalyHandlers.get(address);
      if (handler != null) {
        repalyHandlers.remove(address);
        scheduleHandle(handler, message);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  protected boolean doRegisterHandler(String address, Handler<? extends Message> handler) {
    checkNotNull("address", address);
    checkNotNull("handler", handler);
    JsonArray handlers = handlerMap.getArray(address);
    if (handlers == null) {
      handlerMap.set(address, Json.createArray().push(handler));
      return true;
    } else if (handlers.indexOf(handler) == -1) {
      handlers.push(handler);
    }
    return false;
  }

  @SuppressWarnings("rawtypes")
  protected boolean doUnregisterHandler(String address, Handler<? extends Message> handler) {
    assert address != null : "address shouldn't be null";
    assert handler != null : "handler shouldn't be null";
    JsonArray handlers = handlerMap.getArray(address);
    if (handlers != null) {
      int idx = handlers.indexOf(handler);
      if (idx != -1) {
        handlers.remove(idx);
      }
      if (handlers.length() == 0) {
        handlerMap.remove(address);
        return true;
      }
    }
    return false;
  }

  protected boolean isLocalFork(String address) {
    assert address != null : "address shouldn't be null";
    return forkLocal && address.startsWith(LOCAL);
  }

  protected String makeUUID() {
    return idGenerator.next(36);
  }

  protected void scheduleHandle(final Object handler, final Object message) {
    Platform.scheduleDefferrd(new Handler<Void>() {

      @Override
      public void handle(Void ignore) {
        Platform.handle(handler, message);
      }

    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected <T> void sendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler) {
    checkNotNull("address", address);
    String replyAddress = null;
    if (replyHandler != null) {
      replyAddress = makeUUID();
      repalyHandlers.set(replyAddress, replyHandler);
    }
    if (isLocalFork(address)) {
      address = address.substring(LOCAL.length());
      if (replyAddress != null) {
        replyAddress = LOCAL + replyAddress;
      }
    }
    deliverMessage(address, new DefaultMessage(send, this, address, replyAddress, message));
  }
}
