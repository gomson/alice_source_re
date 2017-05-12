package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import java.awt.Dimension;



















public class OffscreenRenderTarget
  extends RenderTarget
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget
{
  private Dimension m_size = new Dimension();
  
  OffscreenRenderTarget(Renderer renderer) { super(renderer); }
  


  public Dimension getSize() { return m_size; }
  
  public Dimension getSize(Dimension rv) {
    width = m_size.width;
    height = m_size.height;
    return rv;
  }
  
  public void setSize(int width, int height) { m_size.width = width;
    m_size.height = height;
  }
  
  public void setSize(Dimension size) { setSize(width, height); }
  
  protected boolean isSizeValid() {
    return (m_size.width > 0) && (m_size.height > 0);
  }
}
