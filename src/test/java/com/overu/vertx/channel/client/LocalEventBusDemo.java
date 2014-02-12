package com.overu.vertx.channel.client;

import com.overu.vertx.channel.client.impl.SimpleBus;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;
import com.overu.vertx.channel.impl.java.JavaPlatform;

import org.junit.Assert;

import java.io.IOException;

public class LocalEventBusDemo {

  static class Any {
    String str;
    LocalEventBusDemo demo;

    public Any(String str, LocalEventBusDemo demo) {
      this.str = str;
      this.demo = demo;
    }
  }

  private static HandlerRegistration handlerRegistration;

  static {
    JavaPlatform.register();
  }

  public static void main(String[] arge) throws IOException {
    System.out.println("start!");

    Bus bus = new SimpleBus();
    final LocalEventBusDemo demo = new LocalEventBusDemo();

    handlerRegistration = bus.registerHandler("someaddress", new MessageHandler<Any>() {

      @Override
      public void handle(Message<Any> event) {
        Assert.assertEquals("some string", event.body().str);
        Assert.assertSame(demo, event.body().demo);

        event.reply("reply");

        handlerRegistration.unRegisterHandler();
        handlerRegistration = null;
      }
    });

    bus.send("someaddress", new Any("some string", demo), new Handler<Message<String>>() {

      @Override
      public void handle(Message<String> event) {
        Assert.assertEquals("reply", event.body());

        System.exit(0);
      }
    });

    System.in.read();

  }
}
