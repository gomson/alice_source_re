package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Transformable;

















public abstract class TransformableProxy
  extends ReferenceFrameProxy
{
  public TransformableProxy() {}
  
  protected boolean listenToHierarchyAndAbsoluteTransformationChanges()
  {
    return false;
  }
  
  protected void changed(Property property, Object value) {
    if ((property != Transformable.LOCAL_TRANSFORMATION_PROPERTY) && 
      (property != Transformable.IS_FIRST_CLASS_PROPERTY))
    {
      super.changed(property, value);
    }
  }
}
