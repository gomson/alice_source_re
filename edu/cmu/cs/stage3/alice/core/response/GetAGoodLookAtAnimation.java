package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Interpolator;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;















public class GetAGoodLookAtAnimation
  extends OrientationAnimation
{
  public final ReferenceFrameProperty target = new ReferenceFrameProperty(this, "target", null);
  public class RuntimeGetAGoodLookAtAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeGetAGoodLookAtAnimation() { super(); }
    
    private ReferenceFrame m_target;
    private Matrix44 m_transformationBegin;
    private Matrix44 m_transformationEnd;
    public void prologue(double t) {
      super.prologue(t);
      m_target = target.getReferenceFrameValue();
      
      if (m_target == null) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_null_"), null, target);
      }
      if (m_target == m_subject) {
        throw new SimulationPropertyException(Messages.getString("target_value_must_not_be_equal_to_the_subject_value_"), getCurrentStack(), target);
      }
      
      m_transformationBegin = m_subject.getTransformation(m_asSeenBy);
      m_transformationEnd = new Matrix44(m_subject.calculateGoodLookAt(m_target, m_asSeenBy));
    }
    
    protected boolean affectQuaternion() {
      return true;
    }
    
    protected Quaternion getTargetQuaternion() {
      return m_transformationEnd.getAxes().getQuaternion();
    }
    
    public void update(double t) {
      double portion = getPortion(t);
      







      double x = Interpolator.interpolate(m_transformationBegin.m30, m_transformationEnd.m30, portion);
      double y = Interpolator.interpolate(m_transformationBegin.m31, m_transformationEnd.m31, portion);
      double z = Interpolator.interpolate(m_transformationBegin.m32, m_transformationEnd.m32, portion);
      
      m_subject.setPositionRightNow(x, y, z, m_asSeenBy);
      super.update(t);
    }
    
    public void epilogue(double t) {
      if ((m_subject != null) && (m_transformationEnd != null)) {
        m_subject.setPositionRightNow(m_transformationEnd.m30, m_transformationEnd.m31, m_transformationEnd.m32, m_asSeenBy);
      }
      super.epilogue(t);
    }
  }
  
  public GetAGoodLookAtAnimation() {}
}
