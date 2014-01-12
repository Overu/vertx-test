package com.overu.vertx.json.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.overu.vertx.json.JsonException;

public class JacksonUtil {

  private final static ObjectMapper mapper = new ObjectMapper();
  private final static ObjectMapper prettyMapper = new ObjectMapper();

  static {
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    prettyMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  @SuppressWarnings("unchecked")
  public static <T> T decodeValue(String str, Class<?> clazz) throws JsonException {
    try {
      return (T) mapper.readValue(str, clazz);
    } catch (Exception e) {
      throw new JsonException("Can't parse JSON string: " + e.getMessage());
    }
  }

  public static String encode(Object obj) throws JsonException {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new JsonException("Failed to encode as JSON: " + e.getMessage());
    }
  }

  public static String encodePrettily(Object obj) throws JsonException {
    try {
      return prettyMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new JsonException("Failed to encode as JSON: " + e.getMessage());
    }
  }

}
