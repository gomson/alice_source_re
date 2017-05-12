package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
















abstract class ContainerProxy
  extends ComponentProxy
{
  private ComponentProxy[] m_childrenProxies;
  
  ContainerProxy() {}
  
  public void handleChildAdd(ComponentProxy childProxy)
  {
    m_childrenProxies = null;
  }
  
  public void handleChildRemove(ComponentProxy childProxy) {
    m_childrenProxies = null;
  }
  
  private Container getSceneGraphContainer() {
    return (Container)getSceneGraphElement();
  }
  
  public ComponentProxy[] getChildrenProxies() {
    if (m_childrenProxies == null) {
      Container sgContainer = getSceneGraphContainer();
      Component[] sgChildren = sgContainer
        .getChildren();
      m_childrenProxies = new ComponentProxy[sgChildren.length];
      for (int i = 0; i < sgChildren.length; i++) {
        m_childrenProxies[i] = ((ComponentProxy)getProxyFor(sgChildren[i]));
      }
    }
    return m_childrenProxies;
  }
  
  public void setup(RenderContext context)
  {
    ComponentProxy[] childrenProxies = getChildrenProxies();
    for (int i = 0; i < childrenProxies.length; i++) {
      childrenProxies[i].setup(context);
    }
  }
  
  public void render(RenderContext context)
  {
    ComponentProxy[] childrenProxies = getChildrenProxies();
    
    for (int i = 0; i < childrenProxies.length; i++) {
      childrenProxies[i].render(context);
    }
  }
  

  public void pick(PickContext context, PickParameters pickParameters)
  {
    ComponentProxy[] childrenProxies = getChildrenProxies();
    for (int i = 0; i < childrenProxies.length; i++) {
      childrenProxies[i].pick(context, pickParameters);
    }
  }
}
