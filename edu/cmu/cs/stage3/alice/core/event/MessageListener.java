package edu.cmu.cs.stage3.alice.core.event;

public abstract interface MessageListener
{
  public abstract void messageSent(MessageEvent paramMessageEvent);
}
