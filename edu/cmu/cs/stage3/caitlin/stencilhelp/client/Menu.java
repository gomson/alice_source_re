package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Vector;

public class Menu implements StencilObject, StencilPanelMessageListener, MouseEventListener, StencilFocusListener, ReadWriteListener
{
  protected Vector shapes = new Vector();
  protected Vector stencilObjectPositionListeners = new Vector();
  protected StencilManager stencilManager = null;
  protected boolean isShowing = false;
  protected int level = 0;
  protected Point centerPoint;
  protected boolean writeEnabled = true;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle rect = null;
  
  private Shape upOption = null;
  private Shape downOption = null;
  private Shape rightOption = null;
  private Shape leftOption = null;
  int fontSize = 30;
  Font font = new Font("Arial", 0, fontSize);
  
  public Menu(StencilManager stencilManager) {
    this.stencilManager = stencilManager;
  }
  
  protected void updateShapes(Point p) {
    centerPoint = p;
    previousRect = getRectangle();
    if (level == 1) {
      shapes.removeAllElements();
      
      Point upPosition = new Point(x - 40, y - 50);
      upOption = new RoundRectangle2D.Double(x, y, 80.0D, 30.0D, 10.0D, 10.0D);
      shapes.addElement(new ScreenShape(Color.blue, upOption, true, 0));
      Shape s = createWordShape("new", new Point(x + 14, y + 25));
      shapes.addElement(new ScreenShape(Color.white, s, true, 1));
      
      Point rightPosition = new Point(x - 90, y - 15);
      rightOption = new RoundRectangle2D.Double(x, y, 80.0D, 30.0D, 10.0D, 10.0D);
      shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 2));
      s = createWordShape("load", new Point(x + 10, y + 25));
      shapes.addElement(new ScreenShape(Color.white, s, true, 3));
      
      Point leftPosition = new Point(x + 20, y - 15);
      leftOption = new RoundRectangle2D.Double(x, y, 80.0D, 30.0D, 10.0D, 10.0D);
      shapes.addElement(new ScreenShape(Color.blue, leftOption, true, 4));
      s = createWordShape("save", new Point(x + 8, y + 25));
      shapes.addElement(new ScreenShape(Color.white, s, true, 5));
      
      Point downPosition = new Point(x - 40, y + 20);
      downOption = new RoundRectangle2D.Double(x, y, 80.0D, 30.0D, 10.0D, 10.0D);
      shapes.addElement(new ScreenShape(Color.blue, downOption, true, 6));
      s = createWordShape("lock", new Point(x + 11, y + 25));
      shapes.addElement(new ScreenShape(Color.white, s, true, 7));
      
