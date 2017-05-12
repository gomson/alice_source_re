package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import java.awt.Dimension;

public abstract interface OffscreenRenderTarget
  extends RenderTarget
{
  public abstract void setSize(int paramInt1, int paramInt2);
  
  public abstract void setSize(Dimension paramDimension);
}
