package edu.cmu.cs.stage3.media.event;

public abstract interface PlayerListener
{
  public abstract void stateChanged(PlayerEvent paramPlayerEvent);
  
  public abstract void endReached(PlayerEvent paramPlayerEvent);
}