      previousRect = rect;
      rect = new Rectangle(x - 90, y - 50, x + 90, y + 50);
    }
    else if (level == 0) {
      shapes.removeAllElements();
      
      if (writeEnabled)
      {
        Point upPosition = new Point(x - 40, y - 50);
        upOption = new RoundRectangle2D.Double(x, y, 90.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, upOption, true, 0));
        Shape s = createWordShape("note", new Point(x + 15, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 1));
        
        Point rightPosition = new Point(x - 90, y - 15);
        rightOption = new RoundRectangle2D.Double(x, y, 90.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 2));
        s = createWordShape("frame", new Point(x + 6, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 3));
        
        Point leftPosition = new Point(x + 20, y - 15);
        leftOption = new RoundRectangle2D.Double(x, y, 90.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, leftOption, true, 4));
        s = createWordShape("clear", new Point(x + 10, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 5));
        
        Point downPosition = new Point(x - 40, y + 20);
        downOption = new RoundRectangle2D.Double(x, y, 90.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, downOption, true, 6));
        s = createWordShape("other", new Point(x + 8, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 7));
        
        previousRect = rect;
        rect = new Rectangle(x - 90, y - 50, x + 95, y + 50);
      } else {
        Point rightPosition = new Point(x - 90, y - 15);
        rightOption = new RoundRectangle2D.Double(x, y, 65.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, rightOption, true, 0));
        Shape s = createWordShape("load", new Point(x + 5, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 1));
        

        Point downPosition = new Point(x - 40, y + 20);
        downOption = new RoundRectangle2D.Double(x, y, 65.0D, 30.0D, 10.0D, 10.0D);
        shapes.addElement(new ScreenShape(Color.blue, downOption, true, 2));
        s = createWordShape("lock", new Point(x + 5, y + 25));
        shapes.addElement(new ScreenShape(Color.white, s, true, 3));
        
        previousRect = rect;
        rect = new Rectangle(x - 90, y - 15, x + 25, y + 50);
      }
    }
    isModified = true;
  }
  
  protected Shape createWordShape(String word, Point startPos) {
    TextLayout wordLayout = new TextLayout(word, font, new java.awt.font.FontRenderContext(null, false, false));
    AffineTransform textAt = new AffineTransform();
    textAt.translate(x, y);
    Shape s = wordLayout.getOutline(textAt);
    
    return s;
  }
  
  public Vector getShapes()
  {
    if (isShowing)
      return shapes;
    return null;
  }
  
  public Rectangle getRectangle() { if (isShowing) {
      return rect;
    }
    return null;
  }
  
  public Rectangle getPreviousRectangle() { Rectangle pRect = previousRect;
    
    return pRect;
  }
  
  public boolean isModified() { if (isModified) {
      isModified = false;
      return true; }
    return false;
  }
  
  public boolean intersectsRectangle(Rectangle rect) { Rectangle currentRect = getRectangle();
    if (currentRect != null)
      return rect.intersects(currentRect);
    return false;
  }
  
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener) { stencilObjectPositionListeners.addElement(posListener); }
  
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener) {
    stencilObjectPositionListeners.remove(posListener);
  }
  
  public String getComponentID() { return null; }
  

  public void messageReceived(int messageID, Object data)
  {
    if (writeEnabled) {
      isShowing = true;
      level = 0;
      updateShapes((Point)data);
      stencilManager.requestFocus(this);
    }
  }
  
  public boolean contains(Point point)
  {
    if (isShowing) {
      if (writeEnabled)
        return true;
      return false;
    }
    








    return false;
  }
  
  public boolean mousePressed(MouseEvent e) { return false; }
  

  public boolean mouseReleased(MouseEvent e) { return false; }
  
  public boolean mouseClicked(MouseEvent e) {
    isShowing = false;
    if (writeEnabled) {
      if (level == 0) {
        previousRect = rect;
        if (upOption.contains(e.getPoint())) {
          stencilManager.createNewNote(centerPoint);
        } else if (downOption.contains(e.getPoint())) {
          level = 1;
          isShowing = true;
          updateShapes(e.getPoint());
        } else if (rightOption.contains(e.getPoint())) {
          stencilManager.createNewFrame(centerPoint);
        } else if (leftOption.contains(e.getPoint())) {
          stencilManager.removeAllObjects();
        }
        isModified = true;
      } else {
        if (upOption.contains(e.getPoint())) {
          stencilManager.insertNewStencil();
        } else if (downOption.contains(e.getPoint())) {
          stencilManager.toggleLock();
        } else if (rightOption.contains(e.getPoint())) {
          stencilManager.loadStencilsFile();
        } else if (leftOption.contains(e.getPoint())) {
          stencilManager.saveStencilsFile();
        }
        previousRect = rect;
        isModified = true;
      }
    } else {
      if (downOption.contains(e.getPoint())) {
        stencilManager.toggleLock();
      } else if (rightOption.contains(e.getPoint())) {
        stencilManager.loadStencilsFile();
      }
      previousRect = rect;
      isModified = true;
    }
    return true;
  }
  
  public boolean mouseEntered(MouseEvent e) { return false; }
  
  public boolean mouseExited(MouseEvent e) {
    return false;
  }
  
  public boolean mouseMoved(MouseEvent e) { return false; }
  
  public boolean mouseDragged(MouseEvent e) {
    return false;
  }
  


  public void focusGained() { isShowing = true; }
  
  public void focusLost() {
    isShowing = false;
    previousRect = rect;
    isModified = true;
  }
  
  public void setWriteEnabled(boolean enabled)
  {
    writeEnabled = enabled;
  }
}
