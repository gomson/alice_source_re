package edu.cmu.cs.stage3.alice.core.manipulator;

import edu.cmu.cs.stage3.awt.AWTUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;













public class ScreenWrappingMouseListener
  implements MouseListener, MouseMotionListener
{
  protected int pressedx = 0;
  protected int pressedy = 0;
  protected int lastx = 0;
  protected int lasty = 0;
  protected int offsetx = 0;
  protected int offsety = 0;
  protected int dx = 0;
  protected int dy = 0;
  protected boolean mouseIsDown = false;
  private int leftEdge;
  private int rightEdge;
  private int topEdge;
  private int bottomEdge;
  protected boolean doWrap = false;
  private Point tempPoint = new Point();
  private boolean actionAborted = false;
  
  public ScreenWrappingMouseListener() {}
  
  public synchronized boolean isMouseDown() { return mouseIsDown; }
  
  protected Component component;
  public synchronized int getPressedX() {
    return pressedx;
  }
  
  public synchronized int getPressedY() {
    return pressedy;
  }
  
  public synchronized int getOffsetX() {
    return offsetx;
  }
  
  public synchronized int getOffsetY() {
    return offsety;
  }
  
  public synchronized int getDX() {
    return dx;
  }
  
  public synchronized int getDY() {
    return dy;
  }
  
  public boolean isActionAborted() {
    return actionAborted;
  }
  
  public synchronized void abortAction() {
    actionAborted = true;
    mouseIsDown = false;
    component.removeMouseMotionListener(this);
  }
  
  public synchronized void mousePressed(MouseEvent ev) {
    component = ev.getComponent();
    
    if (AWTUtilities.isSetCursorLocationSupported()) {
      doWrap = true;
    } else {
      doWrap = false;
    }
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)screenSize.getWidth();
    int screenHeight = (int)screenSize.getHeight();
    leftEdge = 0;
    rightEdge = (screenWidth - 1);
    topEdge = 0;
    bottomEdge = (screenHeight - 1);
    
    pressedx = (this.lastx = ev.getX());
    pressedy = (this.lasty = ev.getY());
    offsetx = 0;
    offsety = 0;
    
    mouseIsDown = true;
    
    ev.getComponent().addMouseMotionListener(this);
  }
  
  public synchronized void mouseReleased(MouseEvent ev) {
    if (!actionAborted) {
      mouseIsDown = false;
      ev.getComponent().removeMouseMotionListener(this);
    } else {
      actionAborted = false;
    }
  }
  
  public synchronized void mouseDragged(MouseEvent ev) {
    if (mouseIsDown) {
      int x = ev.getX();
      int y = ev.getY();
      
      offsetx = (x - pressedx);
      offsety = (y - pressedy);
      
      dx = (x - lastx);
      dy = (y - lasty);
      
      lastx = x;
      lasty = y;
      
      if (doWrap) {
        tempPoint.setLocation(x, y);
        SwingUtilities.convertPointToScreen(tempPoint, ev.getComponent());
        if (tempPoint.x <= leftEdge) {
          tempPoint.x = (rightEdge - 1 - (leftEdge - tempPoint.x));
          lastx += rightEdge - leftEdge;
          pressedx += rightEdge - leftEdge;
          AWTUtilities.setCursorLocation(tempPoint);
        } else if (tempPoint.x >= rightEdge) {
          tempPoint.x = (leftEdge + 1 + (tempPoint.x - rightEdge));
          lastx -= rightEdge - leftEdge;
          pressedx -= rightEdge - leftEdge;
          AWTUtilities.setCursorLocation(tempPoint);
        }
        if (tempPoint.y <= topEdge) {
          tempPoint.y = (bottomEdge - 1 - (topEdge - tempPoint.y));
          lasty += bottomEdge - topEdge;
          pressedy += bottomEdge - topEdge;
          AWTUtilities.setCursorLocation(tempPoint);
        } else if (tempPoint.y >= bottomEdge) {
          tempPoint.y = (topEdge + 1 + (tempPoint.y - bottomEdge));
          lasty -= bottomEdge - topEdge;
          pressedy -= bottomEdge - topEdge;
          AWTUtilities.setCursorLocation(tempPoint);
        }
      }
    }
  }
  
  public void mouseClicked(MouseEvent ev) {}
  
  public void mouseEntered(MouseEvent ev) {}
  
  public void mouseExited(MouseEvent ev) {}
  
  public void mouseMoved(MouseEvent ev) {}
}
