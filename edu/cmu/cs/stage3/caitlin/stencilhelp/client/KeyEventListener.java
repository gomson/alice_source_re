package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.event.KeyEvent;
import java.util.EventListener;

public abstract interface KeyEventListener
  extends EventListener
{
  public abstract boolean keyTyped(KeyEvent paramKeyEvent);
  
  public abstract boolean keyPressed(KeyEvent paramKeyEvent);
  
  public abstract boolean keyReleased(KeyEvent paramKeyEvent);
}
