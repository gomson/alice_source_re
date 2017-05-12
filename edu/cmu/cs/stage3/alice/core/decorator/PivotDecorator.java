package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;












public class PivotDecorator
  extends Decorator
{
  public PivotDecorator() {}
  
  private LineArray m_sgLineArray = null;
  private Transformable m_transformable = null;
  private Box m_overrideBoundingBox = null;
  
  protected ReferenceFrame getReferenceFrame() {
    return getTransformable();
  }
  
  public Transformable getTransformable() { return m_transformable; }
  
  public void setTransformable(Transformable transformable) {
    if (transformable != m_transformable) {
      m_transformable = transformable;
      markDirty();
      updateIfShowing();
    }
  }
  

  public Box getOverrideBoundingBox() { return m_overrideBoundingBox; }
  
  public void setOverrideBoundingBox(Box overrideBoundingBox) {
    m_overrideBoundingBox = overrideBoundingBox;
    markDirty();
  }
  
  public void internalRelease(int pass)
  {
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
    Box box;
    Box box;
    if (m_overrideBoundingBox == null) {
      box = m_transformable.getBoundingBox();
    } else {
      box = m_overrideBoundingBox;
    }
    if ((box == null) || (box.getMinimum() == null) || (box.getMaximum() == null)) {
      return;
    }
    
    boolean requiresVerticesToBeUpdated = isDirty();
    if (m_sgLineArray == null) {
      m_sgLineArray = new LineArray();
      m_sgVisual.setGeometry(m_sgLineArray);
      m_sgLineArray.setBonus(getTransformable());
      requiresVerticesToBeUpdated = true;
    }
    if (requiresVerticesToBeUpdated) {
      Vector3d min = box.getMinimum();
      Vector3d max = box.getMaximum();
      double distanceAcross = MathUtilities.subtract(max, min).length();
      double delta = distanceAcross * 0.1D;
      Color xColor = Color.RED;
      Color yColor = Color.GREEN;
      Color zColor = Color.BLUE;
      Vertex3d[] vertices = new Vertex3d[6];
      vertices[0] = new Vertex3d(new Point3d(0.0D, 0.0D, 0.0D), null, xColor, null, null);
      vertices[1] = new Vertex3d(new Point3d(x + delta, 0.0D, 0.0D), null, xColor, null, null);
      vertices[2] = new Vertex3d(new Point3d(0.0D, 0.0D, 0.0D), null, yColor, null, null);
      vertices[3] = new Vertex3d(new Point3d(0.0D, y + delta, 0.0D), null, yColor, null, null);
      vertices[4] = new Vertex3d(new Point3d(0.0D, 0.0D, 0.0D), null, zColor, null, null);
      vertices[5] = new Vertex3d(new Point3d(0.0D, 0.0D, z + delta), null, zColor, null, null);
      m_sgLineArray.setVertices(vertices);
    }
    setIsDirty(false);
  }
}
