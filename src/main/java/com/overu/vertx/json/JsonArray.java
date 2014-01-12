package com.overu.vertx.json;

public interface JsonArray extends JsonElement {

  @SuppressWarnings("unchecked")
  @Override
  JsonArray clear();

  @SuppressWarnings("unchecked")
  @Override
  JsonArray copy();

  <T> T get(int index);

  JsonArray getArray(int index);

  boolean getBoolean(int index);

  double getNumber(int index);

  JsonObject getObject(int index);

  String getString(int index);

  JsonType getType(int index);

  int indexOf(Object value);

  JsonArray insert(int index, Object value);

  int length();

  JsonArray push(boolean bool_);

  JsonArray push(double number);

  JsonArray push(Object value);

  JsonArray remove(int index);

}
