package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import javax.vecmath.Vector3d;














public abstract class AbstractPointAtAnimation
  extends OrientationAnimation
{
  public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
  public final Vector3Property offset = new Vector3Property(this, "offset", null);
  public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
  
  public abstract class RuntimeAbstractPointAtAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeAbstractPointAtAnimation() { super(); }
    
    protected ReferenceFrame m_target;
    protected Vector3d m_offset;
    protected Vector3d m_upGuide;
    protected boolean m_onlyAffectYaw;
    protected abstract boolean onlyAffectYaw();
    
    protected ReferenceFrame getTarget() {
      return target.getReferenceFrameValue();
    }
    
    protected Transformable getSubject() { return subject.getTransformableValue(); }
    
    protected Vector3d getOffset() {
      return offset.getVector3Value();
    }
    
    protected Vector3d getUpguide() { return upGuide.getVector3Value(); }
    


    public void prologue(double t)
    {
      super.prologue(t);
      
      m_target = getTarget();
      m_offset = getOffset();
      m_upGuide = getUpguide();
      m_onlyAffectYaw = onlyAffectYaw();
      if (m_target == null) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_null_"), null, target);
      }
      if (m_target == m_subject) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_equal_to_the_subject_value_"), getCurrentStack(), target);
      }
      if ((m_onlyAffectYaw) && (m_subject.isAncestorOf(m_target)))
        throw new SimulationPropertyException(m_subject.name.getStringValue() + " " + Messages.getString("can_t_turn_to_face_or_turn_away_from_a_part_of_itself_"), getCurrentStack(), target);
    }
    
    protected Matrix33 getTargetMatrix33() {
      return m_subject.calculatePointAt(m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw);
    }
    
    protected Quaternion getTargetQuaternion() {
      return getTargetMatrix33().getQuaternion();
    }
    
    public void update(double t)
    {
      markTargetQuaternionDirty();
      super.update(t);
    }
  }
  
  public AbstractPointAtAnimation() {}
}
