package edu.cmu.cs.stage3.alice.core.event;

public abstract interface ObjectArrayPropertyListener
{
  public abstract void objectArrayPropertyChanging(ObjectArrayPropertyEvent paramObjectArrayPropertyEvent);
  
  public abstract void objectArrayPropertyChanged(ObjectArrayPropertyEvent paramObjectArrayPropertyEvent);
}
