package edu.cmu.cs.stage3.alice.authoringtool.util.event;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;






















public class RenderTargetPickManipulatorEvent
{
  private RenderTarget renderTarget;
  private PickInfo pickInfo;
  
  public RenderTargetPickManipulatorEvent(RenderTarget renderTarget, PickInfo pickInfo)
  {
    this.renderTarget = renderTarget;
    this.pickInfo = pickInfo;
  }
  
  public RenderTarget getRenderTarget() {
    return renderTarget;
  }
  
  public PickInfo getPickInfo() {
    return pickInfo;
  }
}
