package com.overu.vertx.channel.impl.java;

import com.overu.vertx.channel.core.WebSocket;
import com.overu.vertx.json.Json;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaWebSocket implements WebSocket {

  private static final Logger log = Logger.getLogger(JavaWebSocket.class.getName());
  private static Charset charset = Charset.forName("UTF-8");
  private static CharsetDecoder decoder = charset.newDecoder();

  private static String toString(ByteBuffer buffer) throws CharacterCodingException {
    String data = null;
    int old_position = buffer.position();
    data = decoder.decode(buffer).toString();
    buffer.position(old_position);
    return data;
  }

  private WebSocketClient socket;
  private WebSocketHandler eventHandler;

  public JavaWebSocket(String uri) {
    URI serverUri = null;
    try {
      serverUri = new URI(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    socket = new WebSocketClient(serverUri, new Draft_17()) {

      @Override
      public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocket closed, code=" + code + ", reason=" + reason + ", remote=" + remote);
        if (eventHandler == null) {
          return;
        }
        eventHandler.onClose(Json.createObject().set("code", code).set("reason", reason).set("remote", remote));
      }

      @Override
      public void onError(Exception e) {
        log.log(Level.SEVERE, "Websocket Failed With Exception", e);
        if (eventHandler == null) {
          return;
        }

        String message = e.getMessage();
        eventHandler.onError(message == null ? e.getClass().getSimpleName() : message);
      }

      @Override
      public void onMessage(ByteBuffer buffer) {
        final String message;
        try {
          message = JavaWebSocket.toString(buffer);
        } catch (CharacterCodingException e) {
          log.log(Level.SEVERE, "Websocket Failed when Charset Decoding", e);
          return;
        }

        log.finest("Websocket Received: " + message);
        if (eventHandler == null) {
          return;
        }
        eventHandler.onMessage(message);
      }

      @Override
      public void onMessage(String message) {
        log.finest("Websocket received: " + message);
        if (eventHandler == null) {
          return;
        }
        eventHandler.onMessage(message);
      }

      @Override
      public void onOpen(ServerHandshake handshake) {
        log.info("Websocket Connected");
        if (eventHandler == null) {
          return;
        }
        eventHandler.onOpen();
      }
    };

    socket.connect();
  }

  @Override
  public void close() {
    socket.close();
  }

  @Override
  public void send(String data) {
    try {
      log.finest("Websocket send: " + data);
      socket.getConnection().send(data);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setListen(WebSocketHandler handler) {
    this.eventHandler = handler;
  }

}
