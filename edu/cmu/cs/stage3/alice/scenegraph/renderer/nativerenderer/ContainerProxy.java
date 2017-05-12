package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

public abstract class ContainerProxy
  extends ComponentProxy
{
  public ContainerProxy() {}
  
  public void onChildAdded(ComponentProxy componentProxy) {}
  
  public void onChildRemoved(ComponentProxy componentProxy) {}
}
