package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.util.Enumerable;
import javax.vecmath.Vector3d;




















public class Dimension
  extends Enumerable
{
  public static final Dimension ALL = new Dimension(new Vector3d(1.0D, 1.0D, 1.0D));
  public static final Dimension LEFT_TO_RIGHT = new Dimension(MathUtilities.createXAxis());
  public static final Dimension TOP_TO_BOTTOM = new Dimension(MathUtilities.createYAxis());
  public static final Dimension FRONT_TO_BACK = new Dimension(MathUtilities.createZAxis());
  private Vector3d m_scaleAxis;
  
  public Dimension(Vector3d scaleAxis) { m_scaleAxis = scaleAxis; }
  
  public Vector3d getScaleAxis() {
    return m_scaleAxis;
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Direction))) {
      Dimension dimension = (Dimension)o;
      if (m_scaleAxis == null) {
        return m_scaleAxis == null;
      }
      return m_scaleAxis.equals(m_scaleAxis);
    }
    
    return false;
  }
  
  public static Dimension valueOf(String s) {
    return (Dimension)Enumerable.valueOf(s, Dimension.class);
  }
}
