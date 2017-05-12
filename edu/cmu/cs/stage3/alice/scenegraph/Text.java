package edu.cmu.cs.stage3.alice.scenegraph;

import java.awt.Font;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;



















public class Text
  extends Geometry
{
  public Text() {}
  
  public static final Property TEXT_PROPERTY = new Property(Text.class, "TEXT");
  public static final Property FONT_PROPERTY = new Property(Text.class, "FONT");
  public static final Property EXTRUSION_PROPERTY = new Property(Text.class, "EXTRUSION");
  private String m_text = null;
  private Font m_font = null;
  private Vector3d m_extrusion = new Vector3d(0.0D, 0.0D, 1.0D);
  

  public String getText() { return m_text; }
  
  public void setText(String text) {
    if (notequal(m_text, text)) {
      m_text = text;
      onPropertyChange(TEXT_PROPERTY);
    }
  }
  
  public Font getFont() { return m_font; }
  
  public void setFont(Font font) {
    if (notequal(m_font, font)) {
      m_font = font;
      onPropertyChange(FONT_PROPERTY);
    }
  }
  

  public Vector3d getExtrusion() { return m_extrusion; }
  
  public void setExtrusion(Vector3d extrusion) {
    if (notequal(m_extrusion, extrusion)) {
      m_extrusion = extrusion;
      onPropertyChange(EXTRUSION_PROPERTY);
    }
  }
  
  protected void updateBoundingBox() {}
  
  protected void updateBoundingSphere() {}
  
  public void transform(Matrix4d trans) {}
}
