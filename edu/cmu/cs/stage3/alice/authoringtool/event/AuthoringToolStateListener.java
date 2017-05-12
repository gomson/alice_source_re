package edu.cmu.cs.stage3.alice.authoringtool.event;

public abstract interface AuthoringToolStateListener
{
  public abstract void stateChanging(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void stateChanged(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldLoading(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldLoaded(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldUnLoading(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldUnLoaded(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldStarting(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldStarted(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldStopping(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldStopped(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldPausing(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldPaused(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldSaving(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
  
  public abstract void worldSaved(AuthoringToolStateChangedEvent paramAuthoringToolStateChangedEvent);
}
