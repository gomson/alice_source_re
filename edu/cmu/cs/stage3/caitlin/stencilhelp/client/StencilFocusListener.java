package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

public abstract interface StencilFocusListener
  extends EventListener
{
  public abstract void focusGained();
  
  public abstract void focusLost();
}
