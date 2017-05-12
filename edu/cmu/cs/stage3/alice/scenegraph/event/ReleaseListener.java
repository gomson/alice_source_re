package edu.cmu.cs.stage3.alice.scenegraph.event;

public abstract interface ReleaseListener
{
  public abstract void releasing(ReleaseEvent paramReleaseEvent);
  
  public abstract void released(ReleaseEvent paramReleaseEvent);
}
