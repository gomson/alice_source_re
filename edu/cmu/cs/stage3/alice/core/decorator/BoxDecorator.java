package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;













public class BoxDecorator
  extends Decorator
{
  private ReferenceFrame m_referenceFrame;
  private LineArray m_sgLineArray = null;
  private double m_width;
  
  public BoxDecorator() {}
  
  public ReferenceFrame getReferenceFrame() {
    return m_referenceFrame;
  }
  
  public void setReferenceFrame(ReferenceFrame referenceFrame) { m_referenceFrame = referenceFrame; }
  

  public double getWidth() { return m_width; }
  
  public void setWidth(double width) {
    m_width = width;
    markDirty();
  }
  
  public double getHeight() { return m_height; }
  
  public void setHeight(double height) {
    m_height = height;
    markDirty();
  }
  
  public double getDepth() { return m_depth; }
  
  public void setDepth(double depth) {
    m_depth = depth;
    markDirty(); }
  
  private double m_height;
  private double m_depth;
  public void internalRelease(int pass) { switch (pass) {
    case 2: 
      if (m_sgLineArray != null) {
        m_sgLineArray.release();
        m_sgLineArray = null;
      }
      break;
    }
    super.internalRelease(pass);
  }
  
  protected void update() {
    super.update();
    boolean requiresVerticesToBeUpdated = isDirty();
    if (m_sgLineArray == null) {
      m_sgLineArray = new LineArray();
      m_sgVisual.setGeometry(m_sgLineArray);
      m_sgLineArray.setBonus(getReferenceFrame());
      requiresVerticesToBeUpdated = true;
    }
    if (requiresVerticesToBeUpdated) {
      Color color = Color.YELLOW;
      Vertex3d[] vertices = new Vertex3d[24];
      Vector3d min = new Vector3d(-m_width / 2.0D, 0.0D, -m_depth / 2.0D);
      Vector3d max = new Vector3d(m_width / 2.0D, m_height, m_depth / 2.0D);
      Vertex3d[] vs = new Vertex3d[8];
      vs[0] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[1] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[2] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[3] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[4] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[5] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[6] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      vs[7] = new Vertex3d(new Point3d(x, y, z), null, color, null, null);
      
      int bottom = 0;
      int top = 8;
      int sides = 16;
      for (int lcv = 0; lcv < 4; lcv++) {
        vertices[(bottom++)] = vs[lcv];
        vertices[(bottom++)] = vs[((lcv + 1) % 4)];
        vertices[(top++)] = vs[(4 + lcv)];
        vertices[(top++)] = vs[(4 + (lcv + 1) % 4)];
        vertices[(sides++)] = vs[lcv];
        vertices[(sides++)] = vs[(lcv + 4)];
      }
      m_sgLineArray.setVertices(vertices);
    }
    setIsDirty(false);
  }
}
