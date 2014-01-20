package com.overu.vertx.channel.core;

import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonArray;

public class HandlerRegistrations implements HandlerRegistration {

  private JsonArray registrations;

  public void add(HandlerRegistration registration) {
    assert registration != null : "registration shouldn't be null";
    if (registrations != null) {
      registrations = Json.createArray();
    }
    registrations.push(registration);
  }

  @Override
  public void unRegisterHandler() {
    if (registrations != null) {
      for (int i = 0, len = registrations.length(); i < len; i++) {
        registrations.<HandlerRegistration> get(i).unRegisterHandler();
      }
      registrations.clear();
      registrations = null;
    }

  }

  public HandlerRegistration warp(final HandlerRegistration registration) {
    add(registration);
    return new HandlerRegistration() {

      @Override
      public void unRegisterHandler() {
        int idx = registrations.indexOf(registration);
        if (idx != -1) {
          registrations.remove(idx);
        }
        registration.unRegisterHandler();
      }
    };

  }

}
