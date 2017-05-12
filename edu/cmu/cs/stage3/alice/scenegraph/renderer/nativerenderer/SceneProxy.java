package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;















public abstract class SceneProxy
  extends ReferenceFrameProxy
{
  public SceneProxy() {}
  
  protected abstract void onBackgroundChange(BackgroundProxy paramBackgroundProxy);
  
  protected void changed(Property property, Object value)
  {
    if (property == Scene.BACKGROUND_PROPERTY) {
      onBackgroundChange((BackgroundProxy)getProxyFor((Background)value));
    } else {
      super.changed(property, value);
    }
  }
}
