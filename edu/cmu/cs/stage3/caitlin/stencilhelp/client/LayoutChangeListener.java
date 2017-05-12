package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

public abstract interface LayoutChangeListener
  extends EventListener
{
  public abstract boolean layoutChanged();
}
