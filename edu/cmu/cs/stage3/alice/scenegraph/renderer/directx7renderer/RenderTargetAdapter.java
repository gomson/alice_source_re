package edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.TextureMapProxy;

public class RenderTargetAdapter extends edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTargetAdapter {
  public RenderTargetAdapter(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget renderTarget) { m_renderTarget = renderTarget;
    createNativeInstance((Renderer)renderTarget.getRenderer()); }
  
  private edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget m_renderTarget;
  private int m_nativeInstance;
  public void release() { releaseNativeInstance(); }
  

  public java.awt.Graphics getOffscreenGraphics()
  {
    return new Graphics(m_renderTarget);
  }
  
  public java.awt.Graphics getGraphics(TextureMapProxy textureMapProxy) {
    if (textureMapProxy != null) {
      return new Graphics(m_renderTarget, textureMapProxy);
    }
    return null;
  }
  
  protected native void createNativeInstance(Renderer paramRenderer);
  
  protected native void releaseNativeInstance();
  
  public native void reset();
  
  public native void clear(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy, boolean paramBoolean);
  
  public native void render(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy);
  
  public native void pick(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt1, boolean[] paramArrayOfBoolean, int[] paramArrayOfInt2, double[] paramArrayOfDouble);
  
  public native void blt(TextureMapProxy paramTextureMapProxy);
  
  public native void onViewportChange(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native double[] getActualPlane(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy paramOrthographicCameraProxy);
  
  public native double[] getActualPlane(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.PerspectiveCameraProxy paramPerspectiveCameraProxy);
  
  public native double getActualHorizontalViewingAngle(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy paramSymmetricPerspectiveCameraProxy);
  
  public native double getActualVerticalViewingAngle(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.SymmetricPerspectiveCameraProxy paramSymmetricPerspectiveCameraProxy);
  
  public native java.awt.Rectangle getActualViewport(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy);
  
  public native javax.vecmath.Matrix4d getProjectionMatrix(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy);
  
  public native boolean isLetterboxedAsOpposedToDistorted(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy);
  
  public native void setIsLetterboxedAsOpposedToDistorted(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.CameraProxy paramCameraProxy, boolean paramBoolean);
  
  public native boolean rendersOnEdgeTrianglesAsLines(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy paramOrthographicCameraProxy);
  
  public native void setRendersOnEdgeTrianglesAsLines(edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.OrthographicCameraProxy paramOrthographicCameraProxy, boolean paramBoolean);
  
  public native void setDesiredSize(int paramInt1, int paramInt2);
  
  public native int getWidth();
  
  public native int getHeight();
  
  public native int getPitch();
  
  public native int getBitCount();
  
  public native int getRedBitMask();
  
  public native int getGreenBitMask();
  
  public native int getBlueBitMask();
  
  public native int getAlphaBitMask();
  
  public native void getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int[] paramArrayOfInt);
  
  public native int getWidth(TextureMapProxy paramTextureMapProxy);
  
  public native int getHeight(TextureMapProxy paramTextureMapProxy);
  
  public native int getPitch(TextureMapProxy paramTextureMapProxy);
  
  public native int getBitCount(TextureMapProxy paramTextureMapProxy);
  
  public native int getRedBitMask(TextureMapProxy paramTextureMapProxy);
  
  public native int getGreenBitMask(TextureMapProxy paramTextureMapProxy);
  
  public native int getBlueBitMask(TextureMapProxy paramTextureMapProxy);
  
  public native int getAlphaBitMask(TextureMapProxy paramTextureMapProxy);
  
  public native void getPixels(TextureMapProxy paramTextureMapProxy, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int[] paramArrayOfInt);
  
  public native int getZBufferPitch();
  
  public native int getZBufferBitCount();
  
  public native void getZBufferPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt);
  
  public native void setSilhouetteThickness(double paramDouble);
  
  public native double getSilhouetteThickness();
}
