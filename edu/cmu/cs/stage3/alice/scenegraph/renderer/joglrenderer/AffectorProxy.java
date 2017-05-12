package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

abstract class AffectorProxy
  extends ComponentProxy
{
  AffectorProxy() {}
  
  public void render(RenderContext context) {}
  
  public void pick(PickContext context, PickParameters pickParameters) {}
}
