package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.math.Quaternion;




public abstract class OrientationAnimation
  extends TransformAnimation
{
  public OrientationAnimation() {}
  
  public abstract class RuntimeOrientationAnimation
    extends TransformAnimation.RuntimeTransformAnimation
    implements AbsoluteTransformationListener
  {
    protected Quaternion m_quaternion0;
    private Quaternion m_quaternion1;
    
    public RuntimeOrientationAnimation()
    {
      super();
    }
    
    protected abstract Quaternion getTargetQuaternion();
    
    public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {
      markTargetQuaternionDirty();
    }
    
    protected void markTargetQuaternionDirty() { m_quaternion1 = null; }
    
    protected boolean affectQuaternion() {
      return true;
    }
    
    protected void setSubject(Transformable newSubject) {
      m_subject = newSubject;
      if (m_subject != null) m_quaternion0 = m_subject.getOrientationAsQuaternion(m_asSeenBy);
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      if (m_asSeenBy == null) {
        m_asSeenBy = m_subject.vehicle.getReferenceFrameValue();
      }
      if (affectQuaternion()) {
        m_quaternion0 = m_subject.getOrientationAsQuaternion(m_asSeenBy);
        markTargetQuaternionDirty();
      }
      m_asSeenBy.addAbsoluteTransformationListener(this);
    }
    
    public void update(double t) {
      super.update(t);
      if (affectQuaternion()) {
        if (m_quaternion1 == null) {
          m_quaternion1 = getTargetQuaternion();
        }
        Quaternion q = Quaternion.interpolate(m_quaternion0, m_quaternion1, getPortion(t));
        m_subject.setOrientationRightNow(q, m_asSeenBy);
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      if (m_asSeenBy != null) {
        m_asSeenBy.removeAbsoluteTransformationListener(this);
      }
    }
  }
}
