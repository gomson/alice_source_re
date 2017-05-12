package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

class LineArrayProxy
  extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.LineArrayProxy
{
  LineArrayProxy() {}
  
  protected native void createNativeInstance();
  
  protected native void releaseNativeInstance();
  
  protected native void onBoundChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected native void onVerticesFormatAndLengthChange(int paramInt1, int paramInt2);
  
  protected native void onVerticesVertexPositionChange(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3);
  
  protected native void onVerticesVertexNormalChange(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3);
  
  protected native void onVerticesVertexDiffuseColorChange(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected native void onVerticesVertexSpecularHighlightColorChange(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected native void onVerticesVertexTextureCoordinate0Change(int paramInt, float paramFloat1, float paramFloat2);
  
  protected native void onVerticesBeginChange();
  
  protected native void onVerticesEndChange();
  
  protected native void onVertexLowerBoundChange(int paramInt);
  
  protected native void onVertexUpperBoundChange(int paramInt);
}
