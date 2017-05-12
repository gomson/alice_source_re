package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Vector;

public class Hole
  implements StencilObject, MouseEventListener, KeyEventListener, LayoutChangeListener
{
  Vector shapes = new Vector();
  Vector stencilObjectPositionListeners = new Vector();
  ObjectPositionManager positionManager = null;
  StencilApplication stencilApp = null;
  StencilManager stencilManager = null;
  RoundRectangle2D.Double rect = null;
  String id = null;
  protected boolean isModified = true;
  protected boolean isInitialized = false;
  protected Rectangle previousRect = null;
  
  protected boolean autoAdvance = false;
  protected int advanceEvent = 0;
  
  public static final int ADVANCE_ON_CLICK = 0;
  
  public static final int ADVANCE_ON_PRESS = 1;
  public static final int ADVANCE_ON_ENTER = 2;
  
  public Hole(String id, ObjectPositionManager positionManager, StencilApplication stencilApp, StencilManager stencilManager)
  {
    this.positionManager = positionManager;
    this.stencilApp = stencilApp;
    this.id = id;
    this.stencilManager = stencilManager;
  }
  



  public Vector getShapes() { return shapes; }
  
  public Rectangle getRectangle() {
    if (rect != null)
      return rect.getBounds();
    return null;
  }
  
  public Rectangle getPreviousRectangle() { if (previousRect != null) {
      return previousRect.union(rect.getBounds());
    }
    
    if (rect != null) {
      return rect.getBounds();
    }
    

    return null;
  }
  
  public boolean isModified() {
    if (isModified) {
      isModified = false;
      return true; }
    return false;
  }
  
  public boolean intersectsRectangle(Rectangle rect) { if (this.rect != null)
      return rect.intersects(getRectangle());
    return false;
  }
  
  public Point getNotePoint() { if (rect != null) return new Point((int)rect.getX(), (int)rect.getY());
    return new Point(0, 0);
  }
  
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener) { stencilObjectPositionListeners.addElement(posListener); }
  
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener) {
    stencilObjectPositionListeners.remove(posListener);
  }
  
  public String getComponentID() { return id; }
  
  public void setAutoAdvance(boolean autoAdvance, int advanceEvent)
  {
    this.autoAdvance = autoAdvance;
    this.advanceEvent = advanceEvent;
  }
  
  public boolean getAutoAdvance() { return autoAdvance; }
  
  public int getAdvanceEvent() {
    return advanceEvent;
  }
  

  public boolean contains(Point point)
  {
    if (rect != null)
    {
      Rectangle smallRect = rect.getBounds();
      smallRect.grow(-4, -4);
      return smallRect.contains(point.getX(), point.getY()); }
    return false;
  }
  
  public boolean mousePressed(MouseEvent e) { stencilApp.handleMouseEvent(e);
    
    if ((autoAdvance) && (advanceEvent == 1) && ((e.getModifiers() & 0x10) == 16))
    {


      stencilManager.showNextStencil();
      return true; }
    return false;
  }
  
  public boolean mouseReleased(MouseEvent e) { stencilApp.handleMouseEvent(e);
    return false;
  }
  
  public boolean mouseClicked(MouseEvent e) { stencilApp.handleMouseEvent(e);
    if ((autoAdvance) && (advanceEvent == 0) && ((e.getModifiers() & 0x10) == 16)) {
      try {
        Thread.sleep(700L);
      } catch (Exception localException) {}
      stencilManager.showNextStencil();
      return true; }
    return false;
  }
  
  public boolean mouseEntered(MouseEvent e) { stencilApp.handleMouseEvent(e);
    return false;
  }
  
  public boolean mouseExited(MouseEvent e) { stencilApp.handleMouseEvent(e);
    return false;
  }
  
  public boolean mouseMoved(MouseEvent e) { stencilApp.handleMouseEvent(e);
    return false;
  }
  
  public boolean mouseDragged(MouseEvent e) { stencilApp.handleMouseEvent(e);
    return false;
  }
  
  public boolean keyTyped(KeyEvent e)
  {
    return false;
  }
  
  public boolean keyPressed(KeyEvent e) { return false; }
  
  public boolean keyReleased(KeyEvent e) {
    if ((autoAdvance) && (advanceEvent == 2) && (e.getKeyCode() == 10)) {
      stencilManager.showNextStencil();
      return true;
    }
    return false;
  }
  
  protected void setInitialPosition()
  {
    Rectangle r = positionManager.getInitialBox(id);
    if (r != null) {
      rect = new RoundRectangle2D.Double(x - 2, y - 2, width + 4, height + 4, 10.0D, 10.0D);
      shapes.removeAllElements();
      shapes.addElement(new ScreenShape(null, rect, false, 0));
    }
    isModified = true;
    isInitialized = true;
  }
  
  public boolean updatePosition() { previousRect = getRectangle();
    Rectangle r = positionManager.getBoxForID(id);
    if (r != null) {
      rect = new RoundRectangle2D.Double(x - 2, y - 2, width + 4, height + 4, 10.0D, 10.0D);
      shapes.removeAllElements();
      shapes.addElement(new ScreenShape(null, rect, false, 0));
      
      isModified = true;
      

      return true;
    }
    isModified = true;
    

    return false;
  }
  
  public boolean layoutChanged() {
    boolean success = true;
    success = updatePosition();
    
    if (!success)
    {
      try {
        Thread.sleep(1000L);
      }
      catch (InterruptedException localInterruptedException) {}
      
      success = updatePosition();
    }
    
    if (success)
    {
      return true;
    }
    
    return false;
  }
}
