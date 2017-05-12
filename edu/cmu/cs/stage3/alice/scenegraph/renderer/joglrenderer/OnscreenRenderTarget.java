package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.Component;
import java.awt.Dimension;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;














public class OnscreenRenderTarget
  extends RenderTarget
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget
{
  private GLCanvas m_glCanvas;
  private RenderContext m_renderContext;
  private PickContext m_pickContext;
  
  public OnscreenRenderTarget(Renderer renderer)
  {
    super(renderer);
  }
  
  public void markDirty() {
    getAWTComponent().repaint();
  }
  
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
  
  public Component getAWTComponent() {
    if (m_glCanvas == null) {
      GLCapabilities glCaps = new GLCapabilities();
      





      m_glCanvas = new GLCanvas(glCaps);
      m_renderContext = new RenderContext(this);
      m_glCanvas.addGLEventListener(m_renderContext);
      m_pickContext = new PickContext(this);
      m_glCanvas.addGLEventListener(m_pickContext);
    }
    return m_glCanvas;
  }
  
  public PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    if (m_pickContext != null) {
      return m_pickContext.pick(m_glCanvas, x, y, isSubElementRequired, isOnlyFrontMostRequired);
    }
    return null;
  }
}
