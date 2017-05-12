package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Scene;
















class SceneProxy
  extends ReferenceFrameProxy
{
  SceneProxy() {}
  
  private BackgroundProxy m_backgroundProxy = null;
  
  public BackgroundProxy getBackgroundProxy() { return m_backgroundProxy; }
  
  public void setup(RenderContext context)
  {
    context.beginAffectorSetup();
    super.setup(context);
    context.endAffectorSetup();
  }
  
  protected void changed(Property property, Object value) {
    if (property == Scene.BACKGROUND_PROPERTY) {
      m_backgroundProxy = ((BackgroundProxy)getProxyFor((Background)value));
    } else {
      super.changed(property, value);
    }
  }
}
