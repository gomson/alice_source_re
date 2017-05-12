package edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.BoundEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.HierarchyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.ReleaseEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractRenderer;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import javax.vecmath.Vector3d;









public class Renderer
  extends AbstractRenderer
{
  public Renderer() {}
  
  protected boolean requiresHierarchyAndAbsoluteTransformationListening()
  {
    return false;
  }
  
  protected boolean requiresBoundListening() {
    return false;
  }
  

  protected void dispatchPropertyChange(PropertyEvent propertyEvent) {}
  

  protected void dispatchRelease(ReleaseEvent releaseEvent) {}
  

  protected void dispatchAbsoluteTransformationChange(AbsoluteTransformationEvent absoluteTransformationEvent) {}
  

  protected void dispatchBoundChange(BoundEvent boundEvent) {}
  

  public void dispatchChildAdd(ChildrenEvent childrenEvent) {}
  
  public void dispatchChildRemove(ChildrenEvent childrenEvent) {}
  
  protected void dispatchHierarchyChange(HierarchyEvent hierarchyEvent) {}
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget createOffscreenRenderTarget()
  {
    return new OffscreenRenderTarget(this);
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget createOnscreenRenderTarget() { return new OnscreenRenderTarget(this); }
  
  public PickInfo pick(Component sgComponent, Vector3d v, double planeMinX, double planeMinY, double planeMaxX, double planeMaxY, double nearClippingPlaneDistance, double farClippingPlaneDistance, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    return null;
  }
}
