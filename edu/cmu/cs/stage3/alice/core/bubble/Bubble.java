package edu.cmu.cs.stage3.alice.core.bubble;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.vecmath.Vector3d;







































public abstract class Bubble
{
  public Bubble() {}
  
  private String m_text = null;
  private Color m_backgroundColor = Color.white;
  private Color m_foregroundColor = Color.black;
  private Font m_font = null;
  private boolean m_isShowing = true;
  private ReferenceFrame m_referenceFrame = null;
  private Vector3d m_offsetFromReferenceFrame = new Vector3d();
  
  protected Vector m_subTexts = new Vector();
  protected Point m_pixelOffset = null;
  private Point m_origin = new Point();
  
  private int m_charsPerLine = 32;
  

  public String getText() { return m_text; }
  
  public void setText(String text) {
    m_text = text;
    m_subTexts.clear();
  }
  
  public Color getBackgroundColor() { return m_backgroundColor; }
  
  public void setBackgroundColor(Color backgroundColor) {
    m_backgroundColor = backgroundColor;
  }
  
  public Color getForegroundColor() { return m_foregroundColor; }
  
  public void setForegroundColor(Color foregroundColor) {
    m_foregroundColor = foregroundColor;
  }
  
  public Font getFont() { return m_font; }
  
  public void setFont(Font font) {
    m_font = font;
  }
  
  public void setCharactersPerLine(int charsPerLine) { m_charsPerLine = charsPerLine; }
  
  public boolean isShowing() {
    return m_isShowing;
  }
  
  public void setIsShowing(boolean isShowing) { m_isShowing = isShowing; }
  
  public void show() {
    setIsShowing(true);
  }
  
  public void hide() { setIsShowing(false); }
  
  public ReferenceFrame getReferenceFrame() {
    return m_referenceFrame;
  }
  
  public void setReferenceFrame(ReferenceFrame referenceFrame) { m_referenceFrame = referenceFrame; }
  
  public Vector3d getOffsetFromReferenceFrame() {
    return m_offsetFromReferenceFrame;
  }
  
  public void setOffsetFromReferenceFrame(Vector3d offsetFromReferenceFrame) { m_offsetFromReferenceFrame = offsetFromReferenceFrame; }
  

  protected static final int PAD_X = 10;
  protected static final int PAD_Y = 10;
  public Point getPixelOffset()
  {
    return m_pixelOffset;
  }
  
  public void setPixelOffset(Point pixelOffset) { m_pixelOffset = pixelOffset; }
  


  protected Point getOrigin() { return m_origin; }
  
  public Rectangle2D getTotalBound() {
    if (m_subTexts.size() > 0) {
      SubText subText0 = (SubText)m_subTexts.elementAt(0);
      Rectangle2D totalBound = subText0.getSafeBound();
      for (int i = 1; i < m_subTexts.size(); i++) {
        SubText subTextI = (SubText)m_subTexts.elementAt(i);
        Rectangle2D.union(totalBound, subTextI.getBound(), totalBound);
      }
      return totalBound;
    }
    return null;
  }
  
  public void calculateBounds(RenderTarget rt)
  {
    Rectangle2D totalBound = getTotalBound();
    if ((totalBound == null) && 
      (m_text != null) && 
      (m_font != null)) {
      int n = m_charsPerLine;
      m_subTexts.clear();
      FontRenderContext frc = new FontRenderContext(null, false, false);
      int offsetY = 0;
      int i = 0;
      while (i < m_text.length()) { int limit;
        int limit;
        if (i + n > m_text.length()) {
          limit = m_text.length();
        } else {
          limit = m_text.indexOf(' ', i + n);
          if (limit == -1) {
            limit = m_text.length();
          }
        }
        String substring = m_text.substring(i, limit);
        Rectangle2D boundI = m_font.getStringBounds(substring, frc);
        m_subTexts.addElement(new SubText(substring, boundI, offsetY));
        offsetY = (int)(offsetY + boundI.getHeight());
        i = limit + 1;
      }
    }
  }
  
  public void calculateOrigin(RenderTarget rt)
  {
    m_origin.x = 300;
    m_origin.y = 300;
    if (m_referenceFrame != null) {
      edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = rt.getCameras();
      if (sgCameras.length > 0) {
        edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = sgCameras[0];
        edu.cmu.cs.stage3.alice.core.Camera camera = (edu.cmu.cs.stage3.alice.core.Camera)sgCamera.getBonus();
        if ((camera != null) && (camera != m_referenceFrame)) {
          Vector3d xyzInCamera = m_referenceFrame.transformTo(m_offsetFromReferenceFrame, camera);
          Vector3d xyzInViewport = rt.transformFromCameraToViewport(xyzInCamera, sgCamera);
          m_origin.x = ((int)x);
          m_origin.y = ((int)y);
        }
      }
    }
  }
  
  protected abstract void paintBackground(Graphics paramGraphics);
  
  protected void paint(Graphics g) { if (m_isShowing) {
      paintBackground(g);
      if (m_text != null) {
        if (m_font != null) {
          g.setFont(m_font);
        }
        g.setColor(m_foregroundColor);
        if (m_subTexts.size() > 0) {
          SubText subText0 = (SubText)m_subTexts.elementAt(0);
          int offsetX = m_pixelOffset.x;
          int offsetY = m_pixelOffset.y - (int)subText0.getBound().getY();
          for (int i = 0; i < m_subTexts.size(); i++) {
            SubText subTextI = (SubText)m_subTexts.elementAt(i);
            Rectangle2D boundI = subTextI.getBound();
            int x = (int)(offsetX + boundI.getX());
            int y = (int)(offsetY + boundI.getY());
            


            g.drawString(subTextI.getText(), x, y);
          }
        }
      }
    }
  }
}
