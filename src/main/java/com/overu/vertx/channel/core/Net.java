package com.overu.vertx.channel.core;

import org.vertx.java.core.json.JsonObject;

public interface Net {

  WebSocket createWebSocket(String url, JsonObject options);

}
