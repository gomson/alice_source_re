package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;












public class AxesDecorator
  extends Decorator
{
  public AxesDecorator() {}
  
  private LineArray m_sgLineArray = null;
  private ReferenceFrame m_referenceFrame = null;
  private double m_scale = 1.0D;
  

  public double getScale() { return m_scale; }
  
  public void setScale(double scale) {
    m_scale = scale;
    markDirty();
  }
  

  public ReferenceFrame getReferenceFrame() { return m_referenceFrame; }
  
  public void setReferenceFrame(ReferenceFrame referenceFrame) {
    if (referenceFrame != m_referenceFrame) {
      m_referenceFrame = referenceFrame;
      markDirty();
      updateIfShowing();
    }
  }
  
  public void internalRelease(int pass) {
    switch (pass) {
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
    Box box = m_referenceFrame.getBoundingBox();
    if ((box == null) || (box.getMinimum() == null) || (box.getMaximum() == null)) {
      return;
    }
    boolean requiresVerticesToBeUpdated = isDirty();
    if (m_sgLineArray == null) {
      m_sgLineArray = new LineArray();
      m_sgVisual.setGeometry(m_sgLineArray);
      m_sgLineArray.setBonus(getReferenceFrame());
      requiresVerticesToBeUpdated = true;
    }
    if (requiresVerticesToBeUpdated) {
      Color color = Color.YELLOW;
      Vertex3d[] vertices = new Vertex3d[6];
      Vector3d min = box.getMinimum();
      Vector3d max = box.getMaximum();
      Vector3d center = MathUtilities.add(min, max);
      center.scale(0.5D);
      if (m_scale != 0.0D) {
        Vector3d dmin = MathUtilities.subtract(min, center);
        Vector3d dmax = MathUtilities.subtract(max, center);
        dmin.scale(m_scale);
        dmax.scale(m_scale);
        min.add(dmin);
        max.add(dmax);
      }
      vertices[0] = new Vertex3d(new Point3d(x, y, z), null, Color.RED, null, null);
      vertices[1] = new Vertex3d(new Point3d(x, y, z), null, Color.RED, null, null);
      vertices[2] = new Vertex3d(new Point3d(x, y, z), null, Color.GREEN, null, null);
      vertices[3] = new Vertex3d(new Point3d(x, y, z), null, Color.GREEN, null, null);
      vertices[4] = new Vertex3d(new Point3d(x, y, z), null, Color.BLUE, null, null);
      vertices[5] = new Vertex3d(new Point3d(x, y, z), null, Color.BLUE, null, null);
      m_sgLineArray.setVertices(vertices);
    }
    setIsDirty(false);
  }
}
