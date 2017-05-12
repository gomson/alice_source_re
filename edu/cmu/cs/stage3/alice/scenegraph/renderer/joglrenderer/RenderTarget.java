package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;
import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.AbstractProxyRenderTarget;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Matrix4d;





public abstract class RenderTarget
  extends AbstractProxyRenderTarget
{
  protected RenderTarget(Renderer renderer) { super(renderer); }
  
  private RenderContext m_renderContextForGetOffscreenGraphics = null;
  
  protected void performClearAndRenderOffscreen(RenderContext context) { commitAnyPendingChanges();
    


    onClear();
    
    gl.glBlendFunc(770, 771);
    gl.glEnable(3042);
    gl.glEnable(3008);
    gl.glAlphaFunc(516, 0.0F);
    
    Camera[] cameras = getCameras();
    for (i = 0; i < cameras.length; i++) {
      CameraProxy cameraProxyI = (CameraProxy)getProxyFor(cameras[i]);
      cameraProxyI.performClearAndRenderOffscreen(context);
    }
    try {
      m_renderContextForGetOffscreenGraphics = context;
      onRender();
    } finally {
      m_renderContextForGetOffscreenGraphics = null;
    }
    gl.glFlush();
  }
  
  private IntBuffer m_pickBuffer;
  private IntBuffer m_viewportBuffer;
  public PickInfo performPick(PickContext context, PickParameters pickParameters)
  {
    int x = pickParameters.getX();
    int y = pickParameters.getY();
    Camera sgCamera = getCameraAtPixel(x, y);
    
    if (sgCamera != null)
    {
      int CAPACITY = 256;
      if (m_pickBuffer == null) {
        m_pickBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
      } else {
        m_pickBuffer.rewind();
      }
      gl.glSelectBuffer(256, m_pickBuffer);
      
      gl.glRenderMode(7170);
      gl.glInitNames();
      
      int width = context.getWidth();
      int height = context.getHeight();
      
      CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
      Rectangle viewport = cameraProxy.getActualViewport(width, height);
      int[] vp = { x, y, width, height };
      gl.glViewport(x, y, width, height);
      
      gl.glMatrixMode(5889);
      gl.glLoadIdentity();
      
      if (m_viewportBuffer == null) {
        m_viewportBuffer = IntBuffer.allocate(4);
      } else {
        m_viewportBuffer.rewind();
      }
      m_viewportBuffer.put(x);
      m_viewportBuffer.put(y);
      m_viewportBuffer.put(width);
      m_viewportBuffer.put(height);
      m_viewportBuffer.rewind();
      gl.glMatrixMode(5889);
      gl.glLoadIdentity();
      glu.gluPickMatrix(x, height - y, 1.0D, 1.0D, m_viewportBuffer);
      
      cameraProxy.performPick(context, pickParameters);
      
      gl.glFlush();
    }
    return new PickInfo(context, m_pickBuffer, sgCamera);
  }
  
  public void commitAnyPendingChanges() {
    ((Renderer)getRenderer()).commitAnyPendingChanges();
  }
  
  public Matrix4d getProjectionMatrix(Camera sgCamera)
  {
    return sgCamera.getProjection();
  }
  
  public double[] getActualPlane(OrthographicCamera sgOrthographicCamera) {
    return sgOrthographicCamera.getPlane();
  }
  

  public double[] getActualPlane(PerspectiveCamera sgPerspectiveCamera) { return sgPerspectiveCamera.getPlane(); }
  
  public double getActualHorizontalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) {
    Dimension size = getSize();
    SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy)getProxyFor(sgSymmetricPerspectiveCamera);
    return symmetricPerspectiveCameraProxy.getActualHorizontalViewingAngle(width, height);
  }
  
  public double getActualVerticalViewingAngle(SymmetricPerspectiveCamera sgSymmetricPerspectiveCamera) { Dimension size = getSize();
    SymmetricPerspectiveCameraProxy symmetricPerspectiveCameraProxy = (SymmetricPerspectiveCameraProxy)getProxyFor(sgSymmetricPerspectiveCamera);
    return symmetricPerspectiveCameraProxy.getActualVerticalViewingAngle(width, height);
  }
  
  public Rectangle getActualViewport(Camera sgCamera) { Dimension size = getSize();
    CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    return cameraProxy.getActualViewport(width, height);
  }
  
  public Rectangle getViewport(Camera sgCamera) { CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    return cameraProxy.getViewport();
  }
  
  public void setViewport(Camera sgCamera, Rectangle viewport) { CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    cameraProxy.setViewport(viewport);
  }
  
  public boolean isLetterboxedAsOpposedToDistorted(Camera sgCamera) { CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    return cameraProxy.isLetterboxedAsOpposedToDistorted();
  }
  
  public void setIsLetterboxedAsOpposedToDistorted(Camera sgCamera, boolean isLetterboxedAsOpposedToDistorted) { CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    cameraProxy.setIsLetterboxedAsOpposedToDistorted(isLetterboxedAsOpposedToDistorted); }
  
  GLPbuffer m_glPBuffer = null;
  
  public void createGLBuffer(int width, int height) { GLDrawableFactory fac = GLDrawableFactory.getFactory();
    if (fac.canCreateGLPbuffer()) {
      GLCapabilities glCap = new GLCapabilities();
      glCap.setDoubleBuffered(false);
      glCap.setRedBits(8);
      glCap.setBlueBits(8);
      glCap.setGreenBits(8);
      glCap.setAlphaBits(8);
      m_glPBuffer = fac.createGLPbuffer(glCap, null, width, height, null);
    }
  }
  
  public void clearAndRenderOffscreen() { RenderContext m_renderContext = new RenderContext(this);
    if (m_glPBuffer != null) {
      GLContext context = m_glPBuffer.createContext(null);
      context.makeCurrent();
      context.getGL().glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      context.getGL().glClear(16640);
      Dimension d = getSize();
      int width = width;int height = height;
      m_height = height;
      m_width = width;
      m_glPBuffer.addGLEventListener(m_renderContext);
      m_glPBuffer.display();
    }
  }
  
  public boolean rendersOnEdgeTrianglesAsLines(OrthographicCamera orthographicCamera)
  {
    return false;
  }
  


  public void setRendersOnEdgeTrianglesAsLines(OrthographicCamera orthographicCamera, boolean rendersOnEdgeTrianglesAsLines) {}
  

  public Image getOffscreenImage()
  {
    Dimension d = getSize();
    int width = width;int height = height;
    if ((m_glPBuffer == null) || (m_glPBuffer.getWidth() != width) || (m_glPBuffer.getHeight() != height)) {
      createGLBuffer(width, height);
      clearAndRenderOffscreen();
    }
    GLContext context = m_glPBuffer.createContext(null);
    context.makeCurrent();
    BufferedImage image = 
      new BufferedImage(width, height, 2);
    image = Screenshot.readToBufferedImage(width, height, true);
    context.release();
    context.destroy();
    return image;
  }
  
  public java.awt.Graphics getOffscreenGraphics() { return new Graphics(m_renderContextForGetOffscreenGraphics); }
  
  public java.awt.Graphics getGraphics(TextureMap textureMap) {
    return null;
  }
  
  public Image getZBufferImage() { return null; }
  
  public Image getImage(TextureMap textureMap) {
    return null;
  }
  


  public void copyOffscreenImageToTextureMap(TextureMap textureMap) {}
  

  public void setSilhouetteThickness(double silhouetteThickness) {}
  
  public double getSilhouetteThickness() { return 0.0D; }
  
  private double[] getActualNearPlane(Camera sgCamera, int width, int height) {
    CameraProxy cameraProxy = (CameraProxy)getProxyFor(sgCamera);
    double[] ret = new double[4];
    return cameraProxy.getActualNearPlane(ret, width, height);
  }
  
  private Camera getCameraAtPixel(int x, int y) { Camera[] sgCameras = getCameras();
    for (int i = sgCameras.length - 1; i >= 0; i--) {
      Camera sgCameraI = sgCameras[i];
      Rectangle viewportI = getActualViewport(sgCameraI);
      if (viewportI.contains(x, y)) {
        return sgCameraI;
      }
    }
    return null;
  }
  
  private static boolean isNaN(double[] array) { for (int i = 0; i < array.length; i++) {
      if (Double.isNaN(array[i])) {
        return true;
      }
    }
    return false;
  }
  



























  private GLUT glut_ = new GLUT();
  
  private int displayList;
  
  public class GLEListener
    implements GLEventListener
  {
    public GLEListener() {}
    
    public void display(GLAutoDrawable drawable)
    {
      GL gl = drawable.getGL();
      gl.glClear(16640);
      gl.glMatrixMode(5888);
      gl.glLoadIdentity();
      
      gl.glTranslatef((float)(Math.random() - 0.5D), (float)(Math.random() - 0.5D), 0.0F);
      double d = 0.9D + 0.2D * Math.random();
      gl.glScaled(d, d, d);
      float[] c = { (float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0F };
      gl.glMaterialfv(1032, 4609, c, 0);
      gl.glCallList(displayList);
    }
    



    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
    



    public void init(GLAutoDrawable drawable)
    {
      GL gl = drawable.getGL();
      

      gl.glEnable(2929);
      gl.glDepthFunc(515);
      

      gl.glMatrixMode(5888);
      gl.glLoadIdentity();
      
      gl.glEnable(2896);
      gl.glEnable(16384);
      
      float[] ambient = { 0.0F, 0.0F, 0.0F, 1.0F };
      float[] diffuse = { 1.0F, 1.0F, 1.0F, 1.0F };
      float[] specular = { 1.0F, 1.0F, 1.0F, 1.0F };
      float[] position = { 0.0F, 10.0F, -15.0F, 1.0F };
      
      gl.glLightfv(16384, 4611, position, 0);
      gl.glLightfv(16384, 4608, ambient, 0);
      gl.glLightfv(16384, 4609, diffuse, 0);
      gl.glLightfv(16384, 4610, specular, 0);
      
      gl.glClearColor(0.1F, 0.1F, 0.1F, 0.0F);
      

      displayList = gl.glGenLists(1);
      gl.glNewList(displayList, 4864);
      glut_.glutSolidSphere(0.3D, 16, 8);
      gl.glEndList();
    }
    

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
      GL gl = drawable.getGL();
      
      gl.glViewport(x, y, width, height);
    }
  }
}
