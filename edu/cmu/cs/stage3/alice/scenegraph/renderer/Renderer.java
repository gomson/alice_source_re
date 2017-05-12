package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import javax.vecmath.Vector3d;

public abstract interface Renderer
{
  public abstract boolean isSoftwareEmulationForced();
  
  public abstract void setIsSoftwareEmulationForced(boolean paramBoolean);
  
  public abstract OnscreenRenderTarget createOnscreenRenderTarget();
  
  public abstract OffscreenRenderTarget createOffscreenRenderTarget();
  
  public abstract OffscreenRenderTarget[] getOffscreenRenderTargets();
  
  public abstract OnscreenRenderTarget[] getOnscreenRenderTargets();
  
  public abstract PickInfo pick(Component paramComponent, Vector3d paramVector3d, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void release();
}
