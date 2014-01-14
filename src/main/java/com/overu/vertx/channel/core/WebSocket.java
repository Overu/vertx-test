package com.overu.vertx.channel.core;

import com.overu.vertx.json.JsonObject;

public interface WebSocket {

  interface WebSocketHandler {

    void onClose(JsonObject reason);

    void onError(String error);

    void onMessage(String message);

    void onOpen();

  }

  WebSocket EMPTY = new WebSocket() {

    @Override
    public void close() {
    }

    @Override
    public void send(String data) {
    }

    @Override
    public void setListen(WebSocketHandler handler) {
    }

  };

  void close();

  void send(String data);

  void setListen(WebSocketHandler handler);

}
