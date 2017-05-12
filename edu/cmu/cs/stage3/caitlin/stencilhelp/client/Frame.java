package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.Vector;

public class Frame implements StencilObject, LayoutChangeListener
{
  protected Vector stencilObjectPositionListeners = new Vector();
  protected Vector shapes = new Vector();
  protected String id = null;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle2D.Double rect = null;
  protected boolean isInitialized = false;
  protected ObjectPositionManager positionManager = null;
  protected boolean missingID = false;
  
  public Frame(String id, ObjectPositionManager positionManager) { this.positionManager = positionManager;
    this.id = id;
  }
  


  public boolean updatePosition()
  {
    previousRect = getRectangle();
    
    if (shapes == null) shapes = new Vector();
    shapes.removeAllElements();
    
    Rectangle r = null;
    try {
      r = positionManager.getBoxForID(id);
    }
    catch (NullPointerException localNullPointerException) {}
    if (r != null)
    {
      rect = new Rectangle2D.Double(x - 3, y - 3, width + 6, height + 6);
      
      RoundRectangle2D.Double rr = new RoundRectangle2D.Double(x - 3, y - 3, width + 6, height + 6, 1.0D, 1.0D);
      shapes.addElement(new ScreenShape(null, rr, true, 0));
      
      Rectangle2D.Double temp = new Rectangle2D.Double(x - 6, y - 6, 3.0D, height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 1));
      
      temp = new Rectangle2D.Double(x - 6, y - 6, width + 12, 3.0D);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 2));
      
      temp = new Rectangle2D.Double(x + width + 3, y - 6, 3.0D, height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 3));
      
      temp = new Rectangle2D.Double(x - 6, y + height + 3, width + 12, 3.0D);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 4));
      
      temp = new Rectangle2D.Double(x - 3, y - 3, width + 6, height + 6);
      shapes.addElement(new ScreenShape(new Color(0, 100, 255, 50), temp, true, 5));
      

      isModified = true;
      return true;
    }
    
    isModified = true;
    return false;
  }
  

  protected void setInitialPosition()
  {
    previousRect = getRectangle();
    
    if (shapes == null) shapes = new Vector();
    shapes.removeAllElements();
    
    Rectangle r = positionManager.getInitialBox(id);
    if (r != null) {
      rect = new Rectangle2D.Double(x - 3, y - 3, width + 6, height + 6);
      
      RoundRectangle2D.Double rr = new RoundRectangle2D.Double(x - 3, y - 3, width + 6, height + 6, 1.0D, 1.0D);
      shapes.addElement(new ScreenShape(null, rr, true, 0));
      
      Rectangle2D.Double temp = new Rectangle2D.Double(x - 6, y - 6, 3.0D, height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 1));
      
      temp = new Rectangle2D.Double(x - 6, y - 6, width + 12, 3.0D);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 2));
      
      temp = new Rectangle2D.Double(x + width + 3, y - 6, 3.0D, height + 12);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 3));
      
      temp = new Rectangle2D.Double(x - 6, y + height + 3, width + 12, 3.0D);
      shapes.addElement(new ScreenShape(Color.red, temp, true, 4));
      
      temp = new Rectangle2D.Double(x - 3, y - 3, width + 6, height + 6);
      shapes.addElement(new ScreenShape(new Color(0, 100, 255, 50), temp, true, 5));
    }
    

    isModified = true;
    isInitialized = true;
  }
  



  public Vector getShapes() { return shapes; }
  
  public Rectangle getRectangle() {
    if (rect != null)
      return rect.getBounds();
    return null;
  }
  
  public Rectangle getPreviousRectangle() { if ((previousRect == null) && (rect != null)) return rect.getBounds();
    return previousRect;
  }
  
  public boolean isModified() { if (isModified) {
      isModified = false;
      return true; }
    return false;
  }
  
  public boolean intersectsRectangle(Rectangle rect) { if (this.rect != null) {
      return rect.intersects(getRectangle());
    }
    return false;
  }
  
  public java.awt.Point getNotePoint()
  {
    if (rect != null) return new java.awt.Point((int)rect.getX(), (int)rect.getY());
    return new java.awt.Point(0, 0);
  }
  
  public void addStencilObjectPositionListener(StencilObjectPositionListener posListener) { stencilObjectPositionListeners.addElement(posListener); }
  
  public void removeStencilObjectPositionListener(StencilObjectPositionListener posListener) {
    stencilObjectPositionListeners.remove(posListener);
  }
  
  public String getComponentID() { return id; }
  

  public boolean layoutChanged()
  {
    boolean success = true;
    success = updatePosition();
    
    return success;
  }
}
