package edu.cmu.cs.stage3.alice.authoringtool.event;

public class AuthoringToolStateAdapter
  implements AuthoringToolStateListener
{
  public AuthoringToolStateAdapter() {}
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}
