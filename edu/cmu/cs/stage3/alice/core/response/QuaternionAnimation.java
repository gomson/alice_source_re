package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.QuaternionProperty;
import edu.cmu.cs.stage3.math.Quaternion;





















public class QuaternionAnimation
  extends OrientationAnimation
{
  public final QuaternionProperty quaternion = new QuaternionProperty(this, "quaternion", new Quaternion());
  public class RuntimeQuaternionAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeQuaternionAnimation() { super(); }
    
    private Quaternion m_quaternion;
    public void prologue(double t) {
      super.prologue(t);
      m_quaternion = quaternion.getQuaternionValue();
    }
    
    protected Quaternion getTargetQuaternion() {
      return m_quaternion;
    }
  }
  
  public QuaternionAnimation() {}
}
