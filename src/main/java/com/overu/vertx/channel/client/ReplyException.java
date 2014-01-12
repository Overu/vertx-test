package com.overu.vertx.channel.client;

public class ReplyException extends RuntimeException {

  private final ReplyFailure failureType;
  private final int failureCode;

  public ReplyException(ReplyFailure failureType) {
    super((String) null);
    this.failureType = failureType;
    this.failureCode = -1;
  }

  public ReplyException(ReplyFailure failureType, int failureCode, String message) {
    super(message);
    this.failureCode = failureCode;
    this.failureType = failureType;
  }

  public ReplyException(ReplyFailure failureType, String message) {
    super(message);
    this.failureCode = -1;
    this.failureType = failureType;
  }

  public int failureCode() {
    return failureCode;
  }

  public ReplyFailure failureType() {
    return failureType;
  }

}
