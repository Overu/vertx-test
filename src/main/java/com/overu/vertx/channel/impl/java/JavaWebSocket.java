package com.overu.vertx.channel.impl.java;

import com.overu.vertx.channel.core.WebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.logging.Logger;

public class JavaWebSocket implements WebSocket {

  private static final Logger log = Logger.getLogger(JavaWebSocket.class.getName());
  private static Charset charset = Charset.forName("UTF-8");
  private static CharsetDecoder decoder = charset.newDecoder();

  @SuppressWarnings("unused")
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
      public void onClose(int arg0, String arg1, boolean arg2) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onError(Exception arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onMessage(String arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onOpen(ServerHandshake arg0) {
        // TODO Auto-generated method stub

      }
    };
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
