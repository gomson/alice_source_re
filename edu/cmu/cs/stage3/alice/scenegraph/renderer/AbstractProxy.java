package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.util.Enumeration;
import java.util.Vector;















public abstract class AbstractProxy
{
  private Element m_sgElement;
  private AbstractProxyRenderer m_abstractProxyRenderer;
  
  public AbstractProxy() {}
  
  public void initialize(Element sgElement, AbstractProxyRenderer renderer)
  {
    m_sgElement = sgElement;
    setRenderer(renderer);
    initializeProperties();
    renderer.addListenersToSGElement(sgElement);
  }
  
  public void release() {
    m_abstractProxyRenderer.removeListenersFromSGElement(m_sgElement);
    m_abstractProxyRenderer.forgetProxyFor(m_sgElement);
    m_sgElement = null;
  }
  

  protected void setRenderer(AbstractProxyRenderer renderer) { m_abstractProxyRenderer = renderer; }
  
  protected void initializeProperties() {
    Enumeration enum0 = Property.getProperties(m_sgElement.getClass()).elements();
    while (enum0.hasMoreElements()) {
      Property property = (Property)enum0.nextElement();
      changed(property, property.get(m_sgElement));
    } }
  
  protected abstract void changed(Property paramProperty, Object paramObject);
  
  public Element getSceneGraphElement() { return m_sgElement; }
  
  public AbstractProxyRenderer getRenderer() {
    return m_abstractProxyRenderer;
  }
  
  protected AbstractProxy getProxyFor(Element sgElement) { return m_abstractProxyRenderer.getProxyFor(sgElement); }
  
  protected AbstractProxy[] getProxiesFor(Element[] sgElements, Class componentType) {
    return m_abstractProxyRenderer.getProxiesFor(sgElements, componentType);
  }
  
  protected void createNecessaryProxies(Element sgElement) { m_abstractProxyRenderer.createNecessaryProxies(sgElement); }
  
  protected void markAllRenderTargetsDirty() {
    m_abstractProxyRenderer.markAllRenderTargetsDirty();
  }
  
  public String toString()
  {
    return getClass().getName().substring("edu.cmu.cs.stage3.alice.scenegraph.renderer.".length()) + "[" + m_sgElement.toString() + "]";
  }
}
