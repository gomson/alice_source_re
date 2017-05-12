package edu.cmu.cs.stage3.alice.scenegraph.renderer.event;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import java.util.EventObject;


















public class RenderTargetEvent
  extends EventObject
{
  public RenderTargetEvent(RenderTarget source)
  {
    super(source);
  }
  
  public RenderTarget getRenderTarget() { return (RenderTarget)getSource(); }
}
