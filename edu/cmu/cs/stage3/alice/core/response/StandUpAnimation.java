package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;











public class StandUpAnimation
  extends OrientationAnimation
{
  public StandUpAnimation() {}
  
  public class RuntimeStandUpAnimation
    extends OrientationAnimation.RuntimeOrientationAnimation
  {
    public RuntimeStandUpAnimation()
    {
      super();
    }
    
    protected Quaternion getTargetQuaternion() { return m_subject.calculateStandUp(m_asSeenBy).getQuaternion(); }
  }
}
