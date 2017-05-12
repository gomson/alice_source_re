package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy;

class AppearanceProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.AppearanceProxy
{
  AppearanceProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onAmbientColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onDiffuseColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onFillingStyleChange(int paramInt);
  
  protected native void onShadingStyleChange(int paramInt);
  
  protected native void onOpacityChange(double paramDouble);
  
  protected native void onSpecularHighlightColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onSpecularHighlightExponentChange(double paramDouble);
  
  protected native void onEmissiveColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onDiffuseColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onOpacityMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onEmissiveColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onSpecularHighlightColorMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onBumpMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected native void onDetailMapChange(TextureMapProxy paramTextureMapProxy);
}
