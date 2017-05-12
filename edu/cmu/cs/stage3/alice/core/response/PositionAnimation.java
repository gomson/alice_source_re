package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import javax.vecmath.Vector3d;



















public class PositionAnimation
  extends AbstractPositionAnimation
{
  public final Vector3Property position = new Vector3Property(this, "position", new Vector3d(0.0D, 0.0D, 0.0D));
  public class RuntimePositionAnimation extends AbstractPositionAnimation.RuntimeAbstractPositionAnimation { public RuntimePositionAnimation() { super(); }
    
    protected Vector3d getPositionBegin() {
      return m_subject.getPosition(ReferenceFrame.ABSOLUTE);
    }
    
    protected Vector3d getPositionEnd() {
      return m_asSeenBy.getPosition(position.getVector3Value(), ReferenceFrame.ABSOLUTE);
    }
  }
  
  public PositionAnimation() {}
}
