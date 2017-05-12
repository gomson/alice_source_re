package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import javax.vecmath.Vector3d;

public class CharacterViewAnimation
  extends AbstractPositionAnimation
{
  public CharacterViewAnimation() {}
  
  public static CharacterViewAnimation createCharacterViewAnimation(Object subject, Object asSeenBy)
  {
    CharacterViewAnimation charViewAnimation = new CharacterViewAnimation();
    subject.set(subject);
    asSeenBy.set(asSeenBy);
    return charViewAnimation; }
  
  public class RuntimeCloseUpAnimation extends AbstractPositionAnimation.RuntimeAbstractPositionAnimation { private Box m_subjectBoundingBox;
    
    public RuntimeCloseUpAnimation() { super(); }
    
    private Model m_characterHead = null;
    private Matrix33 m_orientationBegin = null;
    private Matrix33 m_orientationEnd = null;
    
    protected Vector3d getPositionBegin()
    {
      return m_subject.getPosition(ReferenceFrame.ABSOLUTE);
    }
    
    protected Vector3d getPositionEnd()
    {
      Vector3d v = new Vector3d(0.0D, 0.0D, 0.0D);
      
      if ((asSeenBy.get() instanceof Model)) {
        Model character = (Model)asSeenBy.get();
        Element[] heads = character.search(new ElementNamedCriterion("head", true));
        if ((heads.length > 0) && ((heads[0] instanceof Model))) {
          m_characterHead = ((Model)heads[0]);
          v = m_characterHead.getBoundingBox().getCenterOfFrontFace();
          return m_characterHead.getPosition(v, ReferenceFrame.ABSOLUTE);
        }
        v = character.getBoundingBox().getCenterOfFrontFace();
        y *= 1.8D;
        return character.getPosition(v, ReferenceFrame.ABSOLUTE);
      }
      

      return m_asSeenBy.getPosition(v, ReferenceFrame.ABSOLUTE);
    }
    
    protected Matrix33 getOrientationBegin() {
      return m_subject.getOrientationAsAxes(ReferenceFrame.ABSOLUTE);
    }
    
    protected Matrix33 getOrientationEnd() {
      if (m_characterHead != null) {
        return m_characterHead.getOrientationAsAxes(ReferenceFrame.ABSOLUTE);
      }
      return m_asSeenBy.getOrientationAsAxes(ReferenceFrame.ABSOLUTE);
    }
    

    public void prologue(double t)
    {
      m_asSeenBy = asSeenBy.getReferenceFrameValue();
      if (m_asSeenBy == null) {
        throw new SimulationPropertyException(Messages.getString("character_value_must_not_be_null_"), getCurrentStack(), asSeenBy);
      }
      super.prologue(t);
      if (m_subject == m_asSeenBy) {
        throw new SimulationPropertyException(Messages.getString("subject_and_character_values_must_not_be_the_same_"), getCurrentStack(), subject);
      }
      m_orientationBegin = getOrientationBegin();
      m_orientationEnd = null;
    }
    
    public void update(double t)
    {
      super.update(t);
      if (m_orientationEnd == null) {
        m_orientationEnd = getOrientationEnd();
      }
      m_subject.setOrientationRightNow(Matrix33.interpolate(m_orientationBegin, m_orientationEnd, getPortion(t)), ReferenceFrame.ABSOLUTE);
    }
  }
}
