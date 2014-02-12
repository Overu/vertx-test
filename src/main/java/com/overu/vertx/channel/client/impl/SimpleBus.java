package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.BusHook;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.client.State;
import com.overu.vertx.channel.client.util.IdGenerator;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleBus implements Bus {
  public final static String MODE_MIX = "forkLocal";
  private final static Logger log = Logger.getLogger(SimpleBus.class.getName());

  protected static void checkNotNull(String paramName, Object param) {
    if (param == null) {
      throw new IllegalArgumentException("Parameter " + paramName + " must be specified");
    }
  }

  private final JsonObject options;
  protected final JsonObject handlerMap;
  protected final JsonObject repalyHandlers;
  private final IdGenerator idGenerator;
  private final boolean forkLocal;
  private State state = State.CONNECTION;
  private BusHook hook;

  public SimpleBus() {
    this(null);
  }

  public SimpleBus(JsonObject options) {
    this.options = options;
    handlerMap = Json.createObject();
    repalyHandlers = Json.createObject();
    idGenerator = new IdGenerator();
    state = State.OPEN;

    forkLocal = options != null && options.has(MODE_MIX) ? options.getBoolean(MODE_MIX) : false;

  }

  @Override
  public void close() {
    if (hook == null || hook.handlePreClose()) {
      doClose();
    }
  }

  public JsonObject getOptions() {
    return options == null ? null : options.copy();
  }

  @Override
  public State getReadyState() {
    return state;
  }

  @Override
  public SimpleBus publish(String address, Object message) {
    internalHandleSendOrPub(false, address, message, null);
    return this;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public HandlerRegistration registerHandler(final String address, final Handler<? extends Message> handler) {
    if (hook == null || hook.handlePreRegister(address, handler)) {
      doRegisterHandler(address, handler);
      return new HandlerRegistration() {
        @Override
        public void unRegisterHandler() {
          if (hook == null || hook.handleUnregister(address)) {
            doUnregisterHandler(address, handler);
          }
        }
      };
    }
    return HandlerRegistration.EMPTY;
  }

  @Override
  public <T> SimpleBus send(String address, Object message, Handler<Message<T>> replyHandler) {
    internalHandleSendOrPub(true, address, message, replyHandler);
    return this;
  }

  @Override
  public SimpleBus setHook(BusHook hook) {
    this.hook = hook;
    if (hook != null && state == State.OPEN) {
      hook.handleOpened();
    }
    return this;
  }

  protected void clearHandlers() {
    handlerMap.clear();
    repalyHandlers.clear();
  }

  protected void doClose() {
    state = State.CLOSING;
    doReceiveMessage(new DefaultMessage<Void>(false, null, LOCAL_ON_CLOSE, null, null));
    state = State.CLOSED;
    clearHandlers();
    if (hook != null) {
      hook.handlePostClose();
    }
  }

  protected void doReceiveMessage(Message<?> message) {
    String address = message.address();
    JsonArray handlers = handlerMap.getArray(address);
    if (handlers != null) {
      for (int i = 0, len = handlers.length(); i < len; i++) {
        scheduleHandle(address, handlers.get(i), message);
      }
    } else {
      Object handler = repalyHandlers.get(address);
      if (handler != null) {
        repalyHandlers.remove(address);
        scheduleHandle(address, handler, message);
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected <T> void doSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler) {
    checkNotNull("address", address);
    String replyAddress = null;
    if (replyHandler != null) {
      replyAddress = makeUUID();
    }
    boolean isLocal = isLocalFork(address);
    DefaultMessage msg =
        new DefaultMessage(send, this, isLocal ? address.substring(LOCAL.length()) : address, isLocal && replyHandler != null
            ? (LOCAL + replyAddress) : replyAddress, message);
    if (internalHandleReceiveMessage(msg) && replyHandler != null) {
      repalyHandlers.set(replyAddress, replyHandler);
    }
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

  @SuppressWarnings("rawtypes")
  protected boolean internalHandleReceiveMessage(Message message) {
    if (hook == null || hook.handleReceivMessage(message)) {
      doReceiveMessage(message);
      return true;
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

  protected void scheduleHandle(final String address, final Object handler, final Object message) {
    Platform.schedule().scheduleDeferred(new Handler<Void>() {

      @Override
      public void handle(Void ignore) {
        try {
          Platform.schedule().handle(handler, message);
        } catch (Exception e) {
          log.log(Level.WARNING, "Failed to handle on address: " + address, e);
          doReceiveMessage(new DefaultMessage<JsonObject>(false, null, LOCAL_ON_ERROR, null, Json.createObject().set("address", address)
              .set("cause", e).set("event", message)));
        }
      }

    });
  }

  private <T> void internalHandleSendOrPub(boolean send, String address, Object message, Handler<Message<T>> replyHandler) {
    if (hook == null || hook.handleSendOrPub(send, address, message, replyHandler)) {
      doSendOrPub(send, address, message, replyHandler);
    }
  }
}
