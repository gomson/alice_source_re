package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.DirectionProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.util.HowMuch;
import javax.vecmath.Vector3d;


public class FallDownAnimation
  extends AbstractBodyPositionAnimation
{
  public final DirectionProperty sideToFallOn = new DirectionProperty(this, "sideToFallOn", Direction.FORWARD);
  
  public class RuntimeFallDownAnimation extends AbstractBodyPositionAnimation.RuntimeAbstractBodyPositionAnimation { public RuntimeFallDownAnimation() { super(); }
    

    private Vector3d m_positionBegin;
    private Vector3d m_positionEnd;
    private Direction m_side = null;
    
    public void prologue(double t)
    {
      super.prologue(t);
      
      m_positionBegin = m_subject.getPosition(m_subject.getWorld());
      m_positionEnd = null;
      
      m_side = sideToFallOn.getDirectionValue();
      
      findLegs();
      findArms();
      getInitialOrientations();
      setFinalOrientations();
    }
    
    public void update(double t)
    {
      super.update(t);
      if (m_positionEnd == null) {
        m_positionEnd = getPositionEnd();
      }
      
      m_subject.setPositionRightNow(MathUtilities.interpolate(m_positionBegin, m_positionEnd, getPortion(t)), ReferenceFrame.ABSOLUTE);
      
      setOrientation(rightUpperArm, rightUpperArmInitialOrient, rightUpperArmFinalOrient, getPortion(t));
      setOrientation(rightLowerArm, rightLowerArmInitialOrient, rightLowerArmFinalOrient, getPortion(t));
      setOrientation(leftUpperArm, leftUpperArmInitialOrient, leftUpperArmFinalOrient, getPortion(t));
      setOrientation(leftLowerArm, leftLowerArmInitialOrient, leftLowerArmFinalOrient, getPortion(t));
      
      adjustHeight();
    }
    
    protected Vector3d getPositionEnd() {
      Vector3d endPos = m_subject.getPosition(m_subject.getWorld());
      
      double pivotToGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
      double pivotToFront = 0.0D;
      
      if (m_side.equals(Direction.FORWARD)) {
        pivotToFront = m_subject.getBoundingBox(m_subject).getCenterOfFrontFace().z;
        
        if ((leftUpper != null) && (leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getMinimum() != null)) {
          pivotToFront = leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getCenterOfFrontFace().z;
        }
      } else if (m_side.equals(Direction.BACKWARD)) {
        pivotToFront = m_subject.getBoundingBox(m_subject).getCenterOfBackFace().z;
        
        if ((leftUpper != null) && (leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getMinimum() != null)) {
          pivotToFront = -1.0D * leftUpper.getBoundingBox(leftUpper, HowMuch.INSTANCE).getCenterOfBackFace().z;
        }
      } else if (m_side.equals(Direction.LEFT)) {
        pivotToFront = -0.8D * m_subject.getBoundingBox(m_subject).getCenterOfLeftFace().x;
      } else if (m_side.equals(Direction.RIGHT)) {
        pivotToFront = -0.8D * m_subject.getBoundingBox(m_subject).getCenterOfLeftFace().x;
      }
      y = (y - pivotToGround + 0.9D * pivotToFront);
      
      return endPos;
    }
    
    protected Quaternion getTargetQuaternion()
    {
      Matrix44 quat = m_subject.getTransformation(m_subject.getWorld());
      m_subject.standUpRightNow();
      
      if (m_side.equals(Direction.FORWARD)) {
        m_subject.turnRightNow(Direction.FORWARD, 0.25D);
      } else if (m_side.equals(Direction.BACKWARD)) {
        m_subject.turnRightNow(Direction.BACKWARD, 0.25D);
      } else if (m_side.equals(Direction.LEFT)) {
        m_subject.rollRightNow(Direction.LEFT, 0.25D);
      } else if (m_side.equals(Direction.RIGHT)) {
        m_subject.rollRightNow(Direction.RIGHT, 0.25D);
      }
      Matrix33 orient = m_subject.getOrientationAsAxes(m_subject.getWorld());
      m_subject.setAbsoluteTransformationRightNow(quat);
      return orient.getQuaternion();
    }
    






    public void setFinalOrientations()
    {
      if ((m_side.equals(Direction.LEFT)) || (m_side.equals(Direction.RIGHT))) {
        rightUpperArmFinalOrient = new Matrix33();
        rightUpperArmFinalOrient.rotateX(Math.random() * -0.5D * 3.141592653589793D);
        
        leftUpperArmFinalOrient = new Matrix33();
        leftUpperArmFinalOrient.rotateX(Math.random() * -0.5D * 3.141592653589793D);
        
        rightLowerArmFinalOrient = new Matrix33();
        rightLowerArmFinalOrient.rotateX(Math.random() * -0.5D * 3.141592653589793D);
        
        leftLowerArmFinalOrient = new Matrix33();
        leftLowerArmFinalOrient.rotateX(Math.random() * -0.5D * 3.141592653589793D);
      } else if ((m_side.equals(Direction.FORWARD)) || (m_side.equals(Direction.BACKWARD))) {
        rightUpperArmFinalOrient = new Matrix33();
        rightUpperArmFinalOrient.rotateZ(Math.random() * 0.5D * 3.141592653589793D);
        
        leftUpperArmFinalOrient = new Matrix33();
        leftUpperArmFinalOrient.rotateZ(Math.random() * -0.5D * 3.141592653589793D);
        
        rightLowerArmFinalOrient = new Matrix33();
        rightLowerArmFinalOrient.rotateZ(Math.random() * 0.5D * 3.141592653589793D);
        
        leftLowerArmFinalOrient = new Matrix33();
        leftLowerArmFinalOrient.rotateZ(Math.random() * -0.5D * 3.141592653589793D);
      }
    }
  }
  
  public FallDownAnimation() {}
}
