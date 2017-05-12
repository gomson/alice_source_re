package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Appearance;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.FillingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;












class AppearanceProxy
  extends ElementProxy
{
  private boolean m_isShaded;
  
  AppearanceProxy() {}
  
  private int style = 0;
  private boolean m_isAmbientLinkedToDiffuse;
  private float[] m_ambient = new float[4];
  private float[] m_diffuse = new float[4];
  private float[] m_specular = new float[4];
  private float[] m_emissive = new float[4];
  private float m_opacity = 1.0F;
  private float m_shininess;
  private int m_polygonMode;
  private TextureMapProxy m_diffuseColorMapProxy;
  
  private FloatBuffer m_ambientBuffer = FloatBuffer.wrap(m_ambient);
  private FloatBuffer m_diffuseBuffer = FloatBuffer.wrap(m_diffuse);
  private FloatBuffer m_specularBuffer = FloatBuffer.wrap(m_specular);
  private FloatBuffer m_emissiveBuffer = FloatBuffer.wrap(m_emissive);
  
  public void setPipelineState(RenderContext context, int face) {
    context.setIsShadingEnabled(m_isShaded);
    m_diffuse[3] = m_opacity;
    if (m_isShaded) {
      if (style == 0) {
        gl.glShadeModel(7424);
      } else {
        gl.glShadeModel(7425);
      }
      if (m_isAmbientLinkedToDiffuse) {
        gl.glMaterialfv(face, 5634, m_diffuseBuffer);
      } else {
        gl.glMaterialfv(face, 4608, m_ambientBuffer);
        gl.glMaterialfv(face, 4609, m_diffuseBuffer);
      }
      gl.glMaterialfv(face, 4610, m_specularBuffer);
      gl.glMaterialfv(face, 5632, m_emissiveBuffer);
      gl.glMaterialf(face, 5633, m_shininess);
    }
    

    gl.glColor4f(m_diffuse[0], m_diffuse[1], m_diffuse[2], m_diffuse[3]);
    gl.glPolygonMode(face, m_polygonMode);
    
    context.setTextureMapProxy(m_diffuseColorMapProxy);
  }
  
  protected void changed(Property property, Object value) {
    if (property == Appearance.AMBIENT_COLOR_PROPERTY) {
      m_isAmbientLinkedToDiffuse = (value == null);
      if (!m_isAmbientLinkedToDiffuse)
      {

        copy(m_ambient, (Color)value);
      }
    } else if (property == Appearance.DIFFUSE_COLOR_PROPERTY) {
      copy(m_diffuse, (Color)value);
    } else if (property == Appearance.FILLING_STYLE_PROPERTY) {
      if (value.equals(FillingStyle.SOLID)) {
        m_polygonMode = 6914;
      } else if (value.equals(FillingStyle.WIREFRAME)) {
        m_polygonMode = 6913;
      } else if (value.equals(FillingStyle.POINTS)) {
        m_polygonMode = 6912;
      } else {
        throw new RuntimeException();
      }
    } else if (property == Appearance.SHADING_STYLE_PROPERTY) {
      if ((value == null) || (value.equals(ShadingStyle.NONE))) {
        m_isShaded = false;
      } else if (value.equals(ShadingStyle.FLAT)) {
        m_isShaded = true;
        
        style = 0;
      } else if (value.equals(ShadingStyle.SMOOTH)) {
        m_isShaded = true;
        
        style = 1;
      } else {
        throw new RuntimeException();
      }
    } else if (property == Appearance.OPACITY_PROPERTY) {
      m_opacity = ((Number)value).floatValue();
    } else if (property == Appearance.SPECULAR_HIGHLIGHT_COLOR_PROPERTY) {
      copy(m_specular, (Color)value);
    } else if (property == Appearance.SPECULAR_HIGHLIGHT_EXPONENT_PROPERTY) {
      m_shininess = ((Number)value).floatValue();
    } else if (property == Appearance.EMISSIVE_COLOR_PROPERTY) {
      copy(m_emissive, (Color)value);
    } else if (property == Appearance.DIFFUSE_COLOR_MAP_PROPERTY) {
      m_diffuseColorMapProxy = ((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property != Appearance.OPACITY_MAP_PROPERTY)
    {
      if (property != Appearance.EMISSIVE_COLOR_MAP_PROPERTY)
      {
        if (property != Appearance.SPECULAR_HIGHLIGHT_COLOR_MAP_PROPERTY)
        {
          if (property != Appearance.BUMP_MAP_PROPERTY)
          {
            if (property != Appearance.DETAIL_MAP_PROPERTY)
            {

              super.changed(property, value); } } }
      }
    }
  }
  
  public double Showing() {
    return m_opacity;
  }
}
