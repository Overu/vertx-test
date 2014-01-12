package com.overu.vertx.json;

import java.io.Serializable;

public interface JsonElement extends Serializable {

  <T extends JsonElement> T clear();

  <T extends JsonElement> T copy();

  boolean isArray();

  boolean isObject();

  String toJsonString();

}
