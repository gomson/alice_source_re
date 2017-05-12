package edu.cmu.cs.stage3.alice.scenegraph.renderer;

public abstract interface RenderTargetFactory
{
  public abstract boolean isSoftwareEmulationForced();
  
  public abstract void setIsSoftwareEmulationForced(boolean paramBoolean);
  
  public abstract OffscreenRenderTarget createOffscreenRenderTarget();
  
  public abstract OnscreenRenderTarget createOnscreenRenderTarget();
  
  public abstract void releaseOffscreenRenderTarget(OffscreenRenderTarget paramOffscreenRenderTarget);
  
  public abstract void releaseOnscreenRenderTarget(OnscreenRenderTarget paramOnscreenRenderTarget);
  
  public abstract void release();
}
