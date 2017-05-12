package edu.cmu.cs.stage3.alice.core.bubble;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.Vector;



public class TitleBubble
  extends Bubble
{
  private Rectangle actualViewport = null;
  



  public TitleBubble() {}
  


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
    
    setPixelOffset(new Point((int)actualViewport.getX() + (int)(actualViewport.getWidth() / 2.0D - totalBound.getWidth() / 2.0D), (int)actualViewport.getY() + (int)(actualViewport.getHeight() / 2.0D - totalBound.getHeight() / 2.0D)));
  }
  

  protected void paintBackground(Graphics g)
  {
    Point origin = getOrigin();
    
    int x = actualViewport.x;
    int y = actualViewport.y;
    
    int width = actualViewport.width;
    int height = actualViewport.height;
    
    g.setColor(getBackgroundColor());
    g.fillRoundRect(x, y, width, height, 5, 5);
  }
  
  protected void paint(Graphics g)
  {
    if (isShowing()) {
      paintBackground(g);
      if (getText() != null) {
        if (getFont() != null) {
          g.setFont(getFont());
        }
        g.setColor(getForegroundColor());
        if (m_subTexts.size() > 0) {
          SubText subText0 = (SubText)m_subTexts.elementAt(0);
          int offsetX = m_pixelOffset.x;
          int offsetY = m_pixelOffset.y - (int)subText0.getBound().getY();
          for (int i = 0; i < m_subTexts.size(); i++) {
            SubText subTextI = (SubText)m_subTexts.elementAt(i);
            Rectangle2D boundI = subTextI.getBound();
            int x = actualViewport.x + actualViewport.width / 2 - (int)(boundI.getWidth() * 0.85D / 2.0D);
            int y = (int)(actualViewport.y + offsetY + boundI.getY());
            g.drawString(subTextI.getText(), x, y);
          }
        }
      }
    }
  }
}
