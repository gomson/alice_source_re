package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.Dimension;
import java.awt.Graphics;

















public class OffscreenRenderTarget
  extends RenderTarget
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget
{
  private Dimension m_size = new Dimension(120, 90);
  
  OffscreenRenderTarget(Renderer renderer)
  {
    super(renderer);
  }
  





  public Graphics getOffscreenGraphics()
  {
    return null;
  }
  
  public Dimension getSize() { return m_size; }
  
  public Dimension getSize(Dimension rv) {
    width = m_size.width;
    height = m_size.height;
    return rv;
  }
  
  public void setSize(int width, int height) { m_size.width = width;
    m_size.height = height;
    createGLBuffer(width, height);
  }
  
  public void setSize(Dimension size) { setSize(width, height); }
  
  public PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    return null;
  }
}
