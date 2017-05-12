package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextProxy
{
  TextProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onBoundChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onTextChange(String paramString);
  
  protected native void onFontChange(String paramString, int paramInt1, int paramInt2);
  
  protected native void onExtrusionChange(double paramDouble1, double paramDouble2, double paramDouble3);
}
