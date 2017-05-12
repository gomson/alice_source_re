package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class BoxProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.BoxProxy
{
  BoxProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onBoundChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onWidthChange(double paramDouble);
  
  protected native void onHeightChange(double paramDouble);
  
  protected native void onDepthChange(double paramDouble);
}
