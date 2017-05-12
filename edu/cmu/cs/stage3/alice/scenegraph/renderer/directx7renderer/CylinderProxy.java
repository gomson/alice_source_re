package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class CylinderProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CylinderProxy
{
  CylinderProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onBoundChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onBaseRadiusChange(double paramDouble);
  
  protected native void onTopRadiusChange(double paramDouble);
  
  protected native void onHeightChange(double paramDouble);
}
