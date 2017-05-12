package edu.cmu.cs.stage3.alice.core.bubble;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;




public class NarrateBubble
  extends Bubble
{
  private boolean m_displayTopOfScreen = false;
  private Rectangle actualViewport = null;
  
  public NarrateBubble() {
    setCharactersPerLine(60);
  }
  



  public void calculateOrigin(RenderTarget rt) {}
  


  public void calculateBounds(RenderTarget rt)
  {
    Camera[] sgCameras = rt.getCameras();
    if (sgCameras.length > 0) {
      Camera sgCamera = sgCameras[0];
      actualViewport = rt.getActualViewport(sgCamera);
      
      Font font = getFont();
      FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
      

      double charCnt = actualViewport.getWidth() / fontMetrics.charWidth('W');
      
      setCharactersPerLine((int)charCnt * 2);
    }
    
    super.calculateBounds(rt);
    
    Rectangle2D totalBound = getTotalBound();
    
    if (sgCameras.length > 0) {
      Camera sgCamera = sgCameras[0];
      actualViewport = rt.getActualViewport(sgCamera);
      
      double x = actualViewport.getX() + actualViewport.getWidth() / 2.0D - totalBound.getWidth() / 2.0D;
      double y = 0.0D;
      if (m_displayTopOfScreen) {
        y = 10.0D;
        if (totalBound.getY() < 0.0D) y = -1.0D * totalBound.getY() + 10.0D;
      } else {
        y = actualViewport.y + actualViewport.getHeight() - totalBound.getHeight() + 10.0D;
      }
      
      setPixelOffset(new Point((int)x, (int)y));
    }
  }
  
  protected void paintBackground(Graphics g)
  {
    Rectangle2D totalBound = getTotalBound();
    Point origin = getOrigin();
    Point pixelOffset = getPixelOffset();
    
    int x = actualViewport.x;
    int y = (int)(totalBound.getY() + y - 10.0D);
    
    int width = actualViewport.width;
    int height = (int)totalBound.getHeight() + 10 + 10;
    
    g.setColor(getBackgroundColor());
    g.fillRoundRect(x, y, width, height, 5, 5);
    g.setColor(Color.black);
    g.drawRoundRect(x, y, width, height, 5, 5);
  }
}
