package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;

















public abstract class AbstractMoveInDirectionOfAnimation
  extends TransformAnimation
{
  public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(1.0D));
  
  public abstract class RuntimeAbstractMoveInDirectionOfAnimationAnimation extends TransformAnimation.RuntimeTransformAnimation { public RuntimeAbstractMoveInDirectionOfAnimationAnimation() { super(); }
    
    private ReferenceFrame m_target;
    
    protected abstract double getActualAmountValue();
    
    protected Vector3d getVector() { double amountValue = getActualAmountValue();
      if (!Double.isNaN(amountValue)) {
        Vector3d v = m_target.getPosition(m_subject);
        double length = MathUtilities.getLength(v);
        if (length > 0.0D) {
          v.scale(amountValue / length);
        } else {
          v.set(0.0D, 0.0D, amountValue);
        }
        return v;
      }
      return new Vector3d();
    }
    
    private Vector3d m_vector;
    private Vector3d m_vectorPrev;
    public void prologue(double t) { super.prologue(t);
      m_target = target.getReferenceFrameValue();
      if (m_target == null) {
        throw new SimulationPropertyException(Messages.getString("target_must_not_be_null_"), getCurrentStack(), target);
      }
      
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
  
  public AbstractMoveInDirectionOfAnimation() {}
}
