package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;
















public class MoveAtSpeed
  extends DirectionSpeedTransformResponse
{
  public MoveAtSpeed() {}
  
  public final BooleanProperty isScaledBySize = new BooleanProperty(this, "isScaledBySize", Boolean.FALSE);
  
  protected Direction getDefaultDirection() {
    return Direction.FORWARD;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getMoveAxis() != null; }
  
  public class RuntimeMoveAtSpeed extends DirectionSpeedTransformResponse.RuntimeDirectionSpeedTransformResponse { public RuntimeMoveAtSpeed() { super(); }
    
    private Vector3d m_directionVector;
    public void prologue(double t) {
      super.prologue(t);
      Direction directionValue = direction.getDirectionValue();
      if (directionValue != null) {
        m_directionVector = directionValue.getMoveAxis();
        if (isScaledBySize.booleanValue()) {
          Vector3d subjectSize = m_subject.getSize();
          m_directionVector.x *= x;
          m_directionVector.y *= y;
          m_directionVector.z *= z;
        }
      } else {
        m_directionVector = new Vector3d();
      }
    }
    
    public void update(double t) {
      super.update(t);
      double delta = getDT() * getSpeed();
      m_subject.moveRightNow(MathUtilities.multiply(m_directionVector, delta), m_asSeenBy);
    }
  }
}
