package com.overu.vertx.json.impl;

import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonFactory;
import com.overu.vertx.json.JsonObject;

import java.util.List;
import java.util.Map;

public class JreJsonFactory implements JsonFactory {

  @Override
  public JsonArray createArray() {
    return new JreJsonArray();
  }

  @Override
  public JsonObject createObject() {
    return new JreJsonObject();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T parse(String jsonString) {
    Object value = JacksonUtil.decodeValue(jsonString, Object.class);
    if (value instanceof Map) {
      return (T) new JreJsonObject((Map<String, Object>) value);
    } else if (value instanceof List) {
      return (T) new JreJsonArray((List<Object>) value);
    } else {
      return (T) value;
    }
  }

}
