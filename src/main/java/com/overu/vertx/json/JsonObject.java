package com.overu.vertx.json;

public interface JsonObject extends JsonElement {

  @SuppressWarnings("unchecked")
  @Override
  JsonObject clear();

  @SuppressWarnings("unchecked")
  @Override
  JsonObject copy();

  <T> T get(String key);

  JsonArray getArray(String key);

  boolean getBoolean(String key);

  double getNumber(String key);

  JsonObject getObject(String key);

  String getString(String key);

  JsonType getType(String key);

  boolean has(String key);

  String[] keys();

  JsonObject remove(String key);

  JsonObject set(String key, boolean bool_);

  JsonObject set(String key, double number);

  JsonObject set(String key, Object value);

}
