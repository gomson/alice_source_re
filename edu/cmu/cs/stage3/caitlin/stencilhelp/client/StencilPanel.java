package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class StencilPanel extends javax.swing.JPanel implements MouseEventListener, ReadWriteListener
{
  protected StencilManager stencilManager = null;
  protected boolean isDrawing = false;
  protected Vector holes = new Vector();
  protected Vector filledShapes = new Vector();
  protected Vector unfilledShapes = new Vector();
  protected Vector stencilPanelMessageListeners = new Vector();
  protected boolean writeEnabled = true;
  protected Vector holeRegions = new Vector();
  protected Vector clearRegions = new Vector();
  protected boolean nothingToDraw = true;
  protected Color bgColor = new Color(13, 99, 161, 150);
  

  protected java.awt.Image img = null;
  protected BufferedImage bImg = null;
  protected BufferedImage screenBuffer = null;
  protected MediaTracker mTracker = null;
  protected java.awt.TexturePaint tPaint = null;
  
  public StencilPanel(StencilManager stencilManager) {
    this.stencilManager = stencilManager;
    java.awt.Toolkit toolKit = getToolkit();
    img = toolKit.getImage("blue2.png");
    mTracker = new MediaTracker(this);
    mTracker.addImage(img, 1);
  }
  
  public void setIsDrawing(boolean isDrawing) {
    this.isDrawing = isDrawing;
    setVisible(isDrawing);
  }
  
  public boolean getIsDrawing() { return isDrawing; }
  
  public void redraw()
  {
    repaint();
  }
  
  protected void categorizeShapes(Vector shapes, Vector allRegions)
  {
    holes = new Vector();
    filledShapes = new Vector();
    unfilledShapes = new Vector();
    
    holeRegions = new Vector();
    clearRegions = new Vector();
    

    for (int i = 0; i < shapes.size(); i++) {
      ScreenShape currentShape = (ScreenShape)shapes.elementAt(i);
      if (currentShape.getColor() == null) {
        holes.addElement(currentShape);
        if (currentShape.getIndex() == 0) {
          holeRegions.addElement(currentShape.getShape().getBounds());
        }
      } else {
        if ((currentShape.getIndex() == 0) && (allRegions.size() > 0)) {
          Rectangle r = (Rectangle)allRegions.remove(0);
          clearRegions.addElement(r);
        }
        if (currentShape.getIsFilled()) {
          filledShapes.addElement(currentShape);
        } else {
          unfilledShapes.addElement(currentShape);
        }
      }
    }
    


    if ((shapes.size() == 0) && (allRegions.size() > 0)) {
      clearRegions = allRegions;
    }
  }
  

  public void paint(Graphics g)
  {
    super.paint(g);
    
    if ((screenBuffer == null) || (screenBuffer.getWidth() != getWidth()) || (screenBuffer.getHeight() != getHeight())) {
      screenBuffer = new BufferedImage(getWidth(), getHeight(), 
        2);
      
      Graphics graphics = screenBuffer.getGraphics();
      graphics.setColor(bgColor);
      graphics.fillRect(0, 0, getWidth(), getHeight());
    }
    
    Vector allShapes = stencilManager.getUpdateShapes();
    Vector allRegions = stencilManager.getClearRegions();
    categorizeShapes(allShapes, allRegions);
    
    Graphics2D bg2 = (Graphics2D)screenBuffer.getGraphics();
    bg2.setBackground(new Color(255, 255, 255, 0));
    Graphics2D g2 = (Graphics2D)g;
    







    if (isDrawing) {
      Object oldAntialiasing = null;
      if ((g instanceof Graphics2D)) {
        oldAntialiasing = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        bg2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
      }
      
      Area outside = null;
      Color outsideColor = bgColor;
      if (tPaint != null) {
        bg2.setPaint(tPaint);
      } else {
        bg2.setColor(outsideColor);
      }
      

      for (int i = 0; i < clearRegions.size(); i++) {
        Rectangle r = (Rectangle)clearRegions.elementAt(i);
        bg2.clearRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
        bg2.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
      }
      

      for (int i = 0; i < holeRegions.size(); i++) {
        Rectangle r = (Rectangle)holeRegions.elementAt(i);
        bg2.clearRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
      }
      

      for (int i = 0; i < holes.size(); i++) {
        outside = new Area((Rectangle)holeRegions.elementAt(i));
        ScreenShape scrShape = (ScreenShape)holes.elementAt(i);
        
        RoundRectangle2D.Double r = (RoundRectangle2D.Double)scrShape.getShape();
        RoundRectangle2D.Double upperShadow = new RoundRectangle2D.Double(x - 2.0D, y - 2.0D, width + 2.0D, height + 2.0D, arcwidth, archeight);
        RoundRectangle2D.Double lowerShadow = new RoundRectangle2D.Double(x, y, width + 2.0D, height + 2.0D, arcwidth, archeight);
        
        Area upperBorder = new Area(upperShadow);
        Area currentArea = new Area(r);
        upperBorder.subtract(currentArea);
        bg2.setColor(Color.darkGray);
        bg2.fill(upperBorder);
        
        Area lowerBorder = new Area(lowerShadow);
        lowerBorder.subtract(currentArea);
        bg2.setColor(Color.lightGray);
        bg2.fill(lowerBorder);
        
        outside.subtract(currentArea);
        
        bg2.fill(outside);
      }
      
      bg2.setColor(outsideColor);
      
      for (int i = 0; i < filledShapes.size(); i++) {
        ScreenShape currentShape = (ScreenShape)filledShapes.elementAt(i);
        if ((shape instanceof java.awt.geom.Line2D)) {
          bg2.setColor(color);
          bg2.draw(shape);
        } else {
          bg2.setColor(color);
          bg2.fill(shape);
        }
      }
      
      for (int i = 0; i < unfilledShapes.size(); i++) {
        ScreenShape currentShape = (ScreenShape)unfilledShapes.elementAt(i);
        bg2.setColor(color);
        bg2.draw(shape);
      }
      

      g2.drawImage(screenBuffer, null, null);
      
      if ((g instanceof Graphics2D)) {
        ((Graphics2D)g).addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, oldAntialiasing));
      }
    }
  }
  


  public void addMessageListener(StencilPanelMessageListener spmListener)
  {
    stencilPanelMessageListeners.addElement(spmListener);
  }
  
  public void removeMessageListener(StencilPanelMessageListener spmListener) { stencilPanelMessageListeners.remove(spmListener); }
  

  public void removeAllMessageListeners() { stencilPanelMessageListeners.removeAllElements(); }
  
  protected void broadCastMessage(int messageID, Object data) {
    for (int i = 0; i < stencilPanelMessageListeners.size(); i++) {
      StencilPanelMessageListener spmListener = (StencilPanelMessageListener)stencilPanelMessageListeners.elementAt(i);
      spmListener.messageReceived(messageID, data);
    }
  }
  

  public boolean contains(java.awt.Point point)
  {
    return true;
  }
  
  public boolean mousePressed(MouseEvent e) { return false; }
  

  public boolean mouseReleased(MouseEvent e) { return false; }
  
  public boolean mouseClicked(MouseEvent e) {
    if (e.getModifiers() == 4) {
      broadCastMessage(1, e.getPoint());
      

      return true; }
    if ((e.getClickCount() == 2) && 
      (writeEnabled)) {
      stencilManager.createNewHole(e.getPoint());
      return true;
    }
    
    return false;
  }
  
  public boolean mouseEntered(MouseEvent e) { return false; }
  
  public boolean mouseExited(MouseEvent e) {
    return false;
  }
  
  public boolean mouseMoved(MouseEvent e) { return false; }
  
  public boolean mouseDragged(MouseEvent e) {
    return false;
  }
  
  public void setWriteEnabled(boolean enabled)
  {
    writeEnabled = enabled;
  }
  
  protected class HoleData
  {
    public Hole hole;
    public Rectangle clearRect;
    
    protected HoleData() {}
  }
}
