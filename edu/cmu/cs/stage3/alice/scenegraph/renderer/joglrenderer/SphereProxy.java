package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Sphere;





















class SphereProxy
  extends ShapeProxy
{
  SphereProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Sphere.RADIUS_PROPERTY)
    {

      super.changed(property, value);
    }
  }
}
