package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy;

class BackgroundProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BackgroundProxy
{
  BackgroundProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onTextureMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onTextureMapSourceRectangleChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}
