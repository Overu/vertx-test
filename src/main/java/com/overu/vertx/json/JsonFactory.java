package com.overu.vertx.json;

public interface JsonFactory {

  JsonArray createArray();

  JsonObject createObject();

  <T> T parse(String jsonString);

}
