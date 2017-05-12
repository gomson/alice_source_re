package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener;
import edu.cmu.cs.stage3.math.Ray;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public abstract interface RenderTarget
{
  public static final boolean SUB_ELEMENT_IS_REQUIRED = true;
  public static final boolean SUB_ELEMENT_IS_NOT_REQUIRED = false;
  public static final boolean ONLY_FRONT_MOST_VISUAL_IS_REQUIRED = true;
  public static final boolean ALL_VISUALS_ARE_REQUIRED = false;
  
  public abstract String getName();
  
  public abstract void setName(String paramString);
  
  public abstract void release();
  
  public abstract Renderer getRenderer();
  
  public abstract void addCamera(Camera paramCamera);
  
  public abstract void removeCamera(Camera paramCamera);
  
  public abstract Camera[] getCameras();
  
  public abstract void markDirty();
  
  public abstract void clearAndRenderOffscreen();
  
  public abstract boolean rendersOnEdgeTrianglesAsLines(OrthographicCamera paramOrthographicCamera);
  
  public abstract void setRendersOnEdgeTrianglesAsLines(OrthographicCamera paramOrthographicCamera, boolean paramBoolean);
  
  public abstract PickInfo pick(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract Vector4d transformFromViewportToProjection(Vector3d paramVector3d, Camera paramCamera);
  
  public abstract Vector3d transformFromProjectionToCamera(Vector4d paramVector4d, Camera paramCamera);
  
  public abstract Vector3d transformFromViewportToCamera(Vector3d paramVector3d, Camera paramCamera);
  
  public abstract Vector4d transformFromCameraToProjection(Vector3d paramVector3d, Camera paramCamera);
  
  public abstract Vector3d transformFromProjectionToViewport(Vector4d paramVector4d, Camera paramCamera);
  
  public abstract Vector3d transformFromCameraToViewport(Vector3d paramVector3d, Camera paramCamera);
  
  public abstract Image getOffscreenImage();
  
  public abstract Graphics getOffscreenGraphics();
  
  public abstract Image getZBufferImage();
  
  public abstract Image getImage(TextureMap paramTextureMap);
  
  public abstract Graphics getGraphics(TextureMap paramTextureMap);
  
  public abstract void copyOffscreenImageToTextureMap(TextureMap paramTextureMap);
  
  public abstract Dimension getSize();
  
  public abstract Dimension getSize(Dimension paramDimension);
  
  public abstract double[] getActualPlane(OrthographicCamera paramOrthographicCamera);
  
  public abstract double[] getActualPlane(PerspectiveCamera paramPerspectiveCamera);
  
  public abstract double getActualHorizontalViewingAngle(SymmetricPerspectiveCamera paramSymmetricPerspectiveCamera);
  
  public abstract double getActualVerticalViewingAngle(SymmetricPerspectiveCamera paramSymmetricPerspectiveCamera);
  
  public abstract Rectangle getActualViewport(Camera paramCamera);
  
  public abstract Matrix4d getProjectionMatrix(Camera paramCamera);
  
  public abstract Ray getRayAtPixel(Camera paramCamera, int paramInt1, int paramInt2);
  
  public abstract Rectangle getViewport(Camera paramCamera);
  
  public abstract void setViewport(Camera paramCamera, Rectangle paramRectangle);
  
  public abstract boolean isLetterboxedAsOpposedToDistorted(Camera paramCamera);
  
  public abstract void setIsLetterboxedAsOpposedToDistorted(Camera paramCamera, boolean paramBoolean);
  
  public abstract void addRenderTargetListener(RenderTargetListener paramRenderTargetListener);
  
  public abstract void removeRenderTargetListener(RenderTargetListener paramRenderTargetListener);
  
  public abstract RenderTargetListener[] getRenderTargetListeners();
}
