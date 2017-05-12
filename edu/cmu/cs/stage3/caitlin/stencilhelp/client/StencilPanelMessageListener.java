package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.util.EventListener;

public abstract interface StencilPanelMessageListener
  extends EventListener
{
  public static final int SHOW_MENU = 1;
  
  public abstract void messageReceived(int paramInt, Object paramObject);
}
