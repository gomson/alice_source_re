package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;



















public class SizeAnimation
  extends TransformAnimation
{
  public final Vector3Property size = new Vector3Property(this, "size", new Vector3d(1.0D, 1.0D, 1.0D));
  public class RuntimePositionAnimation extends TransformAnimation.RuntimeTransformAnimation { public RuntimePositionAnimation() { super(); }
    
    private Vector3d m_sizeBegin;
    private Vector3d m_sizeEnd;
    public void prologue(double t) {
      super.prologue(t);
      m_sizeBegin = m_subject.getSize();
      m_sizeEnd = size.getVector3Value();
    }
    
    public void update(double t) {
      super.update(t);
      m_subject.setSizeRightNow(MathUtilities.interpolate(m_sizeBegin, m_sizeEnd, getPortion(t)), m_asSeenBy);
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      m_subject.setSizeRightNow(m_sizeEnd, m_asSeenBy);
    }
  }
  
  public SizeAnimation() {}
}
