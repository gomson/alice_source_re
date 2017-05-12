package edu.cmu.cs.stage3.alice.core.event;

public abstract interface PropertyListener
{
  public abstract void propertyChanging(PropertyEvent paramPropertyEvent);
  
  public abstract void propertyChanged(PropertyEvent paramPropertyEvent);
}
