package edu.cmu.cs.stage3.alice.authoringtool.util.event;

public abstract interface RenderTargetPickManipulatorListener
{
  public abstract void prePick(RenderTargetPickManipulatorEvent paramRenderTargetPickManipulatorEvent);
  
  public abstract void postPick(RenderTargetPickManipulatorEvent paramRenderTargetPickManipulatorEvent);
}
