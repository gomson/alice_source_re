package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Vector3d;



public abstract class AbstractPositionAnimation
  extends TransformAnimation
{
  public AbstractPositionAnimation() {}
  
  public abstract class RuntimeAbstractPositionAnimation
    extends TransformAnimation.RuntimeTransformAnimation
    implements AbsoluteTransformationListener
  {
    private Vector3d m_positionBegin;
    private Vector3d m_positionEnd;
    
    public RuntimeAbstractPositionAnimation()
    {
      super();
    }
    
    protected abstract Vector3d getPositionBegin();
    
    protected abstract Vector3d getPositionEnd();
    
    public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {
      m_positionEnd = null;
    }
    
    public void prologue(double t) {
      super.prologue(t);
      if (m_asSeenBy == null) {
        m_asSeenBy = m_subject.vehicle.getReferenceFrameValue();
      }
      m_positionBegin = getPositionBegin();
      m_positionEnd = null;
      m_asSeenBy.addAbsoluteTransformationListener(this);
    }
    
    public void update(double t) {
      super.update(t);
      if (m_positionEnd == null) {
        m_positionEnd = getPositionEnd();
      }
      m_subject.setPositionRightNow(MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), ReferenceFrame.ABSOLUTE);
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      if (m_asSeenBy != null) m_asSeenBy.removeAbsoluteTransformationListener(this);
    }
  }
}
