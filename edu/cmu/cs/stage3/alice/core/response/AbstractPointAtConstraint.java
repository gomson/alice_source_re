package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;

















public class AbstractPointAtConstraint
  extends TransformResponse
{
  public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
  public final Vector3Property offset = new Vector3Property(this, "offset", null);
  public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
  
  public abstract class RuntimeAbstractPointAtConstraint extends TransformResponse.RuntimeTransformResponse { public RuntimeAbstractPointAtConstraint() { super(); }
    
    private Vector3d m_upGuide;
    private Vector3d m_offset;
    private ReferenceFrame m_target;
    private boolean m_onlyAffectYaw;
    protected abstract boolean onlyAffectYaw();
    
    public void prologue(double t) { super.prologue(t);
      m_target = target.getReferenceFrameValue();
      m_offset = offset.getVector3Value();
      m_upGuide = upGuide.getVector3Value();
      m_onlyAffectYaw = onlyAffectYaw();
      if (m_target == null) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_null_"), null, target);
      }
      if (m_target == m_subject) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_equal_to_the_subject_value_"), getCurrentStack(), target);
      }
    }
    
    protected boolean isTurnAroundNecessary() {
      return false;
    }
    
    public void update(double t) {
      super.update(t);
      m_subject.pointAtRightNow(m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw);
      if (isTurnAroundNecessary()) {
        m_subject.rotateRightNow(MathUtilities.getYAxis(), 0.5D);
      }
    }
  }
  
  public AbstractPointAtConstraint() {}
}
