package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;
import edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.math.Vector4;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.vecmath.Matrix4d;

public class RenderTarget extends Element
{
  /**
   * @deprecated
   */
  public final ObjectProperty requiredCapabilities = new ObjectProperty(this, "requiredCapabilities", null, Long.class);
  private OnscreenRenderTarget m_onscreenRenderTarget = null;
  private Component m_awtComponent;
  private Vector m_cameras = new Vector();
  private Camera[] m_cameraArray = null;
  private RenderTargetFactory m_renderTargetFactory;
  private static Dictionary s_componentMap = new Hashtable();
  private static Dictionary s_eventMap = new Hashtable();
  
  public RenderTarget() { requiredCapabilities.deprecate(); }
  
  protected void finalize() throws Throwable
  {
    if (m_awtComponent != null) {
      s_componentMap.remove(m_awtComponent);
    }
    super.finalize();
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      Enumeration enum0 = m_cameras.elements();
      while (enum0.hasMoreElements()) {
        Camera camera = (Camera)enum0.nextElement();
        if (m_onscreenRenderTarget != null)
          m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
      }
    case 2: 
      if ((goto 109) && 
      
        (m_onscreenRenderTarget != null)) {
        if (m_renderTargetFactory != null) {
          m_renderTargetFactory.releaseOnscreenRenderTarget(m_onscreenRenderTarget);
        }
        m_onscreenRenderTarget = null;
      }
      break;
    }
    super.internalRelease(pass);
  }
  
  public PickInfo pick(int x, int y, boolean isSubElementRequired, boolean isOnlyFrontMostRequired) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.pick(x, y, isSubElementRequired, isOnlyFrontMostRequired);
    }
    throw new NullPointerException("internal m_onscreenRenderTarget is null");
  }
  
  public static PickInfo pick(MouseEvent mouseEvent) {
    PickInfo pickInfo = (PickInfo)s_eventMap.get(mouseEvent);
    if (pickInfo == null) {
      RenderTarget renderTarget = (RenderTarget)s_componentMap.get(mouseEvent.getComponent());
      pickInfo = renderTarget.pick(mouseEvent.getX(), mouseEvent.getY(), false, true);
      if (pickInfo != null) {
        s_eventMap.put(mouseEvent, pickInfo);
      }
    }
    return pickInfo;
  }
  
  public void commit(RenderTargetFactory renderTargetFactory) { m_renderTargetFactory = renderTargetFactory;
    m_onscreenRenderTarget = renderTargetFactory.createOnscreenRenderTarget();
    m_awtComponent = m_onscreenRenderTarget.getAWTComponent();
    s_componentMap.put(m_awtComponent, this);
    Enumeration enum0 = m_cameras.elements();
    while (enum0.hasMoreElements()) {
      Camera camera = (Camera)enum0.nextElement();
      m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
      m_onscreenRenderTarget.setIsLetterboxedAsOpposedToDistorted(camera.getSceneGraphCamera(), isLetterboxedAsOpposedToDistorted.booleanValue());
    }
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.Renderer getRenderer() { return m_onscreenRenderTarget.getRenderer(); }
  

  public OnscreenRenderTarget getInternal() { return m_onscreenRenderTarget; }
  
  public void addCamera(Camera camera) {
    if (!m_cameras.contains(camera))
    {
      m_cameras.addElement(camera);
      m_cameraArray = null;
      if (m_onscreenRenderTarget != null)
        m_onscreenRenderTarget.addCamera(camera.getSceneGraphCamera());
    }
  }
  
  public void removeCamera(Camera camera) {
    m_cameras.removeElement(camera);
    m_cameraArray = null;
    if (m_onscreenRenderTarget != null)
      m_onscreenRenderTarget.removeCamera(camera.getSceneGraphCamera());
  }
  
  public Camera[] getCameras() {
    if (m_cameraArray == null) {
      m_cameraArray = new Camera[m_cameras.size()];
      m_cameras.copyInto(m_cameraArray);
    }
    return m_cameraArray;
  }
  
  public double[] getActualPlane(OrthographicCamera orthographicCamera) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getActualPlane(orthographicCamera.getSceneGraphOrthographicCamera());
    }
    return null;
  }
  
  public double[] getActualPlane(PerspectiveCamera perspectiveCamera) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getActualPlane(perspectiveCamera.getSceneGraphPerspectiveCamera());
    }
    return null;
  }
  
  public double getActualHorizontalViewingAngle(SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getActualVerticalViewingAngle(symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
    }
    return NaN.0D;
  }
  
  public double getActualVerticalViewingAngle(SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getActualVerticalViewingAngle(symmetricPerspectiveCamera.getSceneGraphSymmetricPerspectiveCamera());
    }
    return NaN.0D;
  }
  
  public Matrix4d getProjectionMatrix(Camera camera)
  {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getProjectionMatrix(camera.getSceneGraphCamera());
    }
    return null;
  }
  
  public Rectangle getActualViewport(Camera camera)
  {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getActualViewport(camera.getSceneGraphCamera());
    }
    return null;
  }
  
  public Rectangle getViewport(Camera camera) {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getViewport(camera.getSceneGraphCamera());
    }
    return null;
  }
  
  public void setViewport(Camera camera, Rectangle rectangle) {
    if (m_onscreenRenderTarget != null)
      m_onscreenRenderTarget.setViewport(camera.getSceneGraphCamera(), rectangle);
  }
  
  public Vector4 project(Vector3 point, Camera camera) {
    if (m_onscreenRenderTarget != null) {
      Matrix4d projection = m_onscreenRenderTarget.getProjectionMatrix(camera.getSceneGraphCamera());
      Vector4 xyzw = new Vector4(x, y, z, 1.0D);
      return Vector4.multiply(xyzw, projection);
    }
    return null;
  }
  

  public Component getAWTComponent() { return m_awtComponent; }
  
  public java.awt.Image getOffscreenImage() {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getOffscreenImage();
    }
    return null;
  }
  
  public java.awt.Graphics getOffscreenGraphics() {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getOffscreenGraphics();
    }
    return null;
  }
  
  public void addRenderTargetListener(RenderTargetListener renderTargetListener) {
    if (m_onscreenRenderTarget != null) {
      m_onscreenRenderTarget.addRenderTargetListener(renderTargetListener);
    } else
      throw new NullPointerException("internal m_onscreenRenderTarget is null");
  }
  
  public void removeRenderTargetListener(RenderTargetListener renderTargetListener) {
    if (m_onscreenRenderTarget != null) {
      m_onscreenRenderTarget.removeRenderTargetListener(renderTargetListener);
    } else
      throw new NullPointerException("internal m_onscreenRenderTarget is null");
  }
  
  public RenderTargetListener[] getRenderTargetListeners() {
    if (m_onscreenRenderTarget != null) {
      return m_onscreenRenderTarget.getRenderTargetListeners();
    }
    throw new NullPointerException("internal m_onscreenRenderTarget is null");
  }
  
  public void addKeyListener(KeyListener keyListener)
  {
    if (m_awtComponent != null) {
      m_awtComponent.addKeyListener(keyListener);
    } else
      throw new NullPointerException("internal m_awtComponent is null");
  }
  
  public void removeKeyListener(KeyListener keyListener) {
    if (m_awtComponent != null) {
      m_awtComponent.removeKeyListener(keyListener);
    } else
      throw new NullPointerException("internal m_awtComponent is null");
  }
  
  public void addMouseListener(MouseListener mouseListener) {
    if (m_awtComponent != null) {
      m_awtComponent.addMouseListener(mouseListener);
    } else
      throw new NullPointerException("internal m_awtComponent is null");
  }
  
  public void removeMouseListener(MouseListener mouseListener) {
    if (m_awtComponent != null) {
      m_awtComponent.removeMouseListener(mouseListener);
    } else
      throw new NullPointerException("internal m_awtComponent is null");
  }
  
  public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
    if (m_awtComponent != null) {
      m_awtComponent.addMouseMotionListener(mouseMotionListener);
    } else
      throw new NullPointerException("internal m_awtComponent is null");
  }
  
  public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
    if (m_awtComponent != null) {
      m_awtComponent.removeMouseMotionListener(mouseMotionListener);
    } else {
      throw new NullPointerException("internal m_awtComponent is null");
    }
  }
  
  protected void started(World world, double time)
  {
    super.started(world, time);
    m_onscreenRenderTarget.addRenderTargetListener(world.getBubbleManager());
    Component awtComponent = m_onscreenRenderTarget.getAWTComponent();
    if (awtComponent != null) {
      awtComponent.requestFocus();
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    m_onscreenRenderTarget.removeRenderTargetListener(world.getBubbleManager());
  }
}
