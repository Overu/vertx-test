package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.core.Handler;

public class DefaultMessage<T> implements Message<T> {

  protected T body;
  protected Bus bus;
  protected String address;
  protected String replyAddress;
  protected boolean send;

  public DefaultMessage(boolean send, Bus bus, String address, String replyAddress, T body) {
    this.body = body;
    this.bus = bus;
    this.address = address;
    this.replyAddress = replyAddress;
    this.send = send;
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public T body() {
    return body;
  }

  @Override
  public void fail(int failureCode, String message) {

  }

  @Override
  public void reply(Object message) {
    sendReply(message, null);
  }

  @Override
  public <M> void reply(Object message, Handler<Message<M>> replyHandler) {
    sendReply(message, replyHandler);
  }

  @Override
  public String replyAdress() {
    return replyAddress;
  }

  @Override
  public String toString() {
    return body == null ? null : body.toString();
  }

  private <M> void sendReply(Object message, Handler<Message<M>> replyHandler) {
    if (bus != null && replyAddress != null) {
      bus.send(replyAddress, message, replyHandler);
    }
  }

}
