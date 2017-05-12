package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Ray;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;









public abstract class AbstractRenderTarget
  implements RenderTarget
{
  private AbstractRenderer m_abstractRenderer;
  private Vector m_sgCameras = new Vector();
  private Camera[] m_sgCameraArray = null;
  private Vector m_renderTargetListeners = new Vector();
  private RenderTargetListener[] m_renderTargetListenerArray = null;
  private String m_name = null;
  
  protected AbstractRenderTarget(AbstractRenderer abstractRenderer) {
    m_abstractRenderer = abstractRenderer;
    m_abstractRenderer.addRenderTarget(this);
  }
  
  public void release() { m_abstractRenderer.removeRenderTarget(this); }
  
  protected void finalize() throws Throwable
  {
    release();
    super.finalize();
  }
  
  public Renderer getRenderer() { return m_abstractRenderer; }
  
  public void markDirty() {}
  
  public Dimension getSize()
  {
    Dimension size = new Dimension();
    return getSize(size);
  }
  
  public String getName() { return m_name; }
  

  public void setName(String name) { m_name = name; }
  
  public void addCamera(Camera camera) {
    if (!m_sgCameras.contains(camera))
    {

      m_sgCameras.addElement(camera);
      m_sgCameraArray = null;
    }
    






    markDirty();
  }
  
  public void removeCamera(Camera camera) { m_sgCameras.removeElement(camera);
    m_sgCameraArray = null;
    markDirty();
  }
  
  public Camera[] getCameras() { if (m_sgCameraArray == null) {
      m_sgCameraArray = new Camera[m_sgCameras.size()];
      m_sgCameras.copyInto(m_sgCameraArray);
    }
    return m_sgCameraArray;
  }
  
  public void clearCameras() { m_sgCameras.removeAllElements();
    markDirty();
  }
  
  public void addRenderTargetListener(RenderTargetListener renderTargetListener) {
    m_renderTargetListeners.addElement(renderTargetListener);
    m_renderTargetListenerArray = null;
  }
  
  public void removeRenderTargetListener(RenderTargetListener renderTargetListener) { m_renderTargetListeners.removeElement(renderTargetListener);
    m_renderTargetListenerArray = null;
  }
  
  public RenderTargetListener[] getRenderTargetListeners() { if (m_renderTargetListenerArray == null) {
      m_renderTargetListenerArray = new RenderTargetListener[m_renderTargetListeners.size()];
      m_renderTargetListeners.copyInto(m_renderTargetListenerArray);
    }
    return m_renderTargetListenerArray;
  }
  
  public Vector4d transformFromViewportToProjection(Vector3d xyz, Camera camera) {
    Rectangle viewport = getActualViewport(camera);
    double halfWidth = width / 2.0D;
    double halfHeight = height / 2.0D;
    double x = (x - halfWidth) / halfWidth;
    double y = -(y - halfHeight) / halfHeight;
    return new Vector4d(x, y, z, 1.0D);
  }
  
  public Vector3d transformFromProjectionToCamera(Vector4d xyzw, Camera camera) { Matrix4d inverseProjectionMatrix = getProjectionMatrix(camera);
    inverseProjectionMatrix.invert();
    return MathUtilities.createVector3d(MathUtilities.multiply(xyzw, inverseProjectionMatrix));
  }
  
  public Vector3d transformFromViewportToCamera(Vector3d xyz, Camera camera) { return transformFromProjectionToCamera(transformFromViewportToProjection(xyz, camera), camera); }
  
  public Vector4d transformFromCameraToProjection(Vector3d xyz, Camera camera)
  {
    Matrix4d projectionMatrix = getProjectionMatrix(camera);
    return MathUtilities.multiply(xyz, 1.0D, projectionMatrix);
  }
  
  public Vector3d transformFromProjectionToViewport(Vector4d xyzw, Camera camera) { Rectangle viewport = getActualViewport(camera);
    double halfWidth = width / 2.0D;
    double halfHeight = height / 2.0D;
    Vector3d xyz = MathUtilities.createVector3d(xyzw);
    x = ((x + 1.0D) * halfWidth);
    y = (height - (y + 1.0D) * halfHeight);
    return xyz;
  }
  
  public Vector3d transformFromCameraToViewport(Vector3d xyz, Camera camera) { return transformFromProjectionToViewport(transformFromCameraToProjection(xyz, camera), camera); }
  
  public Ray getRayAtPixel(Camera camera, int pixelX, int pixelY)
  {
    Matrix4d inverseProjection = getProjectionMatrix(camera);
    inverseProjection.invert();
    
    Point3d origin = new Point3d(
      m20 / m23, 
      m21 / m23, 
      m22 / m23);
    

    Rectangle viewport = getActualViewport(camera);
    double halfWidth = width / 2.0D;
    double halfHeight = height / 2.0D;
    double x = (pixelX + 0.5D - halfWidth) / halfWidth;
    double y = -(pixelY + 0.5D - halfHeight) / halfHeight;
    
    Vector4d qs = new Vector4d(x, y, 0.0D, 1.0D);
    Vector4d qw = MathUtilities.multiply(qs, inverseProjection);
    
    Vector3d direction = new Vector3d(
      x * m23 - w * m20, 
      y * m23 - w * m21, 
      z * m23 - w * m22);
    
    direction.normalize();
    
    return new Ray(origin, direction);
  }
  
  protected void onClear() {
    m_abstractRenderer.enterIgnore();
    try {
      RenderTargetEvent renderTargetEvent = new RenderTargetEvent(this);
      RenderTargetListener[] rtls = getRenderTargetListeners();
      for (int i = 0; i < rtls.length; i++) {
        rtls[i].cleared(renderTargetEvent);
      }
    } finally {
      m_abstractRenderer.leaveIgnore();
    }
  }
  
  protected void onRender() { m_abstractRenderer.enterIgnore();
    try {
      RenderTargetEvent renderTargetEvent = new RenderTargetEvent(this);
      RenderTargetListener[] rtls = getRenderTargetListeners();
      for (int i = 0; i < rtls.length; i++) {
        rtls[i].rendered(renderTargetEvent);
      }
    } finally {
      m_abstractRenderer.leaveIgnore();
    }
  }
  
  public String toString() {
    return getClass().getName() + "[" + getName() + "]";
  }
}
