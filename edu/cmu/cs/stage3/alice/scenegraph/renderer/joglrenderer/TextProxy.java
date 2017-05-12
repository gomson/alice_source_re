package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Text;





















class TextProxy
  extends GeometryProxy
{
  TextProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Text.TEXT_PROPERTY)
    {
      if (property != Text.FONT_PROPERTY)
      {
        if (property != Text.EXTRUSION_PROPERTY)
        {

          super.changed(property, value);
        }
      }
    }
  }
}
