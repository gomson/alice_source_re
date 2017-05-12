package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Torus;





















class TorusProxy
  extends ShapeProxy
{
  TorusProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Torus.INNER_RADIUS_PROPERTY)
    {
      if (property != Torus.OUTER_RADIUS_PROPERTY)
      {

        super.changed(property, value);
      }
    }
  }
}
