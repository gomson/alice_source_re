package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.Box;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;













public class BoundingBoxDecorator
  extends Decorator
{
  public BoundingBoxDecorator() {}
  
  private LineArray m_sgLineArray = null;
  private ReferenceFrame m_referenceFrame = null;
  

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
  
  protected void update()
  {
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
      Vertex3d[] vertices = new Vertex3d[24];
      Vector3d min = box.getMinimum();
      Vector3d max = box.getMaximum();
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
