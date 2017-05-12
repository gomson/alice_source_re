package edu.cmu.cs.stage3.alice.core.geometry;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.FontProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.math.Box;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;





























public class Text3D
  extends edu.cmu.cs.stage3.alice.core.Geometry
{
  public final StringProperty text = new StringProperty(this, "text", null);
  public final FontProperty font = new FontProperty(this, "font", null);
  public final NumberProperty extrusion = new NumberProperty(this, "extrusion", new Double(0.25D));
  public final NumberProperty curvature = new NumberProperty(this, "curvature", new Integer(2));
  


  private Font m_font = null;
  private int m_curvature = 0;
  
  public Text3D() {
    super(new IndexedTriangleArray());
    
    updateGeometry();
  }
  
  protected void propertyChanged(Property property, Object value)
  {
    if (property == text) {
      updateGeometry();
    } else if ((property == font) || (property == curvature)) {
      if ((m_curvature != curvature.intValue()) || (!m_font.equals(font.getFontValue())))
      {
        updateGeometry();
      }
    } else if (property == extrusion) {
      updateExtrusion();
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  protected void updateExtrusion() {
    double extz = extrusion.doubleValue() / 2.0D;
    Vertex3d[] verts = ((IndexedTriangleArray)getSceneGraphGeometry()).getVertices();
    if (verts != null) {
      for (int i = 0; i < verts.length; i++) {
        position.z = (extz * (position.z > 0.0D ? 1 : -1));
      }
      
      ((IndexedTriangleArray)getSceneGraphGeometry()).setVertices(verts);
    }
  }
  
  protected void updateGeometry() {
    if ((font.getFontValue() == null) || (text.getStringValue() == null) || (extrusion.getNumberValue() == null) || (curvature.getNumberValue() == null)) return;
    m_font = font.getFontValue();
    m_curvature = curvature.intValue();
    
    PolygonGroup pg = new PolygonGroup();
    
    Point2d shiftOffset = new Point2d(0.0D, 0.0D);
    

    int loc = 0;
    int lineCount = 1;
    String m_text = text.getStringValue();
    while (m_text.indexOf('\n', loc) != -1) {
      lineCount++;
      GlyphVector gv = m_font.createGlyphVector(new FontRenderContext(new AffineTransform(), false, true), m_text.substring(loc, m_text.indexOf('\n', loc)));
      x = (gv.getVisualBounds().getWidth() / 2.0D);
      for (int i = 0; i < gv.getNumGlyphs(); i++) {
        Point2d offset = new Point2d(-gv.getGlyphPosition(i).getX() + x, -gv.getGlyphPosition(i).getY() + y);
        buildGlyph(pg, gv.getGlyphOutline(i), offset, curvature.intValue());
      }
      y -= gv.getVisualBounds().getHeight();
      loc = m_text.indexOf('\n', loc) + 1;
    }
    if (loc < m_text.length()) {
      GlyphVector gv = m_font.createGlyphVector(new FontRenderContext(new AffineTransform(), false, true), m_text.substring(loc));
      x = (gv.getVisualBounds().getWidth() / 2.0D);
      for (int i = 0; i < gv.getNumGlyphs(); i++)
      {
        Point2d offset = new Point2d(-gv.getGlyphPosition(i).getX() / 10.0D + x, -gv.getGlyphPosition(i).getY() + y);
        buildGlyph(pg, gv.getGlyphOutline(i), offset, curvature.intValue());
      }
    }
    

    pg.triangulate(extrusion.doubleValue());
    ((IndexedTriangleArray)getSceneGraphGeometry()).setVertices(pg.getVertices());
    ((IndexedTriangleArray)getSceneGraphGeometry()).setIndices(pg.getIndices());
    

    if (pg.getVertices() != null) {
      Vertex3d[] verts = new Vertex3d[pg.getVertices().length];
      double height = getSceneGraphGeometry().getBoundingBox().getHeight() / lineCount;
      for (int i = 0; i < verts.length; i++) {
        verts[i] = new Vertex3d((Point3d)getVerticesposition.clone(), getVerticesnormal, null, null, new TexCoord2f());
        verts[i].scale(1.0D / height, 1.0D / height, 1.0D);
      }
      ((IndexedTriangleArray)getSceneGraphGeometry()).setVertices(verts);
    }
  }
  
  protected void buildGlyph(PolygonGroup pg, Shape outline, Point2d offset, int curvature) {
    PathIterator pi = outline.getPathIterator(new AffineTransform());
    pg.parsePathIterator(pi, offset, curvature);
  }
}
