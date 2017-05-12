package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import javax.vecmath.Vector3d;

















public class ForwardVectorAnimation
  extends OrientationAnimation
{
  public final Vector3Property forward = new Vector3Property(this, "forward", new Vector3(0.0D, 0.0D, 1.0D));
  public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
  public class RuntimeForwardVectorAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeForwardVectorAnimation() { super(); }
    
    private Vector3d m_forward;
    private Vector3d m_upGuide;
    public void prologue(double t) {
      super.prologue(t);
      m_forward = forward.getVector3Value();
      m_upGuide = upGuide.getVector3Value();
    }
    
    protected Quaternion getTargetQuaternion() {
      return Transformable.calculateOrientation(m_forward, m_upGuide).getQuaternion();
    }
  }
  
  public ForwardVectorAnimation() {}
}
