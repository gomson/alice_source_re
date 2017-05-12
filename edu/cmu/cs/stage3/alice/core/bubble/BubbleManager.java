package edu.cmu.cs.stage3.alice.core.bubble;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Vector;







public class BubbleManager
  implements RenderTargetListener
{
  private Bubble[] m_bubbles;
  private RenderTarget m_renderTarget;
  
  public BubbleManager() {}
  
  public void setBubbles(Bubble[] bubbles)
  {
    m_bubbles = bubbles;
    if (m_renderTarget != null)
      m_renderTarget.markDirty();
  }
  
  private boolean layoutBubbles(RenderTarget rt) {
    boolean isPaintRequired = false;
    Vector rects = new Vector();
    for (int i = 0; i < m_bubbles.length; i++) {
      Bubble bubbleI = m_bubbles[i];
      if (bubbleI.isShowing()) {
        bubbleI.calculateBounds(rt);
        bubbleI.calculateOrigin(rt);
        Point pixelOffset = bubbleI.getPixelOffset();
        if (pixelOffset != null) {
          Rectangle2D rect = bubbleI.getTotalBound();
          if (rect != null) {
            rects.addElement(new Rectangle2D.Double(x + rect.getX(), y + rect.getY(), rect.getWidth(), rect.getHeight()));
          }
        }
        isPaintRequired = true;
      }
    }
    if (isPaintRequired) {
      Camera[] sgCameras = rt.getCameras();
      if (sgCameras.length > 0) {
        Camera sgCamera = sgCameras[0];
        Rectangle actualViewport = rt.getActualViewport(sgCamera);
        for (int i = 0; i < m_bubbles.length; i++) {
          Bubble bubbleI = m_bubbles[i];
          if (bubbleI.isShowing()) {
            Point pixelOffset = bubbleI.getPixelOffset();
            Point origin = bubbleI.getOrigin();
            double half;
            double half; if (x > width / 2) {
              half = 0.5D;
            } else {
              half = 0.0D;
            }
            if (pixelOffset == null) {
              Rectangle2D rect = bubbleI.getTotalBound();
              

              double x = x;
              double y = y - 48;
              if (rect != null) {
                y -= rect.getHeight();
                double VIEWPORT_PAD = 64.0D;
                x = Math.min(Math.max(x, 64.0D), width - rect.getWidth() - 64.0D);
                y = Math.min(Math.max(y, 64.0D), height - rect.getHeight() - 64.0D);
              }
              bubbleI.setPixelOffset(new Point((int)x, (int)y));
            }
          }
        }
      }
    }
    return isPaintRequired;
  }
  
  public void cleared(RenderTargetEvent ev) {}
  
  public void rendered(RenderTargetEvent ev) { m_renderTarget = ev.getRenderTarget();
    if (layoutBubbles(m_renderTarget)) {
      Graphics g = m_renderTarget.getOffscreenGraphics();
      try {
        for (int i = 0; i < m_bubbles.length; i++) {
          Bubble bubbleI = m_bubbles[i];
          bubbleI.paint(g);
        }
      } finally {
        g.dispose();
      }
    }
  }
}
