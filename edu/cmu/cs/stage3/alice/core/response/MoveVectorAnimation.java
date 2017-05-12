package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.lang.Messages;
import javax.vecmath.Vector3d;



















public class MoveVectorAnimation
  extends TransformAnimation
{
  public final Vector3Property vector = new Vector3Property(this, "vector", null);
  public class RuntimeMoveVectorAnimation extends TransformAnimation.RuntimeTransformAnimation { public RuntimeMoveVectorAnimation() { super(); }
    
    private Vector3d m_vector;
    private Vector3d m_vectorPrev;
    public void prologue(double t) {
      super.prologue(t);
      m_vectorPrev = new Vector3d();
      m_vector = vector.getVector3dValue();
      if (m_vector == null) {
        throw new SimulationPropertyException(Messages.getString("vector_value_must_not_be_null_"), null, vector);
      }
    }
    
    public void update(double t) {
      super.update(t);
      Vector3d delta = new Vector3d();
      delta.scale(getPortion(t), m_vector);
      delta.sub(m_vectorPrev);
      m_subject.moveRightNow(delta, m_asSeenBy);
      m_vectorPrev.add(delta);
    }
  }
  
  public MoveVectorAnimation() {}
}
