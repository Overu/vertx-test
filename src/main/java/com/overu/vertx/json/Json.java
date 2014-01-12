package com.overu.vertx.json;

public class Json {

  private static JsonFactory FACTORY;

  public static JsonArray createArray() {
    return FACTORY.createArray();
  }

  public static JsonObject createObject() {
    return FACTORY.createObject();
  }

  public static <T> T parse(String jsonString) {
    return FACTORY.parse(jsonString);
  }

  public static void setFactory(JsonFactory factory) {
    FACTORY = factory;
  }

}
