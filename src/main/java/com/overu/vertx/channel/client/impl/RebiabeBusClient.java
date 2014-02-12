package com.overu.vertx.channel.client.impl;

import com.overu.vertx.channel.client.Bus;
import com.overu.vertx.channel.client.BusHook;
import com.overu.vertx.channel.client.Message;
import com.overu.vertx.channel.client.State;
import com.overu.vertx.channel.core.Handler;
import com.overu.vertx.channel.core.HandlerRegistration;
import com.overu.vertx.channel.core.Platform;
import com.overu.vertx.json.Json;
import com.overu.vertx.json.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RebiabeBusClient implements Bus {
  public final static String SEQUENCE_NUMBER = "_seq";
  public final static String ACKNOWLEDGE_DELAY_MILLIS = "acknowledgeDelayMillis";
  private final static Logger log = Logger.getLogger(RebiabeBusClient.class.getName());

  private final int acknowledgeDelayMillis;
  private final JsonObject pendings;
  private final JsonObject currentSequences;
  private final JsonObject knownHeadSequences;
  private final JsonObject acknowledgeScheduled;
  private final JsonObject acknowledgeNumber;
  private final SimpleBus delegate;
  private BusHook hook;

  public RebiabeBusClient(SimpleBus delegate) {
    this.delegate = delegate;
    JsonObject options = delegate.getOptions();
    acknowledgeDelayMillis =
        (options == null || !options.has(ACKNOWLEDGE_DELAY_MILLIS)) ? 3 * 1000 : (int) options.getNumber(ACKNOWLEDGE_DELAY_MILLIS);
    pendings = Json.createObject();
    currentSequences = Json.createObject();
    knownHeadSequences = Json.createObject();
    acknowledgeScheduled = Json.createObject();
    acknowledgeNumber = Json.createObject();

    delegate.setHook(new BusHook.BusHookProxy() {

      @SuppressWarnings("rawtypes")
      @Override
      public boolean handlePreRegister(String address, Handler<? extends Message> handler) {
        pendings.set(address, Json.createObject());
        return super.handlePreRegister(address, handler);
      }

      @Override
      public boolean handleReceivMessage(Message<?> message) {
        if (hook != null && !hook.handleReceivMessage(message)) {
          return false;
        }
        return onReceiveMessage(message);
      }

      @Override
      public boolean handleUnregister(String address) {
        pendings.remove(address);
        knownHeadSequences.remove(address);
        currentSequences.remove(address);
        acknowledgeScheduled.remove(address);
        acknowledgeNumber.remove(address);
        return super.handleUnregister(address);
      }

      @Override
      protected BusHook delegate() {
        return hook;
      }
    });
  }

  @Override
  public void close() {
    pendings.clear();
    knownHeadSequences.clear();
    currentSequences.clear();
    acknowledgeScheduled.clear();
    acknowledgeNumber.clear();

    delegate.close();
  }

  @Override
  public State getReadyState() {
    return delegate.getReadyState();
  }

  @Override
  public Bus publish(String address, Object message) {
    return delegate.publish(address, message);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public HandlerRegistration registerHandler(String address, Handler<? extends Message> handler) {
    return delegate.registerHandler(address, handler);
  }

  @Override
  public <T> Bus send(String address, Object message, Handler<Message<T>> replyHandler) {
    return delegate.send(address, message, replyHandler);
  }

  @Override
  public RebiabeBusClient setHook(BusHook hook) {
    this.hook = hook;
    return this;
  }

  public void synchronizeSequenceNumber(String address, double initialSequenceNumber) {
    currentSequences.set(address, initialSequenceNumber);
    knownHeadSequences.set(address, !knownHeadSequences.has(address) ? initialSequenceNumber : Math.max(initialSequenceNumber,
        knownHeadSequences.getNumber(address)));
    delegate.send(address + ".ack", initialSequenceNumber + 1, null);

  }

  protected boolean onReceiveMessage(Message<?> message) {
    String address = message.address();
    Object body = message.body();
    if (!(body instanceof JsonObject) || !((JsonObject) body).has(SEQUENCE_NUMBER)) {
      return true;
    }
    double sequence = ((JsonObject) body).getNumber(SEQUENCE_NUMBER);
    if (!currentSequences.has(address)) {
      currentSequences.set(address, sequence);
      knownHeadSequences.set(address, sequence);
      return true;
    }

    double currentSequence = currentSequences.getNumber(address);
    if (sequence <= currentSequence) {
      log.log(Level.CONFIG, "Old dup at sequence " + sequence + ", current is now ", currentSequence);
      return false;
    }

    JsonObject pending = pendings.getObject(address);
    JsonObject existing = pendings.getObject("" + sequence);
    if (existing != null) {
      assert sequence > currentSequence + 1 : "should not have pending data";
      log.log(Level.CONFIG, "Dup message: " + message);
      return false;
    }

    knownHeadSequences.set(address, Math.max(knownHeadSequences.getNumber(SEQUENCE_NUMBER), sequence));

    if (sequence > currentSequence + 1) {
      pending.set("" + sequence, message);
      log.log(Level.CONFIG, "Missed message, current sequence=" + currentSequence + " message sequence=" + sequence);
      scheduleAcknowledgment(address);
      return false;
    }

    assert sequence == currentSequence + 1 : "other cases should have been caught";
    String next;
    while (true) {
      delegate.doReceiveMessage(message);
      currentSequences.set(address, ++currentSequence);
      next = currentSequence + 1 + "";
      message = pending.get(next);
      if (message != null) {
        pending.remove(next);
      } else {
        break;
      }
    }

    assert !pendings.has(next);
    return false;
  }

  private void scheduleAcknowledgment(final String address) {
    if (!acknowledgeScheduled.has(address)) {
      acknowledgeScheduled.set(address, true);
      Platform.schedule().scheduleDelay(acknowledgeDelayMillis, new Handler<Void>() {

        @Override
        public void handle(Void event) {
          if (acknowledgeScheduled.has(address)) {
            acknowledgeScheduled.remove(address);
            double knownHeadSequence = knownHeadSequences.getNumber(address);
            double currentSequence = currentSequences.getNumber(address);
            if (knownHeadSequence > currentSequence
                && (!acknowledgeNumber.has(address) || knownHeadSequence > acknowledgeNumber.getNumber(address))) {
              acknowledgeNumber.set(address, knownHeadSequence);
              log.log(Level.CONFIG, "Catching up to " + knownHeadSequence);
              delegate.send(address + ".ack", currentSequence + 1, null);
            } else {
              log.log(Level.FINE, "No need to catchup");
            }
          }
        }

      });
    }
  }
}
