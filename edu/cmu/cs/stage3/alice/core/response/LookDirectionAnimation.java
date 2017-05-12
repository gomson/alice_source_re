package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;





public class LookDirectionAnimation
  extends OrientationAnimation
{
  public final DirectionProperty lookDirection = new DirectionProperty(this, "lookDirection", Direction.FORWARD);
  public final NumberProperty amount = new NumberProperty(this, "amount", new Double(0.2D));
  
  public class RuntimeLookDirectionAnimation extends OrientationAnimation.RuntimeOrientationAnimation { public RuntimeLookDirectionAnimation() { super(); }
    Transformable head = null;
    Direction m_lookDirection = null;
    
    Quaternion initialQuat = null;
    Quaternion targetQuat = null;
    
    public void prologue(double t)
    {
      super.prologue(t);
      m_lookDirection = lookDirection.getDirectionValue();
      findHead();
      if (head != null) m_subject = head;
      initialQuat = head.getOrientationAsQuaternion((ReferenceFrame)head.getParent());
      targetQuat = null;
    }
    

    public void update(double t)
    {
      if (targetQuat == null) {
        targetQuat = getTargetQuaternion();
      }
      Quaternion q = Quaternion.interpolate(initialQuat, targetQuat, getPortion(t));
      head.setOrientationRightNow(q, (ReferenceFrame)head.getParent());
    }
    

    public Quaternion getTargetQuaternion()
    {
      Quaternion quat = head.getOrientationAsQuaternion((ReferenceFrame)head.getParent());
      Matrix33 orient = new Matrix33();
      
      double amt = amount.doubleValue();
      if (!m_lookDirection.equals(Direction.FORWARD)) {
        if (m_lookDirection.equals(Direction.UP)) {
          orient.rotateX(-1.0D * amt * 3.141592653589793D);
        } else if (m_lookDirection.equals(Direction.DOWN)) {
          orient.rotateX(amt * 3.141592653589793D);
        } else if (m_lookDirection.equals(Direction.LEFT)) {
          orient.rotateY(amt * 3.141592653589793D);
        } else if (m_lookDirection.equals(Direction.RIGHT))
          orient.rotateY(-1.0D * amt * 3.141592653589793D);
      }
      quat.setMatrix33(orient);
      return quat;
    }
    
    public void findHead() {
      Element[] heads = m_subject.search(new ElementNameContainsCriterion("head"));
      
      if (heads.length > 0) {
        head = ((Transformable)heads[0]);
      }
    }
  }
  
  public LookDirectionAnimation() {}
}
