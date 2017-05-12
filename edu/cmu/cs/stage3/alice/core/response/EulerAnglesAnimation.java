package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.EulerAnglesProperty;
import edu.cmu.cs.stage3.math.EulerAngles;
import edu.cmu.cs.stage3.math.Quaternion;




















public class EulerAnglesAnimation
  extends OrientationAnimation
{
  public final EulerAnglesProperty eulerAngles = new EulerAnglesProperty(this, "eulerAngles", new EulerAngles(0.0D, 0.0D, 0.0D));
  public class RuntimeEulerAnglesAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeEulerAnglesAnimation() { super(); }
    
    private EulerAngles m_eulerAngles;
    public void prologue(double t) {
      super.prologue(t);
      m_eulerAngles = EulerAngles.revolutionsToRadians(eulerAngles.getEulerAnglesValue());
    }
    
    protected Quaternion getTargetQuaternion() {
      return m_eulerAngles.getQuaternion();
    }
  }
  
  public EulerAnglesAnimation() {}
}
