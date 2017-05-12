package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.util.DirectXVersion;
import java.util.Vector;

















public class DefaultRenderTargetFactory
  implements RenderTargetFactory
{
  private Class m_rendererClass;
  private Renderer m_renderer;
  
  public static Class[] getPotentialRendererClasses()
  {
    Vector vector = new Vector();
    
    if (AikMin.isWindows()) {
      try {
        if (DirectXVersion.getVersion() >= 7.0D) {
          vector.addElement(edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class);
        }
      }
      catch (Throwable localThrowable) {}
    }
    try
    {
      vector.addElement(edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class);
    }
    catch (Throwable localThrowable1) {}
    
    Class[] array = new Class[vector.size()];
    vector.copyInto(array);
    return array;
  }
  


  public DefaultRenderTargetFactory() { this(null); }
  
  public DefaultRenderTargetFactory(Class rendererClass) {
    if (rendererClass == null) {
      rendererClass = getPotentialRendererClasses()[0];
    }
    m_rendererClass = rendererClass;
  }
  
  public boolean isSoftwareEmulationForced() { return getRenderer().isSoftwareEmulationForced(); }
  
  public void setIsSoftwareEmulationForced(boolean isSoftwareEmulationForced) {
    getRenderer().setIsSoftwareEmulationForced(isSoftwareEmulationForced);
  }
  
  private Renderer getRenderer() {
    if (m_renderer == null) {
      try {
        m_renderer = ((Renderer)m_rendererClass.newInstance());
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    return m_renderer;
  }
  
  public OffscreenRenderTarget createOffscreenRenderTarget() {
    return getRenderer().createOffscreenRenderTarget();
  }
  
  public OnscreenRenderTarget createOnscreenRenderTarget() { return getRenderer().createOnscreenRenderTarget(); }
  
  public void releaseOffscreenRenderTarget(OffscreenRenderTarget offscreenRenderTarget)
  {
    offscreenRenderTarget.release();
  }
  
  public void releaseOnscreenRenderTarget(OnscreenRenderTarget onscreenRenderTarget) { onscreenRenderTarget.release(); }
  
  public void release() {}
}
