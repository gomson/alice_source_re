package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import javax.vecmath.Vector3d;





public class KneelAnimation
  extends AbstractBodyPositionAnimation
{
  public final BooleanProperty oneKnee = new BooleanProperty(this, "one knee", Boolean.TRUE);
  
  public class RuntimeKneelAnimation extends AbstractBodyPositionAnimation.RuntimeAbstractBodyPositionAnimation { public RuntimeKneelAnimation() { super(); }
    
    public void prologue(double t)
    {
      super.prologue(t);
      
      findLegs();
      getInitialOrientations();
      setFinalOrientations();
    }
    
    protected Quaternion getTargetQuaternion()
    {
      return m_subject.calculateStandUp(m_subject.getWorld()).getQuaternion();
    }
    

    public void update(double t)
    {
      super.update(t);
      
      setOrientation(rightUpper, rightUpperInitialOrient, rightUpperFinalOrient, getPortion(t));
      setOrientation(rightLower, rightLowerInitialOrient, rightLowerFinalOrient, getPortion(t));
      if (rightFoot != null) { setOrientation(rightFoot, rightFootInitialOrient, rightFootFinalOrient, getPortion(t));
      }
      setOrientation(leftUpper, leftUpperInitialOrient, leftUpperFinalOrient, getPortion(t));
      setOrientation(leftLower, leftLowerInitialOrient, leftLowerFinalOrient, getPortion(t));
      if (leftFoot != null) { setOrientation(leftFoot, leftFootInitialOrient, leftFootFinalOrient, getPortion(t));
      }
      adjustHeight();
    }
    
    public void setFinalOrientations()
    {
      rightUpperFinalOrient = new Matrix33();
      rightLowerFinalOrient = new Matrix33();
      rightLowerFinalOrient.rotateX(1.5707963267948966D);
      rightFootFinalOrient = new Matrix33();
      rightFootFinalOrient.rotateX(1.5707963267948966D);
      
      if (oneKnee.booleanValue()) {
        double lengthSupportLeg = 0.0D;
        
        if (rightLower == null) {
          leftUpperFinalOrient = new Matrix33();
          leftUpperFinalOrient.rotateX(0.7853981633974483D);
          leftFootFinalOrient = new Matrix33();
          leftFootFinalOrient.rotateX(-0.7853981633974483D);
          
          rightUpperFinalOrient = new Matrix33();
          rightUpperFinalOrient.rotateX(-0.7853981633974483D);
          rightFootFinalOrient = new Matrix33();
          rightFootFinalOrient.rotateX(0.7853981633974483D);
        }
        

        if ((rightUpper != null) && (rightLower != null)) {
          Vector3 posLower = rightLower.getPosition(rightUpper);
          Box boxLower = rightLower.getBoundingBox(rightLower);
          
          lengthSupportLeg = Math.abs(y) + Math.abs(getMinimumz);
          
          double lengthLowerLeg = 0.0D;
          if (leftFoot != null) lengthLowerLeg = Math.abs(leftFoot.getPosition(leftLower).y) + Math.abs(leftFoot.getBoundingBox(leftFoot).getMinimum().z); else {
            lengthLowerLeg = Math.abs(leftLower.getBoundingBox(leftLower).getMinimum().y);
          }
          double diff = lengthSupportLeg - lengthLowerLeg;
          double angle = Math.asin(Math.abs(diff) / Math.abs(y));
          
          if (lengthSupportLeg * 2.0D < lengthLowerLeg) {
            leftUpperFinalOrient = new Matrix33();
            leftLowerFinalOrient = new Matrix33();
            leftLowerFinalOrient.rotateX(0.7853981633974483D);
            leftFootFinalOrient = new Matrix33();
            leftFootFinalOrient.rotateX(-0.7853981633974483D);
            
            rightUpperFinalOrient = new Matrix33();
            rightLowerFinalOrient = new Matrix33();
            rightLowerFinalOrient.rotateX(-0.7853981633974483D);
            rightFootFinalOrient = new Matrix33();
            rightFootFinalOrient.rotateX(0.7853981633974483D);
          } else if (diff < 0.0D) {
            leftUpperFinalOrient = new Matrix33();
            leftUpperFinalOrient.rotateX(-1.5707963267948966D - angle);
            leftLowerFinalOrient = new Matrix33();
            leftLowerFinalOrient.rotateX(1.5707963267948966D + angle);
            leftFootFinalOrient = new Matrix33();
          }
          else {
            leftUpperFinalOrient = new Matrix33();
            leftUpperFinalOrient.rotateX(-1.5707963267948966D + angle);
            leftLowerFinalOrient = new Matrix33();
            leftLowerFinalOrient.rotateX(1.5707963267948966D - angle);
            leftFootFinalOrient = new Matrix33();
          }
          
        }
      }
      else if (rightLower == null) {
        leftUpperFinalOrient = new Matrix33();
        leftUpperFinalOrient.rotateX(1.5707963267948966D);
        leftFootFinalOrient = new Matrix33();
        leftFootFinalOrient.rotateX(1.5707963267948966D);
        
        rightUpperFinalOrient = new Matrix33();
        rightUpperFinalOrient.rotateX(1.5707963267948966D);
        rightFootFinalOrient = new Matrix33();
        rightFootFinalOrient.rotateX(1.5707963267948966D);
      }
      else {
        leftUpperFinalOrient = new Matrix33();
        leftLowerFinalOrient = new Matrix33();
        leftLowerFinalOrient.rotateX(1.5707963267948966D);
        leftFootFinalOrient = new Matrix33();
        leftFootFinalOrient.rotateX(1.5707963267948966D);
      }
    }
  }
  
  public KneelAnimation() {}
}
