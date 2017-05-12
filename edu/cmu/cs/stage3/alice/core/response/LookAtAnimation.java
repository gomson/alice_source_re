package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import javax.vecmath.Vector3d;

public class LookAtAnimation
  extends AbstractPointAtAnimation
{
  public final BooleanProperty onlyAffectYaw = new BooleanProperty(this, "onlyAffectYaw", Boolean.FALSE);
  
  public LookAtAnimation() {}
  
  private Model getHead(Transformable subject) { Element[] heads = subject.search(new ElementNamedCriterion("head", true));
    if ((heads.length > 0) && ((heads[0] instanceof Model))) {
      return (Model)heads[0];
    }
    return null;
  }
  
  public class RuntimeLookAtAnimation extends AbstractPointAtAnimation.RuntimeAbstractPointAtAnimation {
    public RuntimeLookAtAnimation() { super(); }
    Direction m_direction = null;
    double m_turnAmount = 0.0D;
    double m_turnAmountPrev = 0.0D;
    Transformable m_subjectTrans = null;
    
    public void prologue(double t)
    {
      m_target = target.getReferenceFrameValue();
      if (m_target == null) {
        throw new SimulationPropertyException(Messages.getString("character_value_must_not_be_null_"), getCurrentStack(), target);
      }
      super.prologue(t);
      
      m_turnAmountPrev = 0.0D;
      if (m_subject == m_target) {
        throw new SimulationPropertyException(Messages.getString("subject_and_character_values_must_not_be_the_same_"), getCurrentStack(), subject);
      }
      
      if (!m_subject.equals(subject.getTransformableValue()))
      {



        m_subjectTrans = subject.getTransformableValue();
        
        Matrix33 targetMatrix = m_subjectTrans.calculatePointAt(m_target, m_offset, m_upGuide, m_asSeenBy, true);
        Matrix33 subjectMatrix = m_subjectTrans.getOrientationAsAxes();
        
        Vector3d targetForward = targetMatrix.getRow(2);
        Vector3d subjectForward = subjectMatrix.getRow(2);
        
        double cosAngle = subjectForward.dot(targetForward) / (targetForward.length() * subjectForward.length());
        cosAngle = Math.acos(cosAngle);
        

        cosAngle /= 6.283185307179586D;
        



        if (cosAngle > 0.25D) {
          m_turnAmount = (cosAngle - 0.25D);
          

          Vector3d targetPos = m_target.getPosition(m_subjectTrans);
          if (x < 0.0D) {
            m_direction = Direction.LEFT;
          } else {
            m_direction = Direction.LEFT;
          }
        }
        else {
          m_turnAmount = 0.0D;
        }
      }
    }
    





    public void update(double t)
    {
      super.update(t);
      
      if (m_turnAmount > 0.0D) {
        double delta = m_turnAmount * getPortion(t) - m_turnAmountPrev;
        m_subjectTrans.rotateRightNow(m_direction.getTurnAxis(), delta, m_subjectTrans);
        m_turnAmountPrev += delta;
      }
    }
    

    protected boolean onlyAffectYaw()
    {
      Model head = LookAtAnimation.this.getHead(subject.getTransformableValue());
      if (head != null) return false;
      return true;
    }
    
    protected ReferenceFrame getTarget()
    {
      ReferenceFrame targetRef = target.getReferenceFrameValue();
      if ((targetRef instanceof Transformable)) {
        Model head = LookAtAnimation.this.getHead((Transformable)targetRef);
        if (head != null) return head;
        return target.getReferenceFrameValue(); }
      return target.getReferenceFrameValue();
    }
    
    protected Transformable getSubject()
    {
      Model head = LookAtAnimation.this.getHead(subject.getTransformableValue());
      if (head != null) return head;
      return subject.getTransformableValue();
    }
    
    protected Vector3d getOffset()
    {
      ReferenceFrame targetRef = target.getReferenceFrameValue();
      if ((targetRef instanceof Transformable)) {
        Model head = LookAtAnimation.this.getHead((Transformable)targetRef);
        if (head != null) return head.getBoundingBox().getCenter();
        return target.getReferenceFrameValue().getBoundingBox().getCenter(); }
      return target.getReferenceFrameValue().getBoundingBox().getCenter();
    }
  }
}
