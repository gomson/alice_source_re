package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNameContainsCriterion;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import javax.vecmath.Vector3d;

public class AbstractBodyPositionAnimation extends OrientationAnimation
{
  public AbstractBodyPositionAnimation() {}
  
  public abstract class RuntimeAbstractBodyPositionAnimation extends OrientationAnimation.RuntimeOrientationAnimation
  {
    public RuntimeAbstractBodyPositionAnimation()
    {
      super();
    }
    
    protected Transformable rightUpper = null;
    protected Transformable rightLower = null;
    protected Transformable rightFoot = null;
    
    protected Transformable leftUpper = null;
    protected Transformable leftLower = null;
    protected Transformable leftFoot = null;
    

    protected Transformable rightUpperArm = null;
    protected Transformable rightLowerArm = null;
    
    protected Transformable leftUpperArm = null;
    protected Transformable leftLowerArm = null;
    


    protected Matrix33 rightUpperInitialOrient = null;
    protected Matrix33 rightLowerInitialOrient = null;
    protected Matrix33 rightFootInitialOrient = null;
    
    protected Matrix33 leftUpperInitialOrient = null;
    protected Matrix33 leftLowerInitialOrient = null;
    protected Matrix33 leftFootInitialOrient = null;
    
    protected Matrix33 rightUpperFinalOrient = null;
    protected Matrix33 rightLowerFinalOrient = null;
    protected Matrix33 rightFootFinalOrient = null;
    
    protected Matrix33 leftUpperFinalOrient = null;
    protected Matrix33 leftLowerFinalOrient = null;
    protected Matrix33 leftFootFinalOrient = null;
    

    protected Matrix33 rightUpperArmInitialOrient = null;
    protected Matrix33 rightLowerArmInitialOrient = null;
    
    protected Matrix33 leftUpperArmInitialOrient = null;
    protected Matrix33 leftLowerArmInitialOrient = null;
    
    protected Matrix33 rightUpperArmFinalOrient = null;
    protected Matrix33 rightLowerArmFinalOrient = null;
    
    protected Matrix33 leftUpperArmFinalOrient = null;
    protected Matrix33 leftLowerArmFinalOrient = null;
    
    protected void adjustHeight() {
      double distanceAboveGround = 0.0D;
      

      if (m_subject != null) {
        distanceAboveGround = m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y;
        
        double roundHeight = Math.round(m_subject.getBoundingBox(m_subject.getWorld()).getCenterOfBottomFace().y);
        int level = (int)Math.round(roundHeight / 256.0D);
        m_subject.moveRightNow(edu.cmu.cs.stage3.alice.core.Direction.DOWN, distanceAboveGround - 256.0D * level, m_subject.getWorld());
      }
    }
    
    public Transformable getTransformableChild(Transformable parent) {
      if (parent == null) return null;
      Element[] legBits = parent.getChildren(Transformable.class);
      

      if (legBits.length == 1)
        return (Transformable)legBits[0];
      return null;
    }
    
    protected void setOrientation(Transformable part, Matrix33 initialOrient, Matrix33 finalOrient, double portion) {
      if (part != null) {
        Matrix33 currentOrient = Matrix33.interpolate(initialOrient, finalOrient, portion);
        
        part.setOrientationRightNow(currentOrient, (ReferenceFrame)part.getParent());
      }
    }
    
    public void getInitialOrientations()
    {
      if (rightUpper != null) rightUpperInitialOrient = rightUpper.getOrientationAsAxes((ReferenceFrame)rightUpper.getParent());
      if (rightLower != null) rightLowerInitialOrient = rightLower.getOrientationAsAxes((ReferenceFrame)rightLower.getParent());
      if (rightFoot != null) { rightFootInitialOrient = rightFoot.getOrientationAsAxes((ReferenceFrame)rightFoot.getParent());
      }
      if (leftUpper != null) leftUpperInitialOrient = leftUpper.getOrientationAsAxes((ReferenceFrame)leftUpper.getParent());
      if (leftLower != null) leftLowerInitialOrient = leftLower.getOrientationAsAxes((ReferenceFrame)leftLower.getParent());
      if (leftFoot != null) { leftFootInitialOrient = leftFoot.getOrientationAsAxes((ReferenceFrame)leftFoot.getParent());
      }
      if (rightUpperArm != null) { rightUpperArmInitialOrient = rightUpperArm.getOrientationAsAxes((ReferenceFrame)rightUpperArm.getParent());
      }
      if (rightLowerArm != null) { rightLowerArmInitialOrient = rightLowerArm.getOrientationAsAxes((ReferenceFrame)rightLowerArm.getParent());
      }
      if (leftUpperArm != null) leftUpperArmInitialOrient = leftUpperArm.getOrientationAsAxes((ReferenceFrame)leftUpperArm.getParent());
      if (leftLowerArm != null) leftLowerArmInitialOrient = leftLowerArm.getOrientationAsAxes((ReferenceFrame)leftLowerArm.getParent());
    }
    
    public void findArms()
    {
      Element[] arms = m_subject.search(new ElementNameContainsCriterion("Arm"));
      for (int i = 0; i < arms.length; i++) {
        if ((arms[i].getKey().indexOf("left") != -1) && ((arms[i] instanceof Transformable))) {
          leftUpperArm = ((Transformable)arms[i]);
          leftLowerArm = getTransformableChild(leftUpperArm);
          if ((leftLowerArm != null) && (leftLowerArm.name.getStringValue().indexOf("Hand") != -1)) {
            leftLowerArm = null;
          }
        } else if ((arms[i].getKey().indexOf("right") != -1) && ((arms[i] instanceof Transformable))) {
          rightUpperArm = ((Transformable)arms[i]);
          rightLowerArm = getTransformableChild(rightUpperArm);
          if ((rightLowerArm != null) && (rightLowerArm.name.getStringValue().indexOf("Hand") != -1)) {
            rightLowerArm = null;
          }
        }
      }
    }
    
    public void findLegs()
    {
      Element[] legs = m_subject.search(new ElementNameContainsCriterion("UpperLeg"));
      for (int i = 0; i < legs.length; i++) {
        if ((legs[i].getKey().indexOf("left") != -1) && ((legs[i] instanceof Transformable))) {
          leftUpper = ((Transformable)legs[i]);
          leftLower = getTransformableChild(leftUpper);
          if (leftLower.name.getStringValue().indexOf("Foot") != -1) {
            leftFoot = leftLower;
            leftLower = null;
          }
          if (leftLower != null) {
            leftFoot = getTransformableChild(leftLower);
          } else {
            leftFoot = getTransformableChild(leftUpper);
          }
        } else if ((legs[i].getKey().indexOf("right") != -1) && ((legs[i] instanceof Transformable))) {
          rightUpper = ((Transformable)legs[i]);
          
          rightLower = getTransformableChild(rightUpper);
          if (rightLower.name.getStringValue().indexOf("Foot") != -1) {
            rightFoot = rightLower;
            rightLower = null;
          }
          if (rightLower != null) {
            rightFoot = getTransformableChild(rightLower);
          } else {
            rightFoot = getTransformableChild(rightUpper);
          }
        }
      }
    }
  }
}
