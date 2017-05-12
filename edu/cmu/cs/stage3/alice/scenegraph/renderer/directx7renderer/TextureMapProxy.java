package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class TextureMapProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy
{
  TextureMapProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onImageChange(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  protected native void onFormatChange(int paramInt);
}
