package edu.cmu.cs.stage3.alice.scenegraph.renderer.event;

public abstract interface RenderTargetListener
{
  public abstract void cleared(RenderTargetEvent paramRenderTargetEvent);
  
  public abstract void rendered(RenderTargetEvent paramRenderTargetEvent);
}
