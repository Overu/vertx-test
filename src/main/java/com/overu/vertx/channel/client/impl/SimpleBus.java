package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.client.State;
import com.overu.vertx.channel.client.util.IdGenerator;
import com.overu.vertx.channel.client.util.JsonUtil;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.channel.core.VoidHandler;
import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonObject;

public class SimpleBus implements Bus {

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
    return null;
  }

  @Override
  public Bus registerHandler(String address, Handler<? extends Message> handler) {
    return null;
  }

  @Override
  public <T> Bus send(String address, Object message, Handler<Message<T>> replyHandler) {
    return null;
  }

  @Override
  public Bus unRegisterHandler(String address, Handler<? extends Message> handler) {
    return null;
  }

  protected void checkNotNull(String paramName, Object param) {
    if (param == null) {
      throw new IllegalArgumentException("Parameter " + paramName + " must be specified");
    }
  }

  protected void clearHandlers() {
    handlerMap.clear();
    repalyHandlers.clear();
  }

  protected void deliverMessage(String address, Message message) {
    JsonArray handlers = handlerMap.getArray(address);
    if (handlers != null) {
      for (int i = 0, len = handlers.length(); i < len; i++) {
        scheduleHandle(message, handlers.get(i));
      }
    } else {
      JsonObject handler = repalyHandlers.getObject(address);
      if (handler != null) {
        repalyHandlers.remove(address);
        scheduleHandle(message, handler);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <T> void nativeHandle(T message, Object handler) {
    ((Handler<T>) handler).handle(message);
  }

  protected void scheduleHandle(final Object message, final Object handler) {
    Platform.scheduleDefferrd(new VoidHandler() {

      @Override
      protected void handle() {
        nativeHandle(message, handler);
      }

    });
  }
}
