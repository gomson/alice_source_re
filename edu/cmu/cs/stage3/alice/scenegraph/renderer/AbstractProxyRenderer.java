package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent;
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.Vector;












public abstract class AbstractProxyRenderer
  extends AbstractRenderer
{
  private Hashtable m_sgElementToProxyMap = new Hashtable();
  private Vector m_queuedPropertyChanges = new Vector();
  
  public AbstractProxyRenderer() {}
  
  protected void dispatchPropertyChange(PropertyEvent propertyEvent) { Property property = propertyEvent.getProperty();
    Element sgElement = (Element)propertyEvent.getSource();
    if (!sgElement.isReleased())
    {

      AbstractProxy proxy = getProxyFor(sgElement);
      proxy.changed(property, property.get(sgElement));
      markAllRenderTargetsDirty();
    }
  }
  
  protected void dispatchRelease(ReleaseEvent releaseEvent) {
    Element sgElement = (Element)releaseEvent.getSource();
    AbstractProxy proxy = getProxyFor(sgElement);
    proxy.release();
  }
  
  protected abstract AbstractProxy createProxyFor(Element paramElement);
  
  public AbstractProxy getProxyFor(Element sgElement) { AbstractProxy proxy;
    if (sgElement != null) {
      AbstractProxy proxy = (AbstractProxy)m_sgElementToProxyMap.get(sgElement);
      if (proxy == null) {
        proxy = createProxyFor(sgElement);
        if (proxy != null) {
          m_sgElementToProxyMap.put(sgElement, proxy);
          proxy.initialize(sgElement, this);
          createNecessaryProxies(sgElement);
        } else {
          Element.warnln("warning: could not create proxy for: " + sgElement);
        }
      }
      else if (proxy.getSceneGraphElement() == null) {
        proxy = null;
        Element.warnln(sgElement + "'s proxy has null for a sgElement");
      }
    }
    else {
      proxy = null;
    }
    return proxy;
  }
  
  public AbstractProxy[] getProxiesFor(Element[] sgElements, Class componentType) { if (sgElements != null) {
      AbstractProxy[] proxies = (AbstractProxy[])Array.newInstance(componentType, sgElements.length);
      for (int i = 0; i < sgElements.length; i++) {
        proxies[i] = getProxyFor(sgElements[i]);
      }
      return proxies;
    }
    return null;
  }
  


  public void forgetProxyFor(Element sgElement) { m_sgElementToProxyMap.remove(sgElement); }
  
  public void createNecessaryProxies(Element sgElement) {
    getProxyFor(sgElement);
    if ((sgElement instanceof Container)) {
      Container sgContainer = (Container)sgElement;
      for (int i = 0; i < sgContainer.getChildCount(); i++) {
        Component sgComponent = sgContainer.getChildAt(i);
        getProxyFor(sgComponent);
        if ((sgComponent instanceof Container)) {
          createNecessaryProxies(sgComponent);
        }
      }
    }
  }
}
