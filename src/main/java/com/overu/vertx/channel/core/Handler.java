package com.overu.vertx.channel.core;

public interface Handler<H> {

  void handle(H event);

}
