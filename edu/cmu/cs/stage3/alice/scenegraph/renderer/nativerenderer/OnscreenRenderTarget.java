package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import java.awt.Component;
import java.awt.Dimension;


















public class OnscreenRenderTarget
  extends RenderTarget
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget
{
  private RenderCanvas m_renderCanvas;
  private boolean m_isRepaintAlreadyCalled = false;
  
  OnscreenRenderTarget(Renderer renderer) { super(renderer);
    m_renderCanvas = renderer.createRenderCanvas(this);
  }
  
  public Component getAWTComponent() { return m_renderCanvas; }
  
  public Dimension getSize(Dimension rv) {
    Component awtComponent = getAWTComponent();
    if (awtComponent != null) {
      awtComponent.getSize(rv);
    } else {
      width = 0;
      height = 0;
    }
    return rv;
  }
  
  public void markDirty() {
    super.markDirty();
    if (!m_isRepaintAlreadyCalled)
    {

      getAWTComponent().repaint();
      m_isRepaintAlreadyCalled = true;
    }
  }
  
  public synchronized void clearAndRenderOffscreen() {
    if (m_renderCanvas.acquireDrawingSurface())
      try {
        super.clearAndRenderOffscreen();
      } finally {
        m_renderCanvas.releaseDrawingSurface();
      }
  }
  
  public synchronized void update() {
    if (m_renderCanvas.acquireDrawingSurface()) {
      try {
        m_renderCanvas.swapBuffers();
        m_isRepaintAlreadyCalled = false;
      } finally {
        m_renderCanvas.releaseDrawingSurface();
      }
    }
  }
}
