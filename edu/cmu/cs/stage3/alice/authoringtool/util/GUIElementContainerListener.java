package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;




















public class GUIElementContainerListener
  extends ContainerAdapter
{
  public GUIElementContainerListener() {}
  
  protected static final GUIElementContainerListener staticListener = new GUIElementContainerListener();
  
  public static GUIElementContainerListener getStaticListener() {
    return staticListener;
  }
  

  public void componentRemoved(ContainerEvent ev)
  {
    if ((ev.getChild() instanceof Releasable)) {
      ((Releasable)ev.getChild()).release();
    }
  }
}
