package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Box;
import edu.cmu.cs.stage3.alice.scenegraph.Property;





















class BoxProxy
  extends ShapeProxy
{
  BoxProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Box.WIDTH_PROPERTY)
    {
      if (property != Box.HEIGHT_PROPERTY)
      {
        if (property != Box.DEPTH_PROPERTY)
        {

          super.changed(property, value);
        }
      }
    }
  }
}
