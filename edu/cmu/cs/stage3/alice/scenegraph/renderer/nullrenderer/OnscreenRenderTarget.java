package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;

















public class OnscreenRenderTarget
  extends RenderTarget
  implements edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget
{
  private Canvas m_canvas = new Canvas();
  
  OnscreenRenderTarget(Renderer renderer) { super(renderer); }
  
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
  
  public Component getAWTComponent() { return m_canvas; }
}
