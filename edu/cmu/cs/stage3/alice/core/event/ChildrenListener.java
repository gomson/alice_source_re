package edu.cmu.cs.stage3.alice.core.event;

public abstract interface ChildrenListener
{
  public abstract void childrenChanging(ChildrenEvent paramChildrenEvent);
  
  public abstract void childrenChanged(ChildrenEvent paramChildrenEvent);
}
