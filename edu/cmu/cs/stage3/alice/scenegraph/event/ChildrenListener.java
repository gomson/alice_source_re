package edu.cmu.cs.stage3.alice.scenegraph.event;

public abstract interface ChildrenListener
{
  public abstract void childAdded(ChildrenEvent paramChildrenEvent);
  
  public abstract void childRemoved(ChildrenEvent paramChildrenEvent);
}
