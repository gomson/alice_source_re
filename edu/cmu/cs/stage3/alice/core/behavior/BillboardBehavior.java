package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.lang.Messages;
import javax.vecmath.Vector3d;















public class BillboardBehavior
  extends InternalResponseBehavior
  implements AbsoluteTransformationListener
{
  public BillboardBehavior() {}
  
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final TransformableProperty target = new TransformableProperty(this, "target", null);
  public final Vector3Property offset = new Vector3Property(this, "offset", null);
  public final Vector3Property upGuide = new Vector3Property(this, "upGuide", null);
  public final ReferenceFrameProperty asSeenBy = new ReferenceFrameProperty(this, "asSeenBy", null);
  public final BooleanProperty onlyAffectYaw = new BooleanProperty(this, "onlyAffectYaw", Boolean.FALSE);
  
  private Transformable m_subject;
  private Transformable m_target;
  private Vector3d m_offset;
  private Vector3d m_upGuide;
  private ReferenceFrame m_asSeenBy;
  private boolean m_onlyAffectYaw;
  
  private boolean m_isDirty = false;
  
  protected void propertyChanging(Property property, Object value) {
    if (property == target) {
      if (value == subject.get()) {
        throw new IllegalArgumentException(Messages.getString("billboard_cannot_point_at_self"));
      }
    } else {
      super.propertyChanging(property, value);
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property != target)
    {

      super.propertyChanged(property, value);
    }
  }
  
  public void absoluteTransformationChanged(AbsoluteTransformationEvent absoluteTransformationEvent) {
    m_isDirty = true;
  }
  
  public void internalSchedule(double time, double dt) {
    if (m_isDirty) {
      if ((m_subject != null) && (m_target != null)) {
        m_subject.pointAtRightNow(m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw);
      }
      m_isDirty = false;
    }
  }
  
  public void started(World world, double time) {
    super.started(world, time);
    m_subject = subject.getTransformableValue();
    m_target = target.getTransformableValue();
    m_offset = offset.getVector3Value();
    m_upGuide = upGuide.getVector3Value();
    m_asSeenBy = asSeenBy.getReferenceFrameValue();
    m_onlyAffectYaw = onlyAffectYaw.booleanValue();
    if (m_subject != null) {
      m_subject.addAbsoluteTransformationListener(this);
    }
    if (m_target != null) {
      m_target.addAbsoluteTransformationListener(this);
    }
    m_isDirty = false;
  }
  
  public void stopped(World world, double time) {
    if (m_subject != null) {
      m_subject.removeAbsoluteTransformationListener(this);
    }
    if (m_target != null) {
      m_target.removeAbsoluteTransformationListener(this);
    }
    super.stopped(world, time);
  }
}
