package com.overu.vertx.jsonTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overu.vertx.json.impl.JreJsonArray;
import com.overu.vertx.json.impl.JreJsonFactory;

public class JsonTest extends BaseMapTest {

  static final ObjectMapper o = new ObjectMapper();

  public void testDecode() {
    System.out.println("test!");
    JreJsonArray parse = (JreJsonArray) new JreJsonFactory().parse("[\"a\",\"b\", \"c\"]");
    System.out.print(parse.get(1));
  }
}
