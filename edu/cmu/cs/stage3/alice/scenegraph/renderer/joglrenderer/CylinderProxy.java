package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Cylinder;
import edu.cmu.cs.stage3.alice.scenegraph.Property;





















class CylinderProxy
  extends ShapeProxy
{
  CylinderProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Cylinder.BASE_RADIUS_PROPERTY)
    {
      if (property != Cylinder.TOP_RADIUS_PROPERTY)
      {
        if (property != Cylinder.HEIGHT_PROPERTY)
        {

          super.changed(property, value);
        }
      }
    }
  }
}
