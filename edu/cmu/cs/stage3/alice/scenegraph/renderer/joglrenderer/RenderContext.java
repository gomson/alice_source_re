package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;











class RenderContext
  extends Context
{
  private RenderTarget m_renderTarget;
  private int m_lastTime_nextLightID = 16384;
  
  private int m_nextLightID;
  private boolean m_isFogEnabled;
  private boolean m_renderOpaque;
  private float[] m_ambient = new float[4];
  private FloatBuffer m_ambientBuffer = FloatBuffer.wrap(m_ambient);
  
  private Hashtable m_displayListMap = new Hashtable();
  private Hashtable m_textureBindingMap = new Hashtable();
  
  private TextureMapProxy m_currTextureMapProxy;
  
  private boolean m_isShadingEnabled;
  private Rectangle m_clearRect = new Rectangle();
  
  public RenderContext(RenderTarget renderTarget) {
    m_renderTarget = renderTarget;
    m_renderOpaque = true;
  }
  
  public void init(GLAutoDrawable drawable) {
    super.init(drawable);
    forgetAllTextureMapProxies();
    forgetAllGeometryProxies();
  }
  
  public void display(GLAutoDrawable drawable) {
    super.display(drawable);
    
    m_renderTarget.commitAnyPendingChanges();
    m_clearRect.setBounds(0, 0, 0, 0);
    m_renderTarget.performClearAndRenderOffscreen(this);
    
    if ((m_clearRect.x != 0) || (m_clearRect.y != 0) || (m_clearRect.width != m_width) || (m_clearRect.height != m_height))
    {

      gl.glEnable(3089);
      gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      try {
        if (m_clearRect.x > 0) {
          gl.glScissor(0, 0, m_clearRect.x, m_height);
          gl.glClear(16384);
        }
        if (m_clearRect.x + m_clearRect.width < m_width) {
          gl.glScissor(m_clearRect.x + m_clearRect.width, 0, m_width - m_clearRect.width, m_height);
          gl.glClear(16384);
        }
        if (m_clearRect.y > 0) {
          gl.glScissor(0, 0, m_width, m_clearRect.y);
          gl.glClear(16384);
        }
        if (m_clearRect.y + m_clearRect.height < m_height) {
          gl.glScissor(0, m_clearRect.y + m_clearRect.height, m_width, m_height - m_clearRect.height);
          gl.glClear(16384);
        }
      } finally {
        gl.glDisable(3089);
      }
    }
  }
  
  public void beginAffectorSetup() {
    m_ambient[0] = 0.0F;
    m_ambient[1] = 0.0F;
    m_ambient[2] = 0.0F;
    m_ambient[3] = 1.0F;
    m_nextLightID = 16384;
    
    m_isFogEnabled = false;
    
    m_currTextureMapProxy = null;
  }
  
  public void endAffectorSetup() {
    gl.glLightModelfv(2899, m_ambientBuffer);
    for (int id = m_nextLightID; id < m_lastTime_nextLightID; id++) {
      gl.glDisable(id);
    }
    
    if (m_isFogEnabled)
    {
      gl.glEnable(2912);
    }
    else {
      gl.glDisable(2912);
    }
    






    m_lastTime_nextLightID = m_nextLightID;
    
    gl.glEnable(2929);
    gl.glEnable(2884);
    gl.glCullFace(1029);
  }
  
  public void clear(BackgroundProxy backgroundProxy, Rectangle viewport) {
    gl.glViewport(x, y, width, height);
    if (backgroundProxy != null) {
      backgroundProxy.clear(this);
    }
    if ((m_clearRect.width == 0) || (m_clearRect.height == 0)) {
      m_clearRect.setBounds(viewport);
    } else {
      m_clearRect.union(viewport);
    }
  }
  
  public boolean isFogEnabled()
  {
    return m_isFogEnabled;
  }
  
  public void setRenderTransparent() {
    m_renderOpaque = false;
  }
  
  public void setRenderOpaque() {
    m_renderOpaque = true;
  }
  
  public boolean renderOpaque() {
    return m_renderOpaque;
  }
  
  public void setIsFogEnabled(boolean isFogEnabled) {
    m_isFogEnabled = isFogEnabled;
  }
  
  public void addAmbient(float[] color) {
    m_ambient[0] += color[0];
    m_ambient[1] += color[1];
    m_ambient[2] += color[2];
  }
  
  public int getNextLightID() {
    int id = m_nextLightID;
    
    m_nextLightID += 1;
    return id;
  }
  
  public Integer getDisplayListID(GeometryProxy geometryProxy) {
    return (Integer)m_displayListMap.get(geometryProxy);
  }
  
  public Integer generateDisplayListID(GeometryProxy geometryProxy) {
    Integer id = new Integer(gl.glGenLists(1));
    m_displayListMap.put(geometryProxy, id);
    return id;
  }
  
  private void forgetAllGeometryProxies() {
    Enumeration enum0 = m_displayListMap.keys();
    while (enum0.hasMoreElements()) {
      forgetGeometryProxy((GeometryProxy)enum0.nextElement(), false);
    }
    m_displayListMap.clear();
  }
  
  public void forgetGeometryProxy(GeometryProxy geometryProxy, boolean removeFromMap) {
    Integer value = (Integer)m_displayListMap.get(geometryProxy);
    if (value != null) {
      gl.glDeleteLists(value.intValue(), 1);
      if (removeFromMap) {
        m_displayListMap.remove(geometryProxy);
      }
    }
  }
  

  public void forgetGeometryProxy(GeometryProxy geometryProxy)
  {
    forgetGeometryProxy(geometryProxy, true);
  }
  
  private void forgetAllTextureMapProxies() {
    Enumeration enum0 = m_textureBindingMap.keys();
    while (enum0.hasMoreElements()) {
      forgetTextureMapProxy((TextureMapProxy)enum0.nextElement(), false);
    }
    m_textureBindingMap.clear();
  }
  
  public void forgetTextureMapProxy(TextureMapProxy textureMapProxy, boolean removeFromMap) {
    Integer value = (Integer)m_textureBindingMap.get(textureMapProxy);
    if (value != null) {
      int id = value.intValue();
      IntBuffer atID = IntBuffer.allocate(1);
      atID.put(id);
      atID.rewind();
      gl.glDeleteTextures(atID.limit(), atID);
      if (removeFromMap) {
        m_textureBindingMap.remove(textureMapProxy);
      }
    }
  }
  











  public void forgetTextureMapProxy(TextureMapProxy textureMapProxy)
  {
    forgetTextureMapProxy(textureMapProxy, true);
  }
  
  public void setTextureMapProxy(TextureMapProxy textureMapProxy) {
    if ((textureMapProxy != null) && (textureMapProxy.isImageSet())) {
      gl.glEnable(3553);
      if (m_currTextureMapProxy != textureMapProxy) {
        if (textureMapProxy != null) {
          Integer value = (Integer)m_textureBindingMap.get(textureMapProxy);
          if ((textureMapProxy.prepareByteBufferIfNecessary()) || (value == null)) {
            if (value == null) {
              IntBuffer atID = IntBuffer.allocate(1);
              gl.glGenTextures(atID.limit(), atID);
              value = new Integer(atID.get());
              m_textureBindingMap.put(textureMapProxy, value);
            }
            
            gl.glBindTexture(3553, value.intValue());
            int format;
            int internalFormat;
            int format; if (textureMapProxy.isPotentiallyAlphaBlended()) {
              int internalFormat = 6408;
              format = 6408;
            } else {
              internalFormat = 6407;
              format = 6407;
            }
            ByteBuffer pixels = textureMapProxy.getPixels();
            


            gl.glTexImage2D(3553, 0, internalFormat, textureMapProxy.getWidthPowerOf2(), textureMapProxy.getHeightPowerOf2(), 0, format, 5121, pixels);
            gl.glTexParameterf(3553, 10242, 10497.0F);
            gl.glTexParameterf(3553, 10243, 10497.0F);
            gl.glTexParameterf(3553, 10240, 9729.0F);
            gl.glTexParameterf(3553, 10241, 9729.0F);
          } else {
            gl.glBindTexture(3553, value.intValue());
          }
        }
        m_currTextureMapProxy = textureMapProxy;
      }
    } else {
      gl.glDisable(3553);
    }
  }
  




























  public boolean isShadingEnabled()
  {
    return m_isShadingEnabled;
  }
  
  public void setIsShadingEnabled(boolean isShadingEnabled) {
    m_isShadingEnabled = isShadingEnabled;
    if (m_isShadingEnabled) {
      gl.glEnable(2896);
    } else {
      gl.glDisable(2896);
    }
  }
  
  protected void renderVertex(Vertex3d vertex)
  {
    if ((m_currTextureMapProxy != null) && (textureCoordinate0 != null)) {
      double u = m_currTextureMapProxy.mapU(textureCoordinate0.x);
      double v = m_currTextureMapProxy.mapV(textureCoordinate0.y);
      gl.glTexCoord2d(u, v);
    }
    
    if (diffuseColor != null) {
      gl.glColor4f(diffuseColor.red, diffuseColor.green, diffuseColor.blue, diffuseColor.alpha);
    }
    
    if (m_isShadingEnabled) {
      gl.glNormal3d(normal.x, normal.y, -normal.z);
    }
    gl.glVertex3d(position.x, position.y, -position.z);
  }
}
