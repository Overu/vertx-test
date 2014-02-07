package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.BusHook;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.client.State;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;
import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonObject;

import java.util.logging.Logger;

public class RebiabeBusClient implements Bus {
  public final static String SEQUENCE_NUMBER = "_seq";
  private final static Logger log = Logger.getLogger(RebiabeBusClient.class.getName());
  private final JsonObject pendings;
  private final JsonObject currentSequences;
  private final JsonObject knownHeadSequences;
  private final SimpleBus delegate;
  private BusHook hook;

  public RebiabeBusClient(SimpleBus delegate) {
    this.delegate = delegate;
    pendings = Json.createObject();
    currentSequences = Json.createObject();
    knownHeadSequences = Json.createObject();

    delegate.setHook(new BusHook() {

      @Override
      public boolean handlePreRegister(String address, Handler<? extends Message> handler) {
        pendings.set(address, Json.createObject());
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
    });
  }

  @Override
  public void close() {
    delegate.close();
  }

  @Override
  public State getReadyState() {
    return delegate.getReadyState();
  }

  @Override
  public Bus publish(String address, Object message) {
    return delegate.publish(address, message);
  }

  @Override
  public HandlerRegistration registerHandler(String address, Handler<? extends Message> handler) {
    return delegate.registerHandler(address, handler);
  }

  @Override
  public <T> Bus send(String address, Object message, Handler<Message<T>> replyHandler) {
    return delegate.send(address, message, replyHandler);
  }

  @Override
  public RebiabeBusClient setHook(BusHook hook) {
    this.hook = hook;
    return this;
  }

}
