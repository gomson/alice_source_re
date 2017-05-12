package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.lang.Messages;
import javax.vecmath.Vector3d;


















public abstract class RotateAtSpeed
  extends DirectionSpeedTransformResponse
{
  public RotateAtSpeed() {}
  
  protected Direction getDefaultDirection() { return Direction.LEFT; }
  
  public abstract class RuntimeRotateAtSpeed extends DirectionSpeedTransformResponse.RuntimeDirectionSpeedTransformResponse { public RuntimeRotateAtSpeed() { super(); }
    
    private Vector3d m_axis;
    protected abstract Vector3d getAxis(Direction paramDirection);
    
    public void prologue(double t) { super.prologue(t);
      Direction directionValue = direction.getDirectionValue();
      m_axis = getAxis(directionValue);
      if (m_axis == null) {
        StringBuffer sb = new StringBuffer(Messages.getString("direction_value_must_not_be_"));
        if (directionValue != null) {
          sb.append(directionValue.getRepr());
        } else {
          sb.append(Messages.getString("null"));
        }
        sb.append('.');
        throw new SimulationPropertyException(sb.toString(), null, direction);
      }
    }
    
    public void update(double t) {
      super.update(t);
      m_subject.rotateRightNow(m_axis, getSpeed() * getDT(), m_asSeenBy);
    }
  }
}
