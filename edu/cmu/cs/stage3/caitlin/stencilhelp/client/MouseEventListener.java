package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventListener;

public abstract interface MouseEventListener
  extends EventListener
{
  public abstract boolean contains(Point paramPoint);
  
  public abstract boolean mousePressed(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseReleased(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseClicked(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseEntered(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseExited(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseMoved(MouseEvent paramMouseEvent);
  
  public abstract boolean mouseDragged(MouseEvent paramMouseEvent);
}
