package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import javax.vecmath.Vector3d;





public class Renderer
  extends AbstractProxyRenderer
{
  private static final String RENDERER_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.";
  private static final String SCENEGRAPH_PACKAGE_NAME = "edu.cmu.cs.stage3.alice.scenegraph.";
  
  public Renderer() {}
  
  protected boolean requiresHierarchyAndAbsoluteTransformationListening()
  {
    return true;
  }
  
  protected boolean requiresBoundListening() {
    return false;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget() { return new OffscreenRenderTarget(this); }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() {
    return new OnscreenRenderTarget(this);
  }
  
  protected void dispatchAbsoluteTransformationChange(AbsoluteTransformationEvent absoluteTransformationEvent) {
    Component sgComponent = (Component)absoluteTransformationEvent.getSource();
    ComponentProxy componentProxy = (ComponentProxy)getProxyFor(sgComponent);
    componentProxy.handleAbsoluteTransformationChange();
  }
  
  protected void dispatchBoundChange(BoundEvent boundEvent) {}
  
  public void dispatchChildAdd(ChildrenEvent childrenEvent)
  {
    Container sgContainer = (Container)childrenEvent.getSource();
    ContainerProxy containerProxy = (ContainerProxy)getProxyFor(sgContainer);
    ComponentProxy childProxy = (ComponentProxy)getProxyFor(childrenEvent.getChild());
    containerProxy.handleChildAdd(childProxy);
  }
  
  public void dispatchChildRemove(ChildrenEvent childrenEvent) {
    Container sgContainer = (Container)childrenEvent.getSource();
    ContainerProxy containerProxy = (ContainerProxy)getProxyFor(sgContainer);
    ComponentProxy childProxy = (ComponentProxy)getProxyFor(childrenEvent.getChild());
    containerProxy.handleChildRemove(childProxy);
  }
  
  protected void dispatchHierarchyChange(HierarchyEvent hierarchyEvent) {}
  
  public PickInfo pick(Component sgComponent, Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired)
  {
    return null;
  }
  

  private static final int SCENEGRAPH_PACKAGE_NAME_COUNT = "edu.cmu.cs.stage3.alice.scenegraph.".length();
  
  protected AbstractProxy createProxyFor(Element sgElement) {
    Class sgClass = sgElement.getClass();
    while (sgClass != null) {
      String className = sgClass.getName();
      if (className.startsWith("edu.cmu.cs.stage3.alice.scenegraph.")) {
        break;
      }
      sgClass = sgClass.getSuperclass();
    }
    try
    {
      Class proxyClass = Class.forName("edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer." + sgClass.getName().substring(SCENEGRAPH_PACKAGE_NAME_COUNT) + "Proxy");
      return (AbstractProxy)proxyClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (InstantiationException ie) {
      ie.printStackTrace();
    }
    return null;
  }
}
