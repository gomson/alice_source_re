package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;
import javax.vecmath.Vector3d;

















public abstract class RotateAnimation
  extends DirectionAmountTransformAnimation
{
  public RotateAnimation() {}
  
  protected Direction getDefaultDirection() { return Direction.LEFT; }
  
  public abstract class RuntimeRotateAnimation extends DirectionAmountTransformAnimation.RuntimeDirectionAmountTransformAnimation { public RuntimeRotateAnimation() { super(); }
    
    private Vector3d m_axis;
    private double m_amount;
    private double m_amountPrev;
    protected abstract Vector3d getAxis(Direction paramDirection);
    
    public void prologue(double t) { super.prologue(t);
      Direction directionValue = direction.getDirectionValue();
      m_amount = amount.doubleValue();
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
      m_amountPrev = 0.0D;
    }
    
    public void update(double t) {
      super.update(t);
      double delta = m_amount * getPortion(t) - m_amountPrev;
      m_subject.rotateRightNow(m_axis, delta, m_asSeenBy);
      m_amountPrev += delta;
    }
  }
}
