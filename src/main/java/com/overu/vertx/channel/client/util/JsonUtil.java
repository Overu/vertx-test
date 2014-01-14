package com.overu.vertx.channel.client.util;

import org.vertx.java.core.json.JsonObject;

public class JsonUtil {

  public static JsonObject clear(JsonObject object) {
    object.toMap().clear();
    return object;
  }

  public static boolean isContainer(JsonObject object, String field) {
    if (object.toMap().containsKey(field)) {
      return true;
    }
    return false;
  }

}
