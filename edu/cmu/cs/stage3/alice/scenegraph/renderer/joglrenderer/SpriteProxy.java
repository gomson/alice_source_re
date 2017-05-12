package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Sprite;





















class SpriteProxy
  extends GeometryProxy
{
  SpriteProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, boolean isSubElementRequired) {}
  
  protected void changed(Property property, Object value)
  {
    if (property != Sprite.RADIUS_PROPERTY)
    {

      super.changed(property, value);
    }
  }
}
