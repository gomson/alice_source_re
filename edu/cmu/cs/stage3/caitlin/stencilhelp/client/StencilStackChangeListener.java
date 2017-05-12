package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

public abstract interface StencilStackChangeListener
  extends EventListener
{
  public abstract void numberOfStencilsChanged(int paramInt);
  
  public abstract void currentStencilChanged(int paramInt);
}
