package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.util.Enumerable;
import javax.vecmath.Vector3d;




















public class Direction
  extends Enumerable
{
  public static final Direction LEFT = new Direction(MathUtilities.createNegativeXAxis(), MathUtilities.createNegativeYAxis(), MathUtilities.createZAxis());
  public static final Direction RIGHT = new Direction(MathUtilities.createXAxis(), MathUtilities.createYAxis(), MathUtilities.createNegativeZAxis());
  public static final Direction UP = new Direction(MathUtilities.createYAxis(), null, null);
  public static final Direction DOWN = new Direction(MathUtilities.createNegativeYAxis(), null, null);
  public static final Direction FORWARD = new Direction(MathUtilities.createZAxis(), MathUtilities.createXAxis(), null);
  public static final Direction BACKWARD = new Direction(MathUtilities.createNegativeZAxis(), MathUtilities.createNegativeXAxis(), null);
  private Vector3d m_moveAxis;
  private Vector3d m_turnAxis;
  private Vector3d m_rollAxis;
  
  public Direction(Vector3d moveAxis, Vector3d turnAxis, Vector3d rollAxis) { m_moveAxis = moveAxis;
    m_turnAxis = turnAxis;
    m_rollAxis = rollAxis;
  }
  
  public Vector3d getMoveAxis() { return m_moveAxis; }
  
  public Vector3d getTurnAxis() {
    return m_turnAxis;
  }
  
  public Vector3d getRollAxis() { return m_rollAxis; }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Direction))) {
      Direction direction = (Direction)o;
      if (m_moveAxis != null) {
        if (!m_moveAxis.equals(m_moveAxis)) {
          return false;
        }
      }
      else if (m_moveAxis != null) {
        return false;
      }
      
      if (m_turnAxis != null) {
        if (!m_turnAxis.equals(m_turnAxis)) {
          return false;
        }
      }
      else if (m_turnAxis != null) {
        return false;
      }
      
      if (m_rollAxis != null) {
        if (!m_rollAxis.equals(m_rollAxis)) {
          return false;
        }
      }
      else if (m_rollAxis != null) {
        return false;
      }
      
      return true;
    }
    return false;
  }
  
  public static Direction valueOf(String s) {
    return (Direction)Enumerable.valueOf(s, Direction.class);
  }
}
