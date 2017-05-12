package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.Element;

















public abstract class AbstractProxyRenderTarget
  extends AbstractRenderTarget
{
  protected AbstractProxyRenderer m_abstractProxyRenderer;
  
  protected AbstractProxyRenderTarget(AbstractProxyRenderer abstractProxyRenderer)
  {
    super(abstractProxyRenderer);
    m_abstractProxyRenderer = abstractProxyRenderer;
  }
  
  protected AbstractProxy getProxyFor(Element sgElement) { return m_abstractProxyRenderer.getProxyFor(sgElement); }
  
  protected AbstractProxy[] getProxiesFor(Element[] sgElements, Class componentType) {
    return m_abstractProxyRenderer.getProxiesFor(sgElements, componentType);
  }
  
  public void addCamera(Camera camera) {
    m_abstractProxyRenderer.createNecessaryProxies(camera.getRoot());
    super.addCamera(camera);
  }
}
