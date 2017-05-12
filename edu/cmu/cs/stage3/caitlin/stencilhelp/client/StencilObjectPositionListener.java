package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Rectangle;
import java.util.EventListener;

public abstract interface StencilObjectPositionListener
  extends EventListener
{
  public abstract void stencilObjectMoved(Rectangle paramRectangle);
}
