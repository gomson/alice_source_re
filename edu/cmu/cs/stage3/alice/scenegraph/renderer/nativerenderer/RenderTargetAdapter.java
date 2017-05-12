package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.vecmath.Matrix4d;

public abstract class RenderTargetAdapter
{
  public RenderTargetAdapter() {}
  
  public abstract void reset();
  
  public abstract void release();
  
  public abstract Graphics getOffscreenGraphics();
  
  public abstract Graphics getGraphics(TextureMapProxy paramTextureMapProxy);
  
  public abstract void clear(CameraProxy paramCameraProxy, boolean paramBoolean);
  
  public abstract void render(CameraProxy paramCameraProxy);
  
  public abstract void pick(CameraProxy paramCameraProxy, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt1, boolean[] paramArrayOfBoolean, int[] paramArrayOfInt2, double[] paramArrayOfDouble);
  
  public abstract void blt(TextureMapProxy paramTextureMapProxy);
  
  public abstract void onViewportChange(CameraProxy paramCameraProxy, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract double[] getActualPlane(OrthographicCameraProxy paramOrthographicCameraProxy);
  
  public abstract double[] getActualPlane(PerspectiveCameraProxy paramPerspectiveCameraProxy);
  
  public abstract double getActualHorizontalViewingAngle(SymmetricPerspectiveCameraProxy paramSymmetricPerspectiveCameraProxy);
  
  public abstract double getActualVerticalViewingAngle(SymmetricPerspectiveCameraProxy paramSymmetricPerspectiveCameraProxy);
  
  public abstract Rectangle getActualViewport(CameraProxy paramCameraProxy);
  
  public abstract Matrix4d getProjectionMatrix(CameraProxy paramCameraProxy);
  
  public abstract boolean isLetterboxedAsOpposedToDistorted(CameraProxy paramCameraProxy);
  
  public abstract void setIsLetterboxedAsOpposedToDistorted(CameraProxy paramCameraProxy, boolean paramBoolean);
  
  public abstract boolean rendersOnEdgeTrianglesAsLines(OrthographicCameraProxy paramOrthographicCameraProxy);
  
  public abstract void setRendersOnEdgeTrianglesAsLines(OrthographicCameraProxy paramOrthographicCameraProxy, boolean paramBoolean);
  
  public abstract void setDesiredSize(int paramInt1, int paramInt2);
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract int getPitch();
  
  public abstract int getBitCount();
  
  public abstract int getRedBitMask();
  
  public abstract int getGreenBitMask();
  
  public abstract int getBlueBitMask();
  
  public abstract int getAlphaBitMask();
  
  public abstract void getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int[] paramArrayOfInt);
  
  public abstract int getZBufferPitch();
  
  public abstract int getZBufferBitCount();
  
  public abstract void getZBufferPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt);
  
  public abstract int getWidth(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getHeight(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getPitch(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getBitCount(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getRedBitMask(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getGreenBitMask(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getBlueBitMask(TextureMapProxy paramTextureMapProxy);
  
  public abstract int getAlphaBitMask(TextureMapProxy paramTextureMapProxy);
  
  public abstract void getPixels(TextureMapProxy paramTextureMapProxy, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int[] paramArrayOfInt);
  
  public abstract void setSilhouetteThickness(double paramDouble);
  
  public abstract double getSilhouetteThickness();
}
