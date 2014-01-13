package com.overu.vertx.json.impl;

import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonElement;
import com.overu.vertx.json.JsonObject;
import com.overu.vertx.json.JsonType;

import java.util.List;
import java.util.Map;

public abstract class JreJsonElement implements JsonElement {

  private final static long serialVersionUID = 3661435771481171596L;

  protected boolean needsCopy;

  @Override
  public boolean isArray() {
    return this instanceof JsonArray;
  }

  @Override
  public boolean isObject() {
    return this instanceof JsonObject;
  }

  public abstract Object toNative();

  @Override
  public String toString() {
    return toJsonString();
  }

  protected JsonType getType(Object value) {
    if (value instanceof Map) {
      return JsonType.OBJECT;
    } else if (value instanceof List) {
      return JsonType.ARRAY;
    } else if (value instanceof String) {
      return JsonType.STRING;
    } else if (value instanceof Number) {
      return JsonType.NUMBER;
    } else if (value instanceof Boolean) {
      return JsonType.BOOLEAN;
    } else if (value == null) {
      return JsonType.NULL;
    }
    throw new IllegalArgumentException("Invalid JSON type: " + value.getClass().getName());
  }
}
