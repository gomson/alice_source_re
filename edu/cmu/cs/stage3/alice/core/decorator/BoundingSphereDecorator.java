package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.LineStrip;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.math.Sphere;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;















public class BoundingSphereDecorator
  extends Decorator
{
  private static final int RESOLUTION_THETA = 25;
  private static double[] sines = new double[25];
  private static double[] cosines = new double[25];
  
  static { double dtheta = 0.25132741228718347D;
    double theta = 0.0D;
    for (int i = 0; i < 25; i++) {
      sines[i] = Math.sin(theta);
      cosines[i] = Math.cos(theta);
      theta += dtheta; } }
  
  public BoundingSphereDecorator() {}
  private LineStrip m_sgLineStrip = null;
  private ReferenceFrame m_referenceFrame = null;
  
  public ReferenceFrame getReferenceFrame() {
    return m_referenceFrame; }
  
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
      if (m_sgLineStrip != null) {
        m_sgLineStrip.release();
        m_sgLineStrip = null;
      }
      break;
    }
    super.internalRelease(pass);
  }
  
  protected void update() {
    super.update();
    Sphere sphere = m_referenceFrame.getBoundingSphere();
    if ((sphere == null) || (sphere.getCenter() == null) || (Double.isNaN(sphere.getRadius()))) {
      return;
    }
    boolean requiresVerticesToBeUpdated = isDirty();
    if (m_sgLineStrip == null) {
      m_sgLineStrip = new LineStrip();
      m_sgVisual.setGeometry(m_sgLineStrip);
      m_sgLineStrip.setBonus(getReferenceFrame());
      requiresVerticesToBeUpdated = true;
    }
    if (requiresVerticesToBeUpdated) {
      double r = sphere.getRadius();
      double x = getCenterx;
      double y = getCentery;
      double z = getCenterz;
      Color xColor = Color.RED;
      Color yColor = Color.GREEN;
      Color zColor = Color.BLUE;
      Vertex3d[] xVertices = new Vertex3d[26];
      Vertex3d[] yVertices = new Vertex3d[26];
      Vertex3d[] zVertices = new Vertex3d[26];
      for (int i = 0; i < 25; i++) {
        xVertices[i] = new Vertex3d(new Point3d(x, y + cosines[i] * r, z + sines[i] * r), null, xColor, null, null);
        yVertices[i] = new Vertex3d(new Point3d(x + cosines[i] * r, y, z + sines[i] * r), null, yColor, null, null);
        zVertices[i] = new Vertex3d(new Point3d(x + cosines[i] * r, y + sines[i] * r, z), null, zColor, null, null);
      }
      xVertices[25] = xVertices[0];
      yVertices[25] = yVertices[0];
      zVertices[25] = zVertices[0];
      
      Vertex3d[] vertices = new Vertex3d[xVertices.length + yVertices.length + zVertices.length];
      System.arraycopy(xVertices, 0, vertices, 0, xVertices.length);
      System.arraycopy(yVertices, 0, vertices, xVertices.length, yVertices.length);
      System.arraycopy(zVertices, 0, vertices, xVertices.length + yVertices.length, zVertices.length);
      m_sgLineStrip.setVertices(vertices);
    }
    setIsDirty(false);
  }
}
