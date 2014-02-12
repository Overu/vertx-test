package com.overu.vertx.channel.core;

import com.overu.vertx.json.JsonObject;

public interface Net {

  WebSocket createWebSocket(String url, JsonObject options);

}
