package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;















public class MoveAnimation
  extends DirectionAmountTransformAnimation
{
  public MoveAnimation() {}
  
  public final BooleanProperty isScaledBySize = new BooleanProperty(this, "isScaledBySize", Boolean.FALSE);
  
  protected Direction getDefaultDirection() {
    return Direction.FORWARD;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getMoveAxis() != null; }
  
  public class RuntimeMoveAnimation extends DirectionAmountTransformAnimation.RuntimeDirectionAmountTransformAnimation { public RuntimeMoveAnimation() { super(); }
    
    private Vector3d m_vector;
    private Vector3d m_vectorPrev;
    protected Vector3d getVector() { Direction directionValue = direction.getDirectionValue();
      double amountValue = amount.doubleValue();
      if ((directionValue != null) && (!Double.isNaN(amountValue))) {
        Vector3d v = MathUtilities.multiply(directionValue.getMoveAxis(), amountValue);
        if (isScaledBySize.booleanValue()) {
          Vector3d subjectSize = m_subject.getSize();
          x *= x;
          y *= y;
          z *= z;
        }
        return v;
      }
      return new Vector3d();
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      m_vectorPrev = new Vector3d();
      m_vector = getVector();
    }
    
    public void update(double t) {
      super.update(t);
      Vector3d delta = MathUtilities.subtract(MathUtilities.multiply(m_vector, getPortion(t)), m_vectorPrev);
      m_subject.moveRightNow(delta, m_asSeenBy);
      m_vectorPrev.add(delta);
    }
  }
}
