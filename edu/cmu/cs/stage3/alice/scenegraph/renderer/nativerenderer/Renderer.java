package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxy;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderer;
import javax.vecmath.Vector3d;










public abstract class Renderer
  extends AbstractProxyRenderer
{
  static
  {
    try
    {
      System.loadLibrary("jawt");
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {}
  }
  
  private int m_nativeInstance = 0;
  
  protected abstract void createNativeInstance();
  
  protected abstract void releaseNativeInstance();
  
  protected abstract void pick(ComponentProxy paramComponentProxy, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt1, boolean[] paramArrayOfBoolean, int[] paramArrayOfInt2, double[] paramArrayOfDouble);
  
  protected abstract RenderTargetAdapter createRenderTargetAdapter(RenderTarget paramRenderTarget);
  
  protected abstract RenderCanvas createRenderCanvas(OnscreenRenderTarget paramOnscreenRenderTarget);
  
  protected abstract boolean requiresHierarchyAndAbsoluteTransformationListening();
  
  protected abstract boolean requiresBoundListening();
  
  public Renderer() {
    createNativeInstance();
  }
  
  protected abstract void internalSetIsSoftwareEmulationForced(boolean paramBoolean);
  
  public void setIsSoftwareEmulationForced(boolean isSoftwareEmulationForced) {
    super.setIsSoftwareEmulationForced(isSoftwareEmulationForced);
    internalSetIsSoftwareEmulationForced(isSoftwareEmulationForced);
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(Component sgComponent, Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    commitAnyPendingChanges();
    ComponentProxy componentProxy = (ComponentProxy)getProxyFor(sgComponent);
    int[] atVisual = new int[1];
    boolean[] atIsFrontFacing = { true };
    int[] atSubElement = { -1 };
    double[] atZ = { NaN.0D };
    
    pick(componentProxy, x, y, z, planeMinX, planeMinY, planeMaxX, planeMaxY, nearClippingPlaneDistance, farClippingPlaneDistance, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing, atSubElement, atZ);
    
    Visual[] sgVisuals = null;
    Geometry[] sgGeometries = null;
    int[] subElements = null;
    boolean[] isFrontFacings = null;
    VisualProxy visualProxy = VisualProxy.map(atVisual[0]);
    if (visualProxy != null) {
      sgVisuals = new Visual[1];
      sgVisuals[0] = ((Visual)visualProxy.getSceneGraphElement());
      sgGeometries = new Geometry[1];
      sgGeometries[0] = sgVisuals[0].getGeometry();
      subElements = new int[1];
      subElements[0] = atSubElement[0];
      isFrontFacings = new boolean[1];
      isFrontFacings[0] = atIsFrontFacing[0];
    }
    
    return new PickInfo(sgComponent, null, sgVisuals, isFrontFacings, sgGeometries, subElements, atZ);
  }
  
  protected void dispatchAbsoluteTransformationChange(AbsoluteTransformationEvent absoluteTransformationEvent)
  {
    Component sgComponent = (Component)absoluteTransformationEvent.getSource();
    if (!sgComponent.isReleased())
    {

      ComponentProxy componentProxy = (ComponentProxy)getProxyFor(sgComponent);
      componentProxy.onAbsoluteTransformationChange();
    }
  }
  
  protected void dispatchBoundChange(BoundEvent boundEvent) {}
  
  public void dispatchChildAdd(ChildrenEvent childrenEvent)
  {
    Container sgContainer = (Container)childrenEvent.getSource();
    Component sgChild = childrenEvent.getChild();
    if ((!sgContainer.isReleased()) && (!sgChild.isReleased()))
    {

      ContainerProxy containerProxy = (ContainerProxy)getProxyFor(sgContainer);
      ComponentProxy childProxy = (ComponentProxy)getProxyFor(sgChild);
      containerProxy.onChildAdded(childProxy);
    }
  }
  
  public void dispatchChildRemove(ChildrenEvent childrenEvent) {
    Container sgContainer = (Container)childrenEvent.getSource();
    Component sgChild = childrenEvent.getChild();
    if ((!sgContainer.isReleased()) && (!sgChild.isReleased()))
    {

      ContainerProxy containerProxy = (ContainerProxy)getProxyFor(sgContainer);
      ComponentProxy childProxy = (ComponentProxy)getProxyFor(sgChild);
      containerProxy.onChildRemoved(childProxy);
    }
  }
  
  protected void dispatchHierarchyChange(HierarchyEvent hierarchyEvent) {
    Component sgComponent = (Component)hierarchyEvent.getSource();
    if (!sgComponent.isReleased())
    {

      ComponentProxy componentProxy = (ComponentProxy)getProxyFor(sgComponent);
      componentProxy.onHierarchyChange();
    }
  }
  
  public void commitAnyPendingChanges()
  {
    super.commitAnyPendingChanges();
    ComponentProxy.updateAbsoluteTransformationChanges();
    GeometryProxy.updateBoundChanges();
  }
  
  protected abstract AbstractProxy createProxyFor(Element paramElement);
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget()
  {
    return new OffscreenRenderTarget(this);
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() { return new OnscreenRenderTarget(this); }
}
