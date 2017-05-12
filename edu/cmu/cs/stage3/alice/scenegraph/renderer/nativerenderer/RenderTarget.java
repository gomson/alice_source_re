package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.vecmath.Matrix4d;




public abstract class RenderTarget
  extends AbstractProxyRenderTarget
{
  private RenderTargetAdapter m_adapter;
  
  protected RenderTargetAdapter getAdapter()
  {
    return m_adapter;
  }
  
  private Dictionary m_cameraViewportMap = new Hashtable();
  
  RenderTarget(Renderer renderer) {
    super(renderer);
    m_adapter = renderer.createRenderTargetAdapter(this);
  }
  
  public void release() {
    super.release();
    m_adapter.release();
  }
  
  public Graphics getOffscreenGraphics() {
    return m_adapter.getOffscreenGraphics();
  }
  
  public Graphics getGraphics(TextureMap textureMap) { return m_adapter.getGraphics((TextureMapProxy)getProxyFor(textureMap)); }
  
  public Matrix4d getProjectionMatrix(Camera sgCamera)
  {
    return m_adapter.getProjectionMatrix((CameraProxy)getProxyFor(sgCamera));
  }
  
  public double[] getActualPlane(OrthographicCamera sgOrthographicCamera) { return m_adapter.getActualPlane((OrthographicCameraProxy)getProxyFor(sgOrthographicCamera)); }
  
  public double[] getActualPlane(PerspectiveCamera sgPerspectiveCamera) {
    return m_adapter.getActualPlane((PerspectiveCameraProxy)getProxyFor(sgPerspectiveCamera));
  }
  
  public double getActualHorizontalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) { return m_adapter.getActualHorizontalViewingAngle((SymmetricPerspectiveCameraProxy)getProxyFor(sgSymmetricPerspectiveCamera)); }
  
  public double getActualVerticalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
    return m_adapter.getActualVerticalViewingAngle((SymmetricPerspectiveCameraProxy)getProxyFor(sgSymmetricPerspectiveCamera));
  }
  
  public Rectangle getActualViewport(Camera sgCamera) { return m_adapter.getActualViewport((CameraProxy)getProxyFor(sgCamera)); }
  

  public Rectangle getViewport(Camera sgCamera) { return (Rectangle)m_cameraViewportMap.get(sgCamera); }
  
  public void setViewport(Camera sgCamera, Rectangle viewport) {
    CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    if (viewport == null) {
      m_adapter.onViewportChange(cameraProxy, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
      m_cameraViewportMap.remove(sgCamera);
    } else {
      m_adapter.onViewportChange(cameraProxy, x, y, width, height);
      m_cameraViewportMap.put(sgCamera, viewport);
    }
  }
  
  public boolean rendersOnEdgeTrianglesAsLines(OrthographicCamera sgOrthographicCamera) { return m_adapter.rendersOnEdgeTrianglesAsLines((OrthographicCameraProxy)getProxyFor(sgOrthographicCamera)); }
  
  public void setRendersOnEdgeTrianglesAsLines(OrthographicCamera sgOrthographicCamera, boolean rendersOnEdgeTrianglesAsLines) {
    m_adapter.setRendersOnEdgeTrianglesAsLines((OrthographicCameraProxy)getProxyFor(sgOrthographicCamera), rendersOnEdgeTrianglesAsLines);
  }
  
  public boolean isLetterboxedAsOpposedToDistorted(Camera sgCamera) {
    return m_adapter.isLetterboxedAsOpposedToDistorted((CameraProxy)getProxyFor(sgCamera));
  }
  
  public void setIsLetterboxedAsOpposedToDistorted(Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted) { m_adapter.setIsLetterboxedAsOpposedToDistorted((CameraProxy)getProxyFor(sgCamera), isLetterboxedAsOpposedToDistorted); }
  


  public void commitAnyPendingChanges() { ((Renderer)getRenderer()).commitAnyPendingChanges(); }
  
  public void clearAndRenderOffscreen() {
    commitAnyPendingChanges();
    Camera[] sgCameras = getCameras();
    if (sgCameras.length > 0) {
      for (int i = 0; i < sgCameras.length; i++) {
        CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCameras[i]);
        m_adapter.clear(cameraProxy, i == 0);
        onClear();
        ComponentProxy.updateAbsoluteTransformationChanges();
        GeometryProxy.updateBoundChanges();
        m_adapter.render(cameraProxy);
        onRender();
      }
    }
  }
  
  private CameraProxy getCameraProxy(int x, int y) {
    if ((x >= 0) && (y >= 0) && (x < m_adapter.getWidth()) && (y < m_adapter.getHeight())) {
      Camera[] sgCameras = getCameras();
      if (sgCameras != null) {
        for (int i = 0; i < sgCameras.length; i++) {
          CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCameras[i]);
          Rectangle viewport = m_adapter.getActualViewport(cameraProxy);
          if (viewport != null) {
            if (viewport.contains(x, y)) {
              return cameraProxy;
            }
          } else {
            return cameraProxy;
          }
        }
      }
    }
    return null;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    commitAnyPendingChanges();
    CameraProxy cameraProxy = getCameraProxy(x, y);
    if (cameraProxy != null) {
      int[] atVisual = new int[1];
      boolean[] atIsFrontFacing = { true };
      int[] atSubElement = { -1 };
      double[] atZ = { NaN.0D };
      
      m_adapter.pick(cameraProxy, x, y, isSubElementRequired, isOnlyFrontMostRequired, atVisual, atIsFrontFacing, atSubElement, atZ);
      
      Visual[] sgVisuals = null;
      Geometry[] sgGeometries = null;
      int[] subElements = null;
      boolean[] isFrontFacings = null;
      VisualProxy visualProxy = VisualProxy.map(atVisual[0]);
      if (visualProxy != null) {
        sgVisuals = new Visual[1];
        sgVisuals[0] = ((Visual)visualProxy.getSceneGraphElement());
        sgGeometries = new Geometry[1];
        sgGeometries[0] = sgVisuals[0].getGeometry();
        subElements = new int[1];
        subElements[0] = atSubElement[0];
        isFrontFacings = new boolean[1];
        isFrontFacings[0] = atIsFrontFacing[0];
      }
      return new PickInfo(this, (Camera)cameraProxy.getSceneGraphElement(), x, y, sgVisuals, isFrontFacings, sgGeometries, subElements);
    }
    return null;
  }
  







  public Image getOffscreenImage()
  {
    int width = m_adapter.getWidth();
    int height = m_adapter.getHeight();
    int pitch = m_adapter.getPitch();
    int bitCount = m_adapter.getBitCount();
    int redBitMask = m_adapter.getRedBitMask();
    int greenBitMask = m_adapter.getGreenBitMask();
    int blueBitMask = m_adapter.getBlueBitMask();
    int alphaBitMask = m_adapter.getAlphaBitMask();
    
    int[] pixels = new int[width * height];
    m_adapter.getPixels(0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels);
    BufferedImage bufferedImage = new BufferedImage(width, height, 1);
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
    return bufferedImage;
  }
  































  public Image getZBufferImage()
  {
    int width = m_adapter.getWidth();
    int height = m_adapter.getHeight();
    int zPitch = m_adapter.getZBufferPitch();
    int zBitCount = m_adapter.getZBufferBitCount();
    int[] zPixels = new int[width * height];
    m_adapter.getZBufferPixels(0, 0, width, height, zPitch, zBitCount, zPixels);
    BufferedImage bufferedImage = new BufferedImage(width, height, 1);
    bufferedImage.setRGB(0, 0, width, height, zPixels, 0, width);
    return bufferedImage;
  }
  
  public Image getImage(TextureMap textureMap) {
    TextureMapProxy textureMapProxy = (TextureMapProxy)getProxyFor(textureMap);
    int width = m_adapter.getWidth(textureMapProxy);
    int height = m_adapter.getHeight(textureMapProxy);
    int pitch = m_adapter.getPitch(textureMapProxy);
    int bitCount = m_adapter.getBitCount(textureMapProxy);
    int redBitMask = m_adapter.getRedBitMask(textureMapProxy);
    int greenBitMask = m_adapter.getGreenBitMask(textureMapProxy);
    int blueBitMask = m_adapter.getBlueBitMask(textureMapProxy);
    int alphaBitMask = m_adapter.getAlphaBitMask(textureMapProxy);
    
    int[] pixels = new int[width * height];
    m_adapter.getPixels(textureMapProxy, 0, 0, width, height, pitch, bitCount, redBitMask, greenBitMask, blueBitMask, alphaBitMask, pixels);
    
    BufferedImage bufferedImage = new BufferedImage(width, height, 1);
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
    return bufferedImage;
  }
  
  public void copyOffscreenImageToTextureMap(TextureMap textureMap) {
    TextureMapProxy textureMapProxy = (TextureMapProxy)getProxyFor(textureMap);
    m_adapter.blt(textureMapProxy);
    markDirty();
  }
  
  public void setSilhouetteThickness(double silhouetteThickness) {
    m_adapter.setSilhouetteThickness(silhouetteThickness);
  }
  
  public double getSilhouetteThickness() { return m_adapter.getSilhouetteThickness(); }
}
