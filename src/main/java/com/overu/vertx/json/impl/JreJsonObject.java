package com.overu.vertx.json.impl;

import com.overu.vertx.json.JsonArray;
import com.overu.vertx.json.JsonObject;
import com.overu.vertx.json.JsonType;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class JreJsonObject extends JreJsonElement implements JsonObject {

  private final static long serialVersionUID = -2848796364089017455L;

  @SuppressWarnings("unchecked")
  static Map<String, Object> convertMap(Map<String, Object> map) {
    Map<String, Object> converted = new LinkedHashMap<String, Object>(map.size());
    for (Entry<String, Object> entry : map.entrySet()) {
      Object obj = entry.getValue();
      if (obj instanceof Map) {
        Map<String, Object> jm = (Map<String, Object>) obj;
        converted.put(entry.getKey(), convertMap(jm));
      } else if (obj instanceof List) {
        List<Object> list = (List<Object>) obj;
        converted.put(entry.getKey(), JreJsonArray.convertList(list));
      } else {
        converted.put(entry.getKey(), obj);
      }
    }
    return converted;
  }

  protected Map<String, Object> map;

  public JreJsonObject() {
    this.map = new LinkedHashMap<String, Object>();
  }

  public JreJsonObject(Map<String, Object> map) {
    this.map = map;
  }

  public JreJsonObject(String jsonString) {
    this.map = JacksonUtil.decodeValue(jsonString, Map.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonObject clear() {
    if (needsCopy) {
      map = new LinkedHashMap<String, Object>();
      needsCopy = false;
    } else {
      map.clear();
    }
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JsonObject copy() {
    JreJsonObject copy = new JreJsonObject(map);
    copy.needsCopy = true;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    JreJsonObject that = (JreJsonObject) obj;

    for (Entry<String, Object> entry : this.map.entrySet()) {
      Object value = entry.getValue();
      if (value == null) {
        if (that.map.get(entry.getKey()) != null) {
          return false;
        }
      } else {
        if (!value.equals(that.map.get(entry.getKey()))) {
          return false;
        }
      }
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(String key) {
    Object value = map.get(key);
    if (value instanceof Map) {
      value = new JreJsonObject((Map<String, Object>) value);
    } else if (value instanceof List) {
      value = new JreJsonArray((List<Object>) value);
    }
    return (T) value;
  }

  @Override
  public JsonArray getArray(String key) {
    @SuppressWarnings("unchecked")
    List<Object> l = (List<Object>) map.get(key);
    return l == null ? null : new JreJsonArray(l);
  }

  @Override
  public boolean getBoolean(String key) {
    return (Boolean) map.get(key);
  }

  @Override
  public double getNumber(String key) {
    return ((Number) map.get(key)).doubleValue();
  }

  @Override
  public JsonObject getObject(String key) {
    @SuppressWarnings("unchecked")
    Map<String, Object> m = (Map<String, Object>) map.get(key);
    return m == null ? null : new JreJsonObject(m);
  }

  @Override
  public String getString(String key) {
    return (String) map.get(key);
  }

  @Override
  public JsonType getType(String key) {
    return super.getType(map.get(key));
  }

  @Override
  public boolean has(String key) {
    return map.containsKey(key);
  }

  @Override
  public String[] keys() {
    return map.keySet().toArray(new String[map.size()]);
  }

  @Override
  public JsonObject remove(String key) {
    checkCopy();
    map.remove(key);
    return this;
  }

  @Override
  public JsonObject set(String key, boolean bool_) {
    checkCopy();
    map.put(key, bool_);
    return this;
  }

  @Override
  public JsonObject set(String key, double number) {
    checkCopy();
    map.put(key, number);
    return this;
  }

  @Override
  public JsonObject set(String key, Object value) {
    checkCopy();
    if (value instanceof JreJsonObject) {
      value = ((JreJsonObject) value).map;
    } else if (value instanceof JreJsonArray) {
      value = ((JreJsonArray) value).list;
    }
    map.put(key, value);
    return this;
  }

  @Override
  public String toJsonString() {
    return JacksonUtil.encode(map);
  }

  @Override
  public Map<String, Object> toNative() {
    return map;
  }

  private void checkCopy() {
    if (needsCopy) {
      map = convertMap(map);
      needsCopy = false;
    }
  }

}
