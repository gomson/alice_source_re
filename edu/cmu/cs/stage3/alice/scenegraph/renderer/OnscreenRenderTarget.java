package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import java.awt.Component;

public abstract interface OnscreenRenderTarget
  extends RenderTarget
{
  public abstract Component getAWTComponent();
}
