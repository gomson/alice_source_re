package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import java.awt.Color;

class Graphics extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.Graphics { public Graphics(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget) { super(renderTarget); }
  
  public Graphics(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy textureMapProxy) {
    super(renderTarget, textureMapProxy);
  }
  

  protected native void createNativeInstance(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter paramRenderTargetAdapter);
  

  protected native void createNativeInstance(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter paramRenderTargetAdapter, edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy paramTextureMapProxy);
  
  protected native void releaseNativeInstance();
  
  protected native void setFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt);
  
  public native void translate(int paramInt1, int paramInt2);
  
  public native Color getColor();
  
  public native void setColor(Color paramColor);
  
  public native void setPaintMode();
  
  public native void setXORMode(Color paramColor);
  
  public native void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public native void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public native void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public native void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public native void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public native void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public native void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public native void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
  
  public native void drawString(String paramString, int paramInt1, int paramInt2);
  
  public native void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public String toString()
  {
    return "edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Graphics[" + hashCode() + "]";
  }
}
